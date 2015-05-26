package sinbad2.domain.listener;

public class DomainSetChangeEvent {
	
	private EDomainSetChange _change;
	private Object _oldValue;
	private Object _newValue;
	
	private DomainSetChangeEvent() {
		_change = null;
		_oldValue = null;
		_newValue = null;
	}
	
	public DomainSetChangeEvent(EDomainSetChange change, Object oldValue, Object newValue) {
		this();
		_change = change;
		_oldValue = oldValue;
		_newValue = newValue;
	}
	
	public EDomainSetChange getChange() {
		return _change;
	}
	
	public Object getOldValue() {
		return _oldValue;
	}
	
	public Object getNewValue() {
		return _newValue;
	}

}
