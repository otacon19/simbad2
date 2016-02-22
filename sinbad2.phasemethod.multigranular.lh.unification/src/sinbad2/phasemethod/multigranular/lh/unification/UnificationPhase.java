package sinbad2.phasemethod.multigranular.lh.unification;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
import sinbad2.valuation.unifiedValuation.UnifiedValuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class UnificationPhase implements IPhaseMethod {
	
	public static final String ID = "flintstones.phasemethod.multigranular.lh.unification";
	
	private class MyComparator implements Comparator<Object[]> {
		@Override
		public int compare(Object[] o1, Object[] o2) {
			return Double.compare((Integer) o1[0], (Integer) o2[0]);
		}
	}

	private ValuationSetManager _valuationSetManager;
	private ValuationSet _valuationSet;
	
	private Map<ValuationKey, Valuation> _unifiedEvaluationsResult;
	private Map<ValuationKey, Valuation> _twoTupleEvaluationsResult;
	private Map<Alternative, Valuation> _twoTupleEvaluationsAlternatives;
	
	private List<Object[]> _lhDomains;
	
	private static UnificationPhase _instance = null;
	
	private UnificationPhase() {
		_valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = _valuationSetManager.getActiveValuationSet();
		
		_twoTupleEvaluationsResult = new LinkedHashMap<ValuationKey, Valuation>();
		_twoTupleEvaluationsAlternatives = new LinkedHashMap<Alternative, Valuation>();
	}
	
	public static UnificationPhase getInstance() {
		if(_instance == null) {
			_instance = new UnificationPhase();
		}
		return _instance;
	}

	public ValuationSet getValuationSet() {
		return _valuationSet;
	}

	@Override
	public IPhaseMethod copyStructure() {
		return new UnificationPhase();
	}

	@Override
	public void copyData(IPhaseMethod iMethodPhase) {
		UnificationPhase unification = (UnificationPhase) iMethodPhase;

		clear();

		_valuationSet.setValuations(unification.getValuationSet().getValuations());
	}

	@Override
	public void clear() {
		_valuationSet.clear();
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
	public void activate() {
		_valuationSetManager.setActiveValuationSet(_valuationSet);
	}

	@Override
	public boolean validate() {

		if (_valuationSet.getValuations().isEmpty()) {
			return false;
		}

		return true;
	}

	public Map<ValuationKey, Valuation> unification(FuzzySet unifiedDomain) {
		_unifiedEvaluationsResult = new HashMap<ValuationKey, Valuation>();
		
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
				_unifiedEvaluationsResult.put(vk, valuation);
			}
		}
		return _unifiedEvaluationsResult;
	}
	
	public Map<ValuationKey, Valuation> unifiedEvaluationToTwoTuple(FuzzySet unifiedDomain) {
		
		if(unifiedDomain != null) {
		
			Valuation valuation;

			for(ValuationKey key : _unifiedEvaluationsResult.keySet()) {
				valuation = _unifiedEvaluationsResult.get(key);
				if(valuation instanceof UnifiedValuation) {
					valuation = ((UnifiedValuation) valuation).disunification((FuzzySet) valuation.getDomain());
				} else if(!(valuation instanceof TwoTuple)) {
					valuation = null;
				}
				_twoTupleEvaluationsResult.put(key, valuation);
				_twoTupleEvaluationsAlternatives.put(key.getAlternative(), valuation);
			}
		}

		return _twoTupleEvaluationsResult;
	}
	
	public List<Object[]> generateLH() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
		
		 _lhDomains = new LinkedList<Object[]>();
		
		List<Object[]> domains = new LinkedList<Object[]>();
		Set<String> domainsNames = new HashSet<String>();
		int i = 1, cardinality;

		String domainName;

		Set<Integer> cardinalities = new HashSet<Integer>();
		Domain generateDomain;
		for(Alternative alternative : elementsSet.getAlternatives()) {
			for(Criterion criterion : elementsSet.getCriteria()) {
				if(elementsSet.getElementCriterionSubcriteria(criterion).size() == 0) {
					for(Expert expert : elementsSet.getExperts()) {
						if(elementsSet.getElementExpertChildren(expert).size() == 0) {
							generateDomain = _valuationSet.getValuation(expert, alternative, criterion).getDomain();
							if(generateDomain != null) {
								domainName = generateDomain.getId();
								if(generateDomain instanceof FuzzySet) {
									if(!domainsNames.contains(domainName)) {
										if(((FuzzySet) generateDomain).isBLTS()) {
											cardinality = ((FuzzySet) generateDomain).getLabelSet().getCardinality();
											if(cardinalities.contains(cardinality)) {
												return null;
											} else {
												cardinalities.add(cardinality);
												domains.add(new Object[] {cardinality, domainName, generateDomain });
												domainsNames.add(domainName);
											}
										} else {
											return null;
										}
									}
								} else {
									return null;
								}
							} else {
								return null;
							}
						}
					}
				}
			}
		}

		Collections.sort(domains, new MyComparator());
		
		i = 0;
		Set<Integer> sizes = new HashSet<Integer>();
		int oldValue = -1, currentCardinality, newLevelCardinality, index;
		String generate = "generate";
		Object[] auxEntry;
		for(Object[] entry : domains) {
			currentCardinality = (Integer) entry[0];
			sizes.add(currentCardinality);
			newLevelCardinality = (oldValue * 2) - 1;
			if(oldValue != currentCardinality) {
				if ((newLevelCardinality == currentCardinality) || (oldValue == -1)) {
					oldValue = currentCardinality;
					i++;
					entry[0] = "l(" + i + "," + currentCardinality + ")";
					_lhDomains.add(entry);
				} else {
					while(currentCardinality > newLevelCardinality) {
						auxEntry = new Object[3];
						i++;
						auxEntry[0] = "l(" + i + "," + newLevelCardinality + ")";
						domainName = "generate";
						index = 1;
						while (domainsNames.contains(domainName)) {
							domainName = generate + "_" + index++;
						}
						auxEntry[1] = domainName;
						auxEntry[2] = generateNewLHDomain(newLevelCardinality);
						_lhDomains.add(auxEntry);
						newLevelCardinality = (newLevelCardinality * 2) - 1;
					}

					if(currentCardinality == newLevelCardinality) {
						oldValue = currentCardinality;
						i++;
						entry[0] = "l(" + i + "," + currentCardinality + ")";
						_lhDomains.add(entry);
					} else {
						return null;
					}
				}
			}
		}

		return _lhDomains;
	}

	private FuzzySet generateNewLHDomain(int size) {
		FuzzySet result = new FuzzySet();
		String[] labels = new String[size];
		for (int i = 0; i < size; i++) {
			labels[i] = "s" + i;
		}
		result.createTrapezoidalFunction(labels);
	
		return result;
	}
	
	public List<Object[]> getLHDomains() {
		return _lhDomains;
	}
	
	public Map<Alternative, Valuation> getAlternativesValuations() {
		return _twoTupleEvaluationsAlternatives;
	}
	
	public Map<ValuationKey, Valuation> getValuationsResult() {
		return _twoTupleEvaluationsResult;
	}
}
