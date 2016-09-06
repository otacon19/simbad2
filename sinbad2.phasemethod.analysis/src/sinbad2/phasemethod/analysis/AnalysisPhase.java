package sinbad2.phasemethod.analysis;

import java.util.Map;

import sinbad2.domain.Domain;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class AnalysisPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.analysis"; //$NON-NLS-1$
	
	private Domain _domain;
	private Map<ValuationKey, Valuation> _unifiedValuations;
	
	private ValuationSet _valuationSet;
	
	public AnalysisPhase() {
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = valuationSetManager.getActiveValuationSet();
		
		_domain = null;
		_unifiedValuations = null;
	}
	
	@Override
	public Domain getUnifiedDomain() {
		return _domain;
	}
	
	public void setUnifiedDomain(Domain domain) {
		_domain = domain;
	}
	
	@Override
	public Map<ValuationKey, Valuation> getTwoTupleValuations() {
		return _unifiedValuations;
	}
	
	public void setTwoTupleValuations(Map<ValuationKey, Valuation> unifiedValuations) {
		_unifiedValuations = unifiedValuations;
	}
	
	@Override
	public IPhaseMethod copyStructure() {
		return new AnalysisPhase();
	}

	@Override
	public void copyData(IPhaseMethod iMethodPhase) {
		AnalysisPhase analysisPhase = (AnalysisPhase) iMethodPhase;
		
		clear();
		
		_domain = analysisPhase.getUnifiedDomain();
	}

	@Override
	public void clear() {
		_domain = null;
	}

	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		if (event.getChange().equals(EPhaseMethodStateChange.ACTIVATED)) {
			activate();
		}
	}

	@Override
	public IPhaseMethod clone() {
		AnalysisPhase result = null;

		try {
			result = (AnalysisPhase) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void activate() {}

	@Override
	public boolean validate() {
		
		if(_valuationSet.getValuations().isEmpty()) {
			return false;
		}
		return true;
	}
}
