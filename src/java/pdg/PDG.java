package pdg;

import java.util.function.Consumer;

import pdg.config.ConfigRepository;
import pdg.interop.PDGJavaRunner;
import pdg.query.Query;
import pdg.query.QueryOptions;
import pdg.querybuilder.QueryBuilder;
import pdg.response.QueryResponse;

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
	 * @param configRepository
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
	 * @param configRepository
	 * @param queryOptions
	 */
	public PDG(ConfigRepository configRepository, QueryOptions queryOptions) {
		this(configRepository);
		
		this.queryOptions = queryOptions;
	}
	
	/**
	 * Executes a query using a query string.
	 * @return {@link QueryResponse}
	 */
	public QueryResponse execute(String query) {
		return pdgRunner.executeQuery(query, queryOptions);
	}
	
	/**
	 * Executes a query using the {@link Query} object from the {@link QueryBuilder}.
	 * 
	 * @param query
	 * 
	 * @return {@link QueryResponse}
	 */
	public QueryResponse execute(Query query) {
		return this.pdgRunner.executeQuery(query, queryOptions);
	}
	
	/**
	 * Executes an asynchronous query using the {@link Query} object from the {@link QueryBuilder}.
	 * 
	 * @param query
	 * @param consumer
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
	
}
