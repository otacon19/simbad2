package sinbad2.resolutionphase.ui;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;


public class ResolutionPhasesUIManager {
	
	private final String EXTENSION_POINT = "flintstones.resolutionphase.ui"; //$NON-NLS-1$
	
	private static ResolutionPhasesUIManager _instance = null;
	
	private ResolutionPhaseUI _activeResolutionPhaseUI;
	
	private Map<String, ResolutionPhaseUIRegistryExtension> _registers;
	private Map<String, ResolutionPhaseUI> _resolutionPhasesUIs;
	
	private ResolutionPhasesUIManager() {
		_activeResolutionPhaseUI = null;
		_registers = new HashMap<String, ResolutionPhaseUIRegistryExtension>();
		_resolutionPhasesUIs = new HashMap<String, ResolutionPhaseUI>();
		loadRegistersExtension();
	}
	
	public static ResolutionPhasesUIManager getInstance() {
		if(_instance == null) {
			_instance = new ResolutionPhasesUIManager();
		}
		
		return _instance;
	}
	
	public void loadRegistersExtension() {
		
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(EXTENSION_POINT);
		
		ResolutionPhaseUIRegistryExtension registry;
		for(IConfigurationElement extension: extensions) {
			registry = new ResolutionPhaseUIRegistryExtension(extension);
			_registers.put(registry.getElement(EResolutionPhaseUIElements.id), registry);
		}
	}
	
	public String[] getIdsRegisters() {
		return _registers.keySet().toArray(new String[0]);
	}
	
	public ResolutionPhaseUIRegistryExtension getRegistry(String id) {
		return _registers.get(id);
	}
	
	public ResolutionPhaseUI getUI(String id) {
		
		if(_resolutionPhasesUIs.containsKey(id)) {
			return _resolutionPhasesUIs.get(id);
		} else {
			try {
				return initializeResolutionPhaseUI(id);
			} catch (Exception e) {
				e.getStackTrace();
				
				return null;
			}
		}
	}
	
	public ResolutionPhaseUI getActiveResolutionPhasesUI() {
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
	
	private ResolutionPhaseUI initializeResolutionPhaseUI(String id) {
		ResolutionPhaseUIRegistryExtension resolutionPhaseUIRegistry = getRegistry(id);
		
		ResolutionPhaseUI resolutionPhaseUI = new ResolutionPhaseUI();
		resolutionPhaseUI.setId(id);
		resolutionPhaseUI.setName(resolutionPhaseUIRegistry.getElement(EResolutionPhaseUIElements.name));
		resolutionPhaseUI.setResolutionPhaseId(resolutionPhaseUIRegistry.getElement(EResolutionPhaseUIElements.resolution_phase));
		EResolutionPhaseUIType uiType = resolutionPhaseUIRegistry.getUIType();
		resolutionPhaseUI.setResolutionPhaseUIType(uiType);
		resolutionPhaseUI.setUiId(resolutionPhaseUIRegistry.getUIID(uiType));
		
		resolutionPhaseUI.setRegistry(resolutionPhaseUIRegistry);
		resolutionPhaseUI.getResolutionPhase().registerResolutionPhaseStateListener(resolutionPhaseUI);

		
		_resolutionPhasesUIs.put(id, resolutionPhaseUI);
		
		return resolutionPhaseUI;
	}

}
