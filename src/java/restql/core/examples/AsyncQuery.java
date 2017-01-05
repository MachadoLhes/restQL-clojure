package restql.core.examples;

import restql.core.RestQL;
import restql.core.config.ClassConfigRepository;
import restql.core.query.Query;

/**
 * Created by ideais on 28/12/16.
 */
public class AsyncQuery {

    public static void main(String[] args) throws Exception {
        ClassConfigRepository config = new ClassConfigRepository();
        config.put("person", "http://swapi.co/api/people/:id");

        RestQL restQL = new RestQL(config);
        Query query = restQL.queryBuilder()
                .get("luke")
                .from("person")
                .with("id")
                .value(1)
                .getQuery();

        restQL.executeAsync(query, System.out::println);
        Thread.sleep(5000);
    }

}
