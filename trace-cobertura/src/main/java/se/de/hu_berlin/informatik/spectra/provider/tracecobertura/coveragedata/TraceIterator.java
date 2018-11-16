package se.de.hu_berlin.informatik.spectra.provider.tracecobertura.coveragedata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TraceIterator<T> implements Iterator<T> {
	
	private CompressedTraceBase<T,?> trace;
	private TraceIterator<T> childIterator;

	public TraceIterator(CompressedTraceBase<T,?> trace) {
		this.trace = trace;
		childIterator = (trace.getChild() == null ? null : new TraceIterator<T>(trace.getChild()));
	}

	private int index = 0;
	private int repetitionIndex = -1;
	private int repetitionCounter = 0;

	private List<int[]> resetStateList;

	private void setResetPoint() {
		resetStateList = new ArrayList<>();
		getAndStoreState(resetStateList);
	}

	private void getAndStoreState(List<int[]> resetIndexList2) {
		if (childIterator != null) {
			resetIndexList2.add(new int[] {
					childIterator.index, 
					childIterator.repetitionIndex, 
					childIterator.repetitionCounter });
			childIterator.getAndStoreState(resetIndexList2);
		}
	}

	private void resetState() {
		this.index = trace.getRepetitionMarkers()[repetitionIndex];
		if (childIterator != null) {
			childIterator.setState(resetStateList, 0);
		}
	}

	private void setState(List<int[]> indexList, int index) {
		this.index = indexList.get(index)[0];
		this.repetitionIndex = indexList.get(index)[1];
		this.repetitionCounter = indexList.get(index)[2];
		if (childIterator != null) {
			childIterator.setState(indexList, ++index);
		}
	}

	@Override
	public boolean hasNext() {
		if (childIterator == null) {
			return index < trace.getCompressedTrace().length;
		} else {
			if (repetitionIndex >= 0 && 
					index >= trace.getRepetitionMarkers()[repetitionIndex] 
							+ trace.getRepetitionMarkers()[repetitionIndex+1]) {
				// at the end of the repeated sequence
				if (repetitionCounter <= 1) {
					// no further iteration
					return childIterator.hasNext();
				} else {
					return true;
				}
			} else {
				// inside of repeated sequence
				return childIterator.hasNext();
			}
		}
	}

	@Override
	public T next() {
		if (childIterator == null) {
			return trace.getCompressedTrace()[index++];
		} else {
			// prioritize repetitions in parent 
			// (parent repetitions should be contained in child repetitions)
			if (repetitionIndex >= 0) {
				// inside of a repeated sequence
				if (index < trace.getRepetitionMarkers()[repetitionIndex] 
						+ trace.getRepetitionMarkers()[repetitionIndex+1]) {
					// still inside of the repeated sequence
					++index;
					return childIterator.next();
				} else {
					// at the end of the repeated sequence
					if (repetitionCounter > 1) {
						// still an iteration to go
						--repetitionCounter;
						// reset to previous reset point
						resetState();
					} else {
						// no further iteration
						repetitionIndex = -1;
					}
					// continue with the next item (either at the beginning of the sequence or after the end)
					return next();
				}
			} else {
				// check if we are in a repeated sequence
				for (int i = 0; i < trace.getRepetitionMarkers().length; i += 3) {
					// [start_pos, length, repeat_count]
					if (index >= trace.getRepetitionMarkers()[i] && 
							index < trace.getRepetitionMarkers()[i] + trace.getRepetitionMarkers()[i+1]) {
						// we are in a new repeated sequence!
						repetitionIndex = i;
						repetitionCounter = trace.getRepetitionMarkers()[i+2];
						// set the reset point to this exact point
						setResetPoint();
						return next();
					}
					if (trace.getRepetitionMarkers()[i] > index) {
						// no need to look further than that!
						break;
					}
				}

				// not in a repeated sequence!
				++index;
				return childIterator.next();
			}
		}
	}
}