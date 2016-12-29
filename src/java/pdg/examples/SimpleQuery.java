package pdg.examples;

import pdg.PDG;
import pdg.config.ClassConfigRepository;
import pdg.query.Query;
import pdg.query.QueryOptions;
import pdg.response.QueryItemResponse;
import pdg.response.QueryResponse;

/**
 * Created by ideais on 22/12/16.
 */
public class SimpleQuery {

    public static void main(String[] args) {

        ClassConfigRepository config = new ClassConfigRepository();
        config.put("person", "http://swapi.co/api/people/:id");

        PDG pdg = new PDG(config);

        Query query = pdg.queryBuilder()
                .get("luke")
                    .from("person")
                    .with("id")
                        .value(1)
                .getQuery();

        QueryOptions opts = new QueryOptions();
        opts.setDebugging(true);
        opts.setGlobalTimeout(10000);
        opts.setTimeout(3000);

        QueryResponse response = pdg.execute(query, opts);

        System.out.println(response);

        QueryItemResponse queryItem = response.get("luke", QueryItemResponse.class);

        System.out.println(queryItem.getDetails().getStatus());

    }

}