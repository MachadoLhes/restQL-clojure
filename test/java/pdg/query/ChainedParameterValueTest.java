package pdg.query;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pdg.query.ChainedParameterValue;

public class ChainedParameterValueTest {
	
	/*
	@Test
	public void testSimpleChainedParameter() {
		String correctQuery = ":id [:galaxy :planets :id]";
		
		PDGChainedParameterValue chainedParameter = new PDGChainedParameterValue("id", "galaxy", "planets", "id");
				
		assertEquals(chainedParameter.toString(), correctQuery);
	}
	*/
	
	@Test
	public void testSimpleChainedParameter() {
		String correctQuery = ":id [:galaxy :planets :id]";
		
		String path[] = {"galaxy", "planets", "id"};
		ChainedParameterValue chainedParameter = new ChainedParameterValue("id", path);
				
		assertEquals(chainedParameter.toString(), correctQuery);
	}
	
	/*
	@Test
	public void testSimpleChainedParameterNoExpansion() {
		String correctQuery = ":id ^{:expand false} [:galaxy :planets :id]";
		
		PDGChainedParameterValue chainedParameter = new PDGChainedParameterValue("id", "galaxy", "planets", "id");
		chainedParameter.setShouldExpand(false);
				
		assertEquals(chainedParameter.toString(), correctQuery);
	}
	*/
	
	@Test
	public void testSimpleChainedParameterNoExpansion() {
		String correctQuery = ":id ^{:expand false} [:galaxy :planets :id]";
		
		String path[] = {"galaxy", "planets", "id"};
		
		ChainedParameterValue chainedParameter = new ChainedParameterValue("id", path, false);
		chainedParameter.setShouldExpand(false);
				
		assertEquals(chainedParameter.toString(), correctQuery);
	}
	
}
