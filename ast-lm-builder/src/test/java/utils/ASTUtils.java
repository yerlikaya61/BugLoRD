package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;

/**
 * Some utils for operating on the asts of files
 */
public class ASTUtils {
	
	/**
	 * Print configuration
	 */
	PrettyPrinterConfiguration ppc = new PrettyPrinterConfiguration().setPrintComments(true).setPrintJavaDoc(true);
	
	/**
	 * Writes a compilation unit to disk
	 * @param aCU
	 * The compilation unit
	 * @param aFileName
	 * The name of the new file
	 */
	public void writeCUToFile( CompilationUnit aCU, String aFileName ) {
		File f = new File( aFileName );
		FileWriter fw = null;
		BufferedWriter writer = null;
		
		try {
			fw = new FileWriter( f );
			writer = new BufferedWriter( fw );
			
			writer.write( aCU.toString(ppc));
			
			Log.out( this, "Wrote compilation unit to " + f.getAbsolutePath() );
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if( writer != null ) {
				try {
					writer.close();
				} catch (IOException e) {
					// nothing to do
				}
			}
		}
	}
	
	/**
	 * The variant with a name currently only works for method declarations
	 * because of the getNameAsString method
	 * @param aParentNode
	 * The node that has children
	 * @param aTypeOfNode
	 * The type of node that should be returned
	 * @param aName
	 * The name of the node that should be returned
	 * @return The node with the given type and name or null
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Node> T getNodeFromChildren(Node aParentNode, Class<T> aTypeOfNode, String aName) {

		if (aParentNode == null || aParentNode.getChildNodes() == null) {
			return null;
		}

		for (Node n : aParentNode.getChildNodes()) {
			if (aTypeOfNode.isInstance(n)) {
				// only works for method decs currently
				// could be adjusted if there is a pattern to node with names
				if (n instanceof MethodDeclaration) {
					if (((MethodDeclaration) n).getNameAsString().equalsIgnoreCase(aName)) {
						return (T) n;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Searches for a node of the given type in the list of children and returns
	 * it
	 * @param aParentNode
	 * The node with children
	 * @param aTypeOfNode
	 * The type of node that should be returned
	 * @return The first node with the given type
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Node> T getNodeFromChildren(Node aParentNode, Class<T> aTypeOfNode) {

		if (aParentNode == null || aParentNode.getChildNodes() == null) {
			return null;
		}

		for (Node n : aParentNode.getChildNodes()) {
			if (aTypeOfNode.isInstance(n)) {
				return (T) n;
			}
		}

		return null;
	}
}
