package sinbad2.resolutionscheme.ui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.resolutionphase.ResolutionPhase;
import sinbad2.resolutionphase.ui.ResolutionPhaseUI;
import sinbad2.resolutionphase.ui.ResolutionPhasesUIManager;
import sinbad2.resolutionscheme.ResolutionScheme;
import sinbad2.resolutionscheme.ResolutionSchemesManager;
import sinbad2.resolutionscheme.ui.state.EResolutionSchemeUIStateChanges;
import sinbad2.resolutionscheme.ui.state.IResolutionSchemeUIStateListener;
import sinbad2.resolutionscheme.ui.state.ResolutionSchemeUIStateChangeEvent;

public class ResolutionSchemeUI {
	
	private String _id;
	private String _name;
	private ResolutionScheme _resolutionScheme;
	private ResolutionSchemeUIRegistry _registry;
	
	private List<IResolutionSchemeUIStateListener> _listeners;
	
	private Map<String, ResolutionPhaseUI> _resolutionPhasesUI;
	private Map<String, String> _resolutionPhasesUIIndex;
	
	public ResolutionSchemeUI() {
		_id = null;
		_name = null;
		_resolutionScheme = null;
		_registry = null;
		
		_listeners = new LinkedList<IResolutionSchemeUIStateListener>();
	}
	
	public ResolutionSchemeUI(String id, String name, ResolutionScheme resolutionScheme, ResolutionSchemeUIRegistry registry) {
		this();
		setId(id);
		setName(name);
		setResolutionScheme(resolutionScheme);
		setRegistry(registry);
	}

	public String getId() {
		return _id;
	}

	public void setId(String id) {
		_id = id;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public ResolutionScheme getResolutionScheme() {
		
		if(_resolutionScheme == null) {
			ResolutionSchemesManager resolutionSchemesManager = ResolutionSchemesManager.getInstance();
			String resolutionSchemeID = _registry.getElement(EResolutionSchemeUIElements.scheme);
			_resolutionScheme = resolutionSchemesManager.getResolutionScheme(resolutionSchemeID);
		}
		
		return _resolutionScheme;
	}

	public void setResolutionScheme(ResolutionScheme resolutionScheme) {
		_resolutionScheme = resolutionScheme;
	}

	public ResolutionSchemeUIRegistry getRegistry() {
		return _registry;
	}

	public void setRegistry(ResolutionSchemeUIRegistry registry) {
		_registry = registry;
	}
	
	private void initResolutionPhasesUIIndex() {
		
		if(_resolutionPhasesUIIndex == null) {
			_resolutionPhasesUIIndex = _registry.getResolutionPhasesUIsIDs();
			
			ResolutionPhasesUIManager resolutionPhasesUIManager = ResolutionPhasesUIManager.getInstance();
			_resolutionPhasesUI = new HashMap<String, ResolutionPhaseUI>();
			for(String key: _resolutionPhasesUIIndex.keySet()) {
				_resolutionPhasesUI.put(key, resolutionPhasesUIManager.getUI(_resolutionPhasesUIIndex.get(key)));
			}
		}
	}
	
	public ResolutionPhaseUI getResolutionPhaseUI(String resolutionPhaseId) {

		if(_resolutionPhasesUIIndex == null) {
			initResolutionPhasesUIIndex();
		}
		
		return _resolutionPhasesUI.get(resolutionPhaseId);
	}
	
	public ResolutionPhaseUI getResolutionPhaseUI(ResolutionPhase resolutionPhase) {
		return getResolutionPhaseUI(resolutionPhase.getId());
	}
	
	public void activate() {
		
		if(_resolutionPhasesUIIndex == null) {
			initResolutionPhasesUIIndex();
		}
		
		ResolutionSchemesManager resolutionSchemesManager = ResolutionSchemesManager.getInstance();
		resolutionSchemesManager.activate(getResolutionScheme().getId());
		
		ResolutionPhaseUI resolutionPhaseUI;
		for(String resolutionPhaseId: _resolutionPhasesUIIndex.keySet()) {
			resolutionPhaseUI = getResolutionPhaseUI(resolutionPhaseId);
			resolutionPhaseUI.containerActivate();
		}
		
		notifyResolutionSchemeUIStateChange(new ResolutionSchemeUIStateChangeEvent(EResolutionSchemeUIStateChanges.ACTIVATED));
	}
	
	public void deactivate() {
		
		ResolutionSchemesManager resolutionSchemesManager = ResolutionSchemesManager.getInstance();
		resolutionSchemesManager.deactiveCurrentActive();
		
		ResolutionPhaseUI resolutionPhaseUI;
		for(String resolutionPhaseId: _resolutionPhasesUIIndex.keySet()) {
			resolutionPhaseUI = getResolutionPhaseUI(resolutionPhaseId);
			resolutionPhaseUI.containerDeactivate();
		}
		
		ResolutionPhasesUIManager resolutionPhasesUIManager = ResolutionPhasesUIManager.getInstance();
		resolutionPhasesUIManager.deactiveCurrentActive();
		
		notifyResolutionSchemeUIStateChange(new ResolutionSchemeUIStateChangeEvent(EResolutionSchemeUIStateChanges.DEACTIVATED));
	}
	
	public void registerResolutionSchemeUIStateListener(IResolutionSchemeUIStateListener listener) {
		_listeners.add(listener);
	}

	public void unregisterResolutionSchemeUIStateListener(IResolutionSchemeUIStateListener listener) {
		_listeners.remove(listener);
	}
	
	public void notifyResolutionSchemeUIStateChange(ResolutionSchemeUIStateChangeEvent event){
		for(IResolutionSchemeUIStateListener listener: _listeners) {
			listener.resolutionSchemeUIStateChange(event);
		}
	}
}
