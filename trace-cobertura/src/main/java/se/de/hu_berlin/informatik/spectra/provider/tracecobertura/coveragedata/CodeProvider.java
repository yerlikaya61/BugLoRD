package se.de.hu_berlin.informatik.spectra.provider.tracecobertura.coveragedata;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * Universal API for all methods that are responsible for generating any JASM code that have
 * to be injected into real classes.
 * 
 * The general idea is that injected code is responsible for incrementing counters. The realization of counters
 * is implementation dependent.
 *
 * @author piotr.tabor@gmail.com
 */
public interface CodeProvider {
	/**
	 * Name of a field that have to be injected into instrumented class that is responsible for storing counters
	 */
	public static final String COBERTURA_COUNTERS_FIELD_NAME = "__tracecobertura_counters";

	/**
	 * Name of a method that will to be injected into instrumented class that is responsible for storing
	 * class-map (information on mapping of counter identifiers into lines, jumps and switch-touches).
	 */
	public static final String COBERTURA_CLASSMAP_METHOD_NAME = "__tracecobertura_classmap";

	/**
	 * Name of method that will initialize internal counters variable.
	 */
	public static final String COBERTURA_INIT_METHOD_NAME = "__tracecobertura_init";

	/**
	 * Name of a method that have to be injected into instrumented class that is responsible for reading
	 * value of given counter.
	 * 
	 * Signature of this method is: int[] __cobertura_counter(int counterId);
	 */
	public static final String COBERTURA_GET_AND_RESET_COUNTERS_METHOD_NAME = "__tracecobertura_get_and_reset_counters";

	/**
	 * Generates fields injected into  instrumented class  by cobertura.
	 *
	 * @param cv - ClassVisitor that is listener of code-generation events
	 */
	public abstract void generateCountersField(ClassVisitor cv);

	/**
	 * Injects code that increments counter given by parameter.
	 *
	 * @param nextMethodVisitor - {@link MethodVisitor} that is listener of code-generation events
	 * @param counterId         -  counterId of counter that have to be incremented
	 * @param className         - internal name (asm) of class being instrumented
	 * @param classId			- unique id of the class
	 */
	public abstract void generateCodeThatIncrementsCoberturaCounter(
			MethodVisitor nextMethodVisitor, int counterId, String className, int classId);
	
	/**
	 * Injects code that increments counter given by parameter.
	 *
	 * @param nextMethodVisitor - {@link MethodVisitor} that is listener of code-generation events
	 * @param counterId         -  counterId of counter that have to be incremented
	 * @param className         - internal name (asm) of class being instrumented
	 * @param classId			- unique id of the class
	 */
	public abstract void generateCodeThatIncrementsCoberturaCounterAfterJump(
			MethodVisitor nextMethodVisitor, int counterId, String className, int classId);
	
	/**
	 * Injects code that increments counter given by parameter.
	 *
	 * @param nextMethodVisitor - {@link MethodVisitor} that is listener of code-generation events
	 * @param counterId         -  counterId of counter that have to be incremented
	 * @param className         - internal name (asm) of class being instrumented
	 * @param classId			- unique id of the class
	 */
	public abstract void generateCodeThatIncrementsCoberturaCounterAfterSwitchLabel(
			MethodVisitor nextMethodVisitor, int counterId, String className, int classId);

	/**
	 * Injects code that increments counter given by internal variable.
	 * The id of the variable is identified by lastJumpIdVariableIndex. 
	 * The variable is in most cases set (by {@link #generateCodeThatSetsJumpCounterIdVariable(MethodVisitor, int, int)}
	 * to some counterId and in the target label, the counter identified by the variable is incremented.
	 *
	 * @param nextMethodVisitor       - {@link MethodVisitor} that is listener of code-generation events
	 * @param lastJumpIdVariableIndex - id of the variable used to store counterId that have to be incremented
	 * @param className               - internal name (asm) of class being instrumented
	 * @param classId				  - unique id of the class
	 */
	public abstract void generateCodeThatIncrementsCoberturaCounterFromInternalVariable(
			MethodVisitor nextMethodVisitor, int lastJumpIdVariableIndex,
			String className, int classId);

	/**
	 * Injects code that sets internal variable (identified by lastJumpIdVariableIndex) to given value.
	 *
	 * @param nextMethodVisitor       - {@link MethodVisitor} that is listener of code-generation events
	 * @param new_value               - value to set the variable to
	 * @param lastJumpIdVariableIndex - index of variable that have to be set
	 */
	public abstract void generateCodeThatSetsJumpCounterIdVariable(
			MethodVisitor nextMethodVisitor, int new_value,
			int lastJumpIdVariableIndex);

	/**
	 * Injects code that sets internal variable (identified by lastJumpIdVariableIndex) to zero.
	 *
	 * @param nextMethodVisitor       - {@link MethodVisitor} that is listener of code-generation events
	 * @param lastJumpIdVariableIndex - index of variable that have to be set
	 */
	public abstract void generateCodeThatZeroJumpCounterIdVariable(
			MethodVisitor nextMethodVisitor, int lastJumpIdVariableIndex);

	/*
	 * Injects code that behaves the same as such a code snippet:
	 * <pre>
	 * if (value('lastJumpIdVariableIndex')==neededJumpCounterIdVariableValue){
	 * 	 cobertura_counters.increment(counterIdToIncrement);
	 * }
	 * </pre>
	 * 
	 * This snippet is used in switch case of switch statement. We have a label and we want to ensure that
	 * we are executing the label in effect of switch statement-jump, and not other JUMP or fall-throught.
	 */
	public abstract void generateCodeThatIncrementsCoberturaCounterIfVariableEqualsAndCleanVariable(
			MethodVisitor nextMethodVisitor,
			Integer neededJumpCounterIdVariableValue,
			Integer counterIdToIncrement, int lastJumpIdVariableIndex,
			String className, int classId);

	/**
	 * The version of cobertura prior to 1.10 used *.ser file to store information of lines, jumps, switches and other
	 * constructions used in the class. It was difficult to user to transfer the files after instrumentation into
	 * 'production' directory. To avoid that we are now creating the class-map as a special injected method that is responsible
	 * for keeping such a informations.
	 *
	 * @param cv       - listener used to inject the code
	 * @param classMap - structure that is keeping all collected information about the class. The information from the structure will be stored as
	 *                 method body.
	 */
	public void generateCoberturaClassMapMethod(ClassVisitor cv,
			ClassMap classMap);

	/**
	 * Generate method {@value #COBERTURA_GET_AND_RESET_COUNTERS_METHOD_NAME} that is accessor to counters.
	 * Signature of this method is: static int __cobertura_counter(int counterId);
	 *
	 * @param cv - listener used to inject the code
	 * @param className - name of the class
	 */
	public abstract void generateCoberturaGetAndResetCountersMethod(
			ClassVisitor cv, String className);

	public void generateCoberturaInitMethod(ClassVisitor cv, String className,
			int classId, int countersCnt);

	public abstract void generateCallCoberturaInitMethod(MethodVisitor mv,
			String className);
}