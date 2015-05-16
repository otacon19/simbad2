package sinbad2.core.workspace;

import java.util.LinkedList;
import java.util.List;

import sinbad2.core.workspace.listener.EWorkspaceChange;
import sinbad2.core.workspace.listener.IWorkspaceListener;
import sinbad2.core.workspace.listener.WorkspaceChangeEvent;

public class Workspace {
	
	private static Workspace _instance;

	private IWorkspaceContent _content;
	
	private List<IWorkspaceListener> _listeners;
	
	private Workspace() {
		_listeners = new LinkedList<IWorkspaceListener>();
	}
	
	public static Workspace getWorkspace() {
		if (_instance == null) {
			_instance = new Workspace();
		}

		return _instance;
	}
	
	public void setContent(IWorkspaceContent content) {

		IWorkspaceContent oldValue = null;

		if (_content != null) {
			oldValue = _content.copyData();
		}
		_content = content;
		_content.activate();
		//TODO

		WorkspaceChangeEvent event = new WorkspaceChangeEvent(
				EWorkspaceChange.NEW_CONTENT, oldValue, _content);
		notifyWorkspaceChange(event);
	}
	
	public IWorkspaceContent getContent() {
		return _content;
	}
	
	public Object getElement(String id) {

		Object result = null;

		if (_content != null) {
			result = _content.getElement(id);
		}

		return result;
	}
	
	public void registerWorkspaceListener(IWorkspaceListener listener) {
		_listeners.add(listener);
	}

	public void unregisterWorkspaceListener(IWorkspaceListener listener) {
		_listeners.remove(listener);
	}

	public void notifyWorkspaceChange(WorkspaceChangeEvent event) {
		for (IWorkspaceListener listener : _listeners) {
			listener.notifyWorkspaceChange(event);
		}
	}
}
