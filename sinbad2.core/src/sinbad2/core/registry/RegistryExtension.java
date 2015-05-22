package sinbad2.core.registry;

import org.eclipse.core.runtime.IConfigurationElement;

public class RegistryExtension implements Cloneable {

	private IConfigurationElement _configuration;
	
	private RegistryExtension() {
		_configuration = null;
	}
	
	public RegistryExtension(IConfigurationElement element) {
		this();
		_configuration = element;
	}
	
	public IConfigurationElement getConfiguration() {
		return _configuration;
	}
	
	public String getAttribute(RegistryExtension element) {
		
		String result = null;
		
		if(_configuration != null) {
			result = _configuration.getAttribute(element.toString());
		}
		
		return result;
	}
	
	//TODO hashcode
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		
		RegistryExtension result = null;
		
		result = (RegistryExtension) super.clone();
		result._configuration = this._configuration;
		
		return result;
		
	}
}
