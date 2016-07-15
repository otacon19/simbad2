package sinbad2.element.expert.listener;

public class ExpertsChangeEvent {
	
	private EExpertsChange _change;
	private Object _oldValue;
	private Object _newValue;
	private boolean _inUndoRedo;
	
	public ExpertsChangeEvent() {
		_change = null;
		_oldValue = null;
		_newValue = null;
		_inUndoRedo = false;
	}
	
	public ExpertsChangeEvent(EExpertsChange change, Object oldValue, Object newValue, boolean inUndoRedo) {
		this();
		_change = change;
		_oldValue = oldValue;
		_newValue = newValue;
		_inUndoRedo = inUndoRedo;
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
	
	public boolean getInUndoRedo() {
		return _inUndoRedo;
	}
}
