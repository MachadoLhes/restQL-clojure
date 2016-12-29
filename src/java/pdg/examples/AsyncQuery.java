package pdg.examples;

import pdg.PDG;
import pdg.config.ClassConfigRepository;
import pdg.query.Query;

/**
 * Created by ideais on 28/12/16.
 */
public class AsyncQuery {

    public static void main(String[] args) throws Exception {
        ClassConfigRepository config = new ClassConfigRepository();
        config.put("person", "http://swapi.co/api/people/:id");

        PDG pdg = new PDG(config);
        Query query = pdg.queryBuilder()
                .get("luke")
                .from("person")
                .with("id")
                .value(1)
                .getQuery();

        pdg.executeAsync(query, System.out::println);
        Thread.sleep(5000);
    }

}
