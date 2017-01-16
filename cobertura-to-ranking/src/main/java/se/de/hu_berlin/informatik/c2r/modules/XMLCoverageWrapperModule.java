/**
 * 
 */
package se.de.hu_berlin.informatik.c2r.modules;

import java.io.File;

import se.de.hu_berlin.informatik.stardust.provider.cobertura.CoverageWrapper;
import se.de.hu_berlin.informatik.utils.fileoperations.FileUtils;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;

/**
 * 
 * 
 * @author Simon Heiden
 * 
 */
public class XMLCoverageWrapperModule extends AbstractModule<File,CoverageWrapper> {
	
	public XMLCoverageWrapperModule() {
		super(true);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public CoverageWrapper processItem(final File xmlCoverageFile) {
		if (xmlCoverageFile.getParent().contains("fail")) {
			return new CoverageWrapper(xmlCoverageFile, 
					FileUtils.getFileNameWithoutExtension(xmlCoverageFile.toString()), false);
		} else {
			return new CoverageWrapper(xmlCoverageFile, 
					FileUtils.getFileNameWithoutExtension(xmlCoverageFile.toString()), true);
		}
	}

}
