package sinbad2.resolutionscheme.dm;

import sinbad2.resolutionphase.ResolutionPhasesManager;
import sinbad2.resolutionscheme.ResolutionSchemeImplementation;
import sinbad2.resolutionscheme.state.ResolutionSchemeStateChangeEvent;


public class DecisionMaking extends ResolutionSchemeImplementation {
	
	public static final String ID = "flintstones.resolutionscheme.dm"; //$NON-NLS-1$
	
	@Override
	public ResolutionSchemeImplementation newInstance() {
		return new DecisionMaking();
	}
	
	@Override
	public void notifyResolutionSchemeStateChange(ResolutionSchemeStateChangeEvent event) {

		ResolutionPhasesManager resolutionPhasesManager = ResolutionPhasesManager.getInstance();
		
		switch(event.getChange()) {
			case ACTIVATED:
				resolutionPhasesManager.activate(_phasesNames.get(0));
				break;
			case DEACTIVATED:
				break;
		}
		
	}
}
