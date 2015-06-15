package sinbad2.resolutionscheme;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.core.runtime.CoreException;

import sinbad2.resolutionphase.ResolutionPhase;
import sinbad2.resolutionphase.ResolutionPhasesManager;
import sinbad2.resolutionscheme.state.EResolutionSchemeStateChanges;
import sinbad2.resolutionscheme.state.IResolutionSchemeStateListener;
import sinbad2.resolutionscheme.state.ResolutionSchemeStateChangeEvent;

public class ResolutionScheme {

	private String _id;
	private String _name;
	private List<ResolutionPhase> _phases;
	private ResolutionSchemeImplementation _implementation;
	private ResolutionSchemeRegistryExtension _registry;

	private List<IResolutionSchemeStateListener> _listeners;

	public ResolutionScheme() {
		_id = null;
		_name = null;
		_phases = null;
		_implementation = null;
		_registry = null;

		_listeners = new LinkedList<IResolutionSchemeStateListener>();

	}

	public ResolutionScheme(String id, String name, ResolutionSchemeImplementation implementation, 
			List<ResolutionPhase> phases, ResolutionSchemeRegistryExtension registry) {
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

	public List<ResolutionPhase> getPhases() {
		if (_phases == null) {
			_phases = new LinkedList<ResolutionPhase>();
			String[] phases = _registry.getPhasesID();

			ResolutionPhasesManager resolutionPhasesManager = ResolutionPhasesManager
					.getInstance();
			for (String phase : phases) {
				_phases.add(resolutionPhasesManager.getResolutionPhase(phase));
			}
		}

		return _phases;
	}

	public void setPhases(List<ResolutionPhase> phases) {
		_phases = phases;
	}

	public ResolutionSchemeImplementation getImplementation() {
		
		if (_implementation == null) {
			try {
				_implementation = (ResolutionSchemeImplementation) _registry
						.getConfiguration().createExecutableExtension(
								EResolutionSchemeElements.implementation
										.toString());

				_implementation.setResolutionScheme(this);

				ResolutionSchemesManager rsm = ResolutionSchemesManager
						.getInstance();
				rsm.setImplementationResolutionScheme(_implementation, _id);
				
				registerResolutionSchemeStateListener(_implementation);
			} catch (CoreException e) {
				return null;
			}
		}

		return _implementation;
	}

	public void setImplementation(ResolutionSchemeImplementation implementation) {
		
		if (implementation != _implementation) {
			unregisterResolutionSchemeStateListener(_implementation);
			registerResolutionSchemeStateListener(implementation);
			
			ResolutionSchemesManager rsm = ResolutionSchemesManager.getInstance();
			rsm.setImplementationResolutionScheme(implementation, _id);

			_implementation = implementation;
		}
	}

	public ResolutionSchemeRegistryExtension getregistry() {
		return _registry;
	}

	public void setRegistry(ResolutionSchemeRegistryExtension registry) {
		_registry = registry;

	}

	public void addResolutionPhase(ResolutionPhase resolutionPhase) {
		if (_phases == null) {
			_phases = new LinkedList<ResolutionPhase>();
		}

		_phases.add(resolutionPhase);
	}

	public void activate() {

		for (ResolutionPhase phase : getPhases()) {
			phase.containerActivate();
		}

		notifyResolutionSchemeStateChange(new ResolutionSchemeStateChangeEvent(
				EResolutionSchemeStateChanges.ACTIVATED));
	}

	public void deactivate() {

		for (ResolutionPhase phase : getPhases()) {
			phase.containerDeactivate();
		}

		ResolutionPhasesManager resolutionPhasesManager = ResolutionPhasesManager
				.getInstance();
		resolutionPhasesManager.deactivateCurrentActive();

		notifyResolutionSchemeStateChange(new ResolutionSchemeStateChangeEvent(
				EResolutionSchemeStateChanges.DEACTIVATED));
	}

	public void registerResolutionSchemeStateListener(
			IResolutionSchemeStateListener listener) {
		_listeners.add(listener);
	}

	public void unregisterResolutionSchemeStateListener(
			IResolutionSchemeStateListener listener) {
		_listeners.remove(listener);
	}

	public void notifyResolutionSchemeStateChange(
			ResolutionSchemeStateChangeEvent event) {
		for (IResolutionSchemeStateListener listener : _listeners) {
			listener.notifyResolutionSchemeStateChange(event);
		}
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(_id);
		hcb.append(_implementation);
		hcb.append(_name);
		if(_phases != null) {
			for(ResolutionPhase phase: _phases) {
				hcb.append(phase);
			}
		}
		hcb.append(_registry);
		return hcb.toHashCode();
	}

}
