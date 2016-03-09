package sinbad2.phasemethod.multigranular.elh.unification;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.linguistic.LinguisticValuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class UnificationPhase implements IPhaseMethod {
	
	private class MyComparator implements Comparator<Object[]> {
		@Override
		public int compare(Object[] o1, Object[] o2) {
			return Double.compare((Integer) o1[0], (Integer) o2[0]);
		}
	}
	
	public static final String ID = "flintstones.phasemethod.multigranular.elh.unification";

	private Map<ValuationKey, Valuation> _unifiedValuationsResult;
	
	private List<Object[]> _elhDomains;
	
	private FuzzySet _unifiedDomain;
	
	private ValuationSet _valuationSet;
	
	public UnificationPhase() {
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = valuationSetManager.getActiveValuationSet();
		
		_unifiedValuationsResult = new HashMap<ValuationKey, Valuation>();
		_elhDomains = new LinkedList<Object[]>();
		_unifiedDomain = null;	
	}
	
	public Map<ValuationKey, Valuation> getUnifiedValuationsResult() {
		return _unifiedValuationsResult;
	}
	
	public void setUnifiedValuationsResult(Map<ValuationKey, Valuation> unifiedValuationsResult) {
		_unifiedValuationsResult = unifiedValuationsResult;
	}
	
	public List<Object[]> getELHDomains() {
		return _elhDomains;
	}
	
	public void setELHDomains(List<Object[]> elhDomains) {
		_elhDomains = elhDomains;
	}
	
	public FuzzySet getUnifiedDomain() {
		return _unifiedDomain;
	}
	
	public void setUnifiedDomain(FuzzySet unifiedDomain) {
		_unifiedDomain = unifiedDomain;
	}
	
	@Override
	public IPhaseMethod copyStructure() {
		return new UnificationPhase();
	}

	@Override
	public void copyData(IPhaseMethod iMethodPhase) {
		UnificationPhase unification = (UnificationPhase) iMethodPhase;

		clear();

		_elhDomains = unification.getELHDomains();
		_unifiedDomain = unification.getUnifiedDomain();
		_unifiedValuationsResult = unification.getUnifiedValuationsResult();
	}

	@Override
	public void clear() {
		_elhDomains.clear();
		_unifiedDomain = null;
		_unifiedValuationsResult.clear();
	}

	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		if (event.getChange().equals(EPhaseMethodStateChange.ACTIVATED)) {
			activate();
		}
	}

	@Override
	public IPhaseMethod clone() {
		UnificationPhase result = null;

		try {
			result = (UnificationPhase) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public void activate() {}

	@Override
	public boolean validate() {

		if (_valuationSet.getValuations().isEmpty()) {
			return false;
		}

		return true;
	}

	public Map<ValuationKey, Valuation> unification(FuzzySet unifiedDomain) {
		_unifiedValuationsResult = new HashMap<ValuationKey, Valuation>();
		
		if (unifiedDomain != null) {
			Criterion criterion;
			Valuation valuation;
			Boolean isCost;

			Map<ValuationKey, Valuation> valuations = _valuationSet.getValuations();
			for(ValuationKey vk : valuations.keySet()) {
				criterion = vk.getCriterion();
				valuation = valuations.get(vk);
				isCost = criterion.getCost();

				if(valuation instanceof TwoTuple) {
					if(isCost) {
						valuation = valuation.negateValuation();
					}
					valuation = ((TwoTuple) valuation).transform(unifiedDomain);
				} else if(valuation instanceof LinguisticValuation) {
					if(isCost) {
						valuation = valuation.negateValuation();
					}
	
					valuation = new TwoTuple((FuzzySet) valuation.getDomain(), ((LinguisticValuation) valuation).getLabel()).transform(unifiedDomain);
				} else {
					throw new IllegalArgumentException();
				}
				
				_unifiedValuationsResult.put(vk, valuation);
			}
		}
		return _unifiedValuationsResult;
	}
	
	public List<Object[]> generateLH() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
		
		_elhDomains = new LinkedList<Object[]>();
		
		List<Object[]> domains = new LinkedList<Object[]>();
		Set<String> domainsNames = new HashSet<String>();
		int i = 1, cardinality;

		String domainName;
		Domain generateDomain, domain;
		
		for(Alternative alternative : elementsSet.getAlternatives()) {
			for(Criterion criterion : elementsSet.getAllCriteria()) {
				if(!criterion.hasSubcriteria()) {
					for(Expert expert : elementsSet.getAllExperts()) {
						if(!expert.hasChildren()) {
							Valuation v = _valuationSet.getValuation(expert, alternative, criterion);
							if(v != null) {
								generateDomain = v.getDomain();
								if(generateDomain != null) {
									domainName = generateDomain.getId();
									if(generateDomain instanceof FuzzySet) {
										if(!domainsNames.contains(domainName)) {
											if(((FuzzySet) generateDomain).isBLTS()) {
												cardinality = ((FuzzySet) generateDomain).getLabelSet().getCardinality();
												domains.add(new Object[] { cardinality, domainName, generateDomain });
												domainsNames.add(domainName);
											} 
										}
									} else {
										return null;
									}
								}
							}
						}
					}
				}
			}
		}

		Collections.sort(domains, new MyComparator());
		
		Integer value;
		i = 0;
		Set<Integer> sizes = new HashSet<Integer>();
		int oldValue = -1;
		for(Object[] entry : domains) {
			value = (Integer) entry[0];
			sizes.add(value);
			if(oldValue != value) {
				oldValue = value;
				i++;
			}
			entry[0] = "l(" + i + "," + value + ")";
		}
		
		_elhDomains.addAll(domains);

		String generate = "generate";
		domain = generateUnifiedDomain(sizes);
		int index;
		if(domain != null) {
			domainName = generate;
			index = 1;
			while(domainsNames.contains(domainName)) {
				domainName = generate + "_" + index++;
			}
			cardinality = ((FuzzySet) domain).getLabelSet().getCardinality();
			if(cardinality != oldValue) {
				i++;
			}
			
			_elhDomains.add(new Object[] { "l(" + i + "," + cardinality + ")", domainName, domain });
			domainsNames.add(domainName);
		}
		
		_unifiedDomain = (FuzzySet) _elhDomains.get(_elhDomains.size() - 1)[2];
		
		return _elhDomains;
	}

	private FuzzySet generateUnifiedDomain(Set<Integer> sizes) {

		FuzzySet result = null;

		if(sizes.size() != 0) {
			Integer[] formerModalPoints = sizes.toArray(new Integer[0]);
			for(int i = 0; i < formerModalPoints.length; i++) {
				formerModalPoints[i] = formerModalPoints[i] - 1;
			}

			int unifiedDomainSize = lcm(formerModalPoints) + 1;

			if(!sizes.contains(unifiedDomainSize)) {
				result = new FuzzySet();
				
				String[] labels = new String[unifiedDomainSize];
				for(int i = 0; i < unifiedDomainSize; i++) {
					labels[i] = "s" + i;
				}
				result.createTrapezoidalFunction(labels);
			}
		}
		
		return result;
	}
	
	private int lcm(Integer[] input) {
		int result = input[0];
		for(int i = 1; i < input.length; i++) {
			result = lcm(result, input[i]);
		}
		
		return result;
	}
	
	private int lcm(int a, int b) {
		return a * (b / gcd(a, b));
	}
	
	private int gcd(int a, int b) {
		int aux;
		while(b > 0) {
			aux = b;
			b = a % b;
			a = aux;
		}
		return a;
	}
}
