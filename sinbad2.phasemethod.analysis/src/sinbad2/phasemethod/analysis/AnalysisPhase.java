package sinbad2.phasemethod.analysis;

import sinbad2.domain.Domain;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class AnalysisPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.analysis";
	
	private Domain _domain;
	
	private ValuationSet _valuationSet;
	
	public AnalysisPhase() {
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = valuationSetManager.getActiveValuationSet();
		
		_domain = null;
	}
	
	public Domain getDomain() {
		return _domain;
	}
	
	public void setDomain(Domain domain) {
		_domain = domain;
	}
	
	@Override
	public IPhaseMethod copyStructure() {
		return new AnalysisPhase();
	}

	@Override
	public void copyData(IPhaseMethod iMethodPhase) {
		AnalysisPhase analysisPhase = (AnalysisPhase) iMethodPhase;
		
		clear();
		
		_domain = analysisPhase.getDomain();
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
