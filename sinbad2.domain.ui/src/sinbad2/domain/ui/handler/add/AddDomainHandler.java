package sinbad2.domain.ui.handler.add;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.TwoPaneElementSelector;

import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.ui.DomainUIsManager;
import sinbad2.domain.ui.view.domain.provider.DomainLabelProvider;
import sinbad2.domain.ui.view.domain.provider.DomainValuationLabelProvider;

public class AddDomainHandler extends AbstractHandler {
	
	public static final String ID = "flintstones.domain.add";
	
	public AddDomainHandler() {}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DomainUIsManager domainUIsManager = DomainUIsManager.getInstance();
		DomainsManager domainsManager = DomainsManager.getInstance();
		DomainSet domainSet = domainsManager.getActiveDomainSet();
		
		String[] ids = domainUIsManager.getRegistersIDs();
		
		System.out.println(ids.length);
		
		ILabelProvider domainLabelProvider = new DomainLabelProvider();
		ILabelProvider domainValuationLabelProvider = new DomainValuationLabelProvider();
		TwoPaneElementSelector dialogTwoPanes = new TwoPaneElementSelector(Display.getCurrent().getActiveShell(), 
				domainLabelProvider, domainValuationLabelProvider);
		
		dialogTwoPanes.setTitle("New domain");
		dialogTwoPanes.setMessage("Domain");
		//dialogTwoPanes.setElements();
		dialogTwoPanes.setUpperListLabel("Domains");
		dialogTwoPanes.setLowerListLabel("To assess valuations");
		dialogTwoPanes.setMultipleSelection(false);
		
		return null;
	}

}
