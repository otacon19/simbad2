package sinbad2.phasemethod.todim.resolution;

import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;

public class ResolutionPhase implements IPhaseMethod {
	
	public static final String ID = "flintstones.phasemethod.todim.resolution";

	@Override
	public IPhaseMethod copyStructure() {
		return new ResolutionPhase();
	}

	@Override
	public void copyData(IPhaseMethod iPhaseMethod) {
		clear();
	}
	
	@Override
	public void clear() {	
		
	}

	@Override
	public void activate() {
		
	}

	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public IPhaseMethod clone() {
		return null;
	}
	
	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		
	}
}
