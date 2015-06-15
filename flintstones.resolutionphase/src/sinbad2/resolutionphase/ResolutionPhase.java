 package sinbad2.resolutionphase;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.core.runtime.CoreException;

import sinbad2.resolutionphase.state.EResolutionPhaseStateChange;
import sinbad2.resolutionphase.state.IResolutionPhaseStateListener;
import sinbad2.resolutionphase.state.ResolutionPhaseStateChangeEvent;

public class ResolutionPhase {
	
	private String _id;
	private String _name;
	private IResolutionPhase _implementation;
	private ResolutionPhaseRegistryExtension _registry;

	private List<IResolutionPhaseStateListener> _listeners;

	public ResolutionPhase() {
		_id = null;
		_name = null;
		_implementation = null;
		_registry = null;

		_listeners = new LinkedList<IResolutionPhaseStateListener>();
	}

	public ResolutionPhase(String id, String name, IResolutionPhase implementation, 
			ResolutionPhaseRegistryExtension registry) {
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

	public void setImplementation(IResolutionPhase implementation) {
		if (implementation != _implementation) {
			unregisterResolutionPhaseStateListener(_implementation);
			registerResolutionPhaseStateListener(implementation);

			ResolutionPhasesManager rpm = ResolutionPhasesManager.getInstance();
			rpm.setImplementationResolutionPhase(implementation, _id);

			_implementation = implementation;
		}
	}

	public IResolutionPhase getImplementation() {
		if (_implementation == null) {
			try {
				_implementation = (IResolutionPhase) _registry
						.getConfiguration().createExecutableExtension(
								EResolutionPhaseElements.implementation
										.toString());
				
				ResolutionPhasesManager rpm = ResolutionPhasesManager
						.getInstance();
				rpm.setImplementationResolutionPhase(_implementation, _id);

				registerResolutionPhaseStateListener(_implementation);

			} catch (CoreException e) {
				return null;
			}
		}
		return _implementation;
	}

	public void setRegistryExtension(ResolutionPhaseRegistryExtension registry) {
		_registry = registry;
	}

	public ResolutionPhaseRegistryExtension getRegistry() {
		return _registry;
	}

	public void activate() {
		notifyResolutionPhaseStateChange(new ResolutionPhaseStateChangeEvent(
				EResolutionPhaseStateChange.ACTIVATED));
	}

	public void deactivate() {
		notifyResolutionPhaseStateChange(new ResolutionPhaseStateChangeEvent(
				EResolutionPhaseStateChange.DEACTIVATED));
	}

	public void containerActivate() {
		notifyResolutionPhaseStateChange(new ResolutionPhaseStateChangeEvent(
				EResolutionPhaseStateChange.CONTAINER_ACTIVATED));
	}

	public void containerDeactivate() {
		notifyResolutionPhaseStateChange(new ResolutionPhaseStateChangeEvent(
				EResolutionPhaseStateChange.CONTAINER_DEACTIVATED));
	}

	public void registerResolutionPhaseStateListener(
			IResolutionPhaseStateListener listener) {
		_listeners.add(listener);
	}

	public void unregisterResolutionPhaseStateListener(
			IResolutionPhaseStateListener listener) {
		_listeners.remove(listener);
	}

	public void notifyResolutionPhaseStateChange(
			ResolutionPhaseStateChangeEvent event) {
		for (IResolutionPhaseStateListener listener : _listeners) {
			listener.notifyResolutionPhaseStateChange(event);
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

		ResolutionPhase result = null;

		result = (ResolutionPhase) super.clone();
		result._implementation = _implementation.clone();
		result._registry = (ResolutionPhaseRegistryExtension) _registry.clone();

		return result;

	}
}
