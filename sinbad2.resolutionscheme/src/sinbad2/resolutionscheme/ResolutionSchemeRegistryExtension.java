package sinbad2.resolutionscheme;

import org.eclipse.core.runtime.IConfigurationElement;

import sinbad2.core.registry.RegistryExtension;

public class ResolutionSchemeRegistryExtension extends RegistryExtension {

	public ResolutionSchemeRegistryExtension(IConfigurationElement element) {
		super(element);
		_configuration = super.getConfiguration();
	}

	public String getElement(EResolutionSchemeElements element) {

		String result = null;

		if (_configuration != null) {
			result = _configuration.getAttribute(element.toString());
		}

		return result;
	}

	public String[] getPhasesID() {

		String[] result = null;

		if (_configuration != null) {
			IConfigurationElement[] phases = _configuration
					.getChildren(EResolutionSchemeElements.phase.toString());
			if (phases != null) {
				result = new String[phases.length];
				for (int i = 0; i < phases.length; ++i) {
					result[i] = phases[i]
							.getAttribute(EResolutionSchemeElements.resolution_phase
									.toString());
				}
			}
		}

		return result;

	}

}
