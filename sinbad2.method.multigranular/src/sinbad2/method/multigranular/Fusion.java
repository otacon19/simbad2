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
import sinbad2.method.multigranular.nls.Messages;
import sinbad2.method.state.MethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.HesitantValuation;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class Fusion extends MethodImplementation {
	
	public static final String ID = "flintstones.method.linguistic.multigranular.fusion"; //$NON-NLS-1$
	
	private static final String EVALUATIONS_IN_NOT_BLTS_DOMAIN = Messages.Fusion_Evaluations_in_not_BLTS_domain;
	private static final String EVALUATIONS_IN_NOT_LINGUISTIC_DOMAIN = Messages.Fusion_Evaluations_in_not_linguistic_domain;
	private static final String HESISTANT_VALUATIONS = Messages.Fusion_Hesitant_evaluations;
	private static final String NOT_SET_ALL_ASSIGNMENTS = Messages.Fusion_Not_set_all_assignments;
	
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
							if(v instanceof HesitantValuation) { 
								return HESISTANT_VALUATIONS;
							} else if(v == null) {
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
