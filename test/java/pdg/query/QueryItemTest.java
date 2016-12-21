package pdg.query;

import static org.junit.Assert.*;

import org.junit.Test;

import pdg.query.ChainedParameterValue;
import pdg.query.QueryItem;
import pdg.query.SimpleParameterValue;

public class QueryItemTest {

	@Test
	public void testSimplestQueryItemEver() {
		String correctQuery = ":people {:from :users}";
		
		QueryItem simplestQueryItemEver = new QueryItem("people", "users");
		
		assertEquals(correctQuery, simplestQueryItemEver.toString());
	}
	
	@Test
	public void testSimplestQueryItemEverWithTimeout() {
		String correctQuery = ":people {:from :users :timeout 2000}";
		
		QueryItem simplestQueryItemEver = new QueryItem("people", "users");
		simplestQueryItemEver.setTimeout(2000);
		
		assertEquals(correctQuery, simplestQueryItemEver.toString());
	}

	@Test
	public void testQueryItemWithSimpleWithClause() {
		String colorParam = ":color \"black\"";
		String weightParam = ":weight 120";
		String blackAnimalsName = ":blackAnimals";
		String queryString = blackAnimalsName + " {:from :animals :with {" + colorParam + " " + weightParam +"}}";
		
		
		QueryItem queryWithSimpleWithClause = new QueryItem("blackAnimals", "animals");
		queryWithSimpleWithClause
			.addWithParameter(new SimpleParameterValue<String>("color","black"))
			.addWithParameter(new SimpleParameterValue<Integer>("weight",120));
		
		String generatedQueryString = queryWithSimpleWithClause.toString();
		
		System.out.println("QueryItem[SimpleWith]: " + generatedQueryString);
		
		assertEquals(queryString, generatedQueryString.toString());
	}
	
	@Test
	public void testQueryItemWithAndWithHeaders() {
		String colorParam = ":color \"black\"";
		String weightParam = ":weight 120";
		String blackAnimalsName = ":blackAnimals";
		String withHeaders = ":with-headers {:Authorization \"86fgr30hf40gh0834gf\"}";
		String queryString = blackAnimalsName + " {:from :animals :with {" + colorParam + " " + weightParam +"} "+withHeaders + "}";
		
		
		QueryItem queryWithAndWithHeadersClause = new QueryItem("blackAnimals", "animals");
		queryWithAndWithHeadersClause
			.addHeader("Authorization", "86fgr30hf40gh0834gf")
			.addWithParameter(new SimpleParameterValue<String>("color","black"))
			.addWithParameter(new SimpleParameterValue<Integer>("weight",120));
		
		String generatedQueryString = queryWithAndWithHeadersClause.toString();
		
		System.out.println("QueryItem[SimpleWith]: " + generatedQueryString);
		
		assertEquals(queryString, generatedQueryString.toString());
	}
	
	@Test
	public void testQueryWithChainedWith() {
		String queryString = ":jedis {:from :people :with {:role \"jedi\" :lightSaberColor [:lightsabers :color]}}";
		 
		QueryItem chainedQuery = new QueryItem("jedis", "people");
		chainedQuery
			.addWithParameter(new SimpleParameterValue<String>("role","jedi"))
			.addWithParameter(new ChainedParameterValue("lightSaberColor", new String[]{"lightsabers", "color"}));
		
		System.out.println("QueryItem[ChainedWith]: " + chainedQuery);
		
		assertEquals(queryString, chainedQuery.toString());
	}
	
	@Test
	public void testQueryWithChainedWithNoExpand() {
		String queryString = ":jediLightSabers {:from :people :with {:role \"jedi\" :lightSaberColor ^{:expand false} [:lightsabers :color]}}";
		 
		QueryItem chainedQuery = new QueryItem("jediLightSabers", "people");
		
		ChainedParameterValue chainedValue = new ChainedParameterValue("lightSaberColor", new String[]{"lightsabers", "color"}, Expand.DONT_EXPAND);
		
		chainedQuery
			.addWithParameter(new SimpleParameterValue<String>("role","jedi"))
			.addWithParameter(chainedValue);
		
		System.out.println("QueryItem[ChainedWithNoExpand]: " + chainedQuery);
		
		assertEquals(queryString, chainedQuery.toString());
	}
	
	@Test
	public void testQueryWithListParameter() {
		String queryString = ":jedis {:from :people :with {:role \"jedi\" :name [\"Luke\" \"Obi-Wan\"]}}";
		 
		QueryItem chainedQuery = new QueryItem("jedis", "people");
		chainedQuery
			.addWithParameter(new ListParameterValue<>("name", new String[]{"Luke","Obi-Wan"}))
			.addWithParameter(new SimpleParameterValue<String>("role","jedi"));
		
		System.out.println("QueryItem[ListParam]: " + chainedQuery);
		
		assertEquals(queryString, chainedQuery.toString());
	}
	
	@Test
	public void testQueryWithListParameterDontExpand() {
		String queryString = ":jedis {:from :people :with {:role \"jedi\" :name ^{:expand false} [\"Luke\" \"Obi-Wan\"]}}";
		 
		QueryItem chainedQuery = new QueryItem("jedis", "people");
		chainedQuery
			.addWithParameter(new ListParameterValue<>("name", new String[]{"Luke","Obi-Wan"}, Expand.DONT_EXPAND))
			.addWithParameter(new SimpleParameterValue<String>("role","jedi"));
		
		System.out.println("QueryItem[ListParamDontExpand]: " + chainedQuery);
		
		assertEquals(queryString, chainedQuery.toString());
	}
}
