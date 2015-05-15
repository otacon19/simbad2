package sinbad2.resolutionphase.ui;

import org.eclipse.core.runtime.IConfigurationElement;

public class ResolutionPhaseUIRegistry {
	
	private IConfigurationElement _configuration;
	
	private ResolutionPhaseUIRegistry() {
		_configuration = null;
	}
	
	public ResolutionPhaseUIRegistry(IConfigurationElement element) {
		this();
		_configuration = element;
	}
	
	public IConfigurationElement getConfiguration() {
		return _configuration;
	}
	
	public String getElement(EResolutionPhaseUIElements element) {
		String result = null;
		
		if(_configuration != null) {
			result = _configuration.getAttribute(element.toString());
		}
		
		return result;
	
	}
	
	public EResolutionPhaseUIType getUIType() {
		
		EResolutionPhaseUIType result = null;
		
		if(_configuration != null) {
			EResolutionPhaseUIType[] types = EResolutionPhaseUIType.values();
			int i = 0;
			boolean find = false;
			
			do{
				if(_configuration.getChildren(types[i].toString()).length > 0) {
					result = types[i];
					find = true;
				} else {
					i++;
				}
			} while ((i < types.length) && (!find));
		}
		
		return result;
	}
	
	public String getUIID() {
		EResolutionPhaseUIType type = getUIType();
		
		return getUIID(type);
	}
	
	public String getUIID(EResolutionPhaseUIType type) {
		
		String result = null;
		
		switch (type) {
			case perspective:
				IConfigurationElement uiConfiguration = _configuration.getChildren(EResolutionPhaseUIType.perspective.toString())[0];
				
				result = uiConfiguration.getAttribute(EResolutionPhaseUIElements.id.toString());
		}
		
		return result;
	}

}
