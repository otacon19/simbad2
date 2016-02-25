package sinbad2.method.linguistic.multigranular.elh;

import java.util.HashMap;
import java.util.Map;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.method.MethodImplementation;
import sinbad2.method.state.MethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class ELH extends MethodImplementation {
	
	public static final String ID = "flintstones.method.linguistic.multigranular.elh";
	
	private static final String EVALUATIONS_IN_NOT_BLTS_DOMAINS = "Evaluations in not BLTS domains";
	private static final String EVALUATIONS_IN_NOT_LINGUISTIC_DOMAINS = "Evaluations iN not linguistic domains";
	private static final String EVALUATIONS_IN_DIFFERENT_DOMAINS_WITH_THE_SAME_CARDINALITY = "Evaluations in different domains with the same cardinality";
	private static final String NOT_SET_ALL_ASSIGNMENTS = "Not set all assignemnts";

	private ProblemElementsSet _elementsSet;
	private ValuationSet _valuationSet;
	
	public ELH() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
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

		for(Alternative alternative : _elementsSet.getAlternatives()) {
			for(Criterion criterion : _elementsSet.getAllCriteria()) {
				if(!criterion.hasSubcriteria()) {
					for(Expert expert : _elementsSet.getAllExperts()) {
						if(!expert.hasChildrens()) {
							Valuation v = _valuationSet.getValuation(expert, alternative, criterion);
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
		return "";
	}
}
