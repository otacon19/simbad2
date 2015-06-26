package sinbad2.element.ui.handler.alternative.add;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.operation.AddAlternativeOperation;
import sinbad2.element.ui.nls.Messages;

public class AddAlternativeHandler extends AbstractHandler {
	
	public final static String ID = "flintstones.element.alternative.add"; //$NON-NLS-1$
	
	public AddAlternativeHandler() {}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ProblemElementsManager elementManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementSet = elementManager.getActiveElementSet();
		
		boolean doit = true;
		String id = null;
		
		AddAlternativeInputDialog dialog = new AddAlternativeInputDialog(
				Display.getCurrent().getActiveShell(), Messages.AddAlternativeHandler_Add_alternative, 
				Messages.AddAlternativeHandler_Insert_alternative, "", new AddAlternativeInputValidator(elementSet)); //$NON-NLS-2$ //$NON-NLS-1$
		
		if(dialog.open() == Window.OK) {
			id = dialog.getValue();
		} else {
			doit = false;
		}
		
		if(doit) {
			IUndoableOperation operation = new AddAlternativeOperation(Messages.AddAlternativeHandler_Add_alternative, elementSet, id);
			IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
			
			operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
			operationHistory.execute(operation, null, null);
		}
		
		return null;
	}

}
