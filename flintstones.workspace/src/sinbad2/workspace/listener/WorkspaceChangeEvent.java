package sinbad2.workspace.listener;

public class WorkspaceChangeEvent {
	
	private EWorkspaceChange _change;
	private Object _oldValue;
	private Object _newValue;
	
	private WorkspaceChangeEvent() {
		_change = null;
		_oldValue = null;
		_newValue = null;
	}
	
	public WorkspaceChangeEvent(EWorkspaceChange change, Object oldValue, Object newValue) {
		this();
		_change = change;
		_oldValue = oldValue;
		_newValue = newValue;
	}
	
	public EWorkspaceChange getWorkspaceChange() {
		return _change;
	}
	
	public Object getOldValue() {
		return _oldValue;
	}
	
	public Object getNewValue() {
		return _newValue;
	}
}
