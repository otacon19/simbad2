package sinbad2.method.linguistic.linguistic.twotuple;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.unbalanced.Unbalanced;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.method.MethodImplementation;
import sinbad2.method.linguistic.linguistic.twotuple.nls.Messages;
import sinbad2.method.state.MethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.HesitantValuation;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class TwoTuple extends MethodImplementation {
	
	public static final String ID = "flintstones.method.linguistic.linguistic.twotuple"; //$NON-NLS-1$
	
	private static final String EVALUATIONS_IN_NOT_LINGUISTIC_DOMAINS = Messages.TwoTuple_Evaluations_in_not_linguistic_domain;
	private static final String EVALUATIONS_IN_UNBALANCED_DOMAIN = Messages.TwoTuple_Evaluations_in_unbalanced_domain;
	private static final String EVALUATIONS_IN_DIFFERENT_DOMAINS = Messages.TwoTuple_Evaluations_in_different_domains;
	private static final String HESITANT_VALUATIONS = Messages.TwoTuple_Hesitant_evaluations;
	private static final String NOT_SET_ALL_ASSIGNMENTS = Messages.TwoTuple_Not_set_all_assignments;
	
	private ProblemElementsSet _elementsSet;
	private DomainSet _domainSet;
	private ValuationSet _valuationSet;
	
	public TwoTuple() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		DomainsManager domainsManager = DomainsManager.getInstance();
		_domainSet = domainsManager.getActiveDomainSet();
		
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = valuationSetManager.getActiveValuationSet();
	}

	@Override
	public MethodImplementation newInstance() {
		return new TwoTuple();
	}

	@Override
	public void notifyMethodStateChange(MethodStateChangeEvent event) {}

	@Override
	public String isAvailable() {
		String domainName = null;
		Domain generateDomain;
		
		for(Domain d: _domainSet.getDomains()) {
			if(!(d instanceof FuzzySet)) {
				return EVALUATIONS_IN_NOT_LINGUISTIC_DOMAINS;
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
										if(!(generateDomain instanceof FuzzySet)) {
											return EVALUATIONS_IN_NOT_LINGUISTIC_DOMAINS;
										} else {
											if(generateDomain instanceof Unbalanced) {
												return EVALUATIONS_IN_UNBALANCED_DOMAIN;
											}
										}
									} else {
										if(!domainName.equals(generateDomain.getId())) {
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

		return ""; //$NON-NLS-1$
	}
}
