package pdg.query;

import java.util.HashMap;
import java.util.Map.Entry;

public class QueryItem {

	/**
	 * The query name identifier.
	 */
	private String name;
	
	/**
	 * The API which the query will fetch data
	 */
	private String from;
	
	/**
	 * The query timeout in milliseconds
	 */
	private Integer timeout;
	
	/**
	 * The with parameters
	 */
	private HashMap<String, ParameterValue> with;
	
	/**
	 * The header parameters
	 */
	private HashMap<String, String> withHeaders;
	
	
	/**
	 * Class constructor.
	 * 
	 * @param name The query identifier
	 * @param from The API which the query will fetch data
	 */
	public QueryItem(String name, String from) {
		this(name);
		this.from = from;
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param name The query identifier
	 */
	public QueryItem(String name) {
		this.name = name;
		
		this.with = new HashMap<>();
		this.withHeaders = new HashMap<>();
	}
	
	

	public String getFrom() {
		return from;
	}
	
	public void setFrom(String resourceName) {
		this.from = resourceName;
	}


	public Integer getTimeout() {
		return timeout;
	}


	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}


	public String getName() {
		return name;
	}
	
	/**
	 * Adds a new parameter to the query item.
	 * 
	 * @param parameterValue {@link ParameterValue}
	 * @return {@link QueryItem}
	 */
	public QueryItem addWithParameter(ParameterValue parameterValue) {
		this.with.put(parameterValue.getName(), parameterValue);
		
		return this;
	}
	
	public QueryItem addHeader(String key, String value) {
		
		this.withHeaders.put(key, value);
		
		return this;
	}

	/**
	 * Transforms the with parameters to string.
	 *  
	 * @return The with parameters parsed
	 */
	private String parseWithToString() {
		
		if(this.with.isEmpty())
			return "";
		
		String withQueryString = ":with {";
		
		for(Entry<String,ParameterValue> valueEntry : this.with.entrySet()) {
			withQueryString += valueEntry.getValue().toString()+" ";
		}
		
		withQueryString = withQueryString.trim();
		
		withQueryString += "}";
		
		return withQueryString;
	}
	
	/**
	 * Transforms the with-headers parameters to string.
	 *  
	 * @return The with parameters parsed
	 */
	private String parseWithHeadersToString() {
		if(this.withHeaders.isEmpty())
			return "";
		
		String withHeadersQueryString = ":with-headers {";
		
		for(Entry<String, String> entry : this.withHeaders.entrySet()) {
			withHeadersQueryString += ":" + entry.getKey() + " \"" + entry.getValue() + "\" ";
		}
		
		withHeadersQueryString = withHeadersQueryString.trim();
		
		withHeadersQueryString += "}";
		
		return withHeadersQueryString;
	}
	
	
	@Override
	public String toString(){
		
		String queryString = ":"+this.name+" {";
		
		// Builds the from part
		queryString += ":from :"+this.from+" ";
		
		// Gets timeout if it's the case
		queryString += (this.timeout != null? ":timeout "+this.timeout.toString()+" ": "");
		
		// Builds the with clause
		queryString += this.parseWithToString();
		
		queryString = queryString.trim();
		
		// Builds the with-headers clause
		queryString += " " +this.parseWithHeadersToString();
		
		queryString = queryString.trim();
		
		// Closes the query
		queryString += "}";
				
		return queryString;
	}
	
	
}
