package sinbad2.phasemethod.multigranular.elh.aggregation.listener;

public class AggregationProcessStateChangeEvent {
	
	private EAggregationProcessStateChange _change;
	private Object _oldValue;
	private Object _newValue;
	
	public AggregationProcessStateChangeEvent() {
		_change = null;
		_oldValue = null;
		_newValue = null;
	}
	
	public AggregationProcessStateChangeEvent(EAggregationProcessStateChange change, Object oldValue, Object newValue) {
		this();
		_change = change;
		_oldValue = oldValue;
		_newValue = newValue;
	}
	
	public EAggregationProcessStateChange getChange() {
		return _change;
	}
	
	public Object getOldValue() {
		return _oldValue;
	}
	
	public Object getNewValue() {
		return _newValue;
	}	
}

