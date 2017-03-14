package sinbad2.aggregationoperator;

public enum EAggregationOperatorType {
	TwoTuple("TwoTuple"), Linguistic("Linguistic"), Numeric("Numeric"), Interval("Interval"), UnifiedValuation("UnifiedValuation"),  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	Hesitant("Hesitant"), HesitantTwoTuple("HesitantTwoTuple"); //$NON-NLS-1$
	
	@SuppressWarnings("unused")
	private String _text;
	
	private EAggregationOperatorType(String text) {
		_text = text;
	}
}
