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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.operation.ModifyDomainOperation;
import sinbad2.domain.ui.DomainUIsManager;
import sinbad2.domain.ui.dialog.modifyDialog.ModifyDomainDialog;
import sinbad2.domain.ui.dialog.selectdialog.SelectModifyDomainDialog;
import sinbad2.domain.ui.nls.Messages;
import sinbad2.domain.valuations.DomainsValuationsManager;

public class ModifyDomainHandler extends AbstractHandler {
	
	public static final String ID = "flintstones.domain.modify"; //$NON-NLS-1$
	
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
		DomainsValuationsManager domainValuationsManager = DomainsValuationsManager.getInstance();
		
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		_oldDomain = (Domain) selection.getFirstElement();
		_valuationOldDomain = domainValuationsManager.getValuationSupportedForSpecificDomain(_oldDomain.getId());
				
		DomainUIsManager domainUIsManager = DomainUIsManager.getInstance();
		String valuationID = domainValuationsManager.getValuationSupportedForSpecificDomain(_oldDomain.getId());
		
		List<String> dialogsIds = domainValuationsManager.getValuationModifyDomainDialogs(valuationID);
		
		String dialogIDSelected = ""; //$NON-NLS-1$
		if(dialogsIds.size() == 1) {
			dialogIDSelected = dialogsIds.get(0);
		} else {
			SelectModifyDomainDialog dialog = new SelectModifyDomainDialog(Display.getCurrent().getActiveShell(), dialogsIds);
			
			if(dialog.open() == Window.OK) {
				for(String dialogID: dialogsIds) {
					String description = domainUIsManager.getDescriptionModifyDomainDialog(dialogID);
					if(description == dialog.getDescription()) {
						dialogIDSelected = dialogID;
					}
				}
			}
		}

		if(dialogIDSelected != null && !dialogIDSelected.isEmpty()) {
			ModifyDomainDialog modifyDomainDialog = domainUIsManager.modifyDomainDialog(_oldDomain, dialogIDSelected);
			if(modifyDomainDialog.open() == Window.OK) {
				Domain newDomain = modifyDomainDialog.getNewDomain();
				
				domainValuationsManager.removeSupportedValuationForSpecificDomain(_oldDomain.getId());
				domainValuationsManager.addSupportedValuationForSpecificDomain(newDomain.getId(), _valuationOldDomain);
				
				IUndoableOperation operation = new ModifyDomainOperation(Messages.ModifyDomainHandler_Modify_domain, newDomain, _oldDomain, domainSet);
				IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
				
				operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
				operationHistory.execute(operation, null, null);
			}
		}
		
		return null;
	}


}
