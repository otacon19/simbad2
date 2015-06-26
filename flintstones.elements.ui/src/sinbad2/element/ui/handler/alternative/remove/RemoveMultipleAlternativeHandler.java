package sinbad2.element.ui.handler.alternative.remove;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.alternative.operation.RemoveMultipleAlternativesOperation;
import sinbad2.element.ui.nls.Messages;

public class RemoveMultipleAlternativeHandler extends AbstractHandler {

	public static final String ID = "flintstones.element.alternative.remove"; //$NON-NLS-1$
	
	public RemoveMultipleAlternativeHandler() {}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ProblemElementsManager elementManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementSet = elementManager.getActiveElementSet();
		
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		@SuppressWarnings("unchecked")
		List<Alternative> alternatives = selection.toList();
		
		IUndoableOperation operation = new RemoveMultipleAlternativesOperation(Messages.RemoveMultipleAlternativeHandler_Remove_multiple_alternatives, elementSet, alternatives);
		IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
		
		operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
		operationHistory.execute(operation, null, null);
		
		
		return null;
	}

}
