package pdg.querybuilder;

import pdg.query.*;

public class QueryWithObjectBuilder {
	
	/**
	 * The object being built
	 */
	private ObjectParameterValue objectParameter;
	
	/**
	 * The from builder
	 */
	private QueryFromBuilder fromBuilder;
		
	/**
	 * The parent object builder
	 */
	private QueryWithObjectBuilder parentObjectBuilder;
	
	/**
	 * Constructor for the root object builder.
	 * 
	 * @param objectParameter {@link ObjectParameterValue}
	 * @param fromBuilder {@link QueryFromBuilder}
	 */
	public QueryWithObjectBuilder(ObjectParameterValue objectParameter, QueryFromBuilder fromBuilder) {
		this.objectParameter = objectParameter;
		this.fromBuilder = fromBuilder;
	}
	
	/**
	 * Constructor for child object builder.
	 * 
	 * @param objectParameter {@link ObjectParameterValue}
	 * @param objectBuilder {@link QueryWithObjectBuilder}
	 * @param fromBuilder {@link QueryFromBuilder}
	 */
	public QueryWithObjectBuilder(ObjectParameterValue objectParameter, QueryFromBuilder fromBuilder, QueryWithObjectBuilder objectBuilder) {
		this.objectParameter = objectParameter;
		this.parentObjectBuilder = objectBuilder;
		this.fromBuilder = fromBuilder;
	}
	
	/**
	 * Adds a new simple parameter to the object.
	 * 
	 * @param name {@link String}
	 * @param value T
	 * @param <T> type
	 * 
	 * @return {@link QueryWithObjectBuilder} object builder
	 */
	public <T> QueryWithObjectBuilder value(String name, T value) {
		this.objectParameter.addParameter(new SimpleParameterValue<>(name, value));
		
		return this;
	}
	
	/**
	 * Adds a new list parameter to the object.
	 * 
	 * @param name {@link String}
	 * @param params T[]
	 * @param <T> type
	 * 
	 * @return {@link QueryWithObjectBuilder} object builder
	 */
	public <T> QueryWithObjectBuilder list(String name, T params[]) {
		this.objectParameter.addParameter(new ListParameterValue<>(name, params));
		
		return this;
	}
	
	/**
	 * Adds a new chained parameter to the object.
	 * 
	 * @param name {@link String}
	 * @param params {@link String}[]
	 * 
	 * @return {@link QueryWithObjectBuilder} object builder
	 */
	public QueryWithObjectBuilder chained(String name, String params[]) {
		this.objectParameter.addParameter(new ChainedParameterValue(name, params));
		
		return this;
	}
	
	/**
	 * Creates a new child {@link ObjectParameterValue} and returns
	 * the builder to the child object.
	 * 
	 * @param name The object name
	 * @return {@link QueryWithObjectBuilder} child object builder
	 */
	public QueryWithObjectBuilder object(String name) {
		ObjectParameterValue newObjectParameter = new ObjectParameterValue(name);
		
		this.objectParameter.addParameter(newObjectParameter);
		
		return new QueryWithObjectBuilder(newObjectParameter, this.fromBuilder, this);
	}
	
	/**
	 * Ends the object declaration and returns to the parent QueryWithObjectBuilder.
	 * 
	 * @return {@link QueryWithObjectBuilder} object builder
	 */
	public QueryWithObjectBuilder endObject() {
		return this.parentObjectBuilder;
	}
	
	/**
	 * Returns to the with builder, if it's in the object root.
	 * 
	 * @param name {@link String}
	 * 
	 * @return {@link QueryWithBuilder} with builder
	 */
	public QueryWithBuilder with(String name) {
		return this.fromBuilder.with(name);
	}
	
	/**
	 * Shortcut for QueryFromBuilder.from
	 * 
	 * @param targetResource The API to be requested
	 * @return {@link QueryFromBuilder} from builder
	 */
	public QueryFromBuilder from(String targetResource) {
		return this.fromBuilder.from(targetResource);
	}
	
	/**
	 * Shortcut for QueryBuilder.getQuery
	 * 
	 * @return {@link Query}
	 */
	public Query getQuery() {
		return this.fromBuilder.getQuery();
	}
}
