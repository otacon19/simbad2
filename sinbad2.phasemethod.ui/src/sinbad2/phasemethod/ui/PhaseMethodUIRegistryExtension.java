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
	
	public String[] getViewsID() {
		String[] result = null;

		if (_configuration != null) {
			IConfigurationElement[] phases = _configuration.getChildren(EPhaseMethodUIElements.view.toString());
			if (phases != null) {
				result = new String[phases.length];
				for (int i = 0; i < phases.length; ++i) {
					result[i] = phases[i].getAttribute(EPhaseMethodUIElements.id.toString());
				}
			}
		}

		return result;

	}
	
}
