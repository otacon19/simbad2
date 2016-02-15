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
import sinbad2.method.MethodImplementation;
import sinbad2.method.state.MethodStateChangeEvent;

public class LH extends MethodImplementation {

	public static final String ID = "flintstones.method.linguistic.multigranular.lh";
	
	private DomainSet _domainsSet;
	
	private static final String EVALUATIONS_IS_NOT_A_BLTS_DOMAIN = "Evaluations is not a BLTS domain";
	private static final String EVALUATIONS_IS_NOT_A_LINGUISTIC_DOMAIN = "Evaluations is not a linguistic domain";
	private static final String EVALUATIONS_IN_DIFFERENT_DOMAINS_WITH_THE_SAME_CARDINALITY = "Evaluations in different domains with the same cardinality";
	private static final String IMPOSSIBLE_TO_BUILD_LINGUISTIC_HIERARCHY_TAKING_THE_DOMAINS_USED = "Impossible to build linguistic hierarchy taking the domains used";
	
	private class MyComparator implements Comparator<Object[]> {
		@Override
		public int compare(Object[] o1, Object[] o2) {
			return Double.compare((Integer) o1[0], (Integer) o2[0]);
		}
	}
	
	public LH() {
		DomainsManager domainsManager = DomainsManager.getInstance();
		_domainsSet = domainsManager.getActiveDomainSet();
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
					return EVALUATIONS_IS_NOT_A_BLTS_DOMAIN;
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
				return EVALUATIONS_IS_NOT_A_LINGUISTIC_DOMAIN;
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
		return "";
	}

}

