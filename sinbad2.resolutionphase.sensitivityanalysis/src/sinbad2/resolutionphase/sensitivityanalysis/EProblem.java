package sinbad2.resolutionphase.sensitivityanalysis;

public enum EProblem {
	MOST_CRITICAL_CRITERION("MCC"), MOST_CRITICAL_MEASURE("MCM"); //$NON-NLS-1$ //$NON-NLS-2$
	
	private String _value;
	
	private EProblem(String value) {
		_value = value;
	}
	
	public String getValue() {
		return _value;
	}
}
