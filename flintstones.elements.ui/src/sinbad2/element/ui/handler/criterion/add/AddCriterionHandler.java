package sinbad2.element.ui.handler.criterion.add;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.criterion.operation.AddCriterionOperation;
import sinbad2.element.ui.nls.Messages;

public class AddCriterionHandler extends AbstractHandler {
	
	public final static String ID = "flintstones.element.criterion.add"; //$NON-NLS-1$
	
	public AddCriterionHandler() {}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ProblemElementsManager elementManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementSet = elementManager.getActiveElementSet();
		
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		Criterion parent = (Criterion) selection.getFirstElement();
		
		boolean doit = true;
		String id = null;
		boolean cost = true;
		
		AddCriterionInputDialog dialog = new AddCriterionInputDialog(
				Display.getCurrent().getActiveShell(), Messages.AddCriterionHandler_Add_criterion,
				Messages.AddCriterionHandler_Insert_criterion_id, "", new AddCriterionInputValidator(parent, elementSet), parent); //$NON-NLS-2$
		
		if(dialog.open() == Window.OK) {
			id = dialog.getValue();
			cost = dialog.isCost();
			parent = dialog.isSubcriterion() ? parent : null;
		} else {
			doit = false;
		}
		
		if(doit) {
			IUndoableOperation operation = new AddCriterionOperation(Messages.AddCriterionHandler_Add_criterion, id, cost, parent, elementSet);
			IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
			
			operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
			operationHistory.execute(operation, null, null);
		}
		
		return null;
	}

}
