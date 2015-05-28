package sinbad2.domain.ui.handler.add;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.ui.DomainUI;
import sinbad2.domain.ui.DomainsUIsManager;

public class AddDomainHandler extends AbstractHandler {
	
	public static final String ID = "flintstones.domain.add";
	
	public AddDomainHandler() {}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DomainsUIsManager domainUIsManager = DomainsUIsManager.getInstance();
		DomainsManager domainsManager = DomainsManager.getInstance();
		DomainSet domainSet = domainsManager.getActiveDomainSet();
		
		String[] ids = domainUIsManager.getRegistersIDs();
		
		List<Object[]> input = new LinkedList<Object[]>();
		Domain domain;
		DomainUI domainUI;
		//Map<String, String> 
		
		return null;
	}

}
