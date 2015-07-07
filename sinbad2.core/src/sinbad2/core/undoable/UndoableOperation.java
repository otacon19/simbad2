package sinbad2.core.undoable;

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

public abstract class UndoableOperation extends AbstractOperation implements IOperationHistoryListener {

	private IOperationHistory _operationHistory;
	private static boolean _composite = false;
	protected boolean _inUndoRedo = false;

	public UndoableOperation(String label) {
		super(label);
		_operationHistory = OperationHistoryFactory.getOperationHistory();
	}

	public abstract IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException;

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		IStatus result;
		boolean isComposite = false;
		
		if (_composite == false) {
			addCompositeUndoableOperation(true);
			isComposite = true;
			_composite = true;
		}

		result = executeOperation(monitor, info);
		_inUndoRedo = true;

		if (isComposite) {
			_composite = false;
			_operationHistory.addOperationHistoryListener(this);
		}

		return result;
	}

	private void addCompositeUndoableOperation(boolean redo) {
		IUndoableOperation operation = new CompositeUndoableOperation(redo);
		IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();

		operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
		try {
			operationHistory.execute(operation, null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void historyNotification(OperationHistoryEvent event) {
		
		if(event.getEventType() == OperationHistoryEvent.OPERATION_ADDED) {
			_operationHistory.removeOperationHistoryListener(this);
			addCompositeUndoableOperation(false);
		}
	}

}
