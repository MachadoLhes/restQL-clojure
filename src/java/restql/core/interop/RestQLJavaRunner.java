package restql.core.interop;

import restql.core.config.ConfigRepository;
import restql.core.config.RouteMap;
import restql.core.query.Query;
import restql.core.query.QueryOptions;
import restql.core.response.QueryResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by ideais on 14/12/16.
 */
public class RestQLJavaRunner {

    private RouteMap mappings;
    private Map<String, Class> encoders = new HashMap<>();

    public RestQLJavaRunner(ConfigRepository configRepository) {
        this.mappings = configRepository.getMappings();
    }

    public QueryResponse executeQuery(String query, QueryOptions queryOptions) {
        return new QueryResponse(ClojureRestQLApi.query(
                mappings.toMap(),
                encoders,
                query,
                queryOptions.toMap()));
    }

    public QueryResponse executeQuery(Query query, QueryOptions queryOptions) {
        return executeQuery(query.toString(), queryOptions);
    }

    public void executeQueryAsync(String query, QueryOptions queryOptions, Consumer<QueryResponse> consumer) {
        ClojureRestQLApi.queryAsync(
                mappings.toMap(),
                encoders,
                query,
                queryOptions.toMap(),
                result -> {
                    consumer.accept(new QueryResponse((String) result));
                });
    }

    public void executeQueryAsync(Query query, QueryOptions queryOptions, Consumer<QueryResponse> consumer) {
        executeQueryAsync(query.toString(), queryOptions, consumer);
    }


    public <T> void setEncoder(String name, Class<T> clazz) {
        encoders.put(name, clazz);
    }
}
