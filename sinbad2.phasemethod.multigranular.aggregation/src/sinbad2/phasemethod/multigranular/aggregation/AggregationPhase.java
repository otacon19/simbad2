package sinbad2.phasemethod.multigranular.aggregation;

import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;

public class AggregationPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.multigranular.aggregation";
	
	public AggregationPhase() {}
	
	@Override
	public IPhaseMethod copyStructure() {
		return new AggregationPhase();
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
		AggregationPhase result = null;

		try {
			result = (AggregationPhase) super.clone();
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
