package pdg.querybuilder;

import pdg.query.*;

public class QueryWithBuilder {
		
	
	private QueryItem queryItem;
	
	private QueryFromBuilder queryFromBuilder;
	
	private QueryBuilder queryBuilder;

	private String paramName;
	
	private Boolean shouldExpand;
	
	
	public QueryWithBuilder setQueryBuilder(QueryBuilder queryBuilder) {
		this.queryBuilder = queryBuilder;
		
		return this;
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param paramName The parameter name
	 * @param item {@link QueryItem}
	 * @param fromBuilder {@link QueryFromBuilder}
	 * @param shouldExpand {@link Boolean}
	 */
	public QueryWithBuilder(String paramName, QueryItem item, QueryFromBuilder fromBuilder, Boolean shouldExpand) {
		this.queryItem = item;
		this.paramName = paramName;
		this.queryFromBuilder = fromBuilder;
		this.shouldExpand = shouldExpand;
	}
	
	/**
	 * Filter by simple parameters (String, Double, Integer, etc.).
	 * 
	 * @param value The attribute value
	 * @param <T> type
	 * @return {@link QueryFromBuilder} with builder
	 */
	public <T> QueryFromBuilder value(T value) {
		this.queryItem.addWithParameter(new SimpleParameterValue<T>(this.paramName, value));
		
		return this.queryFromBuilder;
	}
	
	/**
	 * Creates a new object with a simple parameter (String, Double, Integer, etc.).
	 * 
	 * @param name {@link String}
	 * @param value The attribute value
	 * @param <T> type
	 * 
	 * @return {@link QueryWithObjectBuilder} with builder
	 */
	public <T> QueryWithObjectBuilder value(String name, T value) {
		ObjectParameterValue objectParam = new ObjectParameterValue(this.paramName);
		objectParam.setShouldExpand(this.shouldExpand);
		
		this.queryItem.addWithParameter(objectParam);
		
		QueryWithObjectBuilder objectBuilder = new QueryWithObjectBuilder(objectParam, this.queryFromBuilder);
		
		return objectBuilder.value(name, value);
	}
	
	/**
	 * Creates a new chained parameter.
	 * 
	 * @param path The API path
	 * @return {@link QueryFromBuilder} with builder
	 */
	public QueryFromBuilder chained(String path[]) {
		ChainedParameterValue chainedValue = new ChainedParameterValue(this.paramName, path);
		
		chainedValue.setShouldExpand(this.shouldExpand);
		this.queryItem.addWithParameter(chainedValue);
		
		return this.queryFromBuilder;
	}
	
	/**
	 * Creates a new object with a chained parameter.
	 * 
	 * @param name {@link String}
	 * @param path The API path
	 * @return {@link QueryWithObjectBuilder} with builder
	 */
	public QueryWithObjectBuilder chained(String name, String path[]) {
		
		ObjectParameterValue objectParam = new ObjectParameterValue(this.paramName);
		objectParam.setShouldExpand(this.shouldExpand);
		
		this.queryItem.addWithParameter(objectParam);
		
		QueryWithObjectBuilder objectBuilder = new QueryWithObjectBuilder(objectParam, this.queryFromBuilder);
		
		return objectBuilder.chained(name, path);
	}
	
	
	/**
	 * Creates a new list parameter.
	 * 
	 * @param params An array of params
	 * @param <T> type
	 * 
	 * @return {@link QueryFromBuilder} with builder
	 */
	public <T> QueryFromBuilder list(T params[]) {
		this.queryItem.addWithParameter(new ListParameterValue<T>(this.paramName, params, this.shouldExpand));
		
		return this.queryFromBuilder;
	}
	
	/**
	 * Creates a new object with a list parameter.
	 *
	 * @param name {@link String}
	 * @param params An array of params
	 * @param <T> type
	 * 
	 * @return {@link QueryWithObjectBuilder} with builder
	 */
	public <T> QueryWithObjectBuilder list(String name, T params[]) {
		ObjectParameterValue objectParam = new ObjectParameterValue(this.paramName);
		objectParam.setShouldExpand(this.shouldExpand);
		
		this.queryItem.addWithParameter(objectParam);
		
		QueryWithObjectBuilder objectBuilder = new QueryWithObjectBuilder(objectParam, this.queryFromBuilder);
		
		return objectBuilder.list(name, params);
	}
	
	/**
	 * Creates a new nested object inside current object.
	 * @param name {@link String}[]
	 * @return {@link QueryWithObjectBuilder}
	 */
	public QueryWithObjectBuilder object(String name) {
		
		ObjectParameterValue objectParam = new ObjectParameterValue(this.paramName);
		objectParam.setShouldExpand(this.shouldExpand);
		
		this.queryItem.addWithParameter(objectParam);
		
		QueryWithObjectBuilder objectBuilder = new QueryWithObjectBuilder(objectParam, this.queryFromBuilder);
		return objectBuilder.object(name);
	}
	
	/**
	 * Goes back to the from builder
	 * 
	 * @return {@link QueryFromBuilder}
	 */
	public QueryFromBuilder endWith() {
		return this.queryFromBuilder;
	}
	
	/**
	 * Shortcut for QueryBuilder.get
	 * 
	 * @param name {@link String}
	 * @return {@link QueryFromBuilder}
	 */
	public QueryFromBuilder get(String name) {
		return this.queryBuilder.get(name);
	}
	
	/**
	 * Shortcut for QueryBuilder.getQuery
	 * 
	 * @return {@link Query}
	 */
	public Query getQuery() {
		return this.queryBuilder.getQuery();
	}
	
}
