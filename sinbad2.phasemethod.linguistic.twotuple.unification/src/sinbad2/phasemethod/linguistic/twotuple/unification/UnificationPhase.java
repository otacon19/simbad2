package sinbad2.phasemethod.linguistic.twotuple.unification;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.element.criterion.Criterion;
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

	public static final String ID = "flintstones.phasemethod.linguistic.twotuple.unification"; //$NON-NLS-1$
	
	private Domain _unifiedDomain;
	
	private Map<ValuationKey, Valuation> _unifiedValuations;
	private Map<ValuationKey, Valuation> _twoTupleValuations;
	
	private ValuationSet _valuationSet;
	
	private DomainSet _domainsSet;
	
	public UnificationPhase() {
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = valuationSetManager.getActiveValuationSet();
		
		DomainsManager domainsManager = DomainsManager.getInstance();
		_domainsSet = domainsManager.getActiveDomainSet();
		
		_unifiedDomain = _domainsSet.getDomains().get(0);
		
		_unifiedValuations = new HashMap<ValuationKey, Valuation>();
		_twoTupleValuations = new LinkedHashMap<ValuationKey, Valuation>();
	}
	
	public Map<ValuationKey, Valuation> getUnifiedValuations() {
		return _unifiedValuations;
	}
	
	public void setUnifiedValuations(Map<ValuationKey, Valuation> unifiedValuations) {
		_unifiedValuations = unifiedValuations;
	}
	
	@Override
	public Map<ValuationKey, Valuation> getTwoTupleValuations() {
		return _twoTupleValuations;
	}
	
	public void setTwoTupleValuations(Map<ValuationKey, Valuation> twoTupleValuations) {
		_twoTupleValuations = twoTupleValuations;
	}
	
	@Override
	public Domain getUnifiedDomain() {
		return _unifiedDomain;
	}
	
	public void setUnifiedDomain(Domain domain) {
		_unifiedDomain = domain;
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

		if (_valuationSet.getValuations().isEmpty()) {
			return false;
		}
		
		if(_domainsSet.getDomains().isEmpty()) {
			return false;
		}

		return true;
	}

	public Map<ValuationKey, Valuation> unification() {
		_twoTupleValuations = new HashMap<ValuationKey, Valuation>();
		
		if (_unifiedDomain != null) {
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
					valuation = ((TwoTuple) valuation).transform((FuzzySet) _unifiedDomain);
				} else if(valuation instanceof LinguisticValuation) {
					if(isCost) {
						valuation = valuation.negateValuation();
					}
					valuation = new TwoTuple((FuzzySet) valuation.getDomain(), ((LinguisticValuation) valuation).getLabel()).transform((FuzzySet) _unifiedDomain);
				} else {
					throw new IllegalArgumentException();
				}
				_twoTupleValuations.put(vk, valuation);
			}
		}
		
		return _twoTupleValuations;
	}
}
