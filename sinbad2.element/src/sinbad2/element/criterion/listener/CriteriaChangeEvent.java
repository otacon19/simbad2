package sinbad2.element.criterion.listener;


public class CriteriaChangeEvent {
	
	private ECriteriaChange _change;
	private Object _oldValue;
	private Object _newValue;
	private boolean _inUndoRedo;
	
	private CriteriaChangeEvent() {
		_change = null;
		_oldValue = null;
		_newValue = null;
		_inUndoRedo = false;
	}
	
	public CriteriaChangeEvent(ECriteriaChange change, Object oldValue, Object newValue, boolean inUndoRedo) {
		this();
		_change = change;
		_oldValue = oldValue;
		_newValue = newValue;
		_inUndoRedo = inUndoRedo;
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
	
	public boolean getInUndoRedo() {
		return _inUndoRedo;
	}

}
