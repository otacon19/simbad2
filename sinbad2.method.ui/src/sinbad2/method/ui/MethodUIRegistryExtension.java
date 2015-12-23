package sinbad2.method.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;

import sinbad2.core.registry.RegistryExtension;
import sinbad2.phasemethod.ui.EPhaseMethodUIElements;
import sinbad2.phasemethod.ui.PhaseMethodUIManager;
import sinbad2.phasemethod.ui.PhaseMethodUIRegistryExtension;

public class MethodUIRegistryExtension extends RegistryExtension {
	
	public MethodUIRegistryExtension(IConfigurationElement element) {
		super(element);
		_configuration = super.getConfiguration();
	}

	public String getElement(EMethodUIElements element) {

		String result = null;

		if (_configuration != null) {
			result = _configuration.getAttribute(element.toString());
		}

		return result;
	}

	public Map<String, String> getPhasesMethodUIsIDs() {
		
		Map<String, String> result = new HashMap<String, String>();
		
		if(_configuration != null) {
			IConfigurationElement[] uis = _configuration.getChildren(EMethodUIElements.ui.toString());
			if(uis != null) {
				String phaseMethodUIID;
				String phaseMethodID;
				PhaseMethodUIRegistryExtension phaseMethodUIRegistry;
				PhaseMethodUIManager phasesUIManager = PhaseMethodUIManager.getInstance();
				for(int i = 0; i < uis.length; ++i) {
					phaseMethodUIID = uis[i].getAttribute(EMethodUIElements.id.toString());
					phaseMethodUIRegistry = phasesUIManager.getRegistry(phaseMethodUIID);
					phaseMethodID = phaseMethodUIRegistry.getElement(EPhaseMethodUIElements.phase);
					result.put(phaseMethodID, phaseMethodUIID);
				}
			}
		}
		
		return result;
	}
	
}
