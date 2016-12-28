package pdg.interop;

import pdg.config.ConfigRepository;
import pdg.config.RouteMap;
import pdg.query.Query;
import pdg.query.QueryOptions;
import pdg.response.QueryResponse;

import java.util.function.Consumer;

/**
 * Created by ideais on 14/12/16.
 */
public class PDGJavaRunner {

    private RouteMap mappings;

    public PDGJavaRunner(ConfigRepository configRepository) {
        this.mappings = configRepository.getMappings();
    }

    public QueryResponse executeQuery(String query, QueryOptions queryOptions) {
        return new QueryResponse(ClojurePDGApi.query(
                mappings.toMap(),
                null,
                query,
                queryOptions.toMap()));
    }

    public void executeQueryAsync(String query, QueryOptions queryOptions, Consumer<QueryResponse> consumer) {
        ClojurePDGApi.queryAsync(
                mappings.toMap(),
                null,
                query,
                queryOptions.toMap(),
                result -> {
                    consumer.accept(new QueryResponse((String) result));
                });
    }

    public QueryResponse executeQuery(Query query, QueryOptions queryOptions) {
        return executeQuery(query.toString(), queryOptions);
    }


}
