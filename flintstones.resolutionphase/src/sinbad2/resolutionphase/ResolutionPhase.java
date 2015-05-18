 package sinbad2.resolutionphase;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

import sinbad2.resolutionphase.state.EResolutionPhaseStateChange;
import sinbad2.resolutionphase.state.IResolutionPhaseStateListener;
import sinbad2.resolutionphase.state.ResolutionPhaseStateChangeEvent;

public class ResolutionPhase {
	
	private String _id;
	private String _name;
	private ResolutionPhaseImplementation _implementation;
	private ResolutionPhaseRegistryExtension _registry;
	
	private List<IResolutionPhaseStateListener> _listeners;
	
	public ResolutionPhase() {
		_id = null;
		_name = null;
		_implementation = null;
		_registry = null;
		
		_listeners = new LinkedList<IResolutionPhaseStateListener>();
	}
	
	public ResolutionPhase(String id, String name, ResolutionPhaseImplementation implementation, ResolutionPhaseRegistryExtension registry) {
		this();
		setId(id);
		setName(name);
		setImplementation(implementation);
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

	public ResolutionPhaseImplementation getImplementation() {
		
		if(_implementation == null) {
			try {
				_implementation = (ResolutionPhaseImplementation) _registry.getConfiguration().
						createExecutableExtension(EResolutionPhaseElements.implementation.toString());
				
				ResolutionPhasesManager rpm = ResolutionPhasesManager.getInstance();
				rpm.setImplementationResolutionPhase(_implementation, _id);
				
				registerResolutionPhaseStateListener(_implementation);
			} catch(CoreException e) {
				return null;
			}
		}
		return _implementation;
	}

	public void setImplementation(ResolutionPhaseImplementation implementation) {
		
		if(implementation != _implementation) {
			unregisterResolutionPhaseStateListener(_implementation);
			registerResolutionPhaseStateListener(implementation);
			
			ResolutionPhasesManager rpm = ResolutionPhasesManager.getInstance();
			rpm.setImplementationResolutionPhase(implementation, _id);
			
			_implementation = implementation;
			
		}
	}

	public ResolutionPhaseRegistryExtension getRegistry() {
		return _registry;
	}

	public void setRegistry(ResolutionPhaseRegistryExtension registry) {
		_registry = registry;
	}
	
	public void activate() {
		notifyResolutionPhaseStateChange(new ResolutionPhaseStateChangeEvent(EResolutionPhaseStateChange.ACTIVATED));
	}
	
	public void deactivate() {
		notifyResolutionPhaseStateChange(new ResolutionPhaseStateChangeEvent(EResolutionPhaseStateChange.DEACTIVATED));
	}
	
	public void containerActivate() {
		notifyResolutionPhaseStateChange(new ResolutionPhaseStateChangeEvent(EResolutionPhaseStateChange.CONTAINER_ACTIVATED));
	}
	
	public void containerDeactivate() {
		notifyResolutionPhaseStateChange(new ResolutionPhaseStateChangeEvent(EResolutionPhaseStateChange.CONTAINER_DEACTIVATED));
	}
	
	public void registerResolutionPhaseStateListener(IResolutionPhaseStateListener listener) {
		_listeners.add(listener);
	}
	
	public void unregisterResolutionPhaseStateListener(IResolutionPhaseStateListener listener) {
		_listeners.remove(listener);
	}
	
	//TODO hash code
	
	public void notifyResolutionPhaseStateChange(ResolutionPhaseStateChangeEvent event) {
		for(IResolutionPhaseStateListener listener: _listeners) {
			listener.notifyResolutionPhaseStateChange(event);
		}
	}
}
