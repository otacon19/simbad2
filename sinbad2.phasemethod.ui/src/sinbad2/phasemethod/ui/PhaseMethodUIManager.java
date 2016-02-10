package sinbad2.phasemethod.ui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.part.ViewPart;


public class PhaseMethodUIManager {
	
private final String EXTENSION_POINT = "flintstones.phasemethod.ui"; //$NON-NLS-1$
	
	private static PhaseMethodUIManager _instance = null;
	
	private PhaseMethodUI _activePhaseMethodUI;
	private ViewPart _activeStep;
	
	private Map<String, PhaseMethodUIRegistryExtension> _registers;
	private Map<String, PhaseMethodUI> _phasesMethodsUIs;
	private Map<String, List<ViewPart>> _phasesSteps;
	
	private PhaseMethodUIManager() {
		_activePhaseMethodUI = null;
		_activeStep = null;
		
		_registers = new HashMap<String, PhaseMethodUIRegistryExtension>();
		_phasesMethodsUIs = new HashMap<String, PhaseMethodUI>();
		_phasesSteps = new HashMap<String, List<ViewPart>>();
		
		loadRegistersExtension();
	}
	
	public static PhaseMethodUIManager getInstance() {
		if(_instance == null) {
			_instance = new PhaseMethodUIManager();
		}
		
		return _instance;
	}
	
	public void loadRegistersExtension() {
		
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(EXTENSION_POINT);
		
		PhaseMethodUIRegistryExtension registry;
		for(IConfigurationElement extension: extensions) {
			registry = new PhaseMethodUIRegistryExtension(extension);
			_registers.put(registry.getElement(EPhaseMethodUIElements.id), registry);
		}
	}
	
	public String[] getIdsRegisters() {
		return _registers.keySet().toArray(new String[0]);
	}
	
	public PhaseMethodUIRegistryExtension getRegistry(String id) {
		return _registers.get(id);
	}
	
	public PhaseMethodUI getUI(String id) {
		
		if(_phasesMethodsUIs.containsKey(id)) {
			return _phasesMethodsUIs.get(id);
		} else {
			try {
				return initializeResolutionPhaseUI(id);
			} catch (Exception e) {
				e.getStackTrace();
				
				return null;
			}
		}
	}
	
	public Map<String, List<ViewPart>> getPhasesSteps() {
		return _phasesSteps;
	}
	
	public List<ViewPart> getSteps(String id) {
		return _phasesSteps.get(id);
	}
	
	public ViewPart getStep(String id, int numStep) {
		return _phasesSteps.get(id).get(numStep);
	}
	
	public PhaseMethodUI getActiveResolutionPhasesUI() {
		return _activePhaseMethodUI;
	}
	
	public void deactiveCurrentActive() {
		if(_activePhaseMethodUI != null) {
			_activePhaseMethodUI.deactivate();
			_activePhaseMethodUI = null;
		}
	}
	
	public void activate(String id) {
		boolean needActivate = true;
		
		if(_activePhaseMethodUI != null) {
			if(!_activePhaseMethodUI.getId().equals(id)) {
				deactiveCurrentActive();
			} else {
				needActivate = false;
			}
		}
		
		if(needActivate) {
			_activePhaseMethodUI = getUI(id);
			if(_activePhaseMethodUI != null) {
				_activePhaseMethodUI.activate();
			}
		}
	}
	
	public void activateStep(ViewPart step) {
		_activeStep = step;
	}
	
	public void deactivateStep() {
		_activeStep = null;
	}
	
	public ViewPart getNextStep() {
		List<ViewPart> steps = _phasesSteps.get(_activePhaseMethodUI.getId());
		int currentStep = steps.indexOf(_activeStep);
		
		if(currentStep + 1 >= steps.size()) {
			return null;
		}
		
		return steps.get(currentStep + 1);
	}
	
	
	private PhaseMethodUI initializeResolutionPhaseUI(String id) {
		PhaseMethodUIRegistryExtension phaseMethodUIRegistry = getRegistry(id);
		
		PhaseMethodUI phaseMethodUI = new PhaseMethodUI();
		phaseMethodUI.setId(id);
		phaseMethodUI.setName(phaseMethodUIRegistry.getElement(EPhaseMethodUIElements.name));
		phaseMethodUI.setPhaseMethodId(phaseMethodUIRegistry.getElement(EPhaseMethodUIElements.phasemethod));
		
		phaseMethodUI.setRegistry(phaseMethodUIRegistry);
		phaseMethodUI.getPhaseMethod().registerPhaseMethodStateListener(phaseMethodUI);

		_phasesMethodsUIs.put(id, phaseMethodUI);
		
		IConfigurationElement[] views = phaseMethodUIRegistry.getConfiguration().getChildren(EPhaseMethodUIElements.view.toString());
		try {
			List<ViewPart> steps = new LinkedList<ViewPart>();
			for(IConfigurationElement view: views) {
				ViewPart step = (ViewPart) view.createExecutableExtension(EPhaseMethodUIElements.step.toString());
				steps.add(step);
			}
			
			_phasesSteps.put(id, steps);
			
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return phaseMethodUI;
	}
	
}
