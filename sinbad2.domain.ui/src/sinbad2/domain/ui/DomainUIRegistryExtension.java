package sinbad2.domain.ui;

import org.eclipse.core.runtime.IConfigurationElement;

import sinbad2.core.registry.RegistryExtension;

public class DomainUIRegistryExtension extends RegistryExtension {

	public DomainUIRegistryExtension(IConfigurationElement element) {
		super(element);
	}
	
	public String getElement(EDomainUIElements element) {
		String result = null;
		
		if(_configuration != null) {
			result = _configuration.getAttribute(element.toString());
		}
		
		return result;
	}

}
