package restql.core.examples;

import restql.core.RestQL;
import restql.core.config.ClassConfigRepository;
import restql.core.response.QueryResponse;
import restql.core.query.QueryOptions;

/**
 * Created by ideais on 28/12/16.
 */
public class ExpandExample {

    public static void main(String[] args) {

        ClassConfigRepository config = new ClassConfigRepository();
        config.put("cards", "http://api.magicthegathering.io/v1/cards");

        RestQL restQL = new RestQL(config);

        QueryOptions opts = new QueryOptions();
        opts.setDebugging(true);

        QueryResponse response = restQL.execute(
                " [:artifacts {:from :cards\n" +
                        "              :with {:type \"Artifact\"}\n" +
                        "              :select :none}\n" +
                        "\n" +
                        "  :cards {:from [:artifacts :cards]}]",
                opts);

        System.out.println(response);
    }
}