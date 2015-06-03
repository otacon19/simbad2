package sinbad2.valuation.ui;

import org.eclipse.core.runtime.IConfigurationElement;

import sinbad2.core.registry.RegistryExtension;

public class ValuationUIRegistryExtension extends RegistryExtension {

	public ValuationUIRegistryExtension(IConfigurationElement element) {
		super(element);
	}
	
	public String getElement(EValuationUIElements element) {
		String result = null;
		
		if(_configuration != null) {
			result = _configuration.getAttribute(element.toString());
		}
		
		return result;
	}

}
