package sinbad2.resolutionphase;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class ResolutionPhasesManager {
	
	private final String EXTENSION_POINT = "flintstones.resolutionphase"; //$NON-NLS-1$
	
	private static ResolutionPhasesManager _instance = null;
	
	private ResolutionPhase _activeResolutionPhase;
	
	private Map<String, ResolutionPhaseRegistryExtension> _registers;
	private Map<String, ResolutionPhase> _resolutionPhases;
	private Map<ResolutionPhaseImplementation, String> _implementationResolutionPhases;
	
	private ResolutionPhasesManager() {
		_activeResolutionPhase = null;
		
		_registers = new HashMap<String, ResolutionPhaseRegistryExtension>();
		_resolutionPhases = new HashMap<String, ResolutionPhase>();
		_implementationResolutionPhases = new HashMap<ResolutionPhaseImplementation, String>();
		
		loadRegisters();
	}
	
	public static ResolutionPhasesManager getInstance() {
		if(_instance == null) {
			_instance = new ResolutionPhasesManager();
		}
		
		return _instance;
	}
	
	private void loadRegisters() {
		
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(EXTENSION_POINT);
		
		ResolutionPhaseRegistryExtension registry;
		for(IConfigurationElement extension: extensions) {
			registry = new ResolutionPhaseRegistryExtension(extension);
			_registers.put(registry.getAttribute(EResolutionPhaseElements.id), registry);
		}
		
	}
	
	public String[] getIDs() {
		return _registers.keySet().toArray(new String[0]);
	}
	
	public ResolutionPhaseRegistryExtension getRegistry(String id) {
		return _registers.get(id);
	}
	
	public void setImplementationResolutionPhase(ResolutionPhaseImplementation implementation, String resolutionPhaseId) {
		_implementationResolutionPhases.put(implementation, resolutionPhaseId);
	}
	
	public ResolutionPhase getImplementationResolutionPhase(ResolutionPhaseImplementation implementation) {
		ResolutionPhase result = null;
		
		String id = _implementationResolutionPhases.get(implementation);
		
		if(id != null) {
			result = _resolutionPhases.get(id);
		}
		
		return result;
	}
	
	public ResolutionPhase getResolutionPhase(String id) {
		
		if(_resolutionPhases.containsKey(id)) {
			return _resolutionPhases.get(id);
		} else {
			try {
				return initializeResolutionPhase(id);
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	public ResolutionPhase getActivateResolutionPhase() {
		return _activeResolutionPhase;
	}
	
	public void deactiveCurrentActive() {
		if(_activeResolutionPhase != null) {
			_activeResolutionPhase.deactivate();
			_activeResolutionPhase = null;
		}
	}
	
	public void activate(String id) {
		boolean needActivate = true;
		
		if(_activeResolutionPhase != null) {
			if(!_activeResolutionPhase.getId().equals(id)) {
				deactiveCurrentActive();
			} else {
				needActivate = false;
			}
		}
		
		if(needActivate) {
			_activeResolutionPhase = getResolutionPhase(id);
			if(_activeResolutionPhase != null) {
				_activeResolutionPhase.activate();
			}
		}
	}
	
	private ResolutionPhase initializeResolutionPhase(String id) {
		ResolutionPhaseRegistryExtension resolutionPhaseRegistry = getRegistry(id);
		
		ResolutionPhase resolutionPhase = new ResolutionPhase();
		resolutionPhase.setId(id);
		resolutionPhase.setName(resolutionPhaseRegistry.getAttribute(EResolutionPhaseElements.name));
		resolutionPhase.setRegistry(resolutionPhaseRegistry);
		
		_resolutionPhases.put(id, resolutionPhase);
		
		return resolutionPhase;
	}

}
