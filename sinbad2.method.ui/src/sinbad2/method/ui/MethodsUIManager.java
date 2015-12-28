package sinbad2.method.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;


public class MethodsUIManager {
	
private final String EXTENSION_POINT = "flintstones.method.ui"; //$NON-NLS-1$
	
	private static MethodsUIManager _instance = null;
	
	private MethodUI _activeMethodUI;
	
	private Map<String, MethodUIRegistryExtension> _registers;
	private Map<String, MethodUI> _methodsUIs;
	
	private MethodsUIManager() {
		_activeMethodUI = null;
		_registers = new HashMap<String, MethodUIRegistryExtension>();
		_methodsUIs = new HashMap<String, MethodUI>();
		loadRegistersExtension();
	}
	
	public static MethodsUIManager getInstance() {
		if(_instance == null) {
			_instance = new MethodsUIManager();
		}
		
		return _instance;
	}
	
	public void loadRegistersExtension() {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(EXTENSION_POINT);
		
		MethodUIRegistryExtension registry;
		for(IConfigurationElement extension: extensions) {
			registry = new MethodUIRegistryExtension(extension);
			_registers.put(registry.getElement(EMethodUIElements.id), registry);
		}
	}
	
	public String[] getIdsRegisters() {
		return _registers.keySet().toArray(new String[0]);
	}
	
	public MethodUIRegistryExtension getRegistry(String id) {
		return _registers.get(id);
	}
	
	public MethodUI getUI(String id) {
		
		if(_methodsUIs.containsKey(id)) {
			return _methodsUIs.get(id);
		} else {
			try {
				return initializeMethodUI(id);
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	public MethodUI getActivateMethodUI() {
		return _activeMethodUI;
	}
	
	public void activate(String id) {
		boolean needActivate = true;
		
		if(_activeMethodUI != null) {
			if(!_activeMethodUI.getId().equals(id)) {
				deactiveCurrentActive();
			} else {
				needActivate = false;
			}
		}
		
		if(needActivate) {
			_activeMethodUI = getUI(id);
			if(_activeMethodUI != null) {
				_activeMethodUI.activate();
			}
		}
		
	}

	public void deactiveCurrentActive() {
		if(_activeMethodUI != null) {
			_activeMethodUI.deactivate();
			_activeMethodUI = null;
		}
		
	}
	
	private MethodUI initializeMethodUI(String id) {
		MethodUIRegistryExtension methodUIRegistry = getRegistry(id);
		
		MethodUI methodUI = new MethodUI();
		methodUI.setId(id);
		methodUI.setName(methodUIRegistry.getElement(EMethodUIElements.name));
		methodUI.setRegistry(methodUIRegistry);
		
		_methodsUIs.put(id, methodUI);
		
		return methodUI;
		
	}
	
}
