package restql.core.query;

import static org.junit.Assert.*;

import org.junit.Test;

public class SimpleParameterValueTest {

	@Test
	public void testStringParameter() {
		String correctQuery = ":name \"Clark Kent\"";
		
		SimpleParameterValue<String> nameParam = new SimpleParameterValue<>("name","Clark Kent");
		
		assertEquals(correctQuery, nameParam.toString());
	}
	
	@Test
	public void testIntegerParameter() {
		Integer age = new Integer(28);
		String correctQuery = ":age "+age.toString();
		
		SimpleParameterValue<Integer> ageParam = new SimpleParameterValue<>("age",age);
		
		assertEquals(correctQuery, ageParam.toString());
	}	
	
	@Test
	public void testBooleanParameter() {
		Boolean married = true;
		String correctQuery = ":married "+married.toString();
		
		SimpleParameterValue<Boolean> marriedParam = new SimpleParameterValue<>("married");
		marriedParam.setValue(married);
		
		assertEquals(correctQuery, marriedParam.toString());
	}

	@Test
	public void testStringParameterWithEncoder() {
		String correctQuery = ":name ^{:encoder :json} \"Clark Kent\"";

		SimpleParameterValue<String> nameParam = new SimpleParameterValue<>("name","Clark Kent");
		nameParam.setEncoderName("json");

		assertEquals(correctQuery, nameParam.toString());
	}

}
