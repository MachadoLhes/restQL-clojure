package restql.core.query;

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
	 * The name of the encoder used to handle data
	 */
	private String encoderName;

	/**
	 * The parameter name
	 */
	private String name;
	
	/**
	 * Class constructor.
	 * 
	 * @param name The parameter name.
	 */
	public SimpleParameterValue(String name) {
		this.name = name;
	}
	
	/**
	 * Class constructor.
	 *
	 * @param name Must be unique in the context.
	 * @param value The parameter type.
	 */
	public SimpleParameterValue(String name, T value) {
		this(name);
		this.simpleValue = value;
	}

	/**
	 * Class constructor.
	 *
	 * @param name Must be unique in the context.
	 * @param value The parameter type.
	 * @param encoderName The encoder name.
	 */
	public SimpleParameterValue(String name, T value, String encoderName) {
		this(name, value);
		this.setEncoderName(encoderName);
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
	public void setEncoderName(String encoderName) {
		this.encoderName = encoderName;
	}

	@Override
	public String getEncoderName() {
		return this.encoderName;
	}

	@Override
	public String toString() {
		
		String paramValue = (this.getEncoderName() != null ? "^{:encoder :"+this.getEncoderName()+"} " : "" );

		if(this.simpleValue instanceof String)
			paramValue += "\""+this.simpleValue+"\"";
		else
			paramValue += this.simpleValue.toString();
		
		return ":"+this.name+" "+paramValue;
	}
}
