package sinbad2.method.multigranular;

import java.util.List;

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
import sinbad2.method.state.MethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class Fusion extends MethodImplementation {
	
	public static final String ID = "flintstones.method.linguistic.multigranular.fusion";
	
	private static final String EVALUATIONS_IN_NOT_BLTS_DOMAIN = "Evaluations in not BLTS domain";
	private static final String EVALUATIONS_IN_NOT_LINGUISTIC_DOMAIN = "Evaluations in not linguistic domain";
	private static final String NOT_SET_ALL_ASSIGNMENTS = "Not set all assignments";
	
	private DomainSet _domainsSet;
	private ProblemElementsSet _elementsSet;
	private ValuationSet _valuationSet;
	
	public Fusion() {
		DomainsManager domainsManager = DomainsManager.getInstance();
		_domainsSet = domainsManager.getActiveDomainSet();
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = valuationSetManager.getActiveValuationSet();
	}
	
	@Override
	public MethodImplementation newInstance() {
		return new Fusion();
	}

	@Override
	public void notifyMethodStateChange(MethodStateChangeEvent event) {}

	@Override
	public String isAvailable() {		
		List<Domain> domains = _domainsSet.getDomains();
		for(Domain d: domains) {
			if(d instanceof FuzzySet) {
				if(!((FuzzySet) d).isBLTS()) {
					return EVALUATIONS_IN_NOT_BLTS_DOMAIN;
				}
			} else {
				return EVALUATIONS_IN_NOT_LINGUISTIC_DOMAIN;
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
		
		return "";
	}
}
