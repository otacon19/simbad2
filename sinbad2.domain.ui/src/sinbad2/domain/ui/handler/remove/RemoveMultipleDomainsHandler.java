package sinbad2.domain.ui.handler.remove;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.operation.RemoveMultipleDomainsOperation;

public class RemoveMultipleDomainsHandler extends AbstractHandler {
	
	public static final String ID = "flintstones.domain.remove";
	
	public RemoveMultipleDomainsHandler() {}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DomainsManager domainsManager = DomainsManager.getInstance();
		DomainSet domainSet = domainsManager.getActiveDomainSet();
		
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		@SuppressWarnings("unchecked")
		List<Domain> domains = selection.toList();
		
		IUndoableOperation operation = new RemoveMultipleDomainsOperation("Remove multiple domains", domains, domainSet);
		IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
		operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
		operationHistory.execute(operation, null, null);
		
		return null;
	}

}
