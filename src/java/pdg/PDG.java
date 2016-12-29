package pdg;

import pdg.config.ConfigRepository;
import pdg.examples.EncoderExample;
import pdg.interop.PDGJavaRunner;
import pdg.query.Query;
import pdg.query.QueryOptions;
import pdg.querybuilder.QueryBuilder;
import pdg.response.QueryResponse;

import java.util.function.Consumer;

public class PDG {

	/**
	 * The query options
	 */
	private QueryOptions queryOptions;
	
	/**
	 * The java runner bridge to the clojure code 
	 */
	private PDGJavaRunner pdgRunner;
	
	/**
	 * Class constructor with query options set to a production environment. 
	 * 
	 * @param configRepository {@link ConfigRepository}
	 */
	public PDG(ConfigRepository configRepository) {
		this.pdgRunner = new PDGJavaRunner(configRepository);
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
	public PDG(ConfigRepository configRepository, QueryOptions queryOptions) {
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
		return this.pdgRunner.executeQuery(query, queryOptions);
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
	 * Executes a query using the {@link Query} object from the {@link QueryBuilder}.
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
		this.pdgRunner.executeQueryAsync(query.toString(), this.queryOptions, consumer);
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
