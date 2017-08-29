/**
 * 
 */
package se.de.hu_berlin.informatik.sbfl.spectra.modules;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner;
import org.apache.tools.ant.taskdefs.optional.junit.PlainJUnitResultFormatter;

import se.de.hu_berlin.informatik.sbfl.MyJUnitTestRunner;
import se.de.hu_berlin.informatik.sbfl.TestStatistics;
import se.de.hu_berlin.informatik.sbfl.TestWrapper;
import se.de.hu_berlin.informatik.utils.files.FileUtils;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.OutputStreamManipulationUtilities;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;
import se.de.hu_berlin.informatik.utils.processors.sockets.ProcessorSocket;
import se.de.hu_berlin.informatik.utils.threaded.ExecutorServiceProvider;

/**
 * Runs a single test and generates statistics. A timeout may be set
 * such that each executed test that runs longer than this timeout will
 * be aborted and will count as failing.
 * 
 * <p> if the test can't be run at all, this information is given in the
 * returned statistics, together with an error message.
 * 
 * @author Simon Heiden
 */
public class ExtendedTestRunModule extends AbstractProcessor<TestWrapper, TestStatistics> {

	final private String testOutput;
	final private Long timeout;
	final private boolean debugOutput;
	final private int repeatCount;

	//used to execute the tests in a separate thread, one at a time
	final private ExecutorServiceProvider provider;
	
	public ExtendedTestRunModule(final String testOutput, final boolean debugOutput, final Long timeout, ClassLoader cl) {
		this(testOutput, debugOutput, timeout, 1, cl);
	}
	
	public ExtendedTestRunModule(final String testOutput, final boolean debugOutput, final Long timeout, final int repeatCount, ClassLoader cl) {
		super();
		this.testOutput = testOutput;
		this.timeout = timeout;
		this.debugOutput = debugOutput;
		this.repeatCount = repeatCount > 0 ? repeatCount : 1;
		this.provider = new ExecutorServiceProvider(1, cl);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public TestStatistics processItem(final TestWrapper testWrapper, ProcessorSocket<TestWrapper, TestStatistics> socket) {
		socket.forceTrack(testWrapper.toString());
//		Log.out(this, "Now processing: '%s'.", testWrapper);

		//disable std output
		if (!debugOutput) {
			System.out.flush();
			OutputStreamManipulationUtilities.switchOffStdOut();
			System.err.flush();
			OutputStreamManipulationUtilities.switchOffStdErr();
		}
		
		TestStatistics statistics = null;
		for (int i = 0; i < repeatCount; ++i) {
			//execute the test case with the given timeout (may be null for no timeout)
			TestStatistics tempStatistics = runTest(testWrapper, 
					testOutput + File.separator + testWrapper.toString().replace(':','_'), timeout);
			if (statistics == null) {
				statistics = tempStatistics;
			} else {
				statistics.mergeWith(tempStatistics);
			}
		}
		
		//enable std output
		if (!debugOutput) {
			System.out.flush();
			OutputStreamManipulationUtilities.switchOnStdOut();
			System.err.flush();
			OutputStreamManipulationUtilities.switchOnStdErr();
		}
		
		return statistics;
	}

	private TestStatistics runTest(final TestWrapper testWrapper, final String resultFile, final Long timeout) {
//		Log.out(this, "Start Running " + testWrapper);

//		FutureTask<Result> task = runner.getTest();
		
		JUnitTest test = new JUnitTest(testWrapper.getTestClassName());
		test.setSkipNonTests(false);
		String[] methods = new String[] {testWrapper.getTestMethodName()};
		boolean haltOnError = true;
		boolean filtertrace = true;
		boolean haltOnFailure = true;
		boolean showOutput = true;
		boolean logTestListenerEvents = true;
		MyJUnitTestRunner runner = new MyJUnitTestRunner(test, methods, 
				haltOnError, filtertrace, haltOnFailure, showOutput, logTestListenerEvents);
		JUnitResultFormatter resultFormatter = new PlainJUnitResultFormatter();
		resultFormatter.setOutput(System.out);
		runner.addFormatter(resultFormatter);
		
		runner.run();
		
		boolean timeoutOccured = test.runCount() == 0 && test.errorCount() == 0 && test.failureCount() == 0 && test.skipCount() == 0;
		boolean errorOccured = test.errorCount() > 0;
		boolean couldBeFinished = test.runCount() > 0 && test.skipCount() == 0;
		
		boolean wasSuccessful = couldBeFinished && !timeoutOccured && !errorOccured && test.failureCount() == 0;
		
		String errorMsg = null;
		test.errorCount();
		test.failureCount();
		test.getRunTime();
		test.runCount();

		if (resultFile != null) {
			final StringBuilder buff = new StringBuilder();
			if (!couldBeFinished) {
				if (timeoutOccured) {
					buff.append(runner + " TIMEOUT!!!" + System.lineSeparator());
				} else if (errorOccured) {
					buff.append(runner + " ERROR!!!" + System.lineSeparator());
				}
			} else if (!wasSuccessful) {
				buff.append("FAILED!!!" + System.lineSeparator());
//				for (final Failure f : result.getFailures()) {
//					buff.append(f.toString() + System.lineSeparator());
//				}
			}
			
			if (buff.length() != 0) {
				final File out = new File(resultFile);
				try {
					FileUtils.writeString2File(buff.toString(), out);
				} catch (IOException e) {
					Log.err(this, e, "IOException while trying to write to file '%s'", out);
				}
			}
		}

		long duration = test.getRunTime();
		return new TestStatistics(duration, wasSuccessful, 
					timeoutOccured, errorOccured, errorOccured, couldBeFinished, errorMsg);
	}

	@Override
	public boolean finalShutdown() {
		provider.shutdownAndWaitForTermination(20, TimeUnit.SECONDS, false);
		return super.finalShutdown();
	}
	
	
	
}
