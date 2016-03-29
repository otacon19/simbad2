package sinbad2.valuation.hesitant;

public enum EUnaryRelationType {
	LowerThan("lower than"), GreaterThan("greather than"), AtLeast("at least"), AtMost("at most");
	
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
