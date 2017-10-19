package restql.core;

import restql.core.config.ConfigRepository;
import restql.core.interop.ClojureRestQLApi;
import restql.core.query.QueryInterpolator;
import restql.core.query.QueryOptions;
import restql.core.response.QueryResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class RestQL {

	/**
	 * The query options
	 */
	private QueryOptions queryOptions;

	/**
	 * restQL configurations
	 */
	private ConfigRepository configRepository;

	/**
	 * Query encoders
	 */
	private Map<String, Class> encoders = new HashMap<>();

	/**
	 * Class constructor with query options set to a production environment.
	 *
	 * @param configRepository {@link ConfigRepository}
	 */
	public RestQL(ConfigRepository configRepository) {
		this.configRepository = configRepository;
		this.queryOptions = new QueryOptions();

		// Production default to false
		this.queryOptions.setDebugging(false);
	}

	/**
	 * Class constructor with custom query options.
	 *
	 * @param configRepository {@link ConfigRepository}
	 * @param queryOptions     {@link QueryOptions}
	 */
	public RestQL(ConfigRepository configRepository, QueryOptions queryOptions) {
		this.configRepository = configRepository;
		this.queryOptions = queryOptions;
	}

	public QueryResponse executeQuery(String query, QueryOptions queryOptions, Object... args) {
		return new QueryResponse(ClojureRestQLApi.query(configRepository.getMappings().toMap(),
				this.encoders,
				QueryInterpolator.interpolate(query, args),
				queryOptions.toMap()));
	}

	public QueryResponse executeQuery(String query, Object... args) {
		return this.executeQuery(query, this.queryOptions, args);
	}

	public void executeQueryAsync(String query, QueryOptions queryOptions, BiConsumer<QueryResponse, Object> consumer, Object... args) {
		ClojureRestQLApi.queryAsync(configRepository.getMappings().toMap(),
				this.encoders,
				QueryInterpolator.interpolate(query, args),
				queryOptions.toMap(),
				(result, error) ->
						consumer.accept(new QueryResponse((String) result), error));
	}

	public void executeQueryAsync(String query, BiConsumer<QueryResponse, Object> consumer, Object... args) {
		this.executeQueryAsync(query, this.queryOptions, consumer, args);
	}

	public <T> void setEncoder(String name, Class<T> clazz) {
		this.encoders.put(name, clazz);
	}
}
