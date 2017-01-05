package restql.core.query;

import static org.junit.Assert.*;

import org.junit.Test;

import restql.core.exception.UniqueNameViolationException;

public class QueryTest {

	@Test
	public void testSimplestQueryEver() throws UniqueNameViolationException {
		String correctQuery = "[:people {:from :galaxy}]";
		
		QueryItem simplestQueryItemEver = new QueryItem("people", "galaxy");
		Query pdgQuery = new Query();
		pdgQuery.addItem(simplestQueryItemEver);
		
		System.out.println("PDGQuery[SimplestQuery]: " + pdgQuery);
		
		assertEquals(correctQuery, pdgQuery.toString());
		
	}
	
	@Test
	public void testSimplestQueryEverWithMetadata() throws UniqueNameViolationException {
		String correctQuery = "^{:cache-control \"max-age=900\"}[:people {:from :galaxy}]";
		
		QueryItem simplestQueryItemEver = new QueryItem("people", "galaxy");
		Query pdgQuery = new Query();
		pdgQuery.addItem(simplestQueryItemEver);
		pdgQuery.addMeta("cache-control", "max-age=900");
		
		System.out.println("PDGQuery[SimplestQueryWithMetadata]: " + pdgQuery);
		
		assertEquals(correctQuery, pdgQuery.toString());
		
	}
	
	@Test
	public void testSimpleQueryWithSimpleWith() throws UniqueNameViolationException {
		String correctQuery = "[:siths {:from :galaxy :with {:role \"sith\"}}]";
		
		QueryItem simpleQueryItem = new QueryItem("siths", "galaxy");
		simpleQueryItem.addWithParameter(new SimpleParameterValue<String>("role", "sith"));
		
		Query pdgQuery = new Query();
		pdgQuery.addItem(simpleQueryItem);
		
		System.out.println("PDGQuery[SimpleQueryWithSimpleWith]: " + pdgQuery);
		
		assertTrue(correctQuery.equals(pdgQuery.toString()));
	}
	
	@Test(expected = UniqueNameViolationException.class)
	public void testSimplestWrongQueryEver() throws UniqueNameViolationException {
		QueryItem usersPeopleItem = new QueryItem("people", "users");
		QueryItem universePeopleItem = new QueryItem("people", "universe");
		
		Query pdgQuery = new Query();
		pdgQuery.addItem(usersPeopleItem).addItem(universePeopleItem);
	}

}
