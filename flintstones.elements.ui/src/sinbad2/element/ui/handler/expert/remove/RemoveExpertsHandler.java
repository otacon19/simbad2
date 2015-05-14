package sinbad2.element.ui.handler.expert.remove;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.expert.Expert;
import sinbad2.element.expert.operation.RemoveExpertsOperation;
import sinbad2.element.ui.nls.Messages;

public class RemoveExpertsHandler extends AbstractHandler {
	
	public final static String ID = "flintstones.element.expert.remove"; //$NON-NLS-1$

	public RemoveExpertsHandler() {}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementSet = elementsManager.getActiveElementSet();
		
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		
		@SuppressWarnings("unchecked")
		List<Expert> experts = selection.toList();
		
		RemoveExpertsOperation operation = new RemoveExpertsOperation(
				Messages.RemoveExpertsHandler_Remove_multiple_experts, experts, elementSet);
		operation.execute(null, null);
		
		
		return null;
		
	}

}
