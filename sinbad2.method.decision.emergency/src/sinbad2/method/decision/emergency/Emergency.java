package sinbad2.method.decision.emergency;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.method.MethodImplementation;
import sinbad2.method.decision.emergency.nls.Messages;
import sinbad2.method.state.MethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class Emergency extends MethodImplementation {

	public static final String ID = "flintstones.method.decision.emergency"; //$NON-NLS-1$
	
	private static final String NOT_SUPPORTED_DOMAINS = Messages.Emergency_Not_supported_domains;
	private static final String NOT_SET_ALL_ASSIGNMENTS = Messages.Emergency_Not_set_all_assignments;
	
	private DomainSet _domainSet;
	private ProblemElementsSet _elementsSet;
	private ValuationSet _valuationSet;
	
	public Emergency() {
		DomainsManager domainsManager = DomainsManager.getInstance();
		_domainSet = domainsManager.getActiveDomainSet();
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
	
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = valuationSetManager.getActiveValuationSet();
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
		
		for(Expert expert : _elementsSet.getAllExperts()) {
			if(!expert.hasChildren()) {
				for(Criterion criterion : _elementsSet.getAllCriteria()) {
					if(!criterion.hasSubcriteria()) {
						for(Alternative alternative : _elementsSet.getAlternatives()) {
							Valuation v = _valuationSet.getValuation(expert, alternative, criterion);
							if(v == null) {
								return NOT_SET_ALL_ASSIGNMENTS;
							}
						}
					}
				}
			}
		}
		
		return ""; //$NON-NLS-1$
	}

}
