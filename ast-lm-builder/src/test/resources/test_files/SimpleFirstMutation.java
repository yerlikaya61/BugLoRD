public class SimpleFirstMutation {

	public static void main(String[] args) {
		System.out.println( "This file should be mutated.");
	}
	
	private void simpleCalc() {
		int one = 1;
		int two = 2;
		
		int max = one;
		
		if( one > two ) { // something to fix
			max = two;
		}
	}
}
