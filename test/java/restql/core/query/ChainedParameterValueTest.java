package restql.core.query;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ChainedParameterValueTest {
	
	@Test
	public void testSimpleChainedParameter() {
		String correctQuery = ":id [:galaxy :planets :id]";
		
		String path[] = {"galaxy", "planets", "id"};
		ChainedParameterValue chainedParameter = new ChainedParameterValue("id", path);
				
		assertEquals(chainedParameter.toString(), correctQuery);
	}

	
	@Test
	public void testSimpleChainedParameterNoExpansion() {
		String correctQuery = ":id ^{:expand false} [:galaxy :planets :id]";
		
		String path[] = {"galaxy", "planets", "id"};
		
		ChainedParameterValue chainedParameter = new ChainedParameterValue("id", path, false);
		chainedParameter.setShouldExpand(false);
				
		assertEquals(chainedParameter.toString(), correctQuery);
	}

	@Test
	public void testSimpleChainedParameterWithEncoder() {
		String correctQuery = ":id ^{:encoder :xml} [:galaxy :planets :id]";

		String path[] = {"galaxy", "planets", "id"};
		ChainedParameterValue chainedParameter = new ChainedParameterValue("id", path);
		chainedParameter.setEncoderName("xml");

		assertEquals(chainedParameter.toString(), correctQuery);
	}

	@Test
	public void testSimpleChainedParameterNoExpansionAndEncoder() {
		String correctQuery = ":id ^{:expand false :encoder :xml} [:galaxy :planets :id]";

		String path[] = {"galaxy", "planets", "id"};

		ChainedParameterValue chainedParameter = new ChainedParameterValue("id", path, false);
		chainedParameter.setShouldExpand(false);
		chainedParameter.setEncoderName("xml");

		assertEquals(chainedParameter.toString(), correctQuery);
	}
}
