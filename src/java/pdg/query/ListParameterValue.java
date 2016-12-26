package pdg.query;

import java.util.LinkedList;
import java.util.List;

public class ListParameterValue<T> implements ParameterValue {
	
	/**
	 * The name identifier.
	 */
	private String name;

	/**
	 * To return in shouldExpand().
	 */
	private Boolean parameterShouldExpand;

	
	/**
	 * The list of parameters to filter.
	 */
	private List<T> listOfParams;
	
	@Override
	public String getName() {
		
		return this.name;
	}
	
	/**
	 * Class constructor with expand set to true.
	 * 
	 * @param name The name identifier
	 * @param params The list of parameters to filter
	 */
	public ListParameterValue(String name, T params[]) {
		this.name = name;
		this.listOfParams = new LinkedList<>();
		
		for(T param : params) {
			this.listOfParams.add(param);
		}
		
		this.setShouldExpand(Expand.EXPAND);
	}
	
	/**
	 * Class constructor with expand configuration.
	 * 
	 * @param name The name identifier
	 * @param params The list of parameters to filter
	 * @param parameterShouldExpand If it should expand or not
	 */
	public ListParameterValue(String name, T params[], Boolean parameterShouldExpand) {
		this(name,params);
		
		this.setShouldExpand(parameterShouldExpand);
	}
	
	/**
	 * Sets if the parameter should expand or not.
	 * 
	 * @param parameterShouldExpand {@link Boolean}
	 */
	public void setShouldExpand(Boolean parameterShouldExpand) {
		this.parameterShouldExpand = parameterShouldExpand;
	}
	
	@Override
	public String toString() {
		String queryString = ":" + this.getName() + " ";
		
		if(!this.shouldExpand()) {
			queryString += "^{:expand false} ";
		}
		
		queryString += "[";
		
		for(T param : this.listOfParams) {
			if(param instanceof String)
				queryString += "\"" + param + "\" ";
			else
				queryString += param.toString() + " ";
		}
		
		queryString = queryString.trim() + "]";
		
		return queryString;
	}

	@Override
	public Boolean shouldExpand() {
		return parameterShouldExpand;
	}
	
}
