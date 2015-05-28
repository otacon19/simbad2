package sinbad2.rcp;

import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import sinbad2.resolutionphase.ui.EResolutionPhaseUIType;
import sinbad2.resolutionphase.ui.ResolutionPhaseUI;
import sinbad2.resolutionphase.ui.ResolutionPhasesUIManager;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
	

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}
	
	public String getInitialWindowPerspectiveId() {
		ResolutionPhasesUIManager resolutionPhasesUIManager = ResolutionPhasesUIManager.getInstance();
		ResolutionPhaseUI resolutionPhaseUI = resolutionPhasesUIManager.getActiveResolutionPhasesUI();
		
		resolutionPhaseUI = resolutionPhasesUIManager.getUI("flintstones.resolutionphase.framework.ui");
		
		if(EResolutionPhaseUIType.perspective == resolutionPhaseUI.getResolutionPhaseUIType()) {
			return resolutionPhaseUI.getUIId();
		} else {
			return null;
		}
	}

}
