package flintstones.element.ui.handler.expert.add;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import flintstones.element.ElementSet;
import flintstones.element.ElementsManager;
import flintstones.element.expert.Expert;
import flintstones.element.expert.operation.AddExpertOperation;
import flintstones.element.ui.nls.Messages;


public class AddExpertHandler extends AbstractHandler {
	
	public final static String ID = "flintstones.element.expert.add"; //$NON-NLS-1$
	
	 public AddExpertHandler() {}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ElementsManager elementsManager = ElementsManager.getInstance();
		ElementSet elementSet = elementsManager.getActiveElementSet();
		
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
			AddExpertOperation operation = new AddExpertOperation(Messages.AddExpertHandler_Add_expert, id, parent, elementSet);
			operation.execute(null, null);
			
			//TODO operationHistory
			
		}
		
		return null;
	}
	

	
}
