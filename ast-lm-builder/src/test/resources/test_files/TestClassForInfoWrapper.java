package se.de.hu_berlin.informatik.astlmbuilder.parsing.parser;

/**
 * Just a class to load and create info wrapper objects for testing
 */
public class TestClassForInfoWrapper {

	private class InsideTestClass {
		public int oneInt = 1;
		public int secondInt = 2;
		public int thirdInt = 3;
		
		public InsideTestClass another = null;
		
		public int getOneInt() {
			return oneInt;
		}
		
		public void setOneInt( int aOneInt ) {
			oneInt = aOneInt;
		}
		
		public String toString() {
			return "1: " + oneInt + ", 2: " + secondInt + ", 3: " + thridInt;
		}
	}
	
	// lets see if they are accessible in some way
	public static final String A_CONSTANT = "Hi";
	public final String A_STRING = "World";
	private int index = 4711;
	
	/**
	 * This is a comment for the main method
	 * @param args
	 */
	public static void main(String[] args) {
		TestClassForInfoWrapper tc = new TestClassForInfoWrapper();
		tc.doAction( args );
	}
	
	public void doAction( String[] args ) {
		doActionInPrivat( args );
	}
	
	private void doActionInPrivat( String[] args ) {
		System.out.println( "This method was called with " + args.length + " arguments" );
		
		// a condition with a nice block
		if ( calcSumFromTo( 10, 20 ) < 150 ) {
			String output = "The sum is lower";
			double something = 15.0;
			boolean b = false;
			
			getSomeParameters( output, something, b);
		}
	}
	
	public void getSomeParameters( String aStr, double aDbl, boolean aB ) {
		if ( aB ) {
			System.out.println( aStr );
		} else {
			System.out.println( aDbl );
			
			int one, two, three;
			one = 0;
			two = 1;
			three = 2;
			
			System.out.println( calcSumFromTo(calsSumFromTo( one, two), three ) );
		}
		
		// those are used to test the qualifier attribut of the name objects
		InsideTestClass one = new InsideTestClass();
		InsideTestClass two = new InsideTestClass();
		InsideTestClass three = new InsideTestClass();
		
		one.setOneInt( 100 );
		two.oneInt = 200;
		three.oneInt = three.getOneInt();
		
		one.another = new InsideTestClass();
		
		System.out.println( one.toString() + " / " + two.toString() + " / " + three.toString());
	}
	
	private int calcSumFromTo( int aStartIdx, int aEndIdx ) {
		int sum = 0; // 0
		double uselessDoubleWithoutInit; // 1
		boolean uselessBool = false; // 2
		char uselessCharWithSimpleInit = 'x'; // 3
		char uselessCharWithComplexInit = "someChars".charAt( 4 ); // 4
		long uselessLong = 4711l; // 5
		String uselessString = "siebenundvierzig"; // 6
		
		String[] firstArrayInHere = new String[16]; // 7
		
		Integer bigSum = new Integer( 47 ); // 8
		Double bigDouble = null; // 9
		Boolean bigBool = new Boolean( true ); // 10
		Character bigChar = new Character( 'x' ); // 11
		Long bigLong = new Long( 1337l ); // 12
		
		int index = 10; // 13 same name/type as the global variable
		
		/**
		 * I never heard of Gauss and need a loop here
		 */
		for( int i = aStartIdx; i <= aEndIdx; ++i ) { // 14
			sum += i;
		}
		
		int belowInt = 4; // 15 this should not appear in the list for the for statement node
		String belowStr = "neverUsed: " + belowInt; // 16
		System.out.println( belowStr ); // 17
		
		for( int i = 0; i < 100; ++i ) { // 18
			String outerLoop = "outerForLoop"; // 18.0
			while( i < 100 ) { // 18.1
				String innerWhileLoop = "innerWhileLoop"; // 18.1.0
				for( byte b : innerWhileLoop.getBytes() ) { // 18.1.1
					int mostInnerLoop = b; // 18.1.1.0
					System.out.println( mostInnerLoop ); // 18.1.1.1
				}
				String innerWhileLoopEnd = "innerWhileLoopEnd"; // 18.1.2
				System.out.println( innerWhileLoop + innerWhileLoopEnd); // 18.1.3
			}

			String outerLoopEnd = "outerForLoopEnd"; // 18.2
			System.out.println( outerLoop + outerLoopEnd ); // 18.3
		}
		
		long time = System.currentTimeMillis(); // 19
		System.out.println( time ); // 20
		
		return sum + lastGloVar; // 21
	}

	// will this be put to the top of the children of the class declaration in the ast?
	private int lastGloVar = 128;
	
}
