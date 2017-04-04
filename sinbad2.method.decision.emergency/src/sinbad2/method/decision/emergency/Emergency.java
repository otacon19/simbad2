package sinbad2.method.decision.emergency;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.method.MethodImplementation;
import sinbad2.method.decision.emergency.nls.Messages;
import sinbad2.method.state.MethodStateChangeEvent;

public class Emergency extends MethodImplementation {

	public static final String ID = "flintstones.method.decision.emergency"; //$NON-NLS-1$
	
	private static final String NOT_SUPPORTED_DOMAINS = Messages.Emergency_Not_supported_domains;
	
	private DomainSet _domainSet;
	
	public Emergency() {
		DomainsManager domainsManager = DomainsManager.getInstance();
		_domainSet = domainsManager.getActiveDomainSet();
	}
	
	@Override
	public MethodImplementation newInstance() {
		return new Emergency();
	}
	
	@Override
	public void notifyMethodStateChange(MethodStateChangeEvent event) {}


	@Override
	public String isAvailable() {
		for(Domain d: _domainSet.getDomains()) {
			if(!(d instanceof NumericRealDomain)) {	
				return NOT_SUPPORTED_DOMAINS;
			}
		}
		
		return ""; //$NON-NLS-1$
	}

}
