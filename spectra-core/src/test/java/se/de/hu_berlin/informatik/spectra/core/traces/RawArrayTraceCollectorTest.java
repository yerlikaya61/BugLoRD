/**
 * 
 */
package se.de.hu_berlin.informatik.spectra.core.traces;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.de.hu_berlin.informatik.spectra.provider.tracecobertura.coveragedata.CompressedTraceBase;
import se.de.hu_berlin.informatik.spectra.provider.tracecobertura.coveragedata.TraceIterator;
import se.de.hu_berlin.informatik.spectra.provider.tracecobertura.coveragedata.BufferedMap;
import se.de.hu_berlin.informatik.spectra.provider.tracecobertura.coveragedata.CloneableIterator;
import se.de.hu_berlin.informatik.utils.miscellaneous.TestSettings;


/**
 * @author Simon
 *
 */
public class RawArrayTraceCollectorTest extends TestSettings {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	private int[][] s(int... numbers) {
		int[][] result = new int[numbers.length][];
		for (int i = 0; i < numbers.length; ++i) {
			result[i] = new int[] {numbers[i]};
		}
		return result;
	}
	
	/**
	 * Test method for {@link se.de.hu_berlin.informatik.spectra.core.traces.RawArrayTraceCollector#addRawTraceToPool(java.lang.String, java.util.List)}.
	 */
	@Test
	public void testAddRawTraceToPool() throws Exception {
		Path outputDir = Paths.get(getStdTestDir()).resolve("test1");
		RawArrayTraceCollector collector = new RawArrayTraceCollector(outputDir);
		collector.addRawTraceToPool(1, 0, s(1,2, 3,4,5,6, 3,4,5,6, 3,4,5,6,7,8), true, outputDir, "t1");
		collector.addRawTraceToPool(2, 0, s(1,2, 3,4,5,6, 3,4,5,6, 3,4,5,6,7,8), true, outputDir, "t2");
		
		//                             0    1    2    4    4    1    2    4    4    1    2    1      3
		int[][] traceArray = s(1,2, 3,4, 5,6, 5,7, 5,7, 3,4, 5,6, 5,7, 5,7, 3,4, 5,6, 3,4, 5,6,7,8);
		collector.addRawTraceToPool(3, 0, traceArray, true, outputDir, "t3");
		
//		Thread.sleep(20000);
		System.out.println(collector.getGsTree());
		
		CompressedTraceBase<int[], ?> rawTrace = collector.getRawTraces(3).get(0);
		
		System.out.println(traceArray.length + ", " + arrayToString(traceArray));
		
//		System.out.println(rawTrace.getCompressedTrace());
		System.out.println(mapToString(rawTrace.getRepetitionMarkers()));
		System.out.println(mapToString(rawTrace.getChild().getRepetitionMarkers()));
		for (TraceIterator<int[]> iterator = rawTrace.iterator(); iterator.hasNext();) {
			int[] integer = iterator.next();
			System.out.print(Arrays.toString(integer) + ", ");
		}
		System.out.println();
		for (TraceIterator<int[]> iterator = rawTrace.iterator(); iterator.hasNext();) {
			if (iterator.isStartOfRepetition()) {
				System.out.print("s:");
			}
			if (iterator.isEndOfRepetition()) {
				System.out.print("e:");
			}
//			System.out.print(iterator.index + ":");
//			System.out.print(iterator.childIterator.index + ":");
//			System.out.print(iterator.childIterator.childIterator.index + ":");
			int[] integer = iterator.next();
			System.out.print(Arrays.toString(integer) + ", ");
		}
		System.out.println();
		
//		for (TraceIterator<Integer> iterator = rawTrace.iterator(1); iterator.hasNext();) {
//			if (iterator.isStartOfRepetition()) {
//				System.out.print("s:");
//			}
//			if (iterator.isEndOfRepetition()) {
//				System.out.print("e:");
//			}
////			System.out.print(iterator.index + ":");
////			System.out.print(iterator.childIterator.index + ":");
////			System.out.print(iterator.childIterator.childIterator.index + ":");
//			Integer integer = iterator.next();
//			System.out.print(integer + ", ");
//		}
//		System.out.println();
		
		ExecutionTrace executionTrace = collector.getExecutionTraces(3, true).get(0);
		collector.getIndexer().getSequences();
		
		SimpleIndexer simpleIndexer = new SimpleIndexer(collector.getIndexer());
		
		System.out.println(Arrays.toString(executionTrace.reconstructFullMappedTrace(simpleIndexer)));
		
		String result = "[ ";
		for (int j = 0; j < collector.getIndexer().getSequences().length; j++) {
			result += arrayToString(collector.getIndexer().getSequence(j)) + " ";
		}
		result += "]";
		System.out.println(result);
		
		System.out.println(executionTrace.getCompressedTrace());
		System.out.println(mapToString(executionTrace.getRepetitionMarkers()));
		System.out.println(mapToString(executionTrace.getChild().getRepetitionMarkers()));
		for (CloneableIterator<Integer> iterator = executionTrace.iterator(); iterator.hasNext();) {
			Integer integer = iterator.next();
			System.out.print(integer + ", ");
		}
		System.out.println();
		
		System.out.println(executionTrace.getCompressedTrace());
		
		System.out.println(collector.getGsTree());
		
		try {
			collector.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link se.de.hu_berlin.informatik.spectra.core.traces.RawArrayTraceCollector#addRawTraceToPool(java.lang.String, java.util.List)}.
	 */
	@Test
	public void testAddRawTraceToPool2() throws Exception {
		Path outputDir = Paths.get(getStdTestDir()).resolve("test2");
		RawArrayTraceCollector collector = new RawArrayTraceCollector(outputDir);
		collector.addRawTraceToPool(1, 0, s(1,2, 3,4,5,6, 3,4,5,6, 3,4,5,6,7,8), true, outputDir, "t1");
		collector.addRawTraceToPool(2, 0, s(1,2, 3,4,5,6, 3,4,5,6, 3,4,5,6,7,8), true, outputDir, "t2");
		
		int[][] traceArray = s(1,2, 3,4, 1,2, 3,4, 1,2, 3,4, 3,4, 5,6, 5,6, 5,7, 3,4, 5,6, 3,4, 5,6,7,8);
		collector.addRawTraceToPool(3, 0, traceArray, true, outputDir, "t3");
		
		System.out.println(collector.getGsTree());
		
		CompressedTraceBase<int[], ?> rawTrace = collector.getRawTraces(3).get(0);
		
		System.out.println(traceArray.length + ", " + arrayToString(traceArray));
		
//		System.out.println(rawTrace.getCompressedTrace());
		System.out.println(mapToString(rawTrace.getRepetitionMarkers()));
		System.out.println(mapToString(rawTrace.getChild().getRepetitionMarkers()));
		for (TraceIterator<int[]> iterator = rawTrace.iterator(); iterator.hasNext();) {
			int[] integer = iterator.next();
			System.out.print(Arrays.toString(integer) + ", ");
		}
		System.out.println();
		for (TraceIterator<int[]> iterator = rawTrace.iterator(); iterator.hasNext();) {
			if (iterator.isStartOfRepetition()) {
				System.out.print("s:");
			}
			if (iterator.isEndOfRepetition()) {
				System.out.print("e:");
			}
//			System.out.print(iterator.index + ":");
//			System.out.print(iterator.childIterator.index + ":");
//			System.out.print(iterator.childIterator.childIterator.index + ":");
			int[] integer = iterator.next();
			System.out.print(Arrays.toString(integer) + ", ");
		}
		System.out.println();
		
//		for (TraceIterator<Integer> iterator = rawTrace.iterator(1); iterator.hasNext();) {
//			if (iterator.isStartOfRepetition()) {
//				System.out.print("s:");
//			}
//			if (iterator.isEndOfRepetition()) {
//				System.out.print("e:");
//			}
////			System.out.print(iterator.index + ":");
////			System.out.print(iterator.childIterator.index + ":");
////			System.out.print(iterator.childIterator.childIterator.index + ":");
//			Integer integer = iterator.next();
//			System.out.print(integer + ", ");
//		}
//		System.out.println();
		
		ExecutionTrace executionTrace = collector.getExecutionTraces(3, true).get(0);
		collector.getIndexer().getSequences();
		
		SimpleIndexer simpleIndexer = new SimpleIndexer(collector.getIndexer());
		
		System.out.println(Arrays.toString(executionTrace.reconstructFullMappedTrace(simpleIndexer)));
		
		String result = "[ ";
		for (int j = 0; j < collector.getIndexer().getSequences().length; j++) {
			result += arrayToString(collector.getIndexer().getSequence(j)) + " ";
		}
		result += "]";
		System.out.println(result);
		
		System.out.println(executionTrace.getCompressedTrace());
		System.out.println(mapToString(executionTrace.getRepetitionMarkers()));
		System.out.println(mapToString(executionTrace.getChild().getRepetitionMarkers()));
		for (CloneableIterator<Integer> iterator = executionTrace.iterator(); iterator.hasNext();) {
			Integer integer = iterator.next();
			System.out.print(integer + ", ");
		}
		System.out.println();
		
		System.out.println(executionTrace.getCompressedTrace());
		
		System.out.println(collector.getGsTree());
		
		try {
			collector.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Test method for {@link se.de.hu_berlin.informatik.spectra.core.traces.RawArrayTraceCollector#addRawTraceToPool(java.lang.String, java.util.List)}.
	 */
	@Test
	public void testAddRawTraceToPool3() throws Exception {
		Path outputDir = Paths.get(getStdTestDir()).resolve("test3");
		RawArrayTraceCollector collector = new RawArrayTraceCollector(outputDir);
		collector.addRawTraceToPool(1, 0, s(1,2, 3,4,5,6, 3,4,5,6, 3,4,5,6,7,8), true, outputDir, "t1");
		collector.addRawTraceToPool(2, 0, s(1,2, 3,4,5,6, 3,4,5,6, 3,4,5,6,7,8), true, outputDir, "t2");
		
		int[][] traceArray = s(1,2, 3,4, 5,6, 5,7, 3,4, 5,6, 3,4, 5,6,7,8, 5,6,7,8, 5,6,7,8, 5,6,7,8, 5,6,7,8);
		collector.addRawTraceToPool(3, 0, traceArray, true, outputDir, "t3");
		
		System.out.println(collector.getGsTree());
		
		CompressedTraceBase<int[], ?> rawTrace = collector.getRawTraces(3).get(0);
		
		System.out.println(traceArray.length + ", " + arrayToString(traceArray));
		
//		System.out.println(rawTrace.getCompressedTrace());
		System.out.println(mapToString(rawTrace.getRepetitionMarkers()));
		System.out.println(mapToString(rawTrace.getChild().getRepetitionMarkers()));
		for (TraceIterator<int[]> iterator = rawTrace.iterator(); iterator.hasNext();) {
			int[] integer = iterator.next();
			System.out.print(Arrays.toString(integer) + ", ");
		}
		System.out.println();
		for (TraceIterator<int[]> iterator = rawTrace.iterator(); iterator.hasNext();) {
			if (iterator.isStartOfRepetition()) {
				System.out.print("s:");
			}
			if (iterator.isEndOfRepetition()) {
				System.out.print("e:");
			}
//			System.out.print(iterator.index + ":");
//			System.out.print(iterator.childIterator.index + ":");
//			System.out.print(iterator.childIterator.childIterator.index + ":");
			int[] integer = iterator.next();
			System.out.print(Arrays.toString(integer) + ", ");
		}
		System.out.println();
		
//		for (TraceIterator<Integer> iterator = rawTrace.iterator(1); iterator.hasNext();) {
//			if (iterator.isStartOfRepetition()) {
//				System.out.print("s:");
//			}
//			if (iterator.isEndOfRepetition()) {
//				System.out.print("e:");
//			}
////			System.out.print(iterator.index + ":");
////			System.out.print(iterator.childIterator.index + ":");
////			System.out.print(iterator.childIterator.childIterator.index + ":");
//			Integer integer = iterator.next();
//			System.out.print(integer + ", ");
//		}
//		System.out.println();
		
		ExecutionTrace executionTrace = collector.getExecutionTraces(3, true).get(0);
		collector.getIndexer().getSequences();
		
		SimpleIndexer simpleIndexer = new SimpleIndexer(collector.getIndexer());
		
		System.out.println(Arrays.toString(executionTrace.reconstructFullMappedTrace(simpleIndexer)));
		
		String result = "[ ";
		for (int j = 0; j < collector.getIndexer().getSequences().length; j++) {
			result += arrayToString(collector.getIndexer().getSequence(j)) + " ";
		}
		result += "]";
		System.out.println(result);
		
		System.out.println(executionTrace.getCompressedTrace());
		System.out.println(mapToString(executionTrace.getRepetitionMarkers()));
		System.out.println(mapToString(executionTrace.getChild().getRepetitionMarkers()));
		for (CloneableIterator<Integer> iterator = executionTrace.iterator(); iterator.hasNext();) {
			Integer integer = iterator.next();
			System.out.print(integer + ", ");
		}
		System.out.println();
		
		System.out.println(executionTrace.getCompressedTrace());
		
		System.out.println(collector.getGsTree());
		
		try {
			collector.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Test method for {@link se.de.hu_berlin.informatik.spectra.core.traces.RawArrayTraceCollector#addRawTraceToPool(java.lang.String, java.util.List)}.
	 */
	@Test
	public void testAddRawTraceToPool4() throws Exception {
		Path outputDir = Paths.get(getStdTestDir()).resolve("test4");
		RawArrayTraceCollector collector = new RawArrayTraceCollector(outputDir);
		collector.addRawTraceToPool(1, 0, s(1,2, 3,4,5,6, 3,4,5,6, 3,4,5,6,7,8), true, outputDir, "t1");
		collector.addRawTraceToPool(2, 0, s(1,2, 3,4,5,6, 3,4,5,6, 3,4,5,6,7,8), true, outputDir, "t2");
		
		int[][] traceArray = s(1,2,3, 4,5,6,5,6, 4,5,6,5,6,5,6, 7,8);
		collector.addRawTraceToPool(3, 0, traceArray, true, outputDir, "t3");
		
		System.out.println(collector.getGsTree());
		
		CompressedTraceBase<int[], ?> rawTrace = collector.getRawTraces(3).get(0);
		
		System.out.println(traceArray.length + ", " + arrayToString(traceArray));
		
//		System.out.println(rawTrace.getCompressedTrace());
		System.out.println(mapToString(rawTrace.getRepetitionMarkers()));
		System.out.println(mapToString(rawTrace.getChild().getRepetitionMarkers()));
		for (TraceIterator<int[]> iterator = rawTrace.iterator(); iterator.hasNext();) {
			int[] integer = iterator.next();
			System.out.print(Arrays.toString(integer) + ", ");
		}
		System.out.println();
		for (TraceIterator<int[]> iterator = rawTrace.iterator(); iterator.hasNext();) {
			if (iterator.isStartOfRepetition()) {
				System.out.print("s:");
			}
			if (iterator.isEndOfRepetition()) {
				System.out.print("e:");
			}
//			System.out.print(iterator.index + ":");
//			System.out.print(iterator.childIterator.index + ":");
//			System.out.print(iterator.childIterator.childIterator.index + ":");
			int[] integer = iterator.next();
			System.out.print(Arrays.toString(integer) + ", ");
		}
		System.out.println();
		
//		for (TraceIterator<Integer> iterator = rawTrace.iterator(1); iterator.hasNext();) {
//			if (iterator.isStartOfRepetition()) {
//				System.out.print("s:");
//			}
//			if (iterator.isEndOfRepetition()) {
//				System.out.print("e:");
//			}
////			System.out.print(iterator.index + ":");
////			System.out.print(iterator.childIterator.index + ":");
////			System.out.print(iterator.childIterator.childIterator.index + ":");
//			Integer integer = iterator.next();
//			System.out.print(integer + ", ");
//		}
//		System.out.println();
		
		ExecutionTrace executionTrace = collector.getExecutionTraces(3, true).get(0);
		collector.getIndexer().getSequences();
		
		SimpleIndexer simpleIndexer = new SimpleIndexer(collector.getIndexer());
		
		System.out.println(Arrays.toString(executionTrace.reconstructFullMappedTrace(simpleIndexer)));
		
		String result = "[ ";
		for (int j = 0; j < collector.getIndexer().getSequences().length; j++) {
			result += arrayToString(collector.getIndexer().getSequence(j)) + " ";
		}
		result += "]";
		System.out.println(result);
		
		System.out.println(executionTrace.getCompressedTrace());
		System.out.println(mapToString(executionTrace.getRepetitionMarkers()));
		System.out.println(mapToString(executionTrace.getChild().getRepetitionMarkers()));
		for (CloneableIterator<Integer> iterator = executionTrace.iterator(); iterator.hasNext();) {
			Integer integer = iterator.next();
			System.out.print(integer + ", ");
		}
		System.out.println();
		
		System.out.println(executionTrace.getCompressedTrace());
		
		System.out.println(collector.getGsTree());
		
		try {
			collector.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Test method for {@link se.de.hu_berlin.informatik.spectra.core.traces.RawArrayTraceCollector#addRawTraceToPool(java.lang.String, java.util.List)}.
	 */
	@Test
	public void testAddRawTraceToPool5() throws Exception {
		Path outputDir = Paths.get(getStdTestDir()).resolve("test5");
		RawArrayTraceCollector collector = new RawArrayTraceCollector(outputDir);
		collector.addRawTraceToPool(1, 0, s(1,2, 3,4,5,6, 3,4,5,6, 3,4,5,6,7,8), true, outputDir, "t1");
		collector.addRawTraceToPool(2, 0, s(1,2, 3,4,5,6, 3,4,5,6, 3,4,5,6,7,8), true, outputDir, "t2");
		
		int[][] traceArray = s(1,2,3, 4,5,6,7,7,5,6,7,7,7, 4,5,6,7,5,6,7,7,5,6,7,7,7,7, 9,8);
		collector.addRawTraceToPool(3, 0, traceArray, true, outputDir, "t3");
		
		System.out.println(collector.getGsTree());
		
		CompressedTraceBase<int[], ?> rawTrace = collector.getRawTraces(3).get(0);
		
		System.out.println(traceArray.length + ", " + arrayToString(traceArray));
		
//		System.out.println(rawTrace.getCompressedTrace());
		System.out.println(mapToString(rawTrace.getRepetitionMarkers()));
		System.out.println(mapToString(rawTrace.getChild().getRepetitionMarkers()));
		for (TraceIterator<int[]> iterator = rawTrace.iterator(); iterator.hasNext();) {
			int[] integer = iterator.next();
			System.out.print(Arrays.toString(integer) + ", ");
		}
		System.out.println();
		for (TraceIterator<int[]> iterator = rawTrace.iterator(); iterator.hasNext();) {
			if (iterator.isStartOfRepetition()) {
				System.out.print("s:");
			}
			if (iterator.isEndOfRepetition()) {
				System.out.print("e:");
			}
//			System.out.print(iterator.index + ":");
//			System.out.print(iterator.childIterator.index + ":");
//			System.out.print(iterator.childIterator.childIterator.index + ":");
			int[] integer = iterator.next();
			System.out.print(Arrays.toString(integer) + ", ");
		}
		System.out.println();
		
//		for (TraceIterator<Integer> iterator = rawTrace.iterator(1); iterator.hasNext();) {
//			if (iterator.isStartOfRepetition()) {
//				System.out.print("s:");
//			}
//			if (iterator.isEndOfRepetition()) {
//				System.out.print("e:");
//			}
////			System.out.print(iterator.index + ":");
////			System.out.print(iterator.childIterator.index + ":");
////			System.out.print(iterator.childIterator.childIterator.index + ":");
//			Integer integer = iterator.next();
//			System.out.print(integer + ", ");
//		}
//		System.out.println();
		
		ExecutionTrace executionTrace = collector.getExecutionTraces(3, true).get(0);
		collector.getIndexer().getSequences();
		
		SimpleIndexer simpleIndexer = new SimpleIndexer(collector.getIndexer());
		
		System.out.println(Arrays.toString(executionTrace.reconstructFullMappedTrace(simpleIndexer)));
		
		String result = "[ ";
		for (int j = 0; j < collector.getIndexer().getSequences().length; j++) {
			result += arrayToString(collector.getIndexer().getSequence(j)) + " ";
		}
		result += "]";
		System.out.println(result);
		
		System.out.println(executionTrace.getCompressedTrace());
		System.out.println(mapToString(executionTrace.getRepetitionMarkers()));
		System.out.println(mapToString(executionTrace.getChild().getRepetitionMarkers()));
		for (CloneableIterator<Integer> iterator = executionTrace.iterator(); iterator.hasNext();) {
			Integer integer = iterator.next();
			System.out.print(integer + ", ");
		}
		System.out.println();
		
		System.out.println(executionTrace.getCompressedTrace());
		
		System.out.println(collector.getGsTree());
		
		try {
			collector.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String mapToString(BufferedMap<int[]> map) {
		if (map == null) {
			return "null";
		}
		StringBuilder builder = new StringBuilder();
		builder.append("[ ");
		Iterator<Entry<Integer, int[]>> entrySetIterator = map.entrySetIterator();
		while (entrySetIterator.hasNext()) {
			Entry<Integer, int[]> entry = entrySetIterator.next();
			builder.append("[" + entry.getKey() + ": " + entry.getValue()[0] + ", " + entry.getValue()[1] + "] ");
		}
		builder.append("]");
		return builder.toString();
	}

	private String arrayToString(int[][] map) {
		if (map == null) {
			return "null";
		}
		StringBuilder builder = new StringBuilder();
		builder.append("[ ");
		for (int[] entry : map) {
			builder.append(Arrays.toString(entry) + ", ");
		}
		builder.setLength(builder.length() > 2 ? builder.length()-2 : builder.length()-1);
		builder.append(" ]");
		return builder.toString();
	}
	
}