package undoable;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class CompositeUndoableOperation extends AbstractOperation implements IOperationHistoryListener {
	
	private IOperationHistory _operationHistory;
	private int _iterations;
	private boolean _redo;
	
	public CompositeUndoableOperation(boolean redo) {
		super("");
		_operationHistory = OperationHistoryFactory.getOperationHistory();
		_iterations = 0;
		_redo = redo;
	}

	@Override
	public void historyNotification(OperationHistoryEvent event) {
		
		try {
			if(event.getEventType() == OperationHistoryEvent.UNDONE) {
				_operationHistory.removeOperationHistoryListener(this);
				if(!_redo) {
					for(int i = 0; i < _iterations; ++i) {
						_operationHistory.undo(IOperationHistory.GLOBAL_UNDO_CONTEXT, null, null);
					}
				}
			} else if(event.getEventType() == OperationHistoryEvent.REDONE) {
				if(_redo) {
					_operationHistory.removeOperationHistoryListener(this);
					IUndoableOperation operation = null;
					boolean exit = false;
					do {
						if(_operationHistory.canRedo(IOperationHistory.GLOBAL_UNDO_CONTEXT)) {
							operation = _operationHistory.getRedoOperation(IOperationHistory.GLOBAL_UNDO_CONTEXT);
						} else {
							exit = true;
							operation = null;
						}
						
						if(!exit) {
							if(operation instanceof CompositeUndoableOperation) {
								exit = true;
							}
						}
						
						if(operation != null) {
							_operationHistory.redo(IOperationHistory.GLOBAL_UNDO_CONTEXT, null, null);
						}
					} while (!exit);
				}
			}
		} catch (ExecutionException e) {
			
		}
		
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		if(!_redo) {
			IUndoableOperation[] history = _operationHistory.getUndoHistory(IOperationHistory.GLOBAL_UNDO_CONTEXT);
			int pos = history.length - 1;
			boolean find = false;
			do {
				if(pos >= 0) {
					if(history[pos] instanceof CompositeUndoableOperation) {
						find = true;
					} else {
						pos--;
					}
					_iterations++;
				}
			} while ((!find) && (pos >= 0));
			
			if(_iterations == 1) {
				_iterations = 0;
			}
		}
		
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		if(_redo) {
			_operationHistory.addOperationHistoryListener(this);
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		if(!_redo) {
			_operationHistory.addOperationHistoryListener(this);
		}
		
		return Status.OK_STATUS;
	}
	

}
