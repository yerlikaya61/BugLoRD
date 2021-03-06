/*
 * This file is part of the "STARDUST" project. (c) Fabian Keller
 * <hello@fabian-keller.de> For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package se.de.hu_berlin.informatik.spectra.provider.loader.tracecobertura.report;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map.Entry;

import se.de.hu_berlin.informatik.spectra.core.ISpectra;
import se.de.hu_berlin.informatik.spectra.core.ITrace;
import se.de.hu_berlin.informatik.spectra.core.SourceCodeBlock;
import se.de.hu_berlin.informatik.spectra.core.traces.RawArrayTraceCollector;
import se.de.hu_berlin.informatik.spectra.core.traces.SimpleIndexer;
import se.de.hu_berlin.informatik.spectra.provider.loader.AbstractCoverageDataLoader;
import se.de.hu_berlin.informatik.spectra.provider.tracecobertura.coveragedata.ClassData;
import se.de.hu_berlin.informatik.spectra.provider.tracecobertura.coveragedata.CompressedTrace;
import se.de.hu_berlin.informatik.spectra.provider.tracecobertura.coveragedata.LineData;
import se.de.hu_berlin.informatik.spectra.provider.tracecobertura.coveragedata.PackageData;
import se.de.hu_berlin.informatik.spectra.provider.tracecobertura.coveragedata.SourceFileData;
import se.de.hu_berlin.informatik.spectra.provider.tracecobertura.data.CoverageData;
import se.de.hu_berlin.informatik.spectra.provider.tracecobertura.coveragedata.ProjectData;
import se.de.hu_berlin.informatik.spectra.provider.tracecobertura.report.TraceCoberturaReportWrapper;
import se.de.hu_berlin.informatik.spectra.util.SpectraFileUtils;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.tracking.ProgressTracker;

public abstract class TraceCoberturaReportLoader<T, K extends ITrace<T>>
		extends AbstractCoverageDataLoader<T, K, TraceCoberturaReportWrapper> {

	private Path tempOutputDir;
	private RawArrayTraceCollector traceCollector;
	private ProjectData projectData;

	public TraceCoberturaReportLoader(Path tempOutputDir) {
		this.tempOutputDir = tempOutputDir;
		traceCollector = new RawArrayTraceCollector(this.tempOutputDir);
	}
	
	private int traceCount = 0;
	
	@Override
	public boolean loadSingleCoverageData(ISpectra<T, K> lineSpectra, final TraceCoberturaReportWrapper reportWrapper,
			final boolean fullSpectra) {
		if (reportWrapper == null || reportWrapper.getReport() == null) {
			return false;
		}

		
		K trace = null;

		ProjectData projectData = reportWrapper.getReport().getProjectData();
		if (projectData == null) {
			return false;
		} else if (this.projectData == null) {
			this.projectData = projectData;
		}

		String testId;
		if (reportWrapper.getIdentifier() == null) {
			testId = String.valueOf(++traceCount);
		} else {
			++traceCount;
			testId = reportWrapper.getIdentifier();
		}
		
		try {
		trace = lineSpectra.addTrace(testId, traceCount, reportWrapper.isSuccessful());
		
		if (projectData.isReset()) {
			Log.warn(this, "Test '%s' produced no coverage.", testId);
			return true;
		}
		
		boolean coveredLines = false;
		
		// loop over all packages
		Iterator<CoverageData> itPackages = projectData.getPackages().iterator();
		while (itPackages.hasNext()) {
			PackageData packageData = (PackageData) itPackages.next();
			final String packageName = packageData.getName();

			onNewPackage(packageName, trace);

			// loop over all classes of the package
			Iterator<SourceFileData> itSourceFiles = packageData.getSourceFiles().iterator();
			while (itSourceFiles.hasNext()) {
				Iterator<CoverageData> itClasses = itSourceFiles.next().getClasses().iterator();
				while (itClasses.hasNext()) {
					ClassData classData = (ClassData) itClasses.next();
					// TODO: use actual class name!?
					final String actualClassName = classData.getName();
					final String sourceFilePath = classData.getSourceFileName();

					onNewClass(packageName, sourceFilePath, trace);

					// loop over all methods of the class
					// SortedSet<String> sortedMethods = new TreeSet<>();
					// sortedMethods.addAll(classData.getMethodNamesAndDescriptors());
					Iterator<String> itMethods = classData.getMethodNamesAndDescriptors().iterator();
					while (itMethods.hasNext()) {
						final String methodNameAndSig = itMethods.next();
						// String name = methodNameAndSig.substring(0,
						// methodNameAndSig.indexOf('('));
						// String signature =
						// methodNameAndSig.substring(methodNameAndSig.indexOf('('));

						final String methodIdentifier = String.format("%s:%s", actualClassName, methodNameAndSig);

						onNewMethod(packageName, sourceFilePath, methodIdentifier, trace);

						// loop over all lines of the method
						// SortedSet<CoverageData> sortedLines = new
						// TreeSet<>();
						// sortedLines.addAll(classData.getLines(methodNameAndSig));
						Iterator<CoverageData> itLines = classData.getLines(methodNameAndSig).iterator();
						while (itLines.hasNext()) {
							LineData lineData = (LineData) itLines.next();

							// set node involvement
							final T lineIdentifier = getIdentifier(
									packageName, sourceFilePath, methodNameAndSig, lineData.getLineNumber());

							long hits = lineData.getHits();
							if (hits > 0) {
								coveredLines = true;
							}
							onNewLine(
									packageName, sourceFilePath, methodIdentifier, lineIdentifier, lineSpectra, trace,
									fullSpectra, hits);
						}

						onLeavingMethod(packageName, sourceFilePath, methodIdentifier, lineSpectra, trace);
					}

					onLeavingClass(packageName, sourceFilePath, lineSpectra, trace);
				}
			}
			
			onLeavingPackage(packageName, lineSpectra, trace);
		}
		
		if (!coveredLines) {
			Log.warn(this, "Test '%s' covered no lines.", testId);
			if (projectData.getExecutionTraces() == null) {
				Log.err(this, "Execution trace is null for test '%s'.", testId);
				return false;
			}
			if (!projectData.getExecutionTraces().isEmpty()) {
				Log.err(this, "Execution trace for test '%s' is NOT empty.", testId);
				return false;
			}
			return true;
		}
		
		if (projectData.getExecutionTraces() == null) {
			Log.err(this, "Execution trace is null for test '%s'.", testId);
			return false;
		}

		if (projectData.getExecutionTraces().isEmpty()) {
			Log.warn(this, "No execution trace for test '%s'.", testId);
		} else {
			// TODO debug output
			// for (Object classData : projectData.getClasses()) {
			// Log.out(true, this, ((MyClassData)classData).getName());
			// }
//			 Log.out(true, this, "Trace: " + reportWrapper.getIdentifier());
//			String[] idToClassNameMap = projectData.getIdToClassNameMap();
			int threadId = -1;
			for (Iterator<Entry<Long, CompressedTrace>> iterator = projectData.getExecutionTraces().entrySet().iterator(); iterator.hasNext();) {
				Entry<Long, CompressedTrace> entry = iterator.next();
				++threadId;
				
				// collect the raw trace for future compression, etc.
				// this will, among others, extract common sequences for added traces
				traceCollector.addRawTraceToPool(traceCount, threadId, entry.getValue());
				// processed and done with...
				iterator.remove();
				
				
//				// int lastNodeIndex = -1;
//
//				int[][] compressedTrace = entry.getValue().getCompressedTrace();
//				SingleLinkedArrayQueue<Integer> traceOfNodeIDs = new SingleLinkedArrayQueue<>(
//						compressedTrace.length > 1000 ? 1000 : compressedTrace.length);
////				 Log.out(true, this, "Thread: " + compressedExecutionTrace.getKey());
//				// for efficiency (and memory footprint), we iterate only 
//				// over the compressed trace and reuse the repetition markers later
//				for (int[] statement : compressedTrace) {
////					 Log.out(true, this, "statement: " + Arrays.toString(statement));
//					// TODO store the class names with '.' from the beginning, or use the '/' version?
//					String classSourceFileName = idToClassNameMap[statement[0]];
//					if (classSourceFileName == null) {
////						throw new IllegalStateException("No class name found for class ID: " + statement[0]);
//						Log.err(this, "No class name found for class ID: " + statement[0]);
//						return false;
//					}
//					ClassData classData = projectData.getClassData(classSourceFileName.replace('/', '.'));
//
//					if (classData != null) {
//						if (classData.getCounterId2LineNumbers() == null) {
//							Log.err(this, "No counter ID to line number map for class " + classSourceFileName);
//							return false;
//						}
//						int lineNumber = classData.getCounterId2LineNumbers()[statement[1]];
//						
////						// these following lines print out the execution trace
////						String addendum = "";
////						if (statement.length > 2) {
////							switch (statement[2]) {
////							case 0:
////								addendum = " (from branch)";
////								break;
////							case 1:
////								addendum = " (after jump)";
////								break;
////							case 2:
////								addendum = " (after switch label)";
////								break;
////							default:
////								addendum = " (unknown)";
////							}
////						}
////						Log.out(true, this, classSourceFileName + ", counter  ID " + statement[1] +
////								", line " + (lineNumber < 0 ? "(not set)" : String.valueOf(lineNumber)) +
////								addendum);
//
//						// the array is initially set to -1 to indicate counter IDs that were not set, if any
//						if (lineNumber >= 0) {
//							int nodeIndex = getNodeIndex(classData.getSourceFileName(), lineNumber);
//							if (nodeIndex >= 0) {
//								traceOfNodeIDs.add(nodeIndex);
//							} else {
//								String throwAddendum = "";
//								if (statement.length > 2) {
//									switch (statement[2]) {
//									case 0:
//										throwAddendum = " (from branch)";
//										break;
//									case 1:
//										throwAddendum = " (after jump)";
//										break;
//									case 2:
//										throwAddendum = " (after switch label)";
//										break;
//									default:
//										throwAddendum = " (unknown)";
//									}
//								}
//								Log.err(this, "Node not found in spectra: "
//										+ classData.getSourceFileName() + ":" + lineNumber 
//										+ " from counter id " + statement[1] + throwAddendum);
//								return false;
//							}
//						} else if (statement.length <= 2 || statement[2] != 0) {
//							// disregard counter ID 0 if it comes from an internal variable (fake jump?!)
//							// this should actually not be an issue anymore!
////							throw new IllegalStateException("No line number found for counter ID: " + counterId
////									+ " in class: " + classData.getName());
//							Log.err(this, "No line number found for counter ID: " + statement[1]
//									+ " in class: " + classData.getName());
//							return false;
//						} else {
//							Log.err(this, "No line number found for counter ID: " + statement[1]
//									+ " in class: " + classData.getName());
//							return false;
////							// we have to add a dummy node here to not mess up the repetition markers
////							traceOfNodeIDs.add(-1);
////							Log.out(this, "Ignoring counter ID: " + statement[1]
////									+ " in class: " + classData.getName());
//						}
//					} else {
//						throw new IllegalStateException("Class data for '" + classSourceFileName + "' not found.");
//					}
//				}
//
//				compressedTrace = null;
//				// this only takes the compressed trace array that was based on the original input trace;
//				// multiple statements/touch points in the input trace may be mapped to the same node!
//				// we reuse the repetition markers from the input trace here!
//				ExecutionTrace eTrace = new ExecutionTrace(traceOfNodeIDs, entry.getValue());
//				// throw away not needed input traces (memory...)
//				iterator.remove();
//				
////				Log.out(this, "Test: " + testId);
////				Log.out(this, Arrays.toString(eTrace.reconstructFullTrace()));
////				Log.out(this, Arrays.toString(eTrace.getCompressedTrace()));
////				Log.out(this, Arrays.toString(eTrace.getRepetitionMarkers()));
////				if (eTrace.getChild() != null) {
////					Log.out(this, Arrays.toString(eTrace.getChild().getRepetitionMarkers()));
////				}
//				
//				// add the execution trace to the coverage trace and, thus, to the spectra
////				 trace.addExecutionTrace(traceOfNodeIDs);
//				// collect the raw trace for future compression, etc.
//				// this will, among others, extract common sequences for added traces
//				traceCollector.addRawTraceToPool(traceCount, threadId, eTrace);
//				traceOfNodeIDs = null;
			}
		}
		
		return true;
		} catch (Throwable e) {
			Log.err(this, e, "Exception thrown for test '%s'.", testId);
			return false;
		}
	}

	public void addExecutionTracesToSpectra(ISpectra<SourceCodeBlock, ? super K> spectra) {
		// gets called after ALL tests have been processed
		Log.out(SpectraFileUtils.class, "Generating sequence index...");
		try {
			traceCollector.getIndexer().getSequences();
		} catch (UnsupportedOperationException e) {
			traceCollector.getIndexer().getMappedSequences();
		}
		
		Log.out(SpectraFileUtils.class, "Generating execution traces...");
		
		// generate execution traces from raw traces
		spectra.setRawTraceCollector(traceCollector);
				
		ProgressTracker tracker = new ProgressTracker(false);
		// iterate through the traces
		for (ITrace<?> trace : spectra.getTraces()) {
			tracker.track("mem: " + Runtime.getRuntime().freeMemory() + ", " + trace.getIdentifier());
			//				Runtime.getRuntime().gc();

			// execute to generate indexed execution traces;
			// they will get stored to disk and reloaded again, later
			// TODO When saving the spectra, it should only copy the byte array
			trace.getExecutionTraces();
		}
		
		if (!traceCollector.getIndexer().isIndexed()) {
			Log.out(SpectraFileUtils.class, "Generating sequence index (again, due to changes)...");
			try {
				traceCollector.getIndexer().getSequences();
			} catch (UnsupportedOperationException e) {
				traceCollector.getIndexer().getMappedSequences();
			}
		}
		
		Log.out(SpectraFileUtils.class, "Mapping counter IDs to line numbers...");
		
		SimpleIndexer simpleIndexer = new SimpleIndexer(traceCollector.getIndexer(), spectra, projectData);
		
		// store the indexer with the spectra
		spectra.setIndexer(simpleIndexer);
		
		
		
//		// generate the execution traces for each test case and add them to the spectra;
//		// this needs to be done AFTER all tests have been executed
//		for (ITrace<?> trace : spectra.getTraces()) {
//			// generate execution traces from collected raw traces
//			List<ExecutionTrace> executionTraces = traceCollector.getExecutionTraces(trace.getIndex());
//			if (executionTraces != null) {
//				// add those traces to the test case
//				for (ExecutionTrace executionTrace : executionTraces) {
//					trace.addExecutionTrace(executionTrace);
//				}
//			}
//		}
//		
//		// remove temporary zip file containing the raw traces
//		try {
//			traceCollector.finalize();
//		} catch (Throwable e) {
//			// meh...
//		}
	}
	
}
