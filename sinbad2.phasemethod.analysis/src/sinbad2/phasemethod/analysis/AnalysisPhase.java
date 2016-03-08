package sinbad2.phasemethod.analysis;

import sinbad2.domain.Domain;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;

public class AnalysisPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.analysis";
	
	private Domain _domain = null;
	
	private static AnalysisPhase _instance = null;
	
	public static AnalysisPhase getInstance() {
		if(_instance == null) {
			_instance = new AnalysisPhase();
		}
		return _instance;
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
	public void copyData(IPhaseMethod iMethodPhase) {}

	@Override
	public void clear() {}

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
		return true;
	}
}
