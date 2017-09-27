package restql.core.examples;

import com.fasterxml.jackson.databind.util.JSONPObject;
import restql.core.RestQL;
import restql.core.config.ClassConfigRepository;
import restql.core.query.QueryOptions;
import restql.core.response.QueryItemResponse;
import restql.core.response.QueryResponse;

import java.util.HashMap;


public class SimplePostQueryFromLanguage {

	public static void main(String[] args) {

		ClassConfigRepository config = new ClassConfigRepository();
		config.put("mock", "http://httpbin.org/post");

		RestQL restQL = new RestQL(config);

		String query = "from mock with-body {foo: \"bar\"}";
		QueryOptions opts = new QueryOptions();
		opts.setDebugging(false);
		opts.setGlobalTimeout(10000);
		opts.setTimeout(3000);

		QueryResponse response = restQL.executeFromLanguage(query, opts);

		System.out.println(response);

		QueryItemResponse queryItem = response.get("mock", QueryItemResponse.class);

		System.out.println(queryItem.getDetails().getHeaders());
		System.out.println(queryItem.getResult(HashMap.class).get("data"));
	}
}