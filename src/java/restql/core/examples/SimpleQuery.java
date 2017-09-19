package restql.core.examples;

import restql.core.RestQL;
import restql.core.config.ClassConfigRepository;
import restql.core.response.QueryResponse;
import restql.core.query.Query;
import restql.core.query.QueryOptions;
import restql.core.response.QueryItemResponse;

import java.util.Map;

/**
 * Created by ideais on 22/12/16.
 */
public class SimpleQuery {

    public static void main(String[] args) {

        ClassConfigRepository config = new ClassConfigRepository();
        config.put("person", "http://swapi.co/api/people/:id");

        RestQL restQL = new RestQL(config);

        Query query = restQL.queryBuilder()
                .get("luke")
                    .from("person")
                    .with("id")
                        .value(1)
                .getQuery();

        QueryOptions opts = new QueryOptions();
        opts.setDebugging(false);
        opts.setGlobalTimeout(10000);
        opts.setTimeout(3000);

        QueryResponse response = restQL.execute(query, opts);

        System.out.println(response);

        QueryItemResponse queryItem = response.get("luke", QueryItemResponse.class);

        System.out.println(queryItem.getDetails().getHeaders());
//        System.out.println(queryItem.getDetails().getStatus());
//        System.out.println(queryItem.getDetails().getUrl());
//
//        for(Map.Entry<String, String> param : queryItem.getDetails().getHeaders().entrySet()) {
//            System.out.println(param.getKey() + " = " + param.getValue());
//        }

    }

}