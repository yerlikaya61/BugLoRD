package se.de.hu_berlin.informatik.astlmbuilder.parsing;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores data about variables for a specific node
 */
public class SymbolTable {

	List<VariableInfoWrapper> symbolTable = new ArrayList<VariableInfoWrapper>();
	List<VariableInfoWrapper> gloVars = new ArrayList<VariableInfoWrapper>();
	List<VariableInfoWrapper> parVars = new ArrayList<VariableInfoWrapper>();
	List<VariableInfoWrapper> locVars = new ArrayList<VariableInfoWrapper>();

	public SymbolTable(List<VariableInfoWrapper> aSimpleSymbolTable) {
		symbolTable = aSimpleSymbolTable;

		for (VariableInfoWrapper viw : aSimpleSymbolTable) {
			switch (viw.getScope()) {
			case GLOBAL:
				gloVars.add(viw); break;
			case PARAMETER:
				parVars.add(viw); break;
			case LOCAL:
				locVars.add(viw); break;
			default:
				throw new IllegalArgumentException("Illegal scope for variable info wrapper: " + viw.toString());
			}
		}
	}

	/**
	 * Checks the list of local variables if one of them has the given name.
	 * @param aName
	 * The name that is searched for
	 * @return
	 * True if one variable has the name, false otherwise.
	 */
	public boolean localListContainsName( String aName ) {
		
		if (aName == null || aName.trim().length() == 0) {
			return false; // who does this?
		}

		String name = aName.trim().toLowerCase();

		for (VariableInfoWrapper viw : locVars) {
			if (viw.getName().equals(name)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * This should not be used and it only exists for testing and debugging.
	 * @return
	 * The symbol table as a list of variable info wrapper objects
	 */
	public List<VariableInfoWrapper> getRawSymbolTable() {
		return symbolTable;
	}

	/**
	 * Adds a variable info wrapper to the symbol table and puts it into the
	 * according sub table by looking at the scope.
	 * @param aVIW
	 * The info wrapper object for a variable
	 */
	public void addToSymbolTable(VariableInfoWrapper aVIW) {
		symbolTable.add(aVIW);

		switch (aVIW.getScope()) {
		case GLOBAL:
			gloVars.add(aVIW);
		case PARAMETER:
			parVars.add(aVIW);
		case LOCAL:
			locVars.add(aVIW);
		default:
			throw new IllegalArgumentException("Illegal scope for variable info wrapper: " + aVIW.toString());
		}
	}

	/**
	 * @return all variable info wrappers in this symbol table that have a
	 * global scope
	 */
	public List<VariableInfoWrapper> getAllGlobalVarInfoWrapper() {
		return gloVars;
	}

	/**
	 * @return all variable info wrappers in this symbol table that are
	 * arguments
	 */
	public List<VariableInfoWrapper> getAllParameterVarInfoWrapper() {
		return parVars;
	}

	/**
	 * @return all variable info wrappers in this symbol table that have a local
	 * scope
	 */
	public List<VariableInfoWrapper> getAllLocalVarInfoWrapper() {
		return locVars;
	}

	/**
	 * Searches for all variable info objects that have a specific type
	 * @param aType
	 * The type of the variable
	 * @return All global, local and argument variables of the given type
	 */
	public List<VariableInfoWrapper> getAllVarInfoWrapperWithType(String aType) {
		if (aType == null || aType.trim().length() == 0) {
			return null;
		}

		List<VariableInfoWrapper> result = new ArrayList<VariableInfoWrapper>();
		String type = aType.trim().toLowerCase();

		for (VariableInfoWrapper viw : symbolTable) {
			if (viw.getType().equals(type)) {
				result.add(viw);
			}
		}

		return result;
	}

	/**
	 * Searches for all variable info objects that have a specific name
	 * @param aName
	 * The name of the variable
	 * @return
	 * All global, local and argument variables with the given name
	 */
	public List<VariableInfoWrapper> getAllVarInfoWrapperWithName(String aName) {
		if (aName == null || aName.trim().length() == 0) {
			return null;
		}

		List<VariableInfoWrapper> result = new ArrayList<VariableInfoWrapper>();
		String name = aName.trim().toLowerCase();

		for (VariableInfoWrapper viw : symbolTable) {
			if (viw.getName().equals(name)) {
				result.add(viw);
			}
		}

		return result;
	}
	
	/**
	 * Searches for all variable info objects from the local list that have a specific name.
	 * This method was added because the local list is more often used for the generation of
	 * local unique names.
	 * @param aName
	 * The name of the variable
	 * @return
	 * All global, local and argument variables with the given name
	 */
	public List<VariableInfoWrapper> getAllVarInfoWrapperWithNameFromLocals(String aName) {
		if (aName == null || aName.trim().length() == 0) {
			return null;
		}

		List<VariableInfoWrapper> result = new ArrayList<VariableInfoWrapper>();
		String name = aName.trim().toLowerCase();

		for (VariableInfoWrapper viw : locVars) {
			if (viw.getName().equals(name)) {
				result.add(viw);
			}
		}

		return result;
	}

}
