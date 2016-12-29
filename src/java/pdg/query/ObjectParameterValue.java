package pdg.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ObjectParameterValue implements ParameterValue {

	/**
	 * The name identifier.
	 */
	private String name;

	/**
	 * The name of the encoder used to handle data
	 */
	private String encoderName;
	
	/**
	 * The object body.
	 */
	private Map<String, ParameterValue> objectBody;
	
	/**
	 * To return in shouldExpand().
	 */
	private Boolean parameterShouldExpand;
	
	
	/**
	 * Class constructor.
	 * 
	 * @param name {@link String}
	 */
	public ObjectParameterValue(String name) {
		this.name = name;
		this.objectBody = new HashMap<>();
		this.parameterShouldExpand = true;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	/**
	 * Adds a new parameter to the object body.
	 * 
	 * @param param The parameter to add to the object body
	 */
	public void addParameter(ParameterValue param) {
		this.objectBody.put(param.getName(), param);
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
		String queryString = ":" + this.getName() + " ";

		if(!this.shouldExpand() || this.getEncoderName() != null) {
			queryString += "^{";

			// If shouldn't expand
			queryString += (!this.shouldExpand() ? ":expand false " : "");

			// If it has encoder
			queryString += (this.getEncoderName() != null ? ":encoder :"+this.getEncoderName()+" " : "" );


			queryString = queryString.trim() + "} ";
		}
		
		queryString += "{";
		
		for(Entry<String, ParameterValue> bodyParam : this.objectBody.entrySet()) {
			queryString += bodyParam.getValue().toString() + " ";
		}
		
		queryString = queryString.trim() + "}";
		return queryString;
	}
}
