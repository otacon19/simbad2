package sinbad2.element.ui.handler.expert.add;

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
import sinbad2.element.expert.Expert;
import sinbad2.element.expert.operation.AddExpertOperation;
import sinbad2.element.ui.nls.Messages;


public class AddExpertHandler extends AbstractHandler {
	
	public final static String ID = "flintstones.element.expert.add"; //$NON-NLS-1$
	
	 public AddExpertHandler() {}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementSet = elementsManager.getActiveElementSet();
		
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		Expert parent = (Expert) selection.getFirstElement();
		
		boolean doit = true;
		String id = null;
		
		AddExpertInputDialog dialog = new AddExpertInputDialog(Display.getCurrent().getActiveShell(), 
				Messages.AddExpertHandler_Add_expert, Messages.AddExpertHandler_Insert_id_expert, "", new AddExpertInputValidator(parent, elementSet), parent); //$NON-NLS-3$ //$NON-NLS-1$
		
		if(dialog.open() == Window.OK) {
			id = dialog.getValue();
			parent = dialog.isMember() ? parent : null;
		} else {
			doit = false;
		}
		
		if(doit) {	
			IUndoableOperation operation = new AddExpertOperation(Messages.AddExpertHandler_Add_expert, id, parent, elementSet);
			IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
			
			operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
			operationHistory.execute(operation, null, null);
			
		}
		
		return null;
	}
	

	
}
