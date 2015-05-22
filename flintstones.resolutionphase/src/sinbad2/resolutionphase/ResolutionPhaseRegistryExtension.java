package sinbad2.resolutionphase;

import org.eclipse.core.runtime.IConfigurationElement;

import sinbad2.core.registry.RegistryExtension;

public class ResolutionPhaseRegistryExtension extends RegistryExtension {
	
	private IConfigurationElement _configuration;
	
	public ResolutionPhaseRegistryExtension(IConfigurationElement element) {
		super(element);
		_configuration = super.getConfiguration();
	}
	
	public String getAttribute(EResolutionPhaseElements element) {
		
		String result = null;
		
		if(_configuration!= null) {
			result = _configuration.getAttribute(element.toString());
		}
		
		return result;
	}
}
