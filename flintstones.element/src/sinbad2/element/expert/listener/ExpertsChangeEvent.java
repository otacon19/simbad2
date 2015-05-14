package sinbad2.element.expert.listener;

public class ExpertsChangeEvent {
	
	private EExpertsChange _change;
	private Object _oldValue;
	private Object _newValue;
	
	public ExpertsChangeEvent() {
		_change = null;
		_oldValue = null;
		_newValue = null;
	}
	
	public ExpertsChangeEvent(EExpertsChange change, Object oldValue, Object newValue) {
		this();
		_change = change;
		_oldValue = oldValue;
		_newValue = newValue;
	}
	
	public EExpertsChange getChange() {
		return _change;
	}
	
	public Object getOldValue() {
		return _oldValue;
	}
	
	public Object getNewValue() {
		return _newValue;
	}
}
