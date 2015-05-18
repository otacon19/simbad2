package sinbad2.resolutionphase.ui;

import java.util.LinkedList;
import java.util.List;

import sinbad2.resolutionphase.ResolutionPhase;
import sinbad2.resolutionphase.ResolutionPhasesManager;
import sinbad2.resolutionphase.ui.state.EResolutionPhaseUIStateChanges;
import sinbad2.resolutionphase.ui.state.IResolutionPhaseUIStateListener;
import sinbad2.resolutionphase.ui.state.ResolutionPhaseUIStateChangeEvent;

public class ResolutionPhaseUI {
	
	private String _id;
	private String _name;
	private String _resolutionPhaseId;
	private ResolutionPhase _resolutionPhase;
	private String _uiId;
	private Object _ui;
	
	private EResolutionPhaseUIType _resolutionPhaseUIType;
	private ResolutionPhaseUIRegistry _registry;
	
	private List<IResolutionPhaseUIStateListener> _listeners;
	
	
	public ResolutionPhaseUI() {
		_id = null;
		_name = null;
		_resolutionPhaseId = null;
		_resolutionPhase = null;
		_uiId = null;
		_ui = null;
		_resolutionPhaseUIType = null;
		_registry = null;
		
		_listeners = new LinkedList<IResolutionPhaseUIStateListener>();
		
	}
	
	public ResolutionPhaseUI(String id, String name, String resolutionPhaseId, ResolutionPhase resolutionPhase, String uiId, Object ui,
			EResolutionPhaseUIType resolutionPhaseUIType, ResolutionPhaseUIRegistry registry) {
		this();
		setId(id);
		setName(name);
		setResolutionPhaseId(resolutionPhaseId);
		setResolutionPhase(resolutionPhase);
		setUiId(uiId);
		setUi(ui);
		setResolutionPhaseUIType(resolutionPhaseUIType);
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

	public String getResolutionPhaseId() {
		return _resolutionPhaseId;
	}

	public void setResolutionPhaseId(String resolutionPhaseId) {
		_resolutionPhaseId = resolutionPhaseId;
	}

	public ResolutionPhase getResolutionPhase() {
		if(_resolutionPhase == null) {
			ResolutionPhasesManager resolutionPhasesManager = ResolutionPhasesManager.getInstance();
			_resolutionPhase = resolutionPhasesManager.getResolutionPhase(_resolutionPhaseId);
		}
		
		return _resolutionPhase;
	}

	public void setResolutionPhase(ResolutionPhase resolutionPhase) {
		_resolutionPhase = resolutionPhase;
	}

	public String getUiId() {
		return _uiId;
	}

	public void setUiId(String uiId) {
		_uiId = uiId;
	}

	public Object getUi() {
		return _ui;
	}

	public void setUi(Object ui) {
		_ui = ui;
	}

	public EResolutionPhaseUIType getResolutionPhaseUIType() {
		return _resolutionPhaseUIType;
	}

	public void setResolutionPhaseUIType(EResolutionPhaseUIType resolutionPhaseUIType) {
		_resolutionPhaseUIType = resolutionPhaseUIType;
	}

	public ResolutionPhaseUIRegistry getRegistry() {
		return _registry;
	}

	public void setRegistry(ResolutionPhaseUIRegistry registry) {
		_registry = registry;
	}
	
	public void activate() {
		notifyResolutionPhaseUIStateChange(new ResolutionPhaseUIStateChangeEvent(EResolutionPhaseUIStateChanges.ACTIVATED));	
	}
	
	public void deactivate() {
		notifyResolutionPhaseUIStateChange(new ResolutionPhaseUIStateChangeEvent(EResolutionPhaseUIStateChanges.DEACTIVATED));	
	}
	
	public void containerActivate() {
		notifyResolutionPhaseUIStateChange(new ResolutionPhaseUIStateChangeEvent(EResolutionPhaseUIStateChanges.CONTAINER_ACTIVATED));	
	}
	
	public void containerDeactivate() {
		notifyResolutionPhaseUIStateChange(new ResolutionPhaseUIStateChangeEvent(EResolutionPhaseUIStateChanges.CONTAINER_DEACTIVATED));
	}
	
	public void registerResolutionPhaseUIStateListener(IResolutionPhaseUIStateListener listener) {
		_listeners.add(listener);
	}
	
	public void unregisterResolutionPhaseUIStateListener(IResolutionPhaseUIStateListener listener) {
		_listeners.remove(listener);
	}
	
	public void notifyResolutionPhaseUIStateChange(ResolutionPhaseUIStateChangeEvent event) {
		for(IResolutionPhaseUIStateListener listener: _listeners) {
			listener.resolutionPhaseUIStateChange(event);
		}
	}
}
