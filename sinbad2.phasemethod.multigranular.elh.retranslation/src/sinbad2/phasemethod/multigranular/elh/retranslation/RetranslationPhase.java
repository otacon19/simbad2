package sinbad2.phasemethod.multigranular.elh.retranslation;

import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;

public class RetranslationPhase implements IPhaseMethod {
	
	public static final String ID = "flintstones.phasemethod.multigranular.elh.retranslation";

	private static RetranslationPhase _instance = null;
	
	public static RetranslationPhase getInstance() {
		if(_instance == null) {
			_instance = new RetranslationPhase();
		}
		return _instance;
	}
	
	@Override
	public void clear() {}

	@Override
	public IPhaseMethod clone() {
		RetranslationPhase result = null;

		try {
			result = (RetranslationPhase) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return result;
	}
	@Override
	public IPhaseMethod copyStructure() {
		return new RetranslationPhase();
	}

	@Override
	public void copyData(IPhaseMethod iPhaseMethod) {}

	@Override
	public void activate() {}

	@Override
	public boolean validate() {
		return true;
	}
	
	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		if(event.getChange().equals(EPhaseMethodStateChange.ACTIVATED)) {
			activate();
		}
	}
}

