package pdg.examples;

import pdg.PDG;
import pdg.config.ClassConfigRepository;
import pdg.interop.Encoder;
import pdg.query.QueryOptions;
import pdg.response.QueryItemResponse;
import pdg.response.QueryResponse;

/**
 * Created by ideais on 29/12/16.
 */
public class EncoderExample {

    public static class ReverseEncoder extends Encoder {
        @Override
        public String encode() {
            String value = get(new String[]{"value"});
            return new StringBuilder(value).reverse().toString();
        }
    }

    public static void main(String... args) {

        ClassConfigRepository config = new ClassConfigRepository();
        config.put("people", "http://swapi.co/api/people");

        PDG pdg = new PDG(config);
        pdg.setEncoder("reverse", ReverseEncoder.class);

        QueryOptions opts = new QueryOptions();
        opts.setDebugging(true);

        QueryResponse response = pdg.execute(
                "[:res {:from :people\n" +
                "       :with {:name ^{:encoder :reverse}\n" +
                "                     {:value \"ekul\"}}}]", opts);

        System.out.println(response);

        QueryItemResponse queryItem = response.get("luke", QueryItemResponse.class);

        System.out.println(queryItem.getDetails().getStatus());
    }
}