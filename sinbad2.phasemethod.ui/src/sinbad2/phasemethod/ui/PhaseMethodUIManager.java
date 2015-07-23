package sinbad2.phasemethod.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;


public class PhaseMethodUIManager {
	
private final String EXTENSION_POINT = "flintstones.phasemethod.ui"; //$NON-NLS-1$
	
	private static PhaseMethodUIManager _instance = null;
	
	private PhaseMethodUI _activeResolutionPhaseUI;
	
	private Map<String, PhaseMethodUIRegistryExtension> _registers;
	private Map<String, PhaseMethodUI> _phasesMethodsUIs;
	
	private PhaseMethodUIManager() {
		_activeResolutionPhaseUI = null;
		_registers = new HashMap<String, PhaseMethodUIRegistryExtension>();
		_phasesMethodsUIs = new HashMap<String, PhaseMethodUI>();
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
	
	public PhaseMethodUI getActiveResolutionPhasesUI() {
		return _activeResolutionPhaseUI;
	}
	
	public void deactiveCurrentActive() {
		if(_activeResolutionPhaseUI != null) {
			_activeResolutionPhaseUI.deactivate();
			_activeResolutionPhaseUI = null;
		}
	}
	
	public void activate(String id) {
		boolean needActivate = true;
		
		if(_activeResolutionPhaseUI != null) {
			if(!_activeResolutionPhaseUI.getId().equals(id)) {
				deactiveCurrentActive();
			} else {
				needActivate = false;
			}
		}
		
		if(needActivate) {
			_activeResolutionPhaseUI = getUI(id);
			if(_activeResolutionPhaseUI != null) {
				_activeResolutionPhaseUI.activate();
			}
		}
	}
	
	private PhaseMethodUI initializeResolutionPhaseUI(String id) {
		PhaseMethodUIRegistryExtension phaseMethodUIRegistry = getRegistry(id);
		
		PhaseMethodUI phaseMethodUI = new PhaseMethodUI();
		phaseMethodUI.setId(id);
		phaseMethodUI.setName(phaseMethodUIRegistry.getElement(EPhaseMethodUIElements.name));
		phaseMethodUI.setPhaseMethodId(phaseMethodUIRegistry.getElement(EPhaseMethodUIElements.phase));
		
		phaseMethodUI.setRegistry(phaseMethodUIRegistry);
		phaseMethodUI.getPhaseMethod().registerPhaseMethodStateListener(phaseMethodUI);

		
		_phasesMethodsUIs.put(id, phaseMethodUI);
		
		return phaseMethodUI;
	}
	
}
