package restql.core.examples;

import restql.core.RestQL;
import restql.core.config.ClassConfigRepository;
import restql.core.hooks.AfterRequestHook;
import restql.core.interop.Hook;
import restql.core.query.Query;
import restql.core.query.QueryOptions;

import java.util.Map;

/**
 * Created by ideais on 28/12/16.
 */
public class AsyncQuery {

    public static class BeforeRequestHook extends Hook {

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
            if(this.isError())
                System.out.println("Error: "+this.getData().get("errordetail"));
            else
                System.out.println(this.getResponseStatusCode());
        }
    }

    public static class BeforeQueryHook extends Hook {

        public void execute() {
            System.out.println("[BEFORE QUERY]");
            for(Map.Entry<String, Object> e : this.getData().entrySet()) {
                System.out.println(e.getKey()  + " - " + e.getValue());
            }
        }
    }

    public static class AfterQueryHook extends Hook {

        public void execute() {
            System.out.println("[AFTER QUERY]");
            for(Map.Entry<String, Object> e : this.getData().entrySet()) {
                System.out.println(e.getKey()  + " - " + e.getValue());
            }
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
        opts.addHook("before-query", BeforeQueryHook.class);
        opts.addHook("after-query", AfterQueryHook.class);
        opts.addHook("before-request", BeforeRequestHook.class);
        opts.addHook("after-request", SimpleAfterRequestHook.class);

        restQL.executeAsync(query, opts, System.out::println);
        Thread.sleep(5000);
    }

}
