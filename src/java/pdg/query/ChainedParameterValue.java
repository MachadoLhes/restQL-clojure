package pdg.query;

import java.util.LinkedList;

public class ChainedParameterValue implements ParameterValue {

	private String name;
	
	private Boolean parameterShouldExpand;
	
	private LinkedList<String> chainedParameters;
	
		
	/**
	 * Class constructor.
	 * 
	 * @param name The variable name
	 * @param path The query path
	 * @param expand If query should expand or not
	 */
	public ChainedParameterValue(String name, String path[], Boolean expand) {
		
		this.name = name;
		this.chainedParameters = new LinkedList<>();
		
		for(String pathParam : path) {
			this.chainedParameters.add(pathParam);
		}
		
		this.setShouldExpand(expand);
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param name The variable name
	 * @param path The query path
	 */
	public ChainedParameterValue(String name, String path[]) {
		this(name, path, Expand.EXPAND);
	}
	
	
	/**
	 * Sets if the parameter should expand or not.
	 * 
	 * @param parameterShouldExpand
	 */
	public void setShouldExpand(Boolean parameterShouldExpand) {
		this.parameterShouldExpand = parameterShouldExpand;
	}

	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public Boolean shouldExpand() {
		return this.parameterShouldExpand;
	}
	
	@Override
	public String toString() {
		String queryString = ":" + this.name + " ";
		
		if(!this.shouldExpand()) {
			queryString += "^{:expand false} ";
		}
		
		queryString += "[";
		
		for(String chainedParam : this.chainedParameters) {
			queryString += ":" + chainedParam + " ";
		}
		
		queryString = queryString.trim();
		queryString += "]";
		
		return queryString;
	}

}
