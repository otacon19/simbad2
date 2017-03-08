package sinbad2.phasemethod.linguistic.hesitant.unification;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.HesitantValuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class UnificationPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.linguistic.hesitant.unification"; //$NON-NLS-1$
	
	private Domain _unifiedDomain;
	
	private Map<ValuationKey, Valuation> _unifiedValuations;
	private Map<ValuationKey, Valuation> _twoTupleValuations;
	private Map<ValuationKey, TrapezoidalFunction> _envelopeValuations;
	
	private ValuationSet _valutationSet;
	private DomainSet _domainSet;
	
	public UnificationPhase() {
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valutationSet = valuationSetManager.getActiveValuationSet();
		
		DomainsManager domainsManager = DomainsManager.getInstance();
		_domainSet = domainsManager.getActiveDomainSet();
		
		_unifiedDomain = _domainSet.getDomains().get(0);
		
		_unifiedValuations = new HashMap<ValuationKey, Valuation>();
		_twoTupleValuations = new LinkedHashMap<ValuationKey, Valuation>();
		_envelopeValuations = new LinkedHashMap<ValuationKey, TrapezoidalFunction>();
	}
	
	public Map<ValuationKey, Valuation> getUnifiedValuations() {
		return _unifiedValuations;
	}
	
	public void setUnifiedValuations(Map<ValuationKey, Valuation> unifiedValuationsResult) {
		_unifiedValuations = unifiedValuationsResult;
	}
	
	@Override
	public Map<ValuationKey, Valuation> getTwoTupleValuations() {
		return _twoTupleValuations;
	}
	
	public void setTwoTupleValuations(Map<ValuationKey, Valuation> twoTupleValuations) {
		_twoTupleValuations = twoTupleValuations;
	}
	
	public Map<ValuationKey, TrapezoidalFunction> getEnvelopeValuations() {
		return _envelopeValuations;
	}
	
	public void setEnvelopeValuations(Map<ValuationKey, TrapezoidalFunction> envelopeValuations) {
		_envelopeValuations = envelopeValuations;
	}

	@Override
	public Domain getUnifiedDomain() {
		return (FuzzySet) _domainSet.getDomains().get(0);
	}
	
	@Override
	public void setUnifiedDomain(Domain domain) {
		_unifiedDomain = domain;
	}
	
	
	@Override
	public IPhaseMethod copyStructure() {
		return new UnificationPhase();
	}

	@Override
	public void copyData(IPhaseMethod iPhaseMethod) {
		UnificationPhase unification = (UnificationPhase) iPhaseMethod;

		clear();

		_unifiedValuations = unification.getUnifiedValuations();
		_twoTupleValuations = unification.getTwoTupleValuations();
		_envelopeValuations = unification.getEnvelopeValuations();
	}

	@Override
	public void clear() {
		_unifiedValuations.clear();
		_twoTupleValuations.clear();
		_envelopeValuations.clear();
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
		_unifiedDomain = _domainSet.getDomains().get(0);
	}

	@Override
	public boolean validate() {

		if(_domainSet.getDomains().isEmpty()) {
			return false;
		}
		
		if (_valutationSet.getValuations().isEmpty()) {
			return false;
		}

		return true;
	}

	public void unification() {
		_unifiedValuations = new HashMap<ValuationKey, Valuation>();
		_twoTupleValuations = new HashMap<ValuationKey, Valuation>();
		
		if(_unifiedDomain != null) {
			Valuation valuation;

			Map<ValuationKey, Valuation> valuations = _valutationSet.getValuations();
			for(ValuationKey vk : valuations.keySet()) {
				
				valuation = valuations.get(vk);
				
				if(valuation instanceof HesitantValuation) { 
					_envelopeValuations.put(vk, ((HesitantValuation) valuation).calculateFuzzyEnvelope((FuzzySet) _unifiedDomain));
					transformToTwoTuple(vk, _envelopeValuations.get(vk));
				}
			}
		}
	}

	private void transformToTwoTuple(ValuationKey vk, TrapezoidalFunction tmf) {
		IMembershipFunction function;
		FuzzySet result;

		result = (FuzzySet) ((FuzzySet) _unifiedDomain).clone();
		int g = result.getLabelSet().getCardinality();

		for(int i = 0; i < g; i++) {
			function = result.getLabelSet().getLabel(i).getSemantic();
			result.setValue(i, function.maxMin(tmf));
		}
			
		Valuation unifiedValuation = new UnifiedValuation(result);
		_unifiedValuations.put(vk, unifiedValuation);
		
		TwoTuple twoTuple = ((UnifiedValuation) unifiedValuation).disunification(result);
		_twoTupleValuations.put(vk, twoTuple);
	}
}

