package sinbad2.method.linguistic.multigranular.elh;

import java.util.HashMap;
import java.util.Map;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.method.MethodImplementation;
import sinbad2.method.linguistic.multigranular.elh.nls.Messages;
import sinbad2.method.state.MethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.HesitantValuation;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class ELH extends MethodImplementation {
	
	public static final String ID = "flintstones.method.linguistic.multigranular.elh"; //$NON-NLS-1$
	
	private static final String EVALUATIONS_IN_NOT_BLTS_DOMAINS = Messages.ELH_Evaluations_in_not_BLTS_domains;
	private static final String EVALUATIONS_IN_NOT_LINGUISTIC_DOMAINS = Messages.ELH_Evaluations_in_not_linguistic_domain;
	private static final String EVALUATIONS_IN_DIFFERENT_DOMAINS_WITH_THE_SAME_CARDINALITY = Messages.ELH_Evaluations_in_different_domains_with_the_same_cardinality;
	private static final String HESITANT_VALUATIONS = Messages.ELH_Hesitant_evaluations;
	private static final String NOT_SET_ALL_ASSIGNMENTS = Messages.ELH_Not_set_all_assignments;

	private ProblemElementsSet _elementsSet;
	private DomainSet _domainSet;
	private ValuationSet _valuationSet;
	
	public ELH() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		DomainsManager domainsManager = DomainsManager.getInstance();
		_domainSet = domainsManager.getActiveDomainSet();
		
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = valuationSetManager.getActiveValuationSet();
	}
	
	@Override
	public MethodImplementation newInstance() {
		return new ELH();
	}
	
	@Override
	public void notifyMethodStateChange(MethodStateChangeEvent event) {}
	
	@Override
	public String isAvailable() {	
		Map<String, Domain> domains = new HashMap<String, Domain>();
		Map<Integer, String> cardinalities = new HashMap<Integer, String>();
		String domainName;

		Domain generateDomain;

		for(Domain d: _domainSet.getDomains()) {
			if(!(d instanceof FuzzySet)) {
				return EVALUATIONS_IN_NOT_LINGUISTIC_DOMAINS;
			}
		}
		
		for(Alternative alternative : _elementsSet.getAlternatives()) {
			for(Criterion criterion : _elementsSet.getAllCriteria()) {
				if(!criterion.hasSubcriteria()) {
					for(Expert expert : _elementsSet.getAllExperts()) {
						if(!expert.hasChildren()) {
							Valuation v = _valuationSet.getValuation(expert, alternative, criterion);
							
							if(v instanceof HesitantValuation) {
								return HESITANT_VALUATIONS;
							}
							
							if(v != null) {
								generateDomain = v.getDomain();
								if(generateDomain != null) {
									domainName = generateDomain.getId();
									if(generateDomain instanceof FuzzySet) {
										if(((FuzzySet) generateDomain).isBLTS()) {
											if(!domains.containsKey(domainName)) {
												domains.put(domainName, generateDomain);
												if(cardinalities.get(((FuzzySet) generateDomain).getLabelSet().getCardinality()) == null) {
													cardinalities.put(((FuzzySet) generateDomain).getLabelSet().getCardinality(), domainName);
												} else {
													if(!domainName.equals(cardinalities.get(((FuzzySet) generateDomain).getLabelSet().getCardinality()))) {
														return EVALUATIONS_IN_DIFFERENT_DOMAINS_WITH_THE_SAME_CARDINALITY;
													}
												}
											}
										} else {
											return EVALUATIONS_IN_NOT_BLTS_DOMAINS;
										}
									} else {
										return EVALUATIONS_IN_NOT_LINGUISTIC_DOMAINS;
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
