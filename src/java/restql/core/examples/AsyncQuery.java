package restql.core.examples;

import restql.core.RestQL;
import restql.core.config.ClassConfigRepository;
import restql.core.hooks.AfterQueryHook;
import restql.core.hooks.AfterRequestHook;
import restql.core.hooks.QueryHook;
import restql.core.hooks.RequestHook;
import restql.core.query.Query;
import restql.core.query.QueryOptions;

import java.util.Map;

/**
 * Created by ideais on 28/12/16.
 */
public class AsyncQuery {

    public static class BeforeRequestHook extends RequestHook {

        public void execute() {
            System.out.println("[BEFORE REQUEST]");
            for(Map.Entry<String, Object> e : this.getData().entrySet()) {
                System.out.println(e.getKey()  + " - " + e.getValue());
            }
        }
    }

    public static class SimpleAfterRequestHook extends AfterRequestHook {

        public void execute() {
            System.out.println("[AFTER REQUEST]");
            System.out.println(this.getUrl() + " => " + this.getTimeout() + " CODE: " + this.getResponseStatusCode()
                    + " (" + this.getReponseTime() + ")");

            if(this.isError())
                System.out.println("Error: "+this.getData().get("errordetail"));
            else {
                for(Map.Entry<String, String> e : this.getHeaders().entrySet()) {
                    System.out.println(e.getKey() + " = " + e.getValue());
                }
            }

        }
    }

    public static class BeforeQueryHook extends QueryHook {

        public void execute() {
            System.out.println("[BEFORE QUERY] "+this.getQuery());
        }
    }

    public static class SimpleAfterQueryHook extends AfterQueryHook {

        public void execute() {
            System.out.println("[AFTER QUERY] "+this.getQuery());
            System.out.println(this.getResult().toString());
        }
    }

    public static void main(String[] args) throws Exception {
        ClassConfigRepository config = new ClassConfigRepository();
        config.put("person", "http://swapi.co/api/people/:id");
        config.put("people", "http://swapi.co/api/people");

        RestQL restQL = new RestQL(config);
        Query query = restQL.queryBuilder()
                .get("luke")
                    .from("person")
                        .with("id")
                            .value(1)
                .get("people")
                    .from("people")
                    .timeout(10)
                .getQuery();

        QueryOptions opts = new QueryOptions();

        opts.setBeforeQueryHook(BeforeQueryHook.class);
        opts.setAfterQuerytHook(SimpleAfterQueryHook.class);
        opts.setBeforeRequestHook(BeforeRequestHook.class);
        opts.setAfterRequestHook(SimpleAfterRequestHook.class);

        restQL.executeAsync(query, opts, System.out::println);
        Thread.sleep(5000);
    }

}
