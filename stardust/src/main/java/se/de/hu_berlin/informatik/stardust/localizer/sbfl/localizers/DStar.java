/*
 * This file is part of the "STARDUST" project.
 *
 * (c) Fabian Keller <hello@fabian-keller.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package se.de.hu_berlin.informatik.stardust.localizer.sbfl.localizers;

import se.de.hu_berlin.informatik.stardust.localizer.sbfl.AbstractSpectrumBasedFaultLocalizer;
import se.de.hu_berlin.informatik.stardust.spectra.INode;

/**
 * DStar, D* (Wong) fault localizer $\frac{\EF^x}{\EP+\NF}$
 * 
 * @param <T>
 *            type used to identify nodes in the system
 */
public class DStar<T> extends AbstractSpectrumBasedFaultLocalizer<T> {

	private double star;
	
    /**
     * Create fault localizer
     */
    public DStar() {
        this(2.0);
    }

    public DStar(double star) {
		this.star = star;
	}

	@Override
    public double suspiciousness(final INode<T> node, ComputationStrategies strategy) {
        return Math.pow((double)node.getEF(strategy), star) / (double)(node.getEP(strategy) + node.getNF(strategy));
    }

    @Override
    public String getName() {
        return "dstar";
    }

}
