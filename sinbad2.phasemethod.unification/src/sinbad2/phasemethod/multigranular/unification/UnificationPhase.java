package sinbad2.phasemethod.multigranular.unification;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
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

	public static final String ID = "flintstones.phasemethod.multigranular.fusion.unification";

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
	public void copyData(IPhaseMethod iMethodPhase) {
		UnificationPhase unification = (UnificationPhase) iMethodPhase;

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
		
		if (unifiedDomain != null) {
			Criterion criterion;
			Valuation valuation;
			FuzzySet fuzzySet;
			Boolean isCost;

			Map<ValuationKey, Valuation> valuations = _valutationSet.getValuations();
			for(ValuationKey vk : valuations.keySet()) {
				criterion = vk.getCriterion();
				valuation = valuations.get(vk);
				isCost = criterion.getCost();

				if(valuation instanceof UnifiedValuation) {
					Valuation auxValuation = ((UnifiedValuation) valuation).disunification((FuzzySet) valuation.getDomain());
					if(isCost) {
						auxValuation = auxValuation.negateValuation();
					}
					fuzzySet = ((TwoTuple) auxValuation).unification(unifiedDomain);
					valuation = new UnifiedValuation(fuzzySet);
				} else if(valuation instanceof LinguisticValuation) {
					if(isCost) {
						valuation = valuation.negateValuation();
					}
					fuzzySet = ((LinguisticValuation) valuation).unification(unifiedDomain);
					valuation = new UnifiedValuation(fuzzySet);
				}
				_unifiedEvaluationsResult.put(vk, valuation);
			}
		}
		
		unifiedEvaluationToTwoTuple(unifiedDomain);
		
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

	public Map<ValuationKey, Valuation> getValuationsResult() {
		return _twoTupleEvaluationsResult;
	}
}
