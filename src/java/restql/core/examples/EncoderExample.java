package restql.core.examples;

import restql.core.RestQL;
import restql.core.config.ClassConfigRepository;
import restql.core.interop.Encoder;
import restql.core.query.QueryOptions;
import restql.core.response.QueryItemResponse;
import restql.core.response.QueryResponse;

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

        RestQL restQL = new RestQL(config);
        restQL.setEncoder("reverse", ReverseEncoder.class);

        QueryOptions opts = new QueryOptions();
        opts.setDebugging(true);

        QueryResponse response = restQL.execute(
                "[:res {:from :people\n" +
                "       :with {:name ^{:encoder :reverse}\n" +
                "                     {:value \"ekul\"}}}]", opts);

        System.out.println(response);

        QueryItemResponse queryItem = response.get("luke", QueryItemResponse.class);

        System.out.println(queryItem.getDetails().getStatus());
    }
}