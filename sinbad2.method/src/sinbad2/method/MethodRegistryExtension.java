package sinbad2.method;

import org.eclipse.core.runtime.IConfigurationElement;

import sinbad2.core.registry.RegistryExtension;

public class MethodRegistryExtension extends RegistryExtension {
	
	public MethodRegistryExtension(IConfigurationElement element) {
		super(element);
		_configuration = super.getConfiguration();
	}

	public String getElement(EMethodElements element) {

		String result = null;

		if (_configuration != null) {
			result = _configuration.getAttribute(element.toString());
		}

		return result;
	}

	public String[] getPhasesMethodId() {

		String[] result = null;

		if (_configuration != null) {
			IConfigurationElement[] phases = _configuration.getChildren(EMethodElements.phasemethod.toString());
			if (phases != null) {
				result = new String[phases.length];
				for (int i = 0; i < phases.length; ++i) {
					result[i] = phases[i].getAttribute(EMethodElements.phase.toString());
				}
			}
		}

		return result;
	}
}
