package sinbad2.phasemethod;

import org.eclipse.core.runtime.IConfigurationElement;

import sinbad2.core.registry.RegistryExtension;

public class PhaseMethodRegistryExtension extends RegistryExtension {
	
	public PhaseMethodRegistryExtension(IConfigurationElement element) {
		super(element);
	}
	
	public String getAttribute(EPhaseMethodElements element) {
		
		String result = null;
		
		if(_configuration!= null) {
			result = _configuration.getAttribute(element.toString());
		}
		
		return result;
	}

}
