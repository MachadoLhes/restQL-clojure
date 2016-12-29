package pdg.query;

import pdg.interop.Encoder;

public interface ParameterValue {
	
	public String getName();
	
	public default Boolean shouldExpand() {
		return true;
	}

	public default String getEncoderName() { return null; }

	public void setEncoderName(String encoderName);

	public String toString();
	
}
