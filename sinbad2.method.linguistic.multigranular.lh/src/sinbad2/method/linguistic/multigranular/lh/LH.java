package sinbad2.method.linguistic.multigranular.lh;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
import sinbad2.method.linguistic.multigranular.lh.nls.Messages;
import sinbad2.method.state.MethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.HesitantValuation;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class LH extends MethodImplementation {

	public static final String ID = "flintstones.method.linguistic.multigranular.lh"; //$NON-NLS-1$
	
	private DomainSet _domainsSet;
	
	private static final String EVALUATIONS_IN_NOT_BLTS_DOMAINS = Messages.LH_Evaluations_in_not_BLTS_domains;
	private static final String EVALUATIONS_IN_NOT_LINGUISTIC_DOMAIN = Messages.LH_Evaluations_in_not_linguistic_domain;
	private static final String EVALUATIONS_IN_DIFFERENT_DOMAINS_WITH_THE_SAME_CARDINALITY = Messages.LH_Evaluations_in_different_domains_with_the_same_cardinality;
	private static final String IMPOSSIBLE_TO_BUILD_LINGUISTIC_HIERARCHY_TAKING_THE_DOMAINS_USED = Messages.LH_Impossible_to_build_linguistic_hierarchy_taking_the_domains_used;
	private static final String HESITANT_VALUATIONS = Messages.LH_Hesitant_evaluations;
	private static final String NOT_SET_ALL_ASSIGNMENTS = Messages.LH_Not_set_all_assignments;
	
	private ProblemElementsSet _elementsSet;
	private ValuationSet _valuationSet;
	
	private class MyComparator implements Comparator<Object[]> {
		@Override
		public int compare(Object[] o1, Object[] o2) {
			return Double.compare((Integer) o1[0], (Integer) o2[0]);
		}
	}
	
	public LH() {
		DomainsManager domainsManager = DomainsManager.getInstance();
		_domainsSet = domainsManager.getActiveDomainSet();
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = valuationSetManager.getActiveValuationSet();
	}
	
	@Override
	public MethodImplementation newInstance() {
		return new LH();
	}

	@Override
	public void notifyMethodStateChange(MethodStateChangeEvent event) {}

	@Override
	public String isAvailable() {
		List<Object[]> validDomains = new LinkedList<Object[]>();
		Set<String> domainsNames = new HashSet<String>();
		Set<Integer> cardinalities = new HashSet<Integer>();
		int cardinality = 0;
		
		List<Domain> domains = _domainsSet.getDomains();
		for(Domain d: domains) {
			if(d instanceof FuzzySet) {
				if(!((FuzzySet) d).isBLTS()) {
					return EVALUATIONS_IN_NOT_BLTS_DOMAINS;
				} else {
					cardinality = ((FuzzySet) d).getLabelSet().getCardinality();
					if(cardinalities.contains(cardinality)) {
						return EVALUATIONS_IN_DIFFERENT_DOMAINS_WITH_THE_SAME_CARDINALITY;
					} else {
						cardinalities.add(cardinality);
						validDomains.add(new Object[] {cardinality, ((FuzzySet) d).getName(), d });
						domainsNames.add(((FuzzySet) d).getName());
					}
				}
			} else {
				return EVALUATIONS_IN_NOT_LINGUISTIC_DOMAIN;
			}
		}
		
		Collections.sort(validDomains, new MyComparator());
		Set<Integer> sizes = new HashSet<Integer>();
		int value, oldValue = -1, newLevel;

		for(Object[] entry : validDomains) {
			value = (Integer) entry[0];
			sizes.add(value);
			newLevel = (oldValue * 2) - 1;
			if(oldValue != value) {
				if((newLevel == value) || (oldValue == -1)) {
					oldValue = value;
				} else {
					while(value > newLevel) {
						newLevel = (newLevel * 2) - 1;
					}

					if(value == newLevel) {
						oldValue = value;
					} else {
						return IMPOSSIBLE_TO_BUILD_LINGUISTIC_HIERARCHY_TAKING_THE_DOMAINS_USED;
					}
				}
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

