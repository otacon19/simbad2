package sinbad2.resolutionphase.frameworkstructuring.domainassignments.listener;

public class DomainAssignmentsChangeEvent {
	
	private EDomainAssignmentsChange _change;
	private Object _oldValue;
	private Object _newValue;
	private boolean _inUndoRedo;
	
	private DomainAssignmentsChangeEvent() {
		_change = null;
		_oldValue = null;
		_newValue = null;
		_inUndoRedo = false;
	}
	
	public DomainAssignmentsChangeEvent(EDomainAssignmentsChange change, Object oldValue, Object newValue, boolean inUndoRedo) {
		this();
		_change = change;
		_oldValue = oldValue;
		_newValue = newValue;
		_inUndoRedo = inUndoRedo;
	}
	
	public EDomainAssignmentsChange getChange() {
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
