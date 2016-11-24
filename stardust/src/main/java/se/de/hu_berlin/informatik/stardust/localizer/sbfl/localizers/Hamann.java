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
 * Hamann fault localizer $\frac{\EF+\NP-\NF-\EP}{\EF+\NF+\EP+\NP}$
 * 
 * @param <T>
 *            type used to identify nodes in the system
 */
public class Hamann<T> extends AbstractSpectrumBasedFaultLocalizer<T> {

    /**
     * Create fault localizer
     */
    public Hamann() {
        super();
    }

    @Override
    public double suspiciousness(final INode<T> node) {
    	double numerator = node.getEF() + node.getNP() - node.getNF() - node.getEP();
    	if (numerator == 0) {
    		return 0;
    	}
        return numerator / (double)(node.getEF() + node.getNF() + node.getEP() + node.getNP());
    }

    @Override
    public String getName() {
        return "Hamann";
    }

}