package sinbad2.element.ui.handler.criterion.modify;


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
import sinbad2.element.criterion.operation.ModifyCriterionOperation;
import sinbad2.element.ui.nls.Messages;

public class ModifyCriterionHandler extends AbstractHandler {
	
	public final static String ID = "flintstones.element.criterion.modify"; //$NON-NLS-1$
	
	private Criterion _criterion = null;
	
	public ModifyCriterionHandler() {}
	
	public ModifyCriterionHandler(Criterion criterion) {
		this();
		
		_criterion = criterion;
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ProblemElementsManager elementManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementSet = elementManager.getActiveElementSet();
		
		Criterion criterion = _criterion;
		if(_criterion == null) {
			IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
			criterion = (Criterion) selection.getFirstElement();
		}
		
		Criterion parent = criterion.getParent();
		
		boolean doit = true;
		String oldId = criterion.getId();
		String newId = null;
		boolean oldCost = criterion.isCost();
		boolean newCost = false;
		
		ModifyCriterionInputDialog dialog = new ModifyCriterionInputDialog(Display.getCurrent().getActiveShell(), Messages.ModifyCriterionHandler_Modify_criterion, 
				Messages.ModifyCriterionHandler_Insert_criterion_id, criterion.getId(), criterion.isCost(), new ModifyCriterionInputValidator(parent, 
						criterion.getId(), elementSet));
		
		if(dialog.open() == Window.OK) {
			newId = dialog.getValue();
			newCost = dialog.isCost();
			if(newId.equals(oldId) && (oldCost == newCost)) {
				doit = false;
			}
		} else {
			doit = false;
		}
		
		if(doit) {
			IUndoableOperation operation = new ModifyCriterionOperation(Messages.ModifyCriterionHandler_Modify_criterion, criterion, newId, newCost, elementSet);
			IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
			
			operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
			operationHistory.execute(operation, null, null);
		}
		
		return null;
		
	}

}
