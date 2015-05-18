package sinbad2.resolutionphase.framework;

import sinbad2.resolutionphase.ResolutionPhaseImplementation;
import sinbad2.resolutionphase.state.EResolutionPhaseStateChange;
import sinbad2.resolutionphase.state.ResolutionPhaseStateChangeEvent;

public class Framework extends ResolutionPhaseImplementation {
	
	public static final String ID = "flintstones.resolutionphase.framework"; //$NON-NLS-1$
	
	@Override
	public ResolutionPhaseImplementation newInstance() {
		return new Framework();
	}
	
	@Override
	public void notifyResolutionPhaseStateChange(ResolutionPhaseStateChangeEvent event) {
		if(event.getChange().equals(EResolutionPhaseStateChange.ACTIVATED)) {
			_elementsManager.setActiveElementSet(_elementSet);
		}
		
	}
}