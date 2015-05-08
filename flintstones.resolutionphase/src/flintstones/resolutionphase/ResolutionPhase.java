package flintstones.resolutionphase;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

import flintstones.resolutionphase.state.EResolutionPhaseStateChanges;
import flintstones.resolutionphase.state.IResolutionPhaseStateListener;
import flintstones.resolutionphase.state.ResolutionPhaseStateChangeEvent;

/**
 * ResolutionPhase.java
 * 
 * Clase que define una fase de resoluciñon
 * 
 * @author Labella Romero, Álvaro
 * @version 1.0
 *
 */
public class ResolutionPhase implements Cloneable {
	
	private String _id;
	private String _name;
	private IResolutionPhase _implementation;
	private ResolutionPhaseRegistry _registry;
	
	private List<IResolutionPhaseStateListener> _listeners;
	
	public ResolutionPhase() {
		_id = null;
		_name = null;
		_implementation = null;
		_registry = null;
		
		_listeners = new LinkedList<IResolutionPhaseStateListener>();
	}
	
	public ResolutionPhase(String id, String name, IResolutionPhase implementation, ResolutionPhaseRegistry registry) {
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

	public IResolutionPhase getImplementation() {
		
		if(_implementation == null) {
			try {
				_implementation = (IResolutionPhase) _registry.getConfiguration().
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

	public void setImplementation(IResolutionPhase implementation) {
		
		if(implementation != _implementation) {
			unregisterResolutionPhaseStateListener(_implementation);
			registerResolutionPhaseStateListener(implementation);
			
			ResolutionPhasesManager rpm = ResolutionPhasesManager.getInstance();
			rpm.setImplementationResolutionPhase(implementation, _id);
			
			_implementation = implementation;
			
		}
	}

	public ResolutionPhaseRegistry getRegistry() {
		return _registry;
	}

	public void setRegistry(ResolutionPhaseRegistry registry) {
		_registry = registry;
	}
	
	public void activate() {
		notifyResolutionPhaseStateChange(new ResolutionPhaseStateChangeEvent(EResolutionPhaseStateChanges.ACTIVATED));
	}
	
	public void deactivate() {
		notifyResolutionPhaseStateChange(new ResolutionPhaseStateChangeEvent(EResolutionPhaseStateChanges.DEACTIVATED));
	}
	
	public void containerActivate() {
		notifyResolutionPhaseStateChange(new ResolutionPhaseStateChangeEvent(EResolutionPhaseStateChanges.CONTAINER_ACTIVATED));
	}
	
	public void containerDeactivate() {
		notifyResolutionPhaseStateChange(new ResolutionPhaseStateChangeEvent(EResolutionPhaseStateChanges.CONTAINER_DEACTIVATED));
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
			listener.resolutionPhaseStateChange(event);
		}
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		ResolutionPhase result = null;
		
		result = (ResolutionPhase) super.clone();
		result._implementation = _implementation.clone();
		result._registry = (ResolutionPhaseRegistry) _registry.clone();
		
		return result;
		
	}
}
