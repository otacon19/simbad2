package sinbad2.method.linguistic.unbalanced.methodology;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.method.MethodImplementation;
import sinbad2.method.state.MethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.HesitantValuation;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class Unbalanced extends MethodImplementation {
	
	public static final String ID = "flintstones.method.linguistic.unbalanced.methodology";
	
	private static final String EVALUATIONS_IN_NOT_UNBALANCED_DOMAIN = "Evaluations in not unbalanced domain";
	private static final String EVALUATIONS_IN_DIFFERENT_DOMAINS = "Evaluations in different domains";
	private static final String HESITANT_VALUATIONS = "Hesitant evaluations";
	private static final String NOT_SET_ALL_ASSIGNMENTS = "Not set all assignments";
	
	private DomainSet _domainSet;
	private ProblemElementsSet _elementsSet;
	private ValuationSet _valuationSet;
	
	public Unbalanced() {
		DomainsManager domainsManager = DomainsManager.getInstance();
		_domainSet = domainsManager.getActiveDomainSet();
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = valuationSetManager.getActiveValuationSet();
	}

	@Override
	public void notifyMethodStateChange(MethodStateChangeEvent event) {}

	@Override
	public MethodImplementation newInstance() {
		return new Unbalanced();
	}

	@Override
	public String isAvailable() {
		Domain generateDomain = null;
		String domainName = null;
		
		for(Domain d: _domainSet.getDomains()) {
			if(!(d instanceof sinbad2.domain.linguistic.unbalanced.Unbalanced)) {
				return EVALUATIONS_IN_NOT_UNBALANCED_DOMAIN;
			}
		}
		
		for(Expert expert : _elementsSet.getAllExperts()) {
			if(!expert.hasChildren()) {
				for(Criterion criterion : _elementsSet.getAllCriteria()) {
					if(!criterion.hasSubcriteria()) {
						for(Alternative alternative : _elementsSet.getAlternatives()) {
							Valuation v = _valuationSet.getValuation(expert, alternative, criterion);
							
							if(v instanceof HesitantValuation) {
								return HESITANT_VALUATIONS;
							}
							
							if(v != null) {
								generateDomain = v.getDomain();
								if(generateDomain != null) {
									if(domainName == null) {
										domainName = generateDomain.getId();
									} else {
										if (!domainName.equals((String) generateDomain.getId())) {
											return EVALUATIONS_IN_DIFFERENT_DOMAINS;
										}
									}
								}
							} else {
								return NOT_SET_ALL_ASSIGNMENTS;
							}
						}
					}
				}
			}
		}

		return "";
	}
}
