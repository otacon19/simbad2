package sinbad2.domain.ui.handler.modify;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.handlers.HandlerUtil;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.operation.ModifyDomainOperation;
import sinbad2.domain.ui.DomainUIsManager;
import sinbad2.domain.ui.dialog.modifyDialog.ModifyDomainDialog;
import sinbad2.domain.valuations.DomainValuationsManager;

public class ModifyDomainHandler extends AbstractHandler {
	
	public static final String ID = "flintstones.domain.modify";
	
	private Domain _oldDomain;
	private String _valuationOldDomain;
	
	public ModifyDomainHandler() {}
	
	public ModifyDomainHandler(Domain oldDomain) {
		_oldDomain = oldDomain;
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DomainsManager domainsManager = DomainsManager.getInstance();
		DomainSet domainSet = domainsManager.getActiveDomainSet();
		DomainValuationsManager domainValuationsManager = DomainValuationsManager.getInstance();
	
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		_oldDomain = (Domain) selection.getFirstElement();
		_valuationOldDomain = domainValuationsManager.getValuationSupportedForNewDomain(_oldDomain.getId());
		
		DomainUIsManager domainUIsManager = DomainUIsManager.getInstance();
		String valuationID = domainValuationsManager.getValuationSupportedForNewDomain(_oldDomain.getId());
		
		List<String> dialogsIds = domainValuationsManager.getValuationModifyDomainDialogs(valuationID);
		
		String selected = "";
		if(dialogsIds.size() == 1) {
			selected = dialogsIds.get(0);
		} else {
			//TODO Diálogo para seleccionar varios diálogos de modificación
		}
		if(selected != null) {
			ModifyDomainDialog modifyDomainDialog = domainUIsManager.modifyDomainDialog(_oldDomain, selected);
			if(modifyDomainDialog.open() == Window.OK) {
				Domain newDomain = modifyDomainDialog.getNewDomain();
				
				domainValuationsManager.removeSupportedValuationForNewDomain(_oldDomain.getId());
				domainValuationsManager.addSupportedValuationForNewDomain(newDomain.getId(), _valuationOldDomain);
				
				IUndoableOperation operation = new ModifyDomainOperation("Modify domain", newDomain, _oldDomain, domainSet);
				IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
				
				operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
				operationHistory.execute(operation, null, null);
			}
		}
		
		return null;
	}


}
