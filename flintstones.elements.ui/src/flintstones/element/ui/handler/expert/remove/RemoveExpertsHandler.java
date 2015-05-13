package flintstones.element.ui.handler.expert.remove;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import flintstones.element.ElementSet;
import flintstones.element.ElementsManager;
import flintstones.element.expert.Expert;
import flintstones.element.expert.operation.RemoveExpertsOperation;
import flintstones.element.ui.nls.Messages;

public class RemoveExpertsHandler extends AbstractHandler {
	
	public final static String ID = "flintstones.element.expert.remove"; //$NON-NLS-1$

	public RemoveExpertsHandler() {}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ElementsManager elementsManager = ElementsManager.getInstance();
		ElementSet elementSet = elementsManager.getActiveElementSet();
		
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		
		@SuppressWarnings("unchecked")
		List<Expert> experts = selection.toList();
		
		RemoveExpertsOperation operation = new RemoveExpertsOperation(
				Messages.RemoveExpertsHandler_Remove_multiple_experts, experts, elementSet);
		operation.execute(null, null);
		
		
		return null;
		
	}

}
