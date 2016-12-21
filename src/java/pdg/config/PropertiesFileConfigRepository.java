package pdg.config;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

public class PropertiesFileConfigRepository implements ConfigRepository {

	/**
	 * API Mappings
	 */
	private RouteMap mappings;
	
	/**
	 * Creates the class and gets mappings from Properties File
	 * @throws IOException 
	 */
	public PropertiesFileConfigRepository(String filename) throws IOException {
		Properties fileProperties = new Properties();
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		fileProperties.load(classLoader.getResourceAsStream(filename));
		
		mappings = new RouteMap();
		
		for(Entry<Object, Object> property : fileProperties.entrySet()) {
			mappings.put((String) property.getKey(), (String) property.getValue());
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public RouteMap getMappings() {
		// TODO Auto-generated method stub
		return this.mappings;
	}

}
