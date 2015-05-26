package sinbad2.domain;

import org.eclipse.core.runtime.IConfigurationElement;

import sinbad2.core.registry.RegistryExtension;


public class DomainRegistryExtension extends RegistryExtension {
	
	public DomainRegistryExtension(IConfigurationElement element) {
		super(element);
	}
	
	public String getAttribute(EDomainElements element) {
		
		String result = null;
		
		if(_configuration != null) {
			result = _configuration.getAttribute(element.toString());
		}
		
		return result;
	}
}
