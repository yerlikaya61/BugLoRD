/**
 * 
 */
package se.de.hu_berlin.informatik.javatokenizer.tokenizelines;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.de.hu_berlin.informatik.spectra.core.SourceCodeBlock;
import se.de.hu_berlin.informatik.utils.files.processors.FileLineProcessor.StringProcessor;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;

/**
 * Takes a String that represents a {@link SourceCodeBlock}
 * and adds a matching sentence to the result list. The matching sentence
 * is obtained from the given sentence map that maps Strings in the
 * input String format to corresponding sentences.
 * 
 * @author Simon Heiden
 */
public class LineMatcher implements StringProcessor<List<String>> {

	
	private Map<String, String> sentenceMap;
	
	private List<String> lines;
	
	/**
	 * Creates a new {@link LineMatcher} object with the given parameters.
	 * @param sentenceMap
	 * maps trace file lines to sentences
	 */
	public LineMatcher(Map<String, String> sentenceMap) {
		this.sentenceMap = sentenceMap;
		lines = new ArrayList<>();
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.stringprocessor.IStringProcessor#process(java.lang.String)
	 */
	@Override
	public boolean process(String line) {
		try {
			SourceCodeBlock block = SourceCodeBlock.getNewBlockFromString(line);

			String path = block.getFilePath();
			Integer lineNo = block.getStartLineNumber();

			String sentence;
			if ((sentence = sentenceMap.get(path + ':' + lineNo)) != null) {
				lines.add(Misc.replaceNewLinesInString(sentence, "_"));
			} else {
				lines.add("");
			}
		} catch (IllegalArgumentException e) {
			Log.err(this, e);
			lines.add("");
			return false;
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.IStringProcessor#getResult()
	 */
	@Override
	public List<String> getFileResult() {
		List<String> temp = lines;
		lines = new ArrayList<>();
		return temp;
	}

}
