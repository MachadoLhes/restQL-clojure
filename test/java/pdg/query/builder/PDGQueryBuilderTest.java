package pdg.query.builder;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pdg.exception.UniqueNameViolationException;
import pdg.query.Expand;
import pdg.query.Query;
import pdg.querybuilder.QueryBuilder;

public class PDGQueryBuilderTest {

	@Test
	public void testSimplestQueryEver() throws UniqueNameViolationException {
		String correctQuery = "[:people {:from :galaxy}]";
		
		QueryBuilder queryBuilder = new QueryBuilder();
		
		queryBuilder
			.get("people")
			.from("galaxy");
		
		Query query = queryBuilder.getQuery(); 
							
		System.out.println("PDGQueryBuilder[SimplestQuery]: " + query);
		
		assertEquals(correctQuery,query.toString());
	}
	
	@Test(expected = UniqueNameViolationException.class)
	public void testSimplestWrongQueryEver() throws UniqueNameViolationException {
		String correctQuery = "[:people {:from :galaxy}]";
		
		QueryBuilder queryBuilder = new QueryBuilder();
		
		queryBuilder
			.get("people")
				.from("galaxy")
			.get("people")
				.from("galaxy");
	}
	
	@Test
	public void testSimplestQueryEverWithTimeout() throws UniqueNameViolationException {
		String correctQuery = "[:people {:from :galaxy :timeout 2000}]";
		
		QueryBuilder queryBuilder = new QueryBuilder();
		
		queryBuilder
			.get("people")
			.from("galaxy")
			.timeout(2000);
		
		Query query = queryBuilder.getQuery(); 
							
		System.out.println("PDGQueryBuilder[SimplestQueryWithTimeout]: " + query);
		
		assertEquals(correctQuery,query.toString());
	}
	
	@Test
	public void testQueryWithSimpleWith() throws UniqueNameViolationException {
		String correctQuery = "[:jedis {:from :galaxy :timeout 2000 :with {:role \"jedi\"}}]";
		
		QueryBuilder queryBuilder = new QueryBuilder();
		
		queryBuilder
			.get("jedis")
				.from("galaxy")
				.timeout(2000)
				.with("role")
					.value("jedi");
		
		Query query = queryBuilder.getQuery(); 
							
		System.out.println("PDGQueryBuilder[QueryWithSimpleWith]: " + query);
		
		assertEquals(correctQuery,query.toString());
	}
	
	@Test
	public void testQueryWithAndWithHeaders() throws UniqueNameViolationException {
		String correctQuery = "[:jedis {:from :galaxy :timeout 2000 :with {:role \"jedi\"} :with-headers {\"secret\" \"SUPER_SECRET_JEDI_TRICK\"}}]";
		
		QueryBuilder queryBuilder = new QueryBuilder();
		
		queryBuilder
			.get("jedis")
				.from("galaxy")
				.timeout(2000)
				.header("secret", "SUPER_SECRET_JEDI_TRICK")
				.with("role")
					.value("jedi");
				
		
		Query query = queryBuilder.getQuery(); 
							
		System.out.println("PDGQueryBuilder[QueryWithAndWithHeaders]: " + query);
		
		assertEquals(correctQuery,query.toString());
	}
	
	@Test
	public void testQueryMultipleGetsWithSimpleWith() throws UniqueNameViolationException {
		String correctQuery = "[:jedis {:from :galaxy :timeout 2000 :with {:role \"jedi\"}} :sith {:from :galaxy :with {:role \"sith\" :alive true}}]";
		
		QueryBuilder queryBuilder = new QueryBuilder();
		
		queryBuilder
			.get("jedis")
				.from("galaxy")
				.timeout(2000)
				.with("role")
					.value("jedi")
			.get("sith")
				.from("galaxy")
				.with("role")
					.value("sith")
				.with("alive")
					.value(true);
		
		Query query = queryBuilder.getQuery(); 
							
		System.out.println("PDGQueryBuilder[QueryMultipleGetsWithSimpleWith]: " + query);
		
		assertEquals(correctQuery,query.toString());
	}
	
	@Test
	public void testQueryWithChainedWith() throws UniqueNameViolationException {
		String correctQuery = "[:jedis {:from :galaxy :timeout 2000 :with {:role \"jedi\"}} "+
						":lightsabers {:from :lightsabers :with {:lightsaber [:jedis :lightsaber :id]}}]";
		
		QueryBuilder queryBuilder = new QueryBuilder();
		
		queryBuilder
			.get("jedis")
				.from("galaxy")
				.timeout(2000)
				.with("role")
					.value("jedi")
			.get("lightsabers")
				.from("lightsabers")
				.with("lightsaber")
					.chained(new String[]{"jedis", "lightsaber", "id"});
		
		Query query = queryBuilder.getQuery();
							
		System.out.println("PDGQueryBuilder[QueryWithChainedWith]: " + query);
		
		assertEquals(correctQuery,query.toString());
	}
	
	@Test
	public void testQueryWithChainedWithNoExpand() throws UniqueNameViolationException {
		String correctQuery = "[:jedis {:from :galaxy :timeout 2000 :with {:role \"jedi\"}} "+
						":lightsabers {:from :lightsabers :with {:lightsaber ^{:expand false} [:jedis :lightsaber :id]}}]";
		
		QueryBuilder queryBuilder = new QueryBuilder();
		
		queryBuilder
			.get("jedis")
				.from("galaxy")
				.timeout(2000)
				.with("role")
					.value("jedi")
			.get("lightsabers")
				.from("lightsabers")
				.with("lightsaber", Expand.DONT_EXPAND)
					.chained(new String[]{"jedis", "lightsaber", "id"});
		
		Query query = queryBuilder.getQuery();
							
		System.out.println("PDGQueryBuilder[QueryWithChainedWithNoExpand]: " + query);
		
		assertEquals(correctQuery,query.toString());
	}

