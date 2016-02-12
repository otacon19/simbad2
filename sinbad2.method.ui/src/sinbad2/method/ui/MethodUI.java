package sinbad2.method.ui;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.part.ViewPart;

import sinbad2.method.Method;
import sinbad2.method.MethodsManager;
import sinbad2.method.ui.state.EMethodUIStateChanges;
import sinbad2.method.ui.state.IMethodUIStateListener;
import sinbad2.method.ui.state.MethodUIStateChangeEvent;
import sinbad2.phasemethod.PhaseMethod;
import sinbad2.phasemethod.ui.PhaseMethodUI;
import sinbad2.phasemethod.ui.PhaseMethodUIManager;

public class MethodUI {
	
	private String _id;
	private String _name;
	private Method _method;
	private MethodUIRegistryExtension _registry;
	
	private List<IMethodUIStateListener> _listeners;
	
	private Map<String, PhaseMethodUI> _phasesMethodUI;
	private Map<String, String> phasesMethodUIIndex;
	
	public MethodUI() {
		_id = null;
		_name = null;
		_method = null;
		_registry = null;
		
		_listeners = new LinkedList<IMethodUIStateListener>();
	}
	
	public MethodUI(String id, String name, Method method, MethodUIRegistryExtension registry) {
		this();
		setId(id);
		setName(name);
		setMethod(method);
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

	public Method getMethod() {
		
		if(_method == null) {
			MethodsManager methodsManager = MethodsManager.getInstance();
			String methodID = _registry.getElement(EMethodUIElements.method);
			_method = methodsManager.getMethod(methodID);
		}
		
		return _method;
	}

	public void setMethod(Method method) {
		_method = method;
	}

	public String getPhasesFormat() {
		PhaseMethodUIManager phaseMethodUIManager = PhaseMethodUIManager.getInstance();
		String result = "";
		
		List<PhaseMethodUI> phasesMethod = getPhasesUI();
		for(PhaseMethodUI phaseMethod: phasesMethod) {
			result += phaseMethod.getName() + '\n';
			List<ViewPart> steps = phaseMethodUIManager.getSteps(phaseMethod.getId());
			for(ViewPart step: steps) {
				result += "\t" + "-" + step.getPartName() + '\n';
			}
		}
		
		return result;
	}
	
	
	public MethodUIRegistryExtension getRegistry() {
		return _registry;
	}

	public void setRegistry(MethodUIRegistryExtension registry) {
		_registry = registry;
	}
	
	private void initPhasesMethodUIIndex() {
		
		if(phasesMethodUIIndex == null) {
			phasesMethodUIIndex = _registry.getPhasesMethodUIsIDs();
			
			PhaseMethodUIManager phasesMethodUIManager = PhaseMethodUIManager.getInstance();
			_phasesMethodUI = new LinkedHashMap<String, PhaseMethodUI>();
			for(String key: phasesMethodUIIndex.keySet()) {
				_phasesMethodUI.put(key, phasesMethodUIManager.getUI(phasesMethodUIIndex.get(key)));
			}
		}
	}
	
	public PhaseMethodUI getPhaseMethodUI(String phaseMethodId) {

		if(phasesMethodUIIndex == null) {
			initPhasesMethodUIIndex();
		}
		
		return _phasesMethodUI.get(phaseMethodId);
	}
	
	public PhaseMethodUI getPhaseMethodUI(PhaseMethod phaseMethod) {
		return getPhaseMethodUI(phaseMethod.getId());
	}
	
	public Map<String, PhaseMethodUI> getPhasesMethodUI() {
		return _phasesMethodUI;
	}
	
	public List<PhaseMethodUI> getPhasesUI() {
		List<PhaseMethodUI> phasesMethodUI = new LinkedList<PhaseMethodUI>();
		for(String key: _phasesMethodUI.keySet()) {
			phasesMethodUI.add(_phasesMethodUI.get(key));
		}
		return phasesMethodUI;
	}
	
	public void activate() {
		
		if(phasesMethodUIIndex == null) {
			initPhasesMethodUIIndex();
		}
		
		MethodsManager methodsManager = MethodsManager.getInstance();
		methodsManager.activate(getMethod().getId());
		
		PhaseMethodUI phaseMethodUI;
		for(String phaseMethodId: phasesMethodUIIndex.keySet()) {
			phaseMethodUI = getPhaseMethodUI(phaseMethodId);
			phaseMethodUI.containerActivate();
		}
		
		notifyMethodUIStateChange(new MethodUIStateChangeEvent(EMethodUIStateChanges.ACTIVATED));
	}
	
	public void deactivate() {
		
		MethodsManager methodsManager = MethodsManager.getInstance();
		methodsManager.deactiveCurrentActive();
		
		PhaseMethodUI phaseMethodUI;
		for(String phaseMethodId: phasesMethodUIIndex.keySet()) {
			phaseMethodUI = getPhaseMethodUI(phaseMethodId);
			phaseMethodUI.containerDeactivate();
		}
		
		PhaseMethodUIManager phasesMethodUIManager = PhaseMethodUIManager.getInstance();
		phasesMethodUIManager.deactiveCurrentActive();
		phasesMethodUIManager.deactivateStep();
		
		notifyMethodUIStateChange(new MethodUIStateChangeEvent(EMethodUIStateChanges.DEACTIVATED));
	}
	
	public void registerMethodUIStateListener(IMethodUIStateListener listener) {
		_listeners.add(listener);
	}

	public void unregisterMethodUIStateListener(IMethodUIStateListener listener) {
		_listeners.remove(listener);
	}
	
	public void notifyMethodUIStateChange(MethodUIStateChangeEvent event){
		for(IMethodUIStateListener listener: _listeners) {
			listener.methodUIStateChange(event);
		}
	}
	
}
