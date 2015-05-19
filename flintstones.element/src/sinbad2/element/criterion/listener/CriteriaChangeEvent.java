package sinbad2.element.criterion.listener;


public class CriteriaChangeEvent {
	
	private ECriteriaChange _change;
	private Object _oldValue;
	private Object _newValue;
	
	private CriteriaChangeEvent() {
		_change = null;
		_oldValue = null;
		_newValue = null;
	}
	
	public CriteriaChangeEvent(ECriteriaChange change, Object oldValue, Object newValue) {
		this();
		_change = change;
		_oldValue = oldValue;
		_newValue = newValue;
	}
	
	public ECriteriaChange getChange() {
		return _change;
	}
	
	public Object getOldValue() {
		return _oldValue;
	}
	
	public Object getNewValue() {
		return _newValue;
	}

}
