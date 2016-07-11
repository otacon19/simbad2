package sinbad2.valuation.hesitant;

public enum EUnaryRelationType {
	LowerThan("lower than"), GreaterThan("greater than"), AtLeast("at least"), AtMost("at most"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	
	private String _relationType;
	
	EUnaryRelationType(String relationType) {
		_relationType = relationType;
		
	}
	
	public String getRelationType() {
		return _relationType;
	}	
	
	public void setRelationType(String relationType) {
		_relationType = relationType;
	}
	
}
