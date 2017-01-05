package restql.core.query;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ListParameterValueTest {

	@Test
	public void testSimpleStringListParameter() {
		String correctQuery = ":location [\"home\" \"airport\"]";
		
		ListParameterValue<String> listOfString = new ListParameterValue<>("location", new String[]{"home","airport"});
		
		assertEquals(correctQuery, listOfString.toString());
	}
	
	@Test
	public void testSimpleIntegerListParameter() {
		String correctQuery = ":sum [25 45]";
		
		ListParameterValue<Integer> listOfString = new ListParameterValue<>("sum", new Integer[]{25,45});
		
		assertEquals(correctQuery, listOfString.toString());
	}

	@Test
	public void testSimpleStringListParameterDontExand() {
		String correctQuery = ":location ^{:expand false} [\"home\" \"airport\"]";
		
		ListParameterValue<String> listOfString = new ListParameterValue<>("location", new String[]{"home","airport"}, Expand.DONT_EXPAND);
		
		assertEquals(correctQuery, listOfString.toString());
	}

	@Test
	public void testSimpleIntegerListParameterWithEncoder() {
		String correctQuery = ":sum ^{:encoder :json} [25 45]";

		ListParameterValue<Integer> listOfString = new ListParameterValue<>("sum", new Integer[]{25,45});
		listOfString.setEncoderName("json");

		assertEquals(correctQuery, listOfString.toString());
	}

	@Test
	public void testSimpleStringListParameterDontExandAndEncoder() {
		String correctQuery = ":location ^{:expand false :encoder :json} [\"home\" \"airport\"]";

		ListParameterValue<String> listOfString = new ListParameterValue<>("location", new String[]{"home","airport"}, Expand.DONT_EXPAND);
		listOfString.setEncoderName("json");

		assertEquals(correctQuery, listOfString.toString());
	}
}
