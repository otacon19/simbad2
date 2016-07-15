package sinbad2.element.ui.handler.criterion.remove;

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
import sinbad2.element.criterion.Criterion;
import sinbad2.element.criterion.operation.RemoveCriterionOperation;
import sinbad2.element.ui.nls.Messages;

public class RemoveCriterionHandler extends AbstractHandler {
	
	public final static String ID = "flintstones.element.criterion.remove"; //$NON-NLS-1$
	
	public RemoveCriterionHandler() {}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ProblemElementsManager elementManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementSet = elementManager.getActiveElementSet();
		
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		Criterion criterion = (Criterion) selection.getFirstElement();
		
		IUndoableOperation operation = new RemoveCriterionOperation(Messages.RemoveCriterionHandler_Remove_criterion, criterion, elementSet);
		IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
		
		operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
		operationHistory.execute(operation, null, null);
		
		return null;
	}

}