	@Test
	public void testQueryWithChainedWithNoExpandAndEncoder() throws UniqueNameViolationException {
		String correctQuery = "[:jedis {:from :galaxy :timeout 2000 :with {:role ^{:encoder :capitalize} \"jedi\"}} "+
				":lightsabers {:from :lightsabers :with {:lightsaber ^{:expand false :encoder :json} [:jedis :lightsaber :id]}}]";

		QueryBuilder queryBuilder = new QueryBuilder();

		queryBuilder
				.get("jedis")
					.from("galaxy")
					.timeout(2000)
						.with("role", "capitalize")
							.value("jedi")
				.get("lightsabers")
					.from("lightsabers")
						.with("lightsaber", Expand.DONT_EXPAND, "json")
						.chained(new String[]{"jedis", "lightsaber", "id"});

		Query query = queryBuilder.getQuery();

		System.out.println("PDGQueryBuilder[QueryWithChainedWithNoExpandAndEncoder]: " + query);

		assertEquals(correctQuery,query.toString());
	}
	
	@Test
	public void testQueryWithListParameter() {
		String correctQuery = "[:jedis {:from :galaxy :timeout 2000 :with {:role \"jedi\" :name [\"Luke\" \"Obi-Wan\"]}}]";
		
		QueryBuilder queryBuilder = new QueryBuilder();
		
		queryBuilder
			.get("jedis")
				.from("galaxy")
				.timeout(2000)
				.with("name")
					.list(new String[]{"Luke","Obi-Wan"})
				.with("role")
					.value("jedi");
					
		
		Query query = queryBuilder.getQuery();
							
		System.out.println("PDGQueryBuilder[QueryListParam]: " + query);
		
		assertEquals(correctQuery,query.toString());
	}
	
	@Test
	public void testSimplestQueryWithMetadata() throws UniqueNameViolationException {
		String correctQuery = "^{:cache-control \"max-age=900\"}[:people {:from :galaxy}]";
		
		QueryBuilder queryBuilder = new QueryBuilder();
		
		queryBuilder
			.meta("cache-control", "max-age=900")
			.get("people")
			.from("galaxy");
		
		Query query = queryBuilder.getQuery(); 
							
		System.out.println("PDGQueryBuilder[SimplestQueryWithMeta]: " + query);
		
		assertEquals(correctQuery,query.toString());
	}
	
	@Test
	public void testQueryWithSimpleObjectParameter() {
		String correctQuery = "[:death-star {:from :spacestations :with "
				+"{:spacestation {:name \"Death Star\"}}}]";
		
		QueryBuilder queryBuilder = new QueryBuilder();
		
		queryBuilder
			.get("death-star")
				.from("spacestations")
				.with("spacestation")
					.value("name", "Death Star");
		
		Query query = queryBuilder.getQuery();
							
		System.out.println("PDGQueryBuilder[QuerySimpleObjectParam]: " + query);
		
		assertEquals(correctQuery,query.toString());
	}
	
	@Test
	public void testQueryWithNestedObjectParameter() {
		String correctQuery = "[:death-star {:from :spacestations :with "
				+"{:spacestation {:weapon {:name \"Superlaser\"} :crew [\"Darth Vader\"]}}}]";
		
		QueryBuilder queryBuilder = new QueryBuilder();
		
		queryBuilder
			.get("death-star")
				.from("spacestations")
				.with("spacestation")
					.list("crew", new String[]{"Darth Vader"})
					.object("weapon")
						.value("name", "Superlaser");
		
		Query query = queryBuilder.getQuery();
							
		System.out.println("PDGQueryBuilder[QueryNestedObjectParam]: " + query);
		
		assertEquals(correctQuery,query.toString());
	}
	
	@Test
	public void testQueryWithNestedObjectParameterDontExpand() {
		String correctQuery = "[:death-star {:from :spacestations :with "
				+"{:spacestation ^{:expand false} {:weapon {:name \"Superlaser\"} :crew [\"Darth Vader\"]}}}]";
		
		QueryBuilder queryBuilder = new QueryBuilder();
		
		queryBuilder
			.get("death-star")
				.from("spacestations")
				.with("spacestation", Expand.DONT_EXPAND)
					.list("crew", new String[]{"Darth Vader"})
					.object("weapon")
						.value("name", "Superlaser");
		
		Query query = queryBuilder.getQuery();
							
		System.out.println("PDGQueryBuilder[QueryNestedObjectParam]: " + query);
		
		assertEquals(correctQuery,query.toString());
	}
	
	@Test
	public void testQueryWithAllShortcuts() {
		String correctQuery = "["
				+ ":empireSpaceStations {:from :spacestations :with {:year [1970 2016] :spacestation {:master {:id [:siths :id]}}}} "
				+":lightsaber {:from :lightsabers :with {:id [:siths :lightsaberId]}} "
				+":siths {:from :siths}"
				+"]";
		
		QueryBuilder queryBuilder = new QueryBuilder();
		
		queryBuilder
			.get("empireSpaceStations")
				.from("spacestations")
					.with("spacestation")
						.object("master")
							.chained("id", new String[]{"siths", "id"})
					.with("year")
						.list(new Integer[]{1970,2016})
			.get("siths")
				.from("siths")
			.get("lightsaber")
				.from("lightsabers")
					.with("id")
						.chained(new String[]{"siths","lightsaberId"});
		
		Query query = queryBuilder.getQuery();
							
		System.out.println("PDGQueryBuilder[HugeShortcutTestQuery]: " + query);
		
		assertEquals(correctQuery,query.toString());
	}
	
}
