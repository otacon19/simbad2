package sinbad2.domain.ui.handler.add;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.TwoPaneElementSelector;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.ui.DomainUIsManager;
import sinbad2.domain.ui.dialog.newDialog.NewDomainDialog;
import sinbad2.domain.ui.view.domain.provider.DomainLabelProvider;
import sinbad2.domain.ui.view.domain.provider.DomainValuationLabelProvider;
import sinbad2.domain.valuations.DomainValuationsManager;

public class AddDomainHandler extends AbstractHandler {
	
	public static final String ID = "flintstones.domain.add";
	
	public AddDomainHandler() {}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DomainUIsManager domainUIsManager = DomainUIsManager.getInstance();
		DomainsManager domainsManager = DomainsManager.getInstance();
		DomainValuationsManager domainValuationsManager = DomainValuationsManager.getInstance();
		
		String[] ids = domainUIsManager.getRegistersIDs();
		
		List<Object[]> input = new ArrayList<Object[]>();
		Domain domain;
		Map<String, String> valuationsSupported;
		
		for(String id: ids) {
			domain = domainsManager.getDomainImplementation(id);
			valuationsSupported = domainValuationsManager.getValuationsSupported();
			if(valuationsSupported != null) {
				input.add(new Object[] { domain, new String[] { id, valuationsSupported.get(id)}});
			}
		}
		
		ILabelProvider domainLabelProvider = new DomainLabelProvider();
		ILabelProvider domainValuationLabelProvider = new DomainValuationLabelProvider();
		TwoPaneElementSelector dialogTwoPanes = new TwoPaneElementSelector(Display.getCurrent().getActiveShell(), 
				domainLabelProvider, domainValuationLabelProvider);
		
		dialogTwoPanes.setTitle("New domain");
		dialogTwoPanes.setMessage("Domain");
		dialogTwoPanes.setElements(input.toArray());
		dialogTwoPanes.setUpperListLabel("Domains");
		dialogTwoPanes.setLowerListLabel("To assess valuations");
		dialogTwoPanes.setMultipleSelection(false);
		
		if(dialogTwoPanes.open() == Window.OK) {
			
			Object[] result = dialogTwoPanes.getResult();
			
			for(int i = 0; i < result.length; ++i) {
				Object[] selections = (Object[]) result[i];
				domain = (Domain) selections[0];
				domain = (Domain) domain.clone();
				
				NewDomainDialog newDomainDialog = null;
				
				List<String> dialogsIDs = domainValuationsManager.getValuationNewDomainDialogs(domainValuationsManager.
						getTypeOfValuation(domain.getId()));

				if(!dialogsIDs.isEmpty()) {
					String selected = null;
					
					if(dialogsIDs.size() == 1) {
						selected = dialogsIDs.get(0);
					}
					
					if(selected != null) {
						newDomainDialog = domainUIsManager.newDomainDialog(domain, selected);
						newDomainDialog.open();
					}
				}			
			}
		}
		
		return null;
	}

}
