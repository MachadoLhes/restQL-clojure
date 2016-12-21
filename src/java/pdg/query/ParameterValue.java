package pdg.query;

public interface ParameterValue {
	
	public String getName();
	
	public default Boolean shouldExpand() {
		return true;
	}
	
	public String toString();
	
}
