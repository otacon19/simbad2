package sinbad2.element.alternative.listener;


public class AlternativesChangeEvent {
	
	private EAlternativesChange _change;
	private Object _oldValue;
	private Object _newValue;
	private boolean _inUndoRedo;
	
	private AlternativesChangeEvent() {
		_change = null;
		_oldValue = null;
		_newValue = null;
		_inUndoRedo = false;
	}
	
	public AlternativesChangeEvent(EAlternativesChange change, Object oldValue, Object newValue, boolean inUndoRedo) {
		this();
		_change = change;
		_oldValue = oldValue;
		_newValue = newValue;
		_inUndoRedo = inUndoRedo;
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
	
	public boolean getInUndoRedo() {
		return _inUndoRedo;
	}
}

