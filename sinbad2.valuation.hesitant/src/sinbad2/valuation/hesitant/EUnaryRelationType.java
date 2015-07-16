package sinbad2.valuation.hesitant;

public enum EUnaryRelationType {
	 AtMost("at most"), AtLeast("at least"), LowerThan("lower than"), GreaterThan("greater than");
	
	private String _relationType;
	
	EUnaryRelationType(String relationType) {
		_relationType = relationType;
		
	}
	
	public String getRelationType() {
		return _relationType;
	}	
	
}
