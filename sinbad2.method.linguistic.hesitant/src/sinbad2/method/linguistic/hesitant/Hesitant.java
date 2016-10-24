package sinbad2.method.linguistic.hesitant;

import java.util.List;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.unbalanced.Unbalanced;
import sinbad2.method.MethodImplementation;
import sinbad2.method.linguistic.hesitant.nls.Messages;
import sinbad2.method.state.MethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.HesitantValuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class Hesitant extends MethodImplementation {
	
	public static final String ID = "flintstones.method.linguistic.hesitant"; //$NON-NLS-1$
	
	private static final String MULTIPLE_DOMAINS = Messages.Hesitant_Multiple_domains;
	private static final String NOT_SUPPORTED_DOMAINS = Messages.Hesitant_Not_supported_domains;
	private static final String NOT_HESITANT_VALUATIONS = Messages.Hesitant_Not_hesitant_evaluations;
	
	private DomainSet _domainSet;
	private ValuationSet _valuationSet;

	public Hesitant() {
		DomainsManager domainsManager = DomainsManager.getInstance();
		_domainSet = domainsManager.getActiveDomainSet();
		
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = valuationSetManager.getActiveValuationSet();
	}
	
	@Override
	public MethodImplementation newInstance() {
		return new Hesitant();
	}

	@Override
	public String isAvailable() {
		List<Domain> domains = _domainSet.getDomains();
		int numDomains = domains.size();
			
		if(numDomains > 1) {
			return MULTIPLE_DOMAINS;
		} else if(numDomains == 1) {
			if(domains.get(0) instanceof Unbalanced) {
				return NOT_SUPPORTED_DOMAINS;
			} else if(domains.get(0) instanceof FuzzySet) {
				for(ValuationKey vk: _valuationSet.getValuations().keySet()) {
					Valuation v = _valuationSet.getValuations().get(vk);
					if(!(v instanceof HesitantValuation)) {
						return NOT_HESITANT_VALUATIONS;
					}
				}
			}
		} else {
			return NOT_SUPPORTED_DOMAINS;
		}
		
		return ""; //$NON-NLS-1$
	}
	
	@Override
	public void notifyMethodStateChange(MethodStateChangeEvent event) {}

}
