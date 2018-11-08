/**
 * 
 */
package se.de.hu_berlin.informatik.gen.spectra.tracecobertura.modules.sub;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map.Entry;

import se.de.hu_berlin.informatik.gen.spectra.modules.AbstractRunTestInNewJVMModuleWithJava7Runner;
import se.de.hu_berlin.informatik.spectra.provider.tracecobertura.coveragedata.CoverageDataFileHandler;
import se.de.hu_berlin.informatik.spectra.provider.tracecobertura.coveragedata.ProjectData;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;

/**
 * Runs a single test inside a new JVM and generates statistics. A timeout may be set
 * such that each executed test that runs longer than this timeout will
 * be aborted and will count as failing.
 * 
 * <p> if the test can't be run at all, this information is given in the
 * returned statistics, together with an error message.
 * 
 * @author Simon Heiden
 */
public class TraceCoberturaRunTestInNewJVMModuleWithJava7Runner extends AbstractRunTestInNewJVMModuleWithJava7Runner<ProjectData> {
	
	private File dataFile;

	public TraceCoberturaRunTestInNewJVMModuleWithJava7Runner(final String testOutput, 
			final boolean debugOutput, final Long timeout, final int repeatCount, 
			String instrumentedClassPath, final Path dataFile, final String javaHome, File projectDir) {
		super(testOutput, debugOutput, timeout, repeatCount, instrumentedClassPath, 
				dataFile, javaHome, projectDir, 
				"-Dnet.sourceforge.cobertura.datafile=" + dataFile.toAbsolutePath().toString());
		this.dataFile = dataFile.toFile();
	}
	
	@Override
	public boolean prepareBeforeRunningTest() {
		ProjectData projectData;
		if (dataFile.exists()) {
			projectData = CoverageDataFileHandler.loadCoverageData(dataFile);
			projectData.reset();
		} else {
			projectData = new ProjectData();
		}
		// reset the coverage data in the data file!
		CoverageDataFileHandler.saveCoverageData(projectData, dataFile);
		return true;
	}

	@Override
	public ProjectData getDataForExecutedTest() {
		if (dataFile.exists()) {
			ProjectData projectData = CoverageDataFileHandler.loadCoverageData(dataFile);
			Log.out(this, "loaded traces:");
			for (Entry<Long, List<String>> entry : projectData.getExecutionTraces().entrySet()) {
				StringBuilder builder = new StringBuilder();
				for (String string : entry.getValue()) {
					builder.append(string).append(",");
				}
				Log.out(this, "loaded trace " + entry.getKey() + ": " + builder.toString());
			}
			return projectData;
		} else {
			Log.err(this, "Cobertura data file does not exist: %s", dataFile);
			return null;
		}
	}

}
