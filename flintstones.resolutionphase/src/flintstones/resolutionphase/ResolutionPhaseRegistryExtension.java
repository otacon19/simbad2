package flintstones.resolutionphase;

import org.eclipse.core.runtime.IConfigurationElement;

public class ResolutionPhaseRegistryExtension implements Cloneable {

	private IConfigurationElement _configuration;
	
	private ResolutionPhaseRegistryExtension() {
		_configuration = null;
	}
	
	public ResolutionPhaseRegistryExtension(IConfigurationElement element) {
		this();
		_configuration = element;
	}
	
	public IConfigurationElement getConfiguration() {
		return _configuration;
	}
	
	public String getAttribute(EResolutionPhaseElements element) {
		
		String result = null;
		
		if(_configuration != null) {
			result = _configuration.getAttribute(element.toString());
		}
		
		return result;
	}
	
	//TODO hash
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		
		ResolutionPhaseRegistryExtension result = null;
		
		result = (ResolutionPhaseRegistryExtension) super.clone();
		result._configuration = this._configuration;
		
		return result;
		
	}
}
