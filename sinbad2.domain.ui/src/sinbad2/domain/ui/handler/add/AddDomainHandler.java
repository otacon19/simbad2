package sinbad2.domain.ui.handler.add;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.TwoPaneElementSelector;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.operation.AddDomainOperation;
import sinbad2.domain.ui.DomainUIsManager;
import sinbad2.domain.ui.dialog.newDialog.NewDomainDialog;
import sinbad2.domain.ui.dialog.selectdialog.SelectNewDomainDialog;
import sinbad2.domain.ui.nls.Messages;
import sinbad2.domain.ui.view.domain.provider.DomainLabelProvider;
import sinbad2.domain.ui.view.domain.provider.DomainValuationLabelProvider;
import sinbad2.domain.valuations.DomainsValuationsManager;

public class AddDomainHandler extends AbstractHandler {
	
	public static final String ID = "flintstones.domain.add"; //$NON-NLS-1$
	
	private DomainUIsManager _domainUIsManager;
	private DomainsManager _domainsManager;
	private DomainSet _domainSet;
	private DomainsValuationsManager _domainValuationsManager;	
	
	public AddDomainHandler() {
		_domainUIsManager = DomainUIsManager.getInstance();
		_domainsManager = DomainsManager.getInstance();
		_domainSet = _domainsManager.getActiveDomainSet();
		_domainValuationsManager = DomainsValuationsManager.getInstance();
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		TwoPaneElementSelector dialogTwoPanes = createTwoPanes();
		
		if(dialogTwoPanes.open() == Window.OK) {
			Domain domain;
			Object[] result = dialogTwoPanes.getResult();
		
			for(int i = 0; i < result.length; ++i) {
				Object[] selections = (Object[]) result[i];
				domain = (Domain) selections[0];
				domain = (Domain) domain.clone();

				NewDomainDialog newDomainDialog = null;
								
				String dialogIDSelected = getSelectedDomainDialog(selections);
					
				if(dialogIDSelected != null && !dialogIDSelected.isEmpty()) {
					newDomainDialog = _domainUIsManager.newDomainDialog(domain, dialogIDSelected);
					if(newDomainDialog.open() == Window.OK) {
						domain = newDomainDialog.getDomain();
						
						_domainValuationsManager.addSupportedValuationForSpecificDomain(domain.getId(), ((String[]) selections[1])[0]);
						
						IUndoableOperation operation = new AddDomainOperation(Messages.AddDomainHandler_Add_domain, domain, _domainSet);
						IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
						operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
						operationHistory.execute(operation, null, null);
					}
				}
			}			
		}
		
		return null;
	}
	
	private TwoPaneElementSelector createTwoPanes() {
		List<String> valuationsSupported;
		List<Object[]> input = new ArrayList<Object[]>();
		Domain domain;
		
		String[] ids = _domainUIsManager.getRegistersIDs();
	
		for(String id: ids) {
			domain = _domainsManager.getDomainImplementation(id);
			valuationsSupported = _domainValuationsManager.getValuationsSupportedForDomain(id);
			if(valuationsSupported != null) {
				for(String valuationID: valuationsSupported) {
					if(_domainValuationsManager.hasValuationsNewDomainDialogs(valuationID)) {
						input.add(new Object[] { domain, new String[] { valuationID, _domainValuationsManager.getNameValuation(valuationID)}});		
					}
				}
			}
		}
		
		return fillTwoPanes(input);	
	}
	
	private TwoPaneElementSelector fillTwoPanes(List<Object[]> input) {
		ILabelProvider domainLabelProvider = new DomainLabelProvider();
		ILabelProvider domainValuationLabelProvider = new DomainValuationLabelProvider();
		TwoPaneElementSelector dialogTwoPanes = new TwoPaneElementSelector(Display.getCurrent().getActiveShell(), domainLabelProvider, domainValuationLabelProvider);
		
		dialogTwoPanes.setTitle(Messages.AddDomainHandler_New_domain);
		dialogTwoPanes.setMessage(Messages.AddDomainHandler_Domain);
		dialogTwoPanes.setElements(input.toArray());
		dialogTwoPanes.setUpperListLabel(Messages.AddDomainHandler_Domains);
		dialogTwoPanes.setLowerListLabel(Messages.AddDomainHandler_To_assess_valuations);
		dialogTwoPanes.setMultipleSelection(false);
		
		return dialogTwoPanes;	
	}
	
	private String getSelectedDomainDialog(Object[] selections) {
		List<String> dialogsIDs = _domainValuationsManager.getValuationNewDomainDialogs(((String[]) selections[1])[0]);
		String dialogIDSelected = null;
		
		if(!dialogsIDs.isEmpty()) {
			if(dialogsIDs.size() == 1) {
				dialogIDSelected = dialogsIDs.get(0);
			} else {
				SelectNewDomainDialog dialog = new SelectNewDomainDialog(Display.getCurrent().getActiveShell(), dialogsIDs);
												
				if(dialog.open() == Window.OK) {
					for(String dialogID: dialogsIDs) {
						String description = _domainUIsManager.getDescriptionNewDomainDialog(dialogID);
						if(description == dialog.getDescription()) {
							dialogIDSelected = dialogID;
						}
					}
				}
			}
		}
		
		return dialogIDSelected;
	}

}
