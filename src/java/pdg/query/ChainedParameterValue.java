package pdg.query;

import java.util.LinkedList;

public class ChainedParameterValue implements ParameterValue {

    /**
     * The name identifier.
     */
	private String name;

    /**
     * The name of the encoder used to handle data
     */
    private String encoderName;

    /**
     * To return in shouldExpand().
     */
	private Boolean parameterShouldExpand;

    /**
     * The list of parameters to chain.
     */
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
     * Class constructor.
     *
     * @param name The variable name
     * @param path The query path
     * @param expand If query should expand or not
     * @param encoderName The encoder name
     */
    public ChainedParameterValue(String name, String path[], Boolean expand, String encoderName) {
        this(name, path, expand);
        this.setEncoderName(encoderName);
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
	public String getName() {
		return this.name;
	}
	
	@Override
	public Boolean shouldExpand() {
		return this.parameterShouldExpand;
	}

	@Override
	public void setEncoderName(String encoderName) {
		this.encoderName = encoderName;
	}

	@Override
	public String getEncoderName() {
		return this.encoderName;
	}


	@Override
	public String toString() {
		String queryString = ":" + this.name + " ";

		if(!this.shouldExpand() || this.getEncoderName() != null) {
			queryString += "^{";

			// If shouldn't expand
			queryString += (!this.shouldExpand() ? ":expand false " : "");

			// If it has encoder
			queryString += (this.getEncoderName() != null ? ":encoder :"+this.getEncoderName()+" " : "" );


			queryString = queryString.trim() + "} ";
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
