package sinbad2.domain.ui.handler.remove;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.operation.RemoveDomainOperation;
import sinbad2.domain.ui.nls.Messages;
import sinbad2.domain.ui.view.domain.DomainViewManager;

public class RemoveDomainHandler extends AbstractHandler {
	
	public static final String ID = "flintstones.domain.remove"; //$NON-NLS-1$
	
	public RemoveDomainHandler() {}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DomainsManager domainsManager = DomainsManager.getInstance();
		DomainSet domainSet = domainsManager.getActiveDomainSet();
		
		Domain domain = DomainViewManager.getInstance().getActiveDomain();
		
		IUndoableOperation operation = new RemoveDomainOperation(Messages.RemoveDomainHandler_Remove_domain, domain, domainSet);
		IOperationHistory operationHistoriy = OperationHistoryFactory.getOperationHistory();
		operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
		operationHistoriy.execute(operation, null, null);
		
		return null;
	}

}
