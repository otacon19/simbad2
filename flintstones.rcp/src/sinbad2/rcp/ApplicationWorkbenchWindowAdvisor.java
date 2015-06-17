package sinbad2.rcp;

import java.util.List;

import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.contexts.IContextService;

import sinbad2.resolutionphase.ResolutionPhase;
import sinbad2.resolutionphase.ui.ResolutionPhaseUI;
import sinbad2.resolutionscheme.ResolutionScheme;
import sinbad2.resolutionscheme.ResolutionSchemesManager;
import sinbad2.resolutionscheme.ui.ResolutionSchemeUI;
import sinbad2.resolutionscheme.ui.ResolutionSchemesUIManager;
import sinbad2.resolutionscheme.ui.perspectives.MultiplePerspectives;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {			
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1024, 700));
		configurer.setTitle("Flintstones");
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(false);
		configurer.setShowPerspectiveBar(false);
		
		PlatformUI.getPreferenceStore().setValue(
				IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS,
				false);
	}
	
	@Override
	public void createWindowContents(Shell shell) {
		super.createWindowContents(shell);
		CBanner multiplePerspectives = findTopBanner(shell);
		
		//TODO todo perspectivas?
		ResolutionSchemesUIManager resolutionSchemesUIManager = ResolutionSchemesUIManager.getInstance();
		ResolutionSchemeUI resolutionSchemeUI = resolutionSchemesUIManager.getActivateResolutionSchemeUI();
		ResolutionSchemesManager resolutionSchemesManager = ResolutionSchemesManager.getInstance();
		ResolutionScheme resolutionScheme = resolutionSchemesManager.getActiveResolutionScheme();
		
		List<ResolutionPhase> phases = resolutionScheme.getPhases();
		ResolutionPhaseUI[] phasesUI = new ResolutionPhaseUI[phases.size()];
		for(int i = 0; i < phases.size(); ++i) {
			phasesUI[i] = resolutionSchemeUI.getResolutionPhaseUI(phases.get(i));
		}
		
		MultiplePerspectives perspectiveSwitcher = new MultiplePerspectives(getWindowConfigurer().getWindow(), multiplePerspectives, phasesUI);
		perspectiveSwitcher.fillSwitcherPerspectives();
		
		IContextService contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);
		contextService.activateContext("flintstones.core.ui");
	}

	private CBanner findTopBanner(Shell shell) {
		Control[] children = shell.getChildren();
		CBanner result = null;
		
		for(Control child: children) {
			if(child instanceof CBanner) {
				if(result != null) {
					throw new IllegalStateException("More than one CBanner");
				}
				result = (CBanner) child;
			}
		}
		if(result == null) {
			throw new IllegalStateException("No CBanner");
		}
		
		return result;
	}
}
