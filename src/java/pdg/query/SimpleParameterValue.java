package pdg.query;

/**
 * @author iago.osilva
 *
 * @param <T> A simple type (Integer, String, ...)
 */
public class SimpleParameterValue<T> implements ParameterValue{
	
	/**
	 * Stands for simple types like String, Boolean, Integer, etc.
	 */
	private T simpleValue;
	
	/**
	 * The parameter name
	 */
	private String name;
	
	/**
	 * Class constructor.
	 * 
	 * @param name The parameter name
	 */
	public SimpleParameterValue(String name) {
		this.name = name;
	}
	
	/**
	 * @param name Must be unique in the context.
	 * @param T The parameter type
	 */
	public SimpleParameterValue(String name, T value) {
		this(name);
		this.simpleValue = value;
	}

	
	public String getName() {
		return this.name;
	}
	
	public T getValue() {
		return this.simpleValue;
	}
	
	public void setValue(T value) {
		this.simpleValue = value;
	}
	
	
	@Override
	public String toString() {
		
		String paramValue = "";
		
		if(this.simpleValue instanceof String)
			paramValue = "\""+this.simpleValue+"\"";
		else
			paramValue = this.simpleValue.toString();
		
		return ":"+this.name+" "+paramValue;
	}
}
