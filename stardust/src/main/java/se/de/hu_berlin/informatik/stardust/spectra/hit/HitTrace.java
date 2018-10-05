/*
 * This file is part of the "STARDUST" project. (c) Fabian Keller
 * <hello@fabian-keller.de> For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package se.de.hu_berlin.informatik.stardust.spectra.hit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import se.de.hu_berlin.informatik.stardust.spectra.INode;
import se.de.hu_berlin.informatik.stardust.spectra.ISpectra;
import se.de.hu_berlin.informatik.stardust.spectra.ITrace;

/**
 * This class represents a single execution trace and its success state.
 *
 * @author Fabian Keller 'dev@fabian-keller.de'
 *
 * @param <T>
 * type used to identify nodes in the system.
 */
public class HitTrace<T> implements ITrace<T> {

	/** Holds the success state of this trace */
	private final boolean successful;

	/** Holds the identifier (test case name) of this trace */
	private final String identifier;

	/** Holds the spectra this trace belongs to */
	protected final ISpectra<T, ?> spectra;

	/**
	 * Stores the involvement of all nodes for this trace. Use
	 * {@link HitSpectra#getNodes()} to get all nodes.
	 */
	private final Set<Integer> involvement = new HashSet<>();
	
	/**
	 * Holds all execution traces for all threads separately. (Lists of node IDs)
	 */
	private final Collection<List<Integer>> executionTraces = new ArrayList<>(1);

	/**
	 * Create a trace for a spectra.
	 * @param spectra
	 * the spectra that the trace belongs to
	 * @param identifier
	 * the identifier of the trace (usually the test case name)
	 * @param successful
	 * true if the trace originates from a successful execution, false otherwise
	 */
	protected HitTrace(final ISpectra<T, ?> spectra, final String identifier, final boolean successful) {
		this.successful = successful;
		this.spectra = Objects.requireNonNull(spectra);
		this.identifier = Objects.requireNonNull(identifier);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSuccessful() {
		return this.successful;
	}

	/** {@inheritDoc} */
	@Override
	public void setInvolvement(final T identifier, final boolean involved) {
		setInvolvement(spectra.getOrCreateNode(identifier), involved);
	}

	/** {@inheritDoc} */
	@Override
	public void setInvolvement(final INode<T> node, final boolean involved) {
		if (node == null) {
			return;
		}
		if (involved) {
			if (involvement.add(node.getIndex())) {
				node.invalidateCachedValues();
			}
		} else if (involvement.contains(node.getIndex())) {
			involvement.remove(node.getIndex());
			node.invalidateCachedValues();
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public void setInvolvement(final int index, final boolean involved) {
		setInvolvement(spectra.getNode(index), involved);
	}

	/** {@inheritDoc} */
	@Override
	public void setInvolvementForIdentifiers(final Map<T, Boolean> nodeInvolvement) {
		for (final Map.Entry<T, Boolean> cur : nodeInvolvement.entrySet()) {
			setInvolvement(cur.getKey(), cur.getValue());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void setInvolvementForNodes(final Map<INode<T>, Boolean> nodeInvolvement) {
		for (final Map.Entry<INode<T>, Boolean> cur : nodeInvolvement.entrySet()) {
			setInvolvement(cur.getKey(), cur.getValue());
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean isInvolved(final INode<T> node) {
		if (node != null) {
			return involvement.contains(node.getIndex());
		} else {
			return false;
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean isInvolved(final T identifier) {
		return isInvolved(spectra.getNode(identifier));
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean isInvolved(final int index) {
		return involvement.contains(index);
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public int involvedNodesCount() {
		return involvement.size();
	}

	@Override
	public Collection<Integer> getInvolvedNodes() {
		return involvement;
	}

	@Override
	public int hashCode() {
		// equality of traces is bound to identifiers
		return getIdentifier().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HitTrace) {
			HitTrace<?> oTrace = (HitTrace<?>) obj;
			if (!this.getIdentifier().equals(oTrace.getIdentifier())) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public Collection<List<Integer>> getExecutionTraces() {
		return executionTraces;
	}

	@Override
	public void addExecutionTrace(List<Integer> executionTrace) {
		executionTraces.add(executionTrace);
	}

	@Override
	public void addExecutionTraceWithIdentifiers(List<T> executionTrace) {
		List<Integer> trace = new ArrayList<Integer>(executionTrace.size());
		for (T identifier : executionTrace) {
			INode<T> node = spectra.getNode(identifier);
			if (node != null) {
				trace.add(node.getIndex());
			} else {
				trace.add(-1);
			}
		}
	}

}
