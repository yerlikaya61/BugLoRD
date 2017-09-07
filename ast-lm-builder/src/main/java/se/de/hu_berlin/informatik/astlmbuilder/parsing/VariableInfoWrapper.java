package se.de.hu_berlin.informatik.astlmbuilder.parsing;

import java.util.EnumSet;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;

/**
 * A wrapper object for different types of variables found in the history of a
 * node.
 * 
 */
public class VariableInfoWrapper {

	public static final String UNKNOWN_STR_VALUE = VariableScope.UNKNOWN.toString();

	public enum VariableScope {
		GLOBAL, PARAMETER, LOCAL, UNKNOWN
	}

	private String type = UNKNOWN_STR_VALUE;
	private String name = UNKNOWN_STR_VALUE;
	private String lastKnownValue = UNKNOWN_STR_VALUE;
	private boolean primitive = false;
	private VariableScope scope = VariableScope.UNKNOWN;
	private EnumSet<Modifier> modifiers;

	/**
	 * Constructor for a variable info wrapper object
	 * 
	 * @param aType
	 * The type of the variable
	 * @param aName
	 * The name of the variable declaration
	 * @param aLastKnownValue
	 * The last known value if it could be extracted easily
	 * @param aIsPrimitive
	 * Flag if the variable is a primitive
	 * @param aScope
	 * Global, Argument, Local or unknown
	 * @param aModifiers
	 * The modifiers of the variable
	 * @param aOriginalNode
	 * The complete node in case we want to get some additional data
	 */
	public VariableInfoWrapper(String aType, String aName, String aLastKnownValue, boolean aIsPrimitive,
			VariableScope aScope, EnumSet<Modifier> aModifiers, Node aOriginalNode) {
		type = aType == null ? UNKNOWN_STR_VALUE : aType.trim().toLowerCase();
		name = aName == null ? UNKNOWN_STR_VALUE : aName.trim().toLowerCase();
		lastKnownValue = aLastKnownValue == null ? UNKNOWN_STR_VALUE : aLastKnownValue;
		primitive = aIsPrimitive;
		scope = aScope;
		modifiers = aModifiers;
	}

	/**
	 * Returns the type of the variable
	 * @return The type of the variable. Integer for example.
	 */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The last known value if a statement could be found.
	 */
	public String getLastKnownValue() {
		return lastKnownValue;
	}

	public void setLastKnownValue(String lastKnownValue) {
		this.lastKnownValue = lastKnownValue;
	}

	/**
	 * @return True if it is a primitive variable. False otherwise.
	 */
	public boolean isPrimitive() {
		return primitive;
	}

	public void setPrimitive(boolean primitive) {
		this.primitive = primitive;
	}

	public VariableScope getScope() {
		return scope;
	}

	public void setScope(VariableScope scope) {
		this.scope = scope;
	}

	public EnumSet<Modifier> getModifiers() {
		return modifiers;
	}

	public void setModifiers(EnumSet<Modifier> modifiers) {
		this.modifiers = modifiers;
	}

	public String toString() {
		return "VIW(t=" + type + ", n=" + name + ", v=" + lastKnownValue + ", s=" + scope + ", p=" + primitive
				+ ", mods=" + modifiers.toString() + ")";
	}
}
