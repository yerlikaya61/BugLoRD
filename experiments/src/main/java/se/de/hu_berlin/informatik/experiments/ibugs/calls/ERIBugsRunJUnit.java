/**
 * 
 */
package se.de.hu_berlin.informatik.experiments.ibugs.calls;

import se.de.hu_berlin.informatik.benchmark.api.BuggyFixedEntity;
import se.de.hu_berlin.informatik.benchmark.api.ibugs.IBugsEntity;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.tm.AbstractProcessorUser;

/**
 * 
 * @author Roy Lieck
 *
 */
public class ERIBugsRunJUnit extends AbstractProcessorUser<BuggyFixedEntity,BuggyFixedEntity> {

	/**
	 * Initializes a {@link ERIBugsRunJUnit} object.
	 */
	public ERIBugsRunJUnit() {
		super();
	}

	@Override
	public BuggyFixedEntity processItem(BuggyFixedEntity buggyEntity) {
		Log.out(this, "Processing %s.", buggyEntity);
		
		/* #====================================================================================
		 * # checkout buggy version and fixed version
		 * #==================================================================================== */
		IBugsEntity ibeBuggy = (IBugsEntity) buggyEntity.getBuggyVersion();
		ibeBuggy.runJUnitTests();
		
		IBugsEntity ibeFixed = (IBugsEntity) buggyEntity.getFixedVersion();
		ibeFixed.runJUnitTests();

		return buggyEntity;
	}

}
