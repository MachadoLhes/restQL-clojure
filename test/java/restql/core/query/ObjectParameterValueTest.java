package restql.core.query;

import static org.junit.Assert.*;

import org.junit.Test;

public class ObjectParameterValueTest {

	@Test
	public void testSimpleObjectParam() {
		String correctQuery = ":location {:lng -43.3333 :lat -22.4}";
		
		ObjectParameterValue objectParam = new ObjectParameterValue("location");
		objectParam.addParameter(new SimpleParameterValue<>("lat", -22.4000));
		objectParam.addParameter(new SimpleParameterValue<>("lng", -43.3333));
		
		assertEquals(correctQuery, objectParam.toString());
	}

	@Test
	public void testSimpleObjectParamNoExpand() {
		String correctQuery = ":location ^{:expand false} {:lng -43.3333 :lat -22.4}";

		ObjectParameterValue objectParam = new ObjectParameterValue("location");
		objectParam.addParameter(new SimpleParameterValue<>("lat", -22.4000));
		objectParam.addParameter(new SimpleParameterValue<>("lng", -43.3333));
		objectParam.setShouldExpand(false);

		assertEquals(correctQuery, objectParam.toString());
	}

	@Test
	public void testSimpleObjectParamNoExpandAndEncoder() {
		String correctQuery = ":location ^{:expand false :encoder :json} {:lng -43.3333 :lat -22.4}";

		ObjectParameterValue objectParam = new ObjectParameterValue("location");
		objectParam.addParameter(new SimpleParameterValue<>("lat", -22.4000));
		objectParam.addParameter(new SimpleParameterValue<>("lng", -43.3333));
		objectParam.setShouldExpand(false);
		objectParam.setEncoderName("json");

		assertEquals(correctQuery, objectParam.toString());
	}
	
	@Test
	public void testObjectParamWithList() {
		String correctQuery = ":spaceship {:crew [\"Kylo Ren\" \"General Hux\"]}";
		
		ObjectParameterValue objectParam = new ObjectParameterValue("spaceship");
		objectParam.addParameter(new ListParameterValue<>("crew", new String[]{"Kylo Ren","General Hux"}));
		
		assertEquals(correctQuery, objectParam.toString());
	}
	
	@Test
	public void testObjectParamWithNestedObject() {
		String correctQuery = ":spacestation {:weapon {:name \"Superlaser\"} "
				+":crew [\"Kylo Ren\" \"General Hux\"]}";
		
		// Weapon
		ObjectParameterValue weaponObjParam = new ObjectParameterValue("weapon");
		weaponObjParam.addParameter(new SimpleParameterValue<>("name", "Superlaser"));
		
		// Spacestation
		ObjectParameterValue spacestationObjParam = new ObjectParameterValue("spacestation");
		spacestationObjParam.addParameter(new ListParameterValue<>("crew", new String[]{"Kylo Ren","General Hux"}));
		spacestationObjParam.addParameter(weaponObjParam);
		
		assertEquals(correctQuery, spacestationObjParam.toString());
	}

}
