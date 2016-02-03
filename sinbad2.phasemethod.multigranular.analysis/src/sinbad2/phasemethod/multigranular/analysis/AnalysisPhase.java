package sinbad2.phasemethod.multigranular.analysis;

import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;

public class AnalysisPhase implements IPhaseMethod {

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
