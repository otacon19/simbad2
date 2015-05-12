package flintstones.resolutionscheme.dm;

import flintstones.resolutionphase.ResolutionPhasesManager;
import flintstones.resolutionscheme.ResolutionSchemeImplementation;
import flintstones.resolutionscheme.state.ResolutionSchemeStateChangeEvent;


public class DecisionMaking extends ResolutionSchemeImplementation {
	
	public static final String ID = "flintstones.resolutionscheme.dm"; //$NON-NLS-1$
	
	@Override
	public ResolutionSchemeImplementation newInstance() {
		return new DecisionMaking();
	}
	
	@Override
	public void resolutionSchemeStateChange(ResolutionSchemeStateChangeEvent event) {

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
