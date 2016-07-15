package sinbad2.resolutionscheme.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class ResolutionSchemesUIManager {
	
	private final String EXTENSION_POINT = "flintstones.resolutionscheme.ui"; //$NON-NLS-1$
	
	private static ResolutionSchemesUIManager _instance = null;
	
	private ResolutionSchemeUI _activeResolutionSchemeUI;
	
	private Map<String, ResolutionSchemeUIRegistryExtension> _registers;
	private Map<String, ResolutionSchemeUI> _resolutionSchemesUIs;
	
	private ResolutionSchemesUIManager() {
		_activeResolutionSchemeUI = null;
		_registers = new HashMap<String, ResolutionSchemeUIRegistryExtension>();
		_resolutionSchemesUIs = new HashMap<String, ResolutionSchemeUI>();
		loadRegistersExtension();
	}
	
	public static ResolutionSchemesUIManager getInstance() {
		if(_instance == null) {
			_instance = new ResolutionSchemesUIManager();
		}
		
		return _instance;
	}
	
	public void loadRegistersExtension() {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(EXTENSION_POINT);
		
		ResolutionSchemeUIRegistryExtension registry;
		for(IConfigurationElement extension: extensions) {
			registry = new ResolutionSchemeUIRegistryExtension(extension);
			_registers.put(registry.getElement(EResolutionSchemeUIElements.id), registry);
		}
	}
	
	public String[] getIdsRegisters() {
		return _registers.keySet().toArray(new String[0]);
	}
	
	public ResolutionSchemeUIRegistryExtension getRegistry(String id) {
		return _registers.get(id);
	}
	
	public ResolutionSchemeUI getUI(String id) {
		
		if(_resolutionSchemesUIs.containsKey(id)) {
			return _resolutionSchemesUIs.get(id);
		} else {
			try {
				return initializeResolutionSchemeUI(id);
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	public ResolutionSchemeUI getActivateResolutionSchemeUI() {
		return _activeResolutionSchemeUI;
	}
	
	public void activate(String id) {
		boolean needActivate = true;
		
		if(_activeResolutionSchemeUI != null) {
			if(!_activeResolutionSchemeUI.getId().equals(id)) {
				deactiveCurrentActive();
			} else {
				needActivate = false;
			}
		}
		
		if(needActivate) {
			_activeResolutionSchemeUI = getUI(id);
			if(_activeResolutionSchemeUI != null) {
				_activeResolutionSchemeUI.activate();
			}
		}
		
	}

	public void deactiveCurrentActive() {
		if(_activeResolutionSchemeUI != null) {
			_activeResolutionSchemeUI.deactivate();
			_activeResolutionSchemeUI = null;
		}
		
	}
	
	private ResolutionSchemeUI initializeResolutionSchemeUI(String id) {
		ResolutionSchemeUIRegistryExtension resolutionSchemeUIRegistry = getRegistry(id);
		
		ResolutionSchemeUI resolutionSchemeUI = new ResolutionSchemeUI();
		resolutionSchemeUI.setId(id);
		resolutionSchemeUI.setName(resolutionSchemeUIRegistry.getElement(EResolutionSchemeUIElements.name));
		resolutionSchemeUI.setRegistry(resolutionSchemeUIRegistry);
		
		_resolutionSchemesUIs.put(id, resolutionSchemeUI);
		
		return resolutionSchemeUI;
		
	}

}
