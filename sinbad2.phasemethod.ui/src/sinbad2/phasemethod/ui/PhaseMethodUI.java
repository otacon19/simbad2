package sinbad2.phasemethod.ui;

import java.util.LinkedList;
import java.util.List;

import sinbad2.phasemethod.PhaseMethod;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.listener.IPhaseMethodStateListener;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.phasemethod.ui.state.EPhaseMethodUIStateChange;
import sinbad2.phasemethod.ui.state.IPhaseMethodUIStateListener;
import sinbad2.phasemethod.ui.state.PhaseMethodUIStateChangeEvent;

public class PhaseMethodUI implements IPhaseMethodStateListener {
	
	private String _id;
	private String _name;
	private String _phaseMethodId;
	private PhaseMethod _phaseMethod;
	private String _uiId;
	private Object _ui;
	
	private PhaseMethodUIRegistryExtension _registry;
	
	private List<IPhaseMethodUIStateListener> _listeners;
	
	
	public PhaseMethodUI() {
		_id = null;
		_name = null;
		_phaseMethodId = null;
		_phaseMethod = null;
		_uiId = null;
		_ui = null;
		_registry = null;
		
		_listeners = new LinkedList<IPhaseMethodUIStateListener>();
		
	}
	
	public PhaseMethodUI(String id, String name, String phaseMethodId, PhaseMethod phaseMethod, String uiId, Object ui,
			PhaseMethodUIRegistryExtension registry) {
		this();
		setId(id);
		setName(name);
		setPhaseMethodId(phaseMethodId);
		setPhaseMethod(phaseMethod);
		setUiId(uiId);
		setUi(ui);
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

	public String getPhaseMethodId() {
		return _phaseMethodId;
	}

	public void setPhaseMethodId(String phaseMethodId) {
		_phaseMethodId = phaseMethodId;
	}

	public PhaseMethod getPhaseMethod() {
		if(_phaseMethod == null) {
			PhasesMethodManager phasesMethodManager = PhasesMethodManager.getInstance();
			_phaseMethod = phasesMethodManager.getPhaseMethod(_phaseMethodId);
		}
		
		return _phaseMethod;
	}

	public void setPhaseMethod(PhaseMethod phaseMethod) {
		_phaseMethod = phaseMethod;
	}

	public String getUIId() {
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

	public PhaseMethodUIRegistryExtension getRegistry() {
		return _registry;
	}

	public void setRegistry(PhaseMethodUIRegistryExtension registry) {
		_registry = registry;
	}
	
	public void activate() {
		notifyPhaseMethodUIStateChange(new PhaseMethodUIStateChangeEvent(EPhaseMethodUIStateChange.ACTIVATED));	
	}
	
	public void deactivate() {
		notifyPhaseMethodUIStateChange(new PhaseMethodUIStateChangeEvent(EPhaseMethodUIStateChange.DEACTIVATED));	
	}
	
	public void containerActivate() {
		notifyPhaseMethodUIStateChange(new PhaseMethodUIStateChangeEvent(EPhaseMethodUIStateChange.CONTAINER_ACTIVATED));	
	}
	
	public void containerDeactivate() {
		notifyPhaseMethodUIStateChange(new PhaseMethodUIStateChangeEvent(EPhaseMethodUIStateChange.CONTAINER_DEACTIVATED));
	}
	
	public void registerPhaseMethodUIStateListener(IPhaseMethodUIStateListener listener) {
		_listeners.add(listener);
	}
	
	public void unregisterPhaseMethodUIStateListener(IPhaseMethodUIStateListener listener) {
		_listeners.remove(listener);
	}
	
	public void notifyPhaseMethodUIStateChange(PhaseMethodUIStateChangeEvent event) {
		for(IPhaseMethodUIStateListener listener: _listeners) {
			listener.phaseMethodUIStateChange(event);
		}
	}

	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		PhaseMethodUIManager phasesMethodUiManager = PhaseMethodUIManager.getInstance();

		switch(event.getChange()) {
			case ACTIVATED:
				phasesMethodUiManager.activate(_id);
			break;
			case DEACTIVATED:
				phasesMethodUiManager.deactiveCurrentActive();
				break;
			default:
		}
		
	}
}
