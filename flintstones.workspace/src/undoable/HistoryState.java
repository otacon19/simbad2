package undoable;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.commands.operations.OperationHistoryFactory;

import undoable.listener.EUndoHistoryChange;
import undoable.listener.IUndoableListener;

public class HistoryState implements IOperationHistoryListener {
	
	private static HistoryState _instance = null;
	
	private IOperationHistory _operationHistory;
	private List<IUndoableListener> _listeners;
	
	private boolean _canUndo;
	private boolean _canRedo;
	
	private HistoryState() {
		_operationHistory = OperationHistoryFactory.getOperationHistory();
		_listeners = new LinkedList<IUndoableListener>();
		
		_operationHistory.addOperationHistoryListener(this);
		
		_canUndo = false;
		_canRedo = false;
		refreshHistory();
	}
	
	public static HistoryState getInstance() {
		
		if(_instance == null) {
			_instance = new HistoryState();
		}
		
		return _instance;
	}
	
	public void clearHistory() {
		_operationHistory.dispose(IOperationHistory.GLOBAL_UNDO_CONTEXT, true, true, true);
	}
	
	public void refreshHistory() {
		boolean newCanRedo = _operationHistory.canRedo(IOperationHistory.GLOBAL_UNDO_CONTEXT);
		
		if(newCanRedo != _canRedo) {
			_canRedo = newCanRedo;
			if(_canRedo) {
				notifyListeners(EUndoHistoryChange.UNDO_ENABLED);
			} else {
				notifyListeners(EUndoHistoryChange.UNDO_DISABLED);
			}
		}
	}
	
	public void registerUndoableListener(IUndoableListener listener) {
		_listeners.add(listener);
		
		if(_canUndo) {
			listener.notifyUndoHistoryChange(EUndoHistoryChange.UNDO_ENABLED);
		} else {
			listener.notifyUndoHistoryChange(EUndoHistoryChange.UNDO_DISABLED);
		}
		
		if(_canRedo) {
			listener.notifyUndoHistoryChange(EUndoHistoryChange.REDO_ENABLED);
		} else {
			listener.notifyUndoHistoryChange(EUndoHistoryChange.REDO_DISABLED);
		}
	}
	
	public void unregisterUndoableListener(IUndoableListener listener) {
		_listeners.remove(listener);
	}
	
	public void notifyListeners(EUndoHistoryChange undoHistoryChange) {
		for(IUndoableListener listener: _listeners) {
			listener.notifyUndoHistoryChange(undoHistoryChange);
		}
	}
	
	@Override
	public void historyNotification(OperationHistoryEvent event) {
		refreshHistory();
	}

}
