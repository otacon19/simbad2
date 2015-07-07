package sinbad2.core.workspace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import sinbad2.core.io.FileMonitor;
import sinbad2.core.io.IFileChangeListener;
import sinbad2.core.undoable.HistoryState;
import sinbad2.core.workspace.listener.EWorkspaceChange;
import sinbad2.core.workspace.listener.IWorkspaceListener;
import sinbad2.core.workspace.listener.WorkspaceChangeEvent;

public class Workspace implements IFileChangeListener {
	
	private static Workspace _instance;

	private String _fileName;
	private IWorkspaceContent _content;
	private long _savedHashCode;
	private long _currentHashCode;
	private boolean _doClose;

	private List<IWorkspaceListener> _listeners;

	private Workspace() {
		_doClose = false;
		_fileName = null;
		_content = null;
		_listeners = new LinkedList<IWorkspaceListener>();

		_savedHashCode = -1l;
		_currentHashCode = -1l;
	}

	public static Workspace getWorkspace() {
		if (_instance == null) {
			_instance = new Workspace();
		}

		return _instance;
	}

	public void setFileName(String fileName) {
		String oldValue = _fileName;
		_fileName = fileName;

		if (oldValue != null) {
			FileMonitor.getInstance().removeFileChangeListener(this, oldValue);
		}

		if (_fileName != null) {
			try {
				FileMonitor.getInstance().addFileChangeListener(this, _fileName, 1000);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		WorkspaceChangeEvent event = new WorkspaceChangeEvent(EWorkspaceChange.NEW_FILE, oldValue, _fileName);
		if (_fileName != null) {
			if (!_fileName.equals(oldValue)) {
				notifyWorkspaceChange(event);
			}
		} else if (oldValue != null) {
			notifyWorkspaceChange(event);
		}
	}

	public String getFileName() {
		return _fileName;
	}

	public void setContent(IWorkspaceContent content) {

		IWorkspaceContent oldValue = null;

		if (_content != null) {
			oldValue = _content.copyData();
		}

		_content = content;
		_content.activate();
		_savedHashCode = _content.hashCode();
		_currentHashCode = _savedHashCode;

		WorkspaceChangeEvent event = new WorkspaceChangeEvent(EWorkspaceChange.NEW_CONTENT, oldValue, _content);
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

	public boolean isClean() {
		return (_savedHashCode == _currentHashCode);
	}

	public void setDoClose(boolean doClose) {
		_doClose = doClose;
	}

	public void close() {
		_doClose = true;
		WorkspaceChangeEvent event = null;
		IWorkspaceContent oldValue = null;

		event = new WorkspaceChangeEvent(EWorkspaceChange.PRE_WORKSPACE_CLOSE, oldValue, _content);
		notifyWorkspaceChange(event);

		if (_doClose) {
			oldValue = _content.copyData();

			_content.clear();
			_currentHashCode = _content.hashCode();
			_savedHashCode = _currentHashCode;

			setFileName(null);
			HistoryState.getInstance().cleanHistory();

			event = new WorkspaceChangeEvent(
					EWorkspaceChange.POST_WORKSPACE_CLOSE, oldValue, _content);
			notifyWorkspaceChange(event);
		}
	}

	public void save(String fileName) throws IOException,
			WorkspaceContentPersistenceException {
		_content.save(fileName);
		long oldValue = new Long(_savedHashCode);
		_savedHashCode = _currentHashCode;

		if (oldValue != _savedHashCode) {
			WorkspaceChangeEvent event = new WorkspaceChangeEvent(
					EWorkspaceChange.HASH_CODE_SAVED, oldValue, _savedHashCode);
			notifyWorkspaceChange(event);
		}
	}

	public void read(String fileName) throws IOException,
			WorkspaceContentPersistenceException {
		IWorkspaceContent newContent = _content.read(fileName);

		close();
		if (_doClose) {
			_content.copyData(newContent);
			setContent(_content);
			setFileName(fileName);
		}
	}

	public long getCurrentHashCode() {
		return _currentHashCode;
	}

	public long getSavedHashCode() {
		return _savedHashCode;
	}

	public void updateHashCode() {
		long oldValue = new Long(_currentHashCode);

		if (_content == null) {
			_currentHashCode = -1l;
		} else {
			_currentHashCode = _content.hashCode();
		}

		if (oldValue != _currentHashCode) {
			WorkspaceChangeEvent event = new WorkspaceChangeEvent(EWorkspaceChange.HASH_CODE_MODIFIED, oldValue, _currentHashCode);
			notifyWorkspaceChange(event);
		}
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

	@Override
	public void fileChanged(File file) {
		if (!file.exists()) {
			FileMonitor.getInstance().removeFileChangeListener(this, _fileName);
			String oldValue = new String(_fileName);
			_fileName = null;
			_savedHashCode = -1l;
			_currentHashCode = _content.hashCode();
			WorkspaceChangeEvent event = new WorkspaceChangeEvent(
					EWorkspaceChange.FILE_REMOVED, oldValue, _fileName);
			notifyWorkspaceChange(event);
		}
	}

}
