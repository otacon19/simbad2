package sinbad2.aggregationoperator;

public enum EAggregationOperatorType {
	TwoTuple("TwoTuple"), Linguistic("Linguistic"), Numeric("Numeric"), Interval("Interval"), UnifiedValuation("UnifiedValuation"), 
	Hesitant("Hesitant");
	
	@SuppressWarnings("unused")
	private String _text;
	
	private EAggregationOperatorType(String text) {
		_text = text;
	}
}
