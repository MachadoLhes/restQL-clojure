package pdg.querybuilder;

import pdg.exception.UniqueNameViolationException;
import pdg.query.Query;
import pdg.query.QueryItem;

public class QueryBuilder {
	
	private Query query;
	
	/**
	 * Class constructor.
	 */
	public QueryBuilder() {
		this.query = new Query();
	}
	
	/**
	 * Creates a new variable named {name} in the query.
	 * 
	 * @param name {@link String}
	 * @return {@link QueryFromBuilder} The builder to chain the from piece.
	 * @throws UniqueNameViolationException if name is repeated
	 */
	public QueryFromBuilder get(String name) throws UniqueNameViolationException {
		QueryItem queryItem = new QueryItem(name); 
		
		this.query.addItem(queryItem);
		
		return new QueryFromBuilder(queryItem, this);
	}
	
	/**
	 * Adds a new meta data parameter to the query.
	 * Example: ("cache-control","max-age=900")
	 * 
	 * @param key The meta data key
	 * @param value The meta data value
	 * @return {@link QueryBuilder} query builder
	 */
	public QueryBuilder meta(String key, String value) {
		this.query.addMeta(key, value);
		
		return this;
	}
	
	/**
	 * Gets the PDGQuery object built.
	 * 
	 * @return {@link Query}
	 */
	public Query getQuery() {
		return this.query;
	}
}
