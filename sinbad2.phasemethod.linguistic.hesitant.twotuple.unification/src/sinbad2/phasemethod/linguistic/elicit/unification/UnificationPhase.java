package sinbad2.phasemethod.linguistic.elicit.unification;

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
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.linguistic.elicit.unification.nls.Messages;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.elicit.ELICIT;
import sinbad2.valuation.hesitant.EUnaryRelationType;
import sinbad2.valuation.hesitant.HesitantValuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class UnificationPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.linguistic.hesitant.twotuple.unification"; //$NON-NLS-1$

	private Map<ValuationKey, Valuation> _twoTupleValuations;
	private Map<ValuationKey, TrapezoidalFunction> _fuzzyNumbers;
	
	private List<Object[]> _elhDomains;
	
	private FuzzySet _unifiedDomain;
	
	private ValuationSet _valuationSet;
	
	private class MyComparator implements Comparator<Object[]> {
		@Override
		public int compare(Object[] o1, Object[] o2) {
			return Double.compare((Integer) o1[0], (Integer) o2[0]);
		}
	}
	
	public UnificationPhase() {
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = valuationSetManager.getActiveValuationSet();
		
		_twoTupleValuations = new HashMap<ValuationKey, Valuation>();
		_fuzzyNumbers = new HashMap<ValuationKey, TrapezoidalFunction>();
		
		_elhDomains = new LinkedList<Object[]>();
		_unifiedDomain = null;	
	}
	
	@Override
	public Map<ValuationKey, Valuation> getTwoTupleValuations() {
		return _twoTupleValuations;
	}
	
	public void setTwoTupleValuations(Map<ValuationKey, Valuation> unifiedValuations) {
		_twoTupleValuations = unifiedValuations;
	}
	
	public Map<ValuationKey, TrapezoidalFunction> getFuzzyNumbers() {
		return _fuzzyNumbers;
	}
	
	public void setFuzzyNumbers(Map<ValuationKey, TrapezoidalFunction> fuzzy_numbers) {
		_fuzzyNumbers = fuzzy_numbers;
	}
	
	public List<Object[]> getELHDomains() {
		return _elhDomains;
	}
	
	public void setELHDomains(List<Object[]> elhDomains) {
		_elhDomains = elhDomains;
	}
	
	@Override
	public FuzzySet getUnifiedDomain() {
		return _unifiedDomain;
	}
	
	@Override
	public void setUnifiedDomain(Domain domain) {
		_unifiedDomain = (FuzzySet) domain;
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
		_twoTupleValuations = unification.getTwoTupleValuations();
		_fuzzyNumbers = unification.getFuzzyNumbers();
	}

	@Override
	public void clear() {
		_elhDomains.clear();
		_unifiedDomain = null;
		_twoTupleValuations.clear();
		_fuzzyNumbers.clear();
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
		_twoTupleValuations = new HashMap<ValuationKey, Valuation>();
		
		if (unifiedDomain != null) {
			Valuation valuation;
			TwoTuple valuationTwoTuple, valuationTwoTupleLower, valuationTwoTupleUpper;

			Map<ValuationKey, Valuation> valuations = _valuationSet.getValuations();
			for(ValuationKey vk : valuations.keySet()) {
				valuation = valuations.get(vk);

				if(valuation instanceof HesitantValuation) {
					if(((HesitantValuation) valuation).isPrimary()) {
						valuationTwoTuple = new TwoTuple((FuzzySet) valuation.getDomain(), ((HesitantValuation) valuation).getLabel()).transform(unifiedDomain);
						valuation = new ELICIT((FuzzySet) valuationTwoTuple.getDomain());
						((ELICIT) valuation).setTwoTupleLabel(valuationTwoTuple);
					} else if(((HesitantValuation) valuation).isUnary()){
						LabelLinguisticDomain term = ((HesitantValuation) valuation).getTerm();
						EUnaryRelationType unary = ((HesitantValuation) valuation).getUnaryRelation();
						valuationTwoTuple = new TwoTuple((FuzzySet) valuation.getDomain(), term).transform(unifiedDomain);
						valuation = new ELICIT((FuzzySet) valuationTwoTuple.getDomain());
						((ELICIT) valuation).setUnaryRelation(unary, valuationTwoTuple);
					} else {
						LabelLinguisticDomain lowerTerm = ((HesitantValuation) valuation).getLowerTerm();
						LabelLinguisticDomain upperTerm = ((HesitantValuation) valuation).getUpperTerm();
						valuationTwoTupleLower = new TwoTuple((FuzzySet) valuation.getDomain(), lowerTerm).transform(unifiedDomain);
						valuationTwoTupleUpper = new TwoTuple((FuzzySet) valuation.getDomain(), upperTerm).transform(unifiedDomain);
						valuation = new ELICIT((FuzzySet) valuationTwoTupleLower.getDomain());
						((ELICIT) valuation).setBinaryRelation(valuationTwoTupleLower, valuationTwoTupleUpper);
					}
				} else {
					throw new IllegalArgumentException();
				}
				
				_twoTupleValuations.put(vk, valuation);
				_fuzzyNumbers.put(vk, ((ELICIT) valuation).calculateFuzzyEnvelope());
			}
		}
		return _twoTupleValuations;
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
			entry[0] = "l(" + i + "," + value + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		
		_elhDomains.addAll(domains);

		String generate = Messages.UnificationPhase_Generate;
		domain = generateUnifiedDomain(sizes);
		int index;
		if(domain != null) {
			domainName = generate;
			index = 1;
			while(domainsNames.contains(domainName)) {
				domainName = generate + "_" + index++; //$NON-NLS-1$
			}
			cardinality = ((FuzzySet) domain).getLabelSet().getCardinality();
			if(cardinality != oldValue) {
				i++;
			}

			_elhDomains.add(new Object[] { "l(" + i + "," + cardinality + ")", domainName, domain }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			domainsNames.add(domainName);
		}
		
		_unifiedDomain = (FuzzySet) ((FuzzySet) _elhDomains.get(_elhDomains.size() - 1)[2]).clone();
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
					labels[i] = "s" + i; //$NON-NLS-1$
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
