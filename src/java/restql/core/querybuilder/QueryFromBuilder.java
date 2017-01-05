package restql.core.querybuilder;

import restql.core.query.Expand;
import restql.core.query.QueryItem;
import restql.core.query.Query;

public class QueryFromBuilder {
	
	private QueryItem queryItem;
	
	private QueryBuilder queryBuilder;
		
	/**
	 * Class constructor
	 * 
	 * @param item The {@link QueryItem} to be built
	 * @param builder {@link QueryBuilder}
	 */
	public QueryFromBuilder(QueryItem item, QueryBuilder builder) {
		this.queryItem = item;
		this.queryBuilder = builder;
	}
	
	/**
	 * Gets data from {targetResource}.
	 * 
	 * @param targetResource The API to be requested
	 * @return {@link QueryFromBuilder} from builder
	 */
	public QueryFromBuilder from(String targetResource) {
		this.queryItem.setFrom(targetResource);
		
		return this;
	}
	
	/**
	 * Adds a new header to the query.
	 * 
	 * @param key The header key
	 * @param value The header value
	 * @return {@link QueryFromBuilder} from builder
	 */
	public QueryFromBuilder header(String key, String value) {
		this.queryItem.addHeader(key, value);
		
		return this;
	}
	
	/**
	 * Sets the query timeout.
	 * 
	 * @param timeout in milliseconds
	 * @return {@link QueryFromBuilder} this object
	 */
	public QueryFromBuilder timeout(Integer timeout) {
		this.queryItem.setTimeout(timeout);
		
		return this;
	}
	
	/**
	 * Filters the data to be retrieved with parameters with expansion.
	 * 
	 * @param name {@link String}
	 * 
	 * @return {@link QueryWithBuilder}
	 */
	public QueryWithBuilder with(String name) {
		return new QueryWithBuilder(name, this.queryItem ,this, Expand.EXPAND, null).setQueryBuilder(this.queryBuilder);
	}
	
	/**
	 * Filters the data to be retrieved with parameters.
	 * 
	 * @param name {@link String}
	 * @param shouldExpand {@link Boolean}
	 *  
	 * @return {@link QueryWithBuilder}
	 */
	public QueryWithBuilder with(String name, Boolean shouldExpand) {
		return new QueryWithBuilder(name, this.queryItem ,this, shouldExpand, null).setQueryBuilder(this.queryBuilder);
	}

	/**
	 * Filters the data to be retrieved with parameters, expansion and encoder.
	 *
	 * @param name {@link String}
	 * @param encoderName {@link String}
	 *
	 * @return {@link QueryWithBuilder}
	 */
	public QueryWithBuilder with(String name, String encoderName) {
		return new QueryWithBuilder(name, this.queryItem ,this, Expand.EXPAND, encoderName).setQueryBuilder(this.queryBuilder);
	}

	/**
	 * Filters the data to be retrieved with parameters and encoder.
	 *
	 * @param name {@link String}
	 * @param shouldExpand {@link Boolean}
	 * @param encoderName {@link String}
	 *
	 * @return {@link QueryWithBuilder}
	 */
	public QueryWithBuilder with(String name, Boolean shouldExpand, String encoderName) {
		return new QueryWithBuilder(name, this.queryItem ,this, shouldExpand, encoderName).setQueryBuilder(this.queryBuilder);
	}

	
	/**
	 * Goes back to the main query builder.
	 * 
	 * @return {@link QueryBuilder}
	 */
	public QueryBuilder endFrom() {
		return this.queryBuilder;
	}
	
	/**
	 * Shortcut for QueryBuilder.get
	 * 
	 * @param name {@link String}
	 * 
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
