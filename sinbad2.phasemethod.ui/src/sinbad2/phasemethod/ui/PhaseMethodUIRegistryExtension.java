package sinbad2.phasemethod.ui;

import org.eclipse.core.runtime.IConfigurationElement;

import sinbad2.core.registry.RegistryExtension;

public class PhaseMethodUIRegistryExtension extends RegistryExtension {
	
	public PhaseMethodUIRegistryExtension(IConfigurationElement element) {
		super(element);
	}
	
	public String getElement(EPhaseMethodUIElements element) {
		String result = null;
		
		if(_configuration != null) {
			result = super.getConfiguration().getAttribute(element.toString());
		}
		
		return result;
	
	}
	
	public String getViews(EPhaseMethodUIElements type) {
		
		String result = null;
		
		switch (type) {
			case view:
				IConfigurationElement uiConfiguration = _configuration.getChildren(EPhaseMethodUIElements.view.toString())[0];
				
				result = uiConfiguration.getAttribute(EPhaseMethodUIElements.id.toString());
		default:
			break;
		}
		
		return result;
	}
	
}
