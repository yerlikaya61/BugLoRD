package mutations;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;

import junit.framework.TestCase;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import utils.ASTUtils;

public class FirstMutationsTest extends TestCase {
	
	private ASTUtils utils = new ASTUtils();
	private String testDir= "test_files/"; // not final
	
	// @BeforeClass would be nice too but needs to be static
	// @Before does not trigger :(
	public void init(){
		ClassLoader classLoader = getClass().getClassLoader();
		File testDirectoryRoot = new File(classLoader.getResource( "test_files/").getFile());
		testDir = testDirectoryRoot.getAbsolutePath() + "\\";
		Log.out(this, "Set test directory to " + testDir);
	}
	
	@Test
	public void testSimpleMutation() {
		init();
		
		// read the ast
		CompilationUnit cu1 = createCUFromTestFile( "SimpleFirstMutation.java" );
		
		// search for the place to mutate
		IfStmt stmt = getIfStmt( cu1 );
		
		// lets do it!
		BinaryExpr cond = (BinaryExpr) stmt.getCondition();
		
		// change it
		Operator op = cond.getOperator(); // is >
		Log.out(this, "Found operator " + op.toString());
		cond.setOperator( Operator.LESS ); // should be <
		Log.out( this, "Changed it to " + Operator.LESS );
		
		// write it back
		utils.writeCUToFile( cu1, testDir + "SimpleFirstMutation_modified.java" );
		
	}

	private CompilationUnit createCUFromTestFile( String aTestFile ) {
		String fullName = testDir + aTestFile;
		Log.out(this, "Attempting to read file " + fullName);

		File testFile = new File( fullName );

		if (!testFile.exists()) {
			Log.err(this, "Could not find the test file at ", testFile.getAbsolutePath());
			return null;
		}

		CompilationUnit cu = null;

		try {
			cu = JavaParser.parse(testFile);
		} catch (FileNotFoundException e) {
			Log.err(this, e);
		}

		return cu;
	}
	
	/**
	 * We want to mutate the first interesting statement in the simple sample class
	 * @param aCU
	 * @return
	 */
	private IfStmt getIfStmt(CompilationUnit aCU) {

		// get the class declaration
		ClassOrInterfaceDeclaration cdec = ASTUtils.getNodeFromChildren(aCU, ClassOrInterfaceDeclaration.class);
		if (cdec == null) {
			return null;
		}

		MethodDeclaration method = ASTUtils.getNodeFromChildren(cdec, MethodDeclaration.class, "simpleCalc");
		if (method == null) {
			return null;
		}

		BlockStmt blockstmt = ASTUtils.getNodeFromChildren(method, BlockStmt.class);
		if (blockstmt == null) {
			return null;
		}
		
		// get the for stmt
		IfStmt forStmt = ASTUtils.getNodeFromChildren(blockstmt, IfStmt.class);
		// not need to check this actually
		if (forStmt == null) {
			return null;
		}
		
		return forStmt;
	}

	

	
}
