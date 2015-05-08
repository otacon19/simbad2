package flintstones.resolutionphase.ui;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;


public class ResolutionPhasesUIManager {
	
	private final String EXTENSION_POINT = "flintstones.resolutionphase.ui"; //$NON-NLS-1$
	
	private static ResolutionPhasesUIManager _instance = null;
	
	private ResolutionPhaseUI _activeResolutionPhaseUI;
	
	private Map<String, ResolutionPhaseUIRegistry> _registers;
	private Map<String, ResolutionPhaseUI> _resolutionPhasesUIs;
	
	private ResolutionPhasesUIManager() {
		_activeResolutionPhaseUI = null;
		_registers = new HashMap<String, ResolutionPhaseUIRegistry>();
		_resolutionPhasesUIs = new HashMap<String, ResolutionPhaseUI>();
		loadRegisters();
	}
	
	public static ResolutionPhasesUIManager getInstance() {
		if(_instance == null) {
			_instance = new ResolutionPhasesUIManager();
		}
		
		return _instance;
	}
	
	public void loadRegisters() {
		
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(EXTENSION_POINT);
		
		ResolutionPhaseUIRegistry registry;
		for(IConfigurationElement extension: extensions) {
			registry = new ResolutionPhaseUIRegistry(extension);
			_registers.put(registry.getElement(EResolutionPhaseUIElements.id), registry);
		}
	}
	
	public String[] getIDs() {
		return _registers.keySet().toArray(new String[0]);
	}
	
	public ResolutionPhaseUIRegistry getRegistry(String id) {
		return _registers.get(id);
	}
	
	public ResolutionPhaseUI getUI(String id) {
		
		if(_resolutionPhasesUIs.containsKey(id)) {
			return _resolutionPhasesUIs.get(id);
		} else {
			try {
				ResolutionPhaseUIRegistry resolutionPhaseUIRegistry = getRegistry(id);
				
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
				
			} catch (Exception e) {
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

}
