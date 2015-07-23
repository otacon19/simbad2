package sinbad2.phasemethod;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.core.runtime.CoreException;

import sinbad2.phasemethod.state.EPhaseMethodStateChange;
import sinbad2.phasemethod.state.IPhaseMethodStateListener;
import sinbad2.phasemethod.state.PhaseMethodStateChangeEvent;

public class PhaseMethod {
	
	private String _id;
	private String _name;
	private IPhaseMethod _implementation;
	private PhaseMethodRegistryExtension _registry;
	
	private List<IPhaseMethodStateListener> _listeners;
	
	public PhaseMethod() {
		_id = null;
		_name = null;
		_implementation = null;
		_registry = null;

		_listeners = new LinkedList<IPhaseMethodStateListener>();
	}

	public PhaseMethod(String id, String name, IPhaseMethod implementation, 
			PhaseMethodRegistryExtension registry) {
		this();
		setId(id);
		setName(name);
		setImplementation(implementation);
		setRegistryExtension(registry);
	}

	public void setId(String id) {
		_id = id;
	}

	public String getId() {
		return _id;
	}

	public void setName(String name) {
		_name = name;
	}

	public String getName() {
		return _name;
	}

	public void setImplementation(IPhaseMethod implementation) {
		
		if (implementation != _implementation) {
			unregisterPhaseMethodStateListener(_implementation);
			registerPhaseMethodStateListener(implementation);

			//TODO añadir al método la fase
			/*ResolutionPhasesManager rpm = ResolutionPhasesManager.getInstance();
			rpm.setImplementationResolutionPhase(implementation, _id);*/

			_implementation = implementation;
		}
	}

	public IPhaseMethod getImplementation() {
		
		if (_implementation == null) {
			try {
				_implementation = (IPhaseMethod) _registry
						.getConfiguration().createExecutableExtension(
								EPhaseMethodElements.implementation
										.toString());
				
				//TODO obtener la implementacion de la fase01
				/*
				ResolutionPhasesManager rpm = ResolutionPhasesManager
						.getInstance();
				rpm.setImplementationResolutionPhase(_implementation, _id);*/

				registerPhaseMethodStateListener(_implementation);

			} catch (CoreException e) {
				return null;
			}
		}
		return _implementation;
	}

	public void setRegistryExtension(PhaseMethodRegistryExtension registry) {
		_registry = registry;
	}

	public PhaseMethodRegistryExtension getRegistry() {
		return _registry;
	}

	public void activate() {
		notifyPhaseMethodStateChange(new PhaseMethodStateChangeEvent(
				EPhaseMethodStateChange.ACTIVATED));
	}

	public void deactivate() {
		notifyPhaseMethodStateChange(new PhaseMethodStateChangeEvent(
				EPhaseMethodStateChange.DEACTIVATED));
	}

	public void containerActivate() {
		notifyPhaseMethodStateChange(new PhaseMethodStateChangeEvent(
				EPhaseMethodStateChange.CONTAINER_ACTIVATED));
	}

	public void containerDeactivate() {
		notifyPhaseMethodStateChange(new PhaseMethodStateChangeEvent(
				EPhaseMethodStateChange.CONTAINER_DEACTIVATED));
	}

	public void registerPhaseMethodStateListener(
			IPhaseMethodStateListener listener) {
		_listeners.add(listener);
	}

	public void unregisterPhaseMethodStateListener(
			IPhaseMethodStateListener listener) {
		_listeners.remove(listener);
	}

	public void notifyPhaseMethodStateChange(
			PhaseMethodStateChangeEvent event) {
		for (IPhaseMethodStateListener listener : _listeners) {
			listener.notifyPhaseMethodStateChange(event);
		}
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(_id);
		hcb.append(_name);
		hcb.append(_implementation);
		hcb.append(_registry);
		return hcb.toHashCode();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {

		PhaseMethod result = null;

		result = (PhaseMethod) super.clone();
		result._implementation = _implementation.clone();
		result._registry = (PhaseMethodRegistryExtension) _registry.clone();

		return result;

	}
	
}
