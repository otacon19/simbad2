package sinbad2.element.ui.handler.alternative.modify;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.alternative.operation.ModifyAlternativeOperation;

public class ModifyAlternativeHandler extends AbstractHandler {
	
	public static final String ID = "flintstones.element.alternative.modify"; //$NON-NLS-1$
	
	private Alternative _modifyAlternative;
	
	public ModifyAlternativeHandler() {}
	
	public ModifyAlternativeHandler(Alternative modifyAlternative) {
		this();
		_modifyAlternative = modifyAlternative;
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ProblemElementsManager elementManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementSet = elementManager.getActiveElementSet();
		
		Alternative alternative = _modifyAlternative;
		if(alternative == null) {
			IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
			alternative = (Alternative) selection.getFirstElement();
		}
		
		boolean doit = true;
		String oldId = alternative.getId();
		String newId = null;
		
		InputDialog dialog = new InputDialog(
				Display.getCurrent().getActiveShell(), "Modify alternative", "Insert alternative", 
				oldId, new ModifyAlternativeInputValidator(elementSet, oldId));
		
		if(dialog.open() == Window.OK) {
			newId = dialog.getValue();
			if(newId == oldId) {
				doit = false;
			}
		} else {
			doit = false;
		}
		
		if(doit) {
			IUndoableOperation operation = new ModifyAlternativeOperation("Modify alternative",elementSet, alternative, newId);
			IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
			
			operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
			operationHistory.execute(operation, null, null);
		}
		
		return null;
	}

}
