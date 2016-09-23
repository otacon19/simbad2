package sinbad2.phasemethod.multigranular.unification;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
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

	public static final String ID = "flintstones.phasemethod.multigranular.fusion.unification"; //$NON-NLS-1$
	
	private Domain _unifiedDomain;
	
	private Map<ValuationKey, Valuation> _unifiedValuations;
	private Map<ValuationKey, Valuation> _twoTupleValuations;
	
	private ValuationSet _valutationSet;
	
	public UnificationPhase() {
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valutationSet = valuationSetManager.getActiveValuationSet();
		
		_unifiedValuations = new HashMap<ValuationKey, Valuation>();
		_twoTupleValuations = new LinkedHashMap<ValuationKey, Valuation>();
	}
	
	public Map<ValuationKey, Valuation> getUnifiedValuations() {
		return _unifiedValuations;
	}
	
	public void setUnifiedValuations(Map<ValuationKey, Valuation> unifiedValuations ) {
		_unifiedValuations = unifiedValuations;
	}
	
	@Override
	public Map<ValuationKey, Valuation> getTwoTupleValuations() {
		return _twoTupleValuations;
	}
	
	public void setTwoTupleValuations(Map<ValuationKey, Valuation> twoTupleValuations ) {
		_twoTupleValuations = twoTupleValuations;
	}

	@Override 
	public Domain getUnifiedDomain() {
		return _unifiedDomain;
	}
	
	public void setUnifiedDomain(Domain unifiedDomain) {
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

		_unifiedValuations = unification.getUnifiedValuations();
		_twoTupleValuations = unification.getTwoTupleValuations();
	}

	@Override
	public void clear() {
		_unifiedValuations.clear();
		_twoTupleValuations.clear();
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

		if (_valutationSet.getValuations().isEmpty()) {
			return false;
		}

		return true;
	}

	public Map<ValuationKey, Valuation> unification() {
		_unifiedValuations = new HashMap<ValuationKey, Valuation>();
		
		if (_unifiedDomain != null) {
			Criterion criterion;
			Valuation valuation;
			FuzzySet fuzzySet;
			Boolean isCost;

			Map<ValuationKey, Valuation> valuations = _valutationSet.getValuations();
			for(ValuationKey vk : valuations.keySet()) {
				criterion = vk.getCriterion();
				valuation = valuations.get(vk);
				isCost = criterion.isCost();

				if(valuation instanceof UnifiedValuation) {
					Valuation auxValuation = ((UnifiedValuation) valuation).disunification((FuzzySet) valuation.getDomain());
					if(isCost) {
						auxValuation = auxValuation.negateValuation();
					}
					fuzzySet = ((TwoTuple) auxValuation).unification(_unifiedDomain);
					valuation = new UnifiedValuation(fuzzySet);
				} else if(valuation instanceof LinguisticValuation) {
					if(isCost) {
						valuation = valuation.negateValuation();
					}
					fuzzySet = ((LinguisticValuation) valuation).unification(_unifiedDomain);
					valuation = new UnifiedValuation(fuzzySet);
				}
				_unifiedValuations.put(vk, valuation);
			}
		}
		
		unifiedEvaluationToTwoTuple();
		
		return _unifiedValuations;
	}
	
	public Map<ValuationKey, Valuation> unifiedEvaluationToTwoTuple() {
		
		if(_unifiedDomain != null) {
		
			Valuation valuation;

			for(ValuationKey key : _unifiedValuations.keySet()) {
				valuation = _unifiedValuations.get(key);
				if(valuation instanceof UnifiedValuation) {
					valuation = ((UnifiedValuation) valuation).disunification((FuzzySet) valuation.getDomain());
				} else if(!(valuation instanceof TwoTuple)) {
					valuation = null;
				}
				_twoTupleValuations.put(key, valuation);
			}
		}

		return _twoTupleValuations;
	}
}
