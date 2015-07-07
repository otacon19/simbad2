package sinbad2.core.undoable.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.OperationHistoryFactory;

import sinbad2.core.undoable.HistoryState;

public class UndoOperationHandler extends AbstractHandler {

	public final static String ID = "sinbad2.workspace.undo"; //$NON-NLS-1$

	private HistoryState _historyState;
	private IOperationHistory _operationHistory;

	public UndoOperationHandler() {
		_historyState = HistoryState.getInstance();
		_operationHistory = OperationHistoryFactory.getOperationHistory();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		_operationHistory.undo(IOperationHistory.GLOBAL_UNDO_CONTEXT, null, null);
		_historyState.refreshHistory();
		return null;
	}

}

