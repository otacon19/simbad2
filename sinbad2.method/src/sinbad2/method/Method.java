package sinbad2.method;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.core.runtime.CoreException;

import sinbad2.method.state.EMethodStateChanges;
import sinbad2.method.state.IMethodStateListener;
import sinbad2.method.state.MethodStateChangeEvent;
import sinbad2.phasemethod.PhaseMethod;
import sinbad2.phasemethod.PhasesMethodManager;

public class Method {
	
	private String _id;
	private String _name;
	private List<PhaseMethod> _phases;
	private MethodImplementation _implementation;
	private MethodRegistryExtension _registry;

	private List<IMethodStateListener> _listeners;

	public Method() {
		_id = null;
		_name = null;
		_phases = null;
		_implementation = null;
		_registry = null;

		_listeners = new LinkedList<IMethodStateListener>();

	}

	public Method(String id, String name, MethodImplementation implementation, 
			List<PhaseMethod> phases, MethodRegistryExtension registry) {
		this();
		setId(id);
		setName(name);
		setImplementation(implementation);
		setPhases(phases);
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

	public List<PhaseMethod> getPhases() {
		if (_phases == null) {
			_phases = new LinkedList<PhaseMethod>();
			String[] phases = _registry.getPhasesMethodId();

			PhasesMethodManager resolutionPhasesManager = PhasesMethodManager
					.getInstance();
			for (String phase : phases) {
				_phases.add(resolutionPhasesManager.getPhaseMethod(phase));
			}
		}

		return _phases;
	}

	public void setPhases(List<PhaseMethod> phases) {
		_phases = phases;
	}

	public MethodImplementation getImplementation() {
		
		if (_implementation == null) {
			try {
				_implementation = (MethodImplementation) _registry
						.getConfiguration().createExecutableExtension(
								EMethodElements.implementation
										.toString());

				_implementation.setMethod(this);

				MethodsManager rsm = MethodsManager
						.getInstance();
				rsm.setImplementationMethod(_implementation, _id);
				
				registerMethodStateListener(_implementation);
			} catch (CoreException e) {
				return null;
			}
		}

		return _implementation;
	}

	public void setImplementation(MethodImplementation implementation) {
		
		if (implementation != _implementation) {
			unregisterMethodStateListener(_implementation);
			registerMethodStateListener(implementation);
			
			MethodsManager rsm = MethodsManager.getInstance();
			rsm.setImplementationMethod(implementation, _id);

			_implementation = implementation;
		}
	}

	public MethodRegistryExtension getregistry() {
		return _registry;
	}

	public void setRegistry(MethodRegistryExtension registry) {
		_registry = registry;

	}

	public void addResolutionPhase(PhaseMethod resolutionPhase) {
		if (_phases == null) {
			_phases = new LinkedList<PhaseMethod>();
		}

		_phases.add(resolutionPhase);
	}

	public void activate() {

		for (PhaseMethod phase : getPhases()) {
			phase.containerActivate();
		}

		notifyMethodStateChange(new MethodStateChangeEvent(
				EMethodStateChanges.ACTIVATED));
	}

	public void deactivate() {

		for (PhaseMethod phase : getPhases()) {
			phase.containerDeactivate();
		}

		PhasesMethodManager resolutionPhasesManager = PhasesMethodManager
				.getInstance();
		resolutionPhasesManager.deactivateCurrentActive();

		notifyMethodStateChange(new MethodStateChangeEvent(
				EMethodStateChanges.DEACTIVATED));
	}

	public void registerMethodStateListener(
			IMethodStateListener listener) {
		_listeners.add(listener);
	}

	public void unregisterMethodStateListener(
			IMethodStateListener listener) {
		_listeners.remove(listener);
	}

	public void notifyMethodStateChange(
			MethodStateChangeEvent event) {
		for (IMethodStateListener listener : _listeners) {
			listener.notifyMethodStateChange(event);
		}
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(_id);
		hcb.append(_implementation);
		hcb.append(_name);
		if(_phases != null) {
			for(PhaseMethod phase: _phases) {
				hcb.append(phase);
			}
		}
		hcb.append(_registry);
		return hcb.toHashCode();
	}
	
}
