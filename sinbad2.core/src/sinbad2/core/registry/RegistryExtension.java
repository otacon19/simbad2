package sinbad2.core.registry;

import org.eclipse.core.runtime.IConfigurationElement;

public class RegistryExtension implements Cloneable {
	
	protected IConfigurationElement _configuration;
	
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
	
	@Override
	public Object clone() {
		
		RegistryExtension result = null;
		
		try {
			result = (RegistryExtension) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		result._configuration = this._configuration;
		
		return result;
		
	}
	
	//TODO hashCode

}
