package flintstones.element.ui.handler.expert.remove;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import flintstones.element.ElementSet;
import flintstones.element.ElementsManager;
import flintstones.element.expert.Expert;
import flintstones.element.expert.operation.RemoveExpertOperation;
import flintstones.element.ui.nls.Messages;

public class RemoveExpertHandler extends AbstractHandler {
	
	public final static String ID = "flintstones.element.expert.remove"; //$NON-NLS-1$
	
	public RemoveExpertHandler() {}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ElementsManager elementsManager = ElementsManager.getInstance();
		ElementSet elementSet = elementsManager.getActiveElementSet();
		
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		Expert expert = (Expert) selection.getFirstElement();
		
		RemoveExpertOperation operation = new RemoveExpertOperation(Messages.RemoveExpertHandler_Remove_expert, expert, elementSet);
		operation.execute(null, null);
		
		
		return null;
	}

}
