package flintstones.resolutionscheme;

import org.eclipse.core.runtime.IConfigurationElement;

public class ResolutionSchemeRegistry {
	
	private IConfigurationElement _configuration;
	
	private ResolutionSchemeRegistry() {
		_configuration = null;
	}
	
	public ResolutionSchemeRegistry(IConfigurationElement element) {
		this();
		_configuration = element;
	}
	
	public IConfigurationElement getConfiguration() {
		return _configuration;
	}
	
	public String getElement(EResolutionSchemeElements element) {
		 
		String result = null;
		
		if(_configuration != null) {
			result = _configuration.getAttribute(element.toString());
		}
		
		return result;
	}
	
	public String[] getPhasesID() {
		
		String[] result = null;
		
		if(_configuration != null) {
			IConfigurationElement[] phases = _configuration.getChildren(EResolutionSchemeElements.phase.toString());
			if(phases != null) {
				result = new String[phases.length];
				for(int i = 0; i < phases.length; ++i) {
					result[i] = phases[i].getAttribute(EResolutionSchemeElements.resolution_phase.toString());
				}
			}
		}
		
		return result;
		
	}
	
	//TODO hasCode
	
}
