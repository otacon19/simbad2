package sinbad2.element.alternative.listener;

public class AlternativesChangeEvent {
	
	private EAlternativesChange _change;
	private Object _oldValue;
	private Object _newValue;
	
	
	private AlternativesChangeEvent() {
		_change = null;
		_oldValue = null;
		_newValue = null;
	}
	
	public AlternativesChangeEvent(EAlternativesChange change, Object oldValue, Object newValue) {
		this();
		_change = change;
		_oldValue = oldValue;
		_newValue = newValue;
	}
	
	public EAlternativesChange getChange() {
		return _change;
	}
	
	public Object getOldValue() {
		return _oldValue;
	}
	
	public Object getNewValue() {
		return _newValue;
	}
}

