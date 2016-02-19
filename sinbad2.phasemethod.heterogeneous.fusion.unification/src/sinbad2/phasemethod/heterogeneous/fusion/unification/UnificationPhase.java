package sinbad2.phasemethod.heterogeneous.fusion.unification;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.integer.IntegerValuation;
import sinbad2.valuation.integer.interval.IntegerInterval;
import sinbad2.valuation.linguistic.LinguisticValuation;
import sinbad2.valuation.real.RealValuation;
import sinbad2.valuation.real.interval.RealInterval;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class UnificationPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.heterogeneous.fusion.unification";

	private ValuationSetManager _valuationSetManager;
	private ValuationSet _valutationSet;
	
	private Map<ValuationKey, Valuation> _unifiedEvaluationsResult;
	private Map<ValuationKey, Valuation> _twoTupleEvaluationsResult;
	private Map<Alternative, Valuation> _twoTupleEvaluationsAlternatives;
	
	private static UnificationPhase _instance = null;
	
	private UnificationPhase() {
		_valuationSetManager = ValuationSetManager.getInstance();
		_valutationSet = _valuationSetManager.getActiveValuationSet();
		
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
		return _valutationSet;
	}

	@Override
	public IPhaseMethod copyStructure() {
		return new UnificationPhase();
	}

	@Override
	public void copyData(IPhaseMethod iPhaseMethod) {
		UnificationPhase unification = (UnificationPhase) iPhaseMethod;

		clear();

		_valutationSet.setValuations(unification.getValuationSet().getValuations());
	}

	@Override
	public void clear() {
		_valutationSet.clear();
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
		_valuationSetManager.setActiveValuationSet(_valutationSet);
	}

	@Override
	public boolean validate() {

		if (_valutationSet.getValuations().isEmpty()) {
			return false;
		}

		return true;
	}

	public Map<ValuationKey, Valuation> unification(FuzzySet unifiedDomain) {
		_unifiedEvaluationsResult = new HashMap<ValuationKey, Valuation>();
		Map<Domain, List<Object[]>> toNormalize = new HashMap<Domain, List<Object[]>>();
		Map<List<Object[]>, ValuationKey> toNormalizeVk = new HashMap<List<Object[]>, ValuationKey>();
		List<Object[]> auxEvaluations;
		
		if (unifiedDomain != null) {
			Expert expert;
			Alternative alternative;
			Criterion criterion;
			Valuation valuation;
			FuzzySet fuzzySet;
			Boolean isCost;

			Map<ValuationKey, Valuation> valuations = _valutationSet.getValuations();
			for(ValuationKey vk : valuations.keySet()) {
				expert = vk.getExpert();
				alternative = vk.getAlternative();
				criterion = vk.getCriterion();
				valuation = valuations.get(vk);
				isCost = criterion.getCost();
				
				if(valuation instanceof IntegerValuation) {
					if(isCost) {
						valuation = valuation.negateValutation();
					}
					if(((NumericIntegerDomain) valuation.getDomain()).getInRange()) {
						fuzzySet = ((IntegerValuation) valuation).unification(unifiedDomain);
						valuation = new UnifiedValuation(fuzzySet);
					} else {
						Object[] auxEvaluation = new Object[4];
						auxEvaluation[0] = expert;
						auxEvaluation[1] = alternative;
						auxEvaluation[2] = criterion;
						auxEvaluation[3] = valuation;

						Domain domain = valuation.getDomain();

						auxEvaluations = toNormalize.get(domain);
						if(auxEvaluations == null) {
							auxEvaluations = new LinkedList<Object[]>();
							toNormalize.put(valuation.getDomain(), auxEvaluations);
							toNormalizeVk.put(auxEvaluations, vk);
						}
						auxEvaluations.add(auxEvaluation);
					}
				} else if(valuation instanceof RealValuation) {
					if(isCost) {
						valuation = valuation.negateValutation();
					}
					if(((NumericRealDomain) valuation.getDomain()).getInRange()) {
						fuzzySet = ((RealValuation) valuation).unification(unifiedDomain);
						valuation = new UnifiedValuation(fuzzySet);
						_unifiedEvaluationsResult.put(vk, valuation);
					} else {
						Object[] auxEvaluation = new Object[4];
						auxEvaluation[0] = expert;
						auxEvaluation[1] = alternative;
						auxEvaluation[2] = criterion;
						auxEvaluation[3] = valuation;

						Domain domain = valuation.getDomain();
						
						auxEvaluations = toNormalize.get(domain);
						if(auxEvaluations == null) {
							auxEvaluations = new LinkedList<Object[]>();
							toNormalize.put(valuation.getDomain(), auxEvaluations);
							toNormalizeVk.put(auxEvaluations, vk);
						}

						auxEvaluations.add(auxEvaluation);
					}
				} else if(valuation instanceof IntegerInterval) {
					if(isCost) {
						valuation = valuation.negateValutation();
					}
					if (((NumericIntegerDomain) valuation.getDomain()).getInRange()) {
						fuzzySet = ((IntegerInterval) valuation).unification(unifiedDomain);
						valuation = new UnifiedValuation(fuzzySet);
						_unifiedEvaluationsResult.put(vk, valuation);
					} else {
						Object[] auxEvaluation = new Object[4];
						auxEvaluation[0] = expert;
						auxEvaluation[1] = alternative;
						auxEvaluation[2] = criterion;
						auxEvaluation[3] = valuation;

						Domain domain = valuation.getDomain();
						
						auxEvaluations = toNormalize.get(domain);
						if(auxEvaluations == null) {
							auxEvaluations = new LinkedList<Object[]>();
							toNormalize.put(domain, auxEvaluations);
							toNormalizeVk.put(auxEvaluations, vk);
						}

						auxEvaluations.add(auxEvaluation);
					}
				} else if(valuation instanceof RealInterval) {
					if(isCost) {
						valuation = valuation.negateValutation();
					}
					if (((NumericRealDomain) valuation.getDomain()).getInRange()) {
						fuzzySet = ((RealInterval) valuation).unification(unifiedDomain);
						valuation = new UnifiedValuation(fuzzySet);
						_unifiedEvaluationsResult.put(vk, valuation);
					} else {
						Object[] auxEvaluation = new Object[4];
						auxEvaluation[0] = expert;
						auxEvaluation[1] = alternative;
						auxEvaluation[2] = criterion;
						auxEvaluation[3] = valuation;

						Domain domain = valuation.getDomain();
						
						auxEvaluations = toNormalize.get(domain);
						if(auxEvaluations == null) {
							auxEvaluations = new LinkedList<Object[]>();
							toNormalize.put(domain, auxEvaluations);
							toNormalizeVk.put(auxEvaluations, vk);
						}

						auxEvaluations.add(auxEvaluation);
					}
				} else if(valuation instanceof UnifiedValuation) {
					Valuation auxValuation = ((UnifiedValuation) valuation).disunification((FuzzySet) valuation.getDomain());
					if(isCost) {
						auxValuation = auxValuation.negateValutation();
					}
					fuzzySet = ((TwoTuple) auxValuation).unification(unifiedDomain);
					valuation = new UnifiedValuation(fuzzySet);
					
					_unifiedEvaluationsResult.put(vk, valuation);
				} else {
					if(isCost) {
						valuation = ((LinguisticValuation) valuation).negateValutation();
					}
					fuzzySet = ((LinguisticValuation) valuation).unification(unifiedDomain);
					valuation = new UnifiedValuation(fuzzySet);
					
					_unifiedEvaluationsResult.put(vk, valuation);
				}
			}
		}
		
		if(toNormalize.size() > 0) {
			normalizeValuations(toNormalize, toNormalizeVk, unifiedDomain);
		}
		
		return _unifiedEvaluationsResult;
	}
	
	private void normalizeValuations(Map<Domain, List<Object[]>> toNormalize, Map<List<Object[]>, ValuationKey> toNormalizeVk, FuzzySet unifiedDomain) {
		NumericIntegerDomain auxNumericIntegerDomain;
		NumericRealDomain auxNumericRealDomain;
		Valuation auxValuation;
		FuzzySet fuzzySet;
		double min, max, measure, minMeasure, maxMeasure;
		
		for(Domain d : toNormalize.keySet()) {
			if(d instanceof NumericIntegerDomain) {
				min = -1;
				max = -1;
				
				ValuationKey vk = toNormalizeVk.get(toNormalize.get(d));
				Valuation v = _valutationSet.getValuation(vk.getExpert(), vk.getAlternative(), vk.getCriterion());
				
				if(v instanceof IntegerValuation) {	
					for(Object[] evaluation : toNormalize.get(d)) {
						auxValuation = (Valuation) evaluation[3];
						measure = ((IntegerValuation) auxValuation).getValue();
						if(min == -1) {
							min = measure;
							max = measure;
						} else {
							if (measure < min) {
								min = measure;
							}
							if (measure > max) {
								max = measure;
							}
						}
					}
					if(min != -1) {
						auxNumericIntegerDomain = new NumericIntegerDomain();
						auxNumericIntegerDomain.setMinMax((int) min, (int) max);
						auxNumericIntegerDomain.setType(((NumericIntegerDomain) d).getType());
						for(Object[] evaluation : toNormalize.get(d)) {
							auxValuation = (Valuation) evaluation[3];
							auxValuation = new IntegerValuation(auxNumericIntegerDomain, ((IntegerValuation) auxValuation).getValue());
	
							fuzzySet = ((IntegerValuation) auxValuation).unification(unifiedDomain);
							UnifiedValuation valuation = new UnifiedValuation(fuzzySet);
	
							_unifiedEvaluationsResult.put(toNormalizeVk.get(d), valuation);
						}
					}
				} else {
					min = -1;
					max = -1;
					for(Object[] evaluation : toNormalize.get(d)) {
						auxValuation = (Valuation) evaluation[3];
						minMeasure = ((IntegerInterval) auxValuation).getMin();

						if(min == -1) {
							min = minMeasure;
							max = minMeasure;
						}
						if(minMeasure < min) {
							min = minMeasure;
						}
						if(minMeasure > max) {
							max = minMeasure;
						}
						maxMeasure = ((IntegerInterval) auxValuation).getMax();
						if(maxMeasure < min) {
							min = maxMeasure;
						}
						if(maxMeasure > max) {
							max = maxMeasure;
						}
					}
					if(min != -1) {
						auxNumericIntegerDomain = new NumericIntegerDomain();
						auxNumericIntegerDomain.setMinMax((int) min, (int) max);
						auxNumericIntegerDomain.setType(((NumericIntegerDomain) d).getType());
						for(Object[] evaluation : toNormalize.get(d)) {
							auxValuation = (Valuation) evaluation[3];
							auxValuation = new IntegerInterval(auxNumericIntegerDomain, ((IntegerInterval) auxValuation).getMin(), ((IntegerInterval) auxValuation).getMax());

							fuzzySet = ((IntegerInterval) auxValuation).unification(unifiedDomain);
							UnifiedValuation valuation = new UnifiedValuation(fuzzySet);

							_unifiedEvaluationsResult.put(toNormalizeVk.get(d), valuation);
						}
					}
				}
			} else if(d instanceof NumericRealDomain) {
				min = -1;
				max = -1;
				
				ValuationKey vk = toNormalizeVk.get(toNormalize.get(d));
				Valuation v = _valutationSet.getValuation(vk.getExpert(), vk.getAlternative(), vk.getCriterion());
				
				if(v instanceof RealValuation) {	
				
					for(Object[] evaluation : toNormalize.get(d)) {
						auxValuation = (Valuation) evaluation[3];
						measure = ((RealValuation) auxValuation).getValue();
						if(min == -1) {
							min = measure;
							max = measure;
						} else {
							if (measure < min) {
								min = measure;
							}
							if (measure > max) {
								max = measure;
							}
						}
					}
					if(min != -1) {
						auxNumericRealDomain = new NumericRealDomain();
						auxNumericRealDomain.setMinMax(min, max);
						auxNumericRealDomain.setType(((NumericRealDomain) d).getType());
						for(Object[] evaluation : toNormalize.get(d)) {
							auxValuation = (Valuation) evaluation[3];
							auxValuation = new RealValuation(auxNumericRealDomain, ((RealValuation) auxValuation).getValue());
	
							fuzzySet = ((IntegerValuation) auxValuation).unification(unifiedDomain);
							UnifiedValuation valuation = new UnifiedValuation(fuzzySet);
	
							_unifiedEvaluationsResult.put(toNormalizeVk.get(d), valuation);
						}
					}
				} else {
					min = -1;
					max = -1;
					for(Object[] evaluation : toNormalize.get(d)) {
						auxValuation = (Valuation) evaluation[3];
						minMeasure = ((RealInterval) auxValuation).getMin();

						if(min == -1) {
							min = minMeasure;
							max = minMeasure;
						}
						if(minMeasure < min) {
							min = minMeasure;
						}
						if(minMeasure > max) {
							max = minMeasure;
						}
						maxMeasure = ((IntegerInterval) auxValuation).getMax();
						if(maxMeasure < min) {
							min = maxMeasure;
						}
						if(maxMeasure > max) {
							max = maxMeasure;
						}
					}
					if(min != -1) {
						auxNumericRealDomain = new NumericRealDomain();
						auxNumericRealDomain.setMinMax( min, max);
						auxNumericRealDomain.setType(((NumericRealDomain) d).getType());
						for(Object[] evaluation : toNormalize.get(d)) {
							auxValuation = (Valuation) evaluation[3];
							auxValuation = new RealInterval(auxNumericRealDomain, ((RealInterval) auxValuation).getMin(), ((RealInterval) auxValuation).getMax());

							fuzzySet = ((RealInterval) auxValuation).unification(unifiedDomain);
							UnifiedValuation valuation = new UnifiedValuation(fuzzySet);

							_unifiedEvaluationsResult.put(toNormalizeVk.get(d), valuation);
						}
					}
				}
			}
		}
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
	
	public Map<Alternative, Valuation> getAlternativesValuations() {
		return _twoTupleEvaluationsAlternatives;
	}
	
	public Map<ValuationKey, Valuation> getValuationsResult() {
		return _twoTupleEvaluationsResult;
	}
}
