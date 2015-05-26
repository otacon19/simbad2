package sinbad2.valuation;

import org.eclipse.core.runtime.IConfigurationElement;

import sinbad2.core.registry.RegistryExtension;

public class ValuationRegistryExtension extends RegistryExtension {

	public ValuationRegistryExtension(IConfigurationElement element) {
		super(element);
	}
	
	public String getElement(EValuationElements element) {
		String result = null;
		
		if(_configuration != null) {
			result = _configuration.getAttribute(element.toString());
		}
		
		return result;
	}
}
