package sinbad2.method.linguistic.linguistic.twotuple;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.unbalanced.Unbalanced;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.method.MethodImplementation;
import sinbad2.method.state.MethodStateChangeEvent;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class TwoTuple extends MethodImplementation {
	
	public static final String ID = "flintstones.method.linguistic.linguistic.twotuple";
	
	private static final String EVALUATIONS_IN_NOT_LINGUISTIC_DOMAINS = "Evaluations in not linguistic domains";
	private static final String EVALUATIONS_IN_UNBALANCED_DOMAIN = "Evaluations in unbalanced domain";
	private static final String EVALUATIONS_IN_DIFFERENT_DOMAINS = "Evaluations in different domains";
	private static final String NOT_SET_ALL_ASSIGNMENTS = "Not set all assignments";
	
	private ProblemElementsSet _elementsSet;
	private ValuationSet _valuationSet;
	
	public TwoTuple() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
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
		
		for(Expert expert : _elementsSet.getExperts()) {
			if(_elementsSet.getElementExpertChildren(expert).size() == 0) {
				for(Criterion criterion : _elementsSet.getCriteria()) {
					if(_elementsSet.getElementCriterionSubcriteria(criterion).size() == 0) {
						for(Alternative alternative : _elementsSet.getAlternatives()) {
							generateDomain = _valuationSet.getValuation(expert, alternative, criterion).getDomain();
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
