package sinbad2.valuation.hesitant;

import sinbad2.valuation.hesitant.nls.Messages;

public enum EUnaryRelationType {
	LowerThan(Messages.EUnaryRelationType_lower_than), GreaterThan(Messages.EUnaryRelationType_greater_than), AtLeast(Messages.EUnaryRelationType_at_least), AtMost(Messages.EUnaryRelationType_at_most); 
	
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
