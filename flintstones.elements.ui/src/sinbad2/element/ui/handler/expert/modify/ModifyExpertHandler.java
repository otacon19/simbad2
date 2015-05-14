package sinbad2.element.ui.handler.expert.modify;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.expert.Expert;
import sinbad2.element.expert.operation.ModifyExpertOperation;
import sinbad2.element.ui.nls.Messages;

public class ModifyExpertHandler extends AbstractHandler {
	
	public final static String ID = "flintstones.element.expert.modify"; //$NON-NLS-1$
	
	public ModifyExpertHandler() {}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementSet = elementsManager.getActiveElementSet();
		
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		Expert expert = (Expert) selection.getFirstElement();
		Expert parent = expert.getParent();
		
		boolean doit = true;
		String oldId = expert.getId();
		String newId = null;
		
		InputDialog dialog = new InputDialog(Display.getCurrent().getActiveShell(), Messages.ModifyExpertHandler_Modify_expert, 
				Messages.ModifyExpertHandler_Insert_expert_id, expert.getId(), new ModifyExpertInputValidator(parent, elementSet, oldId));
		
		if(dialog.open() == Window.OK) {
			newId = dialog.getValue();
			if(newId.equals(oldId)) {
				doit = false;
			}
		} else {
			doit = false;
		}
		
		if(doit) {
			ModifyExpertOperation operation = new ModifyExpertOperation(Messages.ModifyExpertHandler_Modify_expert, expert, newId, elementSet);
			operation.execute(null, null);
		}
		
		return null;
	}

}
