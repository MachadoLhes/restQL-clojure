package restql.core;

import restql.core.interop.RestQLJavaRunner;
import restql.core.response.QueryResponse;
import restql.core.config.ConfigRepository;
import restql.core.query.Query;
import restql.core.query.QueryOptions;
import restql.core.querybuilder.QueryBuilder;

import java.util.function.Consumer;

public class RestQL {

	/**
	 * The query options
	 */
	private QueryOptions queryOptions;
	
	/**
	 * The java runner bridge to the clojure code 
	 */
	private RestQLJavaRunner pdgRunner;
	
	/**
	 * Class constructor with query options set to a production environment. 
	 * 
	 * @param configRepository {@link ConfigRepository}
	 */
	public RestQL(ConfigRepository configRepository) {
		this.pdgRunner = new RestQLJavaRunner(configRepository);
		this.queryOptions = new QueryOptions();
		
		// Production default to false
		this.queryOptions.setDebugging(false);
	}
	
	/**
	 * Class constructor with custom query options.
	 * 
	 * @param configRepository {@link ConfigRepository}
	 * @param queryOptions {@link QueryOptions}
	 */
	public RestQL(ConfigRepository configRepository, QueryOptions queryOptions) {
		this(configRepository);
		
		this.queryOptions = queryOptions;
	}
	
	/**
	 * Executes a query using a query string.
	 * @param query {@link String}
	 * 
	 * @return {@link QueryResponse}
	 */
	public QueryResponse execute(String query) {
		return pdgRunner.executeQuery(query, queryOptions);
	}
	
	/**
	 * Executes a query using the {@link Query} object from the {@link QueryBuilder}.
	 * 
	 * @param query {@link Query}
	 * 
	 * @return {@link QueryResponse}
	 */
	public QueryResponse execute(Query query) {
		return this.pdgRunner.executeQuery(query, this.queryOptions);
	}

	/**
	 * Executes a query using the {@link Query} object from the {@link QueryBuilder}.
	 *
	 * @param query {@link Query}
	 * @param queryOptions {@link QueryOptions}
	 *
	 * @return {@link QueryResponse}
	 */
	public QueryResponse execute(Query query, QueryOptions queryOptions) {
		return this.pdgRunner.executeQuery(query, queryOptions);
	}

	/**
	 * Executes a query using a {@link String}.
	 *
	 * @param query {@link String}
	 * @param queryOptions {@link QueryOptions}
	 *
	 * @return {@link QueryResponse}
	 */
	public QueryResponse execute(String query, QueryOptions queryOptions) {
		return this.pdgRunner.executeQuery(query, queryOptions);
	}
	
	/**
	 * Executes an asynchronous query using the {@link Query} object from the {@link QueryBuilder}.
	 * 
	 * @param query {@link Query}
	 * @param consumer {@link Consumer}
	 * 
	 */
	public void executeAsync(Query query, Consumer<QueryResponse> consumer) {
		this.pdgRunner.executeQueryAsync(query, this.queryOptions, consumer);
	}

	/**
	 * Executes an asynchronous query using a {@link String}.
	 *
	 * @param query {@link String}
	 * @param consumer {@link Consumer}
	 *
	 */
	public void executeAsync(String query, Consumer<QueryResponse> consumer) {
		this.pdgRunner.executeQueryAsync(query, this.queryOptions, consumer);
	}

    /**
     * Executes an asynchronous query using the {@link Query} object from the {@link QueryBuilder}.
     *
     * @param query {@link Query}
     * @param queryOptions {@link QueryOptions}
     * @param consumer {@link Consumer}
     *
     */
    public void executeAsync(Query query, QueryOptions queryOptions, Consumer<QueryResponse> consumer) {
        this.pdgRunner.executeQueryAsync(query, queryOptions, consumer);
    }

    /**
     * Executes an asynchronous query using a {@link String}.
     *
     * @param query {@link String}
     * @param queryOptions {@link QueryOptions}
     * @param consumer {@link Consumer}
     *
     */
    public void executeAsync(String query, QueryOptions queryOptions, Consumer<QueryResponse> consumer) {
        this.pdgRunner.executeQueryAsync(query, queryOptions, consumer);
    }

	/**
	 * Gets a new instance of {@link QueryBuilder}.
	 * 
	 * @return {@link QueryBuilder}
	 */
	public QueryBuilder queryBuilder() {
		return new QueryBuilder();
	}

	/**
	 * Sets an encoder
	 *
	 * @param name {@link String}
	 * @param clazz {@link Class}
	 * @param <T> T
     */
	public <T> void setEncoder(String name, Class<T> clazz) {
		pdgRunner.setEncoder(name, clazz);
	}
}
