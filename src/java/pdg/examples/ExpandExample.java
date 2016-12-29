package pdg.examples;

import pdg.PDG;
import pdg.config.ClassConfigRepository;
import pdg.query.Query;
import pdg.query.QueryOptions;
import pdg.response.QueryItemResponse;
import pdg.response.QueryResponse;

/**
 * Created by ideais on 28/12/16.
 */
public class ExpandExample {

    public static void main(String[] args) {

        ClassConfigRepository config = new ClassConfigRepository();
        config.put("cards", "http://api.magicthegathering.io/v1/cards");

        PDG pdg = new PDG(config);

        QueryOptions opts = new QueryOptions();
        opts.setDebugging(true);

        QueryResponse response = pdg.execute(
                " [:artifacts {:from :cards\n" +
                        "              :with {:type \"Artifact\"}\n" +
                        "              :select :none}\n" +
                        "\n" +
                        "  :cards {:from [:artifacts :cards]}]",
                opts);

        System.out.println(response);
    }
}