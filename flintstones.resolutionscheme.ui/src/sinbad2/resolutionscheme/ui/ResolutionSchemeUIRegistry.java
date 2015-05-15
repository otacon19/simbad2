package sinbad2.resolutionscheme.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;

import sinbad2.resolutionphase.ui.EResolutionPhaseUIElements;
import sinbad2.resolutionphase.ui.ResolutionPhaseUIRegistry;
import sinbad2.resolutionphase.ui.ResolutionPhasesUIManager;

public class ResolutionSchemeUIRegistry {
	
	private IConfigurationElement _configuration;
	
	private ResolutionSchemeUIRegistry() {
		_configuration = null;
	}
	
	public ResolutionSchemeUIRegistry(IConfigurationElement element) {
		this();
		_configuration = element;
	}
	
	public IConfigurationElement getConfiguration() {
		return _configuration;
	}
	
	public String getElement(EResolutionSchemeUIElements element) {
		
		String result = null;
		
		if(_configuration != null) {
			result = _configuration.getAttribute(element.toString());
		}
		
		return result;
	}
	
	public Map<String, String> getResolutionPhasesUIsIDs() {
		
		Map<String, String> result = new HashMap<String, String>();
		
		if(_configuration != null) {
			IConfigurationElement[] uis = _configuration.getChildren(EResolutionSchemeUIElements.ui.toString());
			if(uis != null) {
				String resolutionPhaseUIID;
				String resolutionPhaseID;
				ResolutionPhaseUIRegistry resolutionPhaseUIRegistry;
				ResolutionPhasesUIManager resolutionPhasesUIManager = ResolutionPhasesUIManager.getInstance();
				for(int i = 0; i < uis.length; ++i) {
					resolutionPhaseUIID = uis[i].getAttribute(EResolutionSchemeUIElements.id.toString());
					resolutionPhaseUIRegistry = resolutionPhasesUIManager.getRegistry(resolutionPhaseUIID);
					resolutionPhaseID = resolutionPhaseUIRegistry.getElement(EResolutionPhaseUIElements.resolution_phase);
					result.put(resolutionPhaseID, resolutionPhaseUIID);
				}
			}
		}
		
		return result;
	}
}
