package flintstones.element;

import java.util.LinkedList;
import java.util.List;

public class ElementsManager {
	
	private static ElementsManager _instance = null;
	
	private ElementSet _activeElementSet;
	private List<IElementSetChangeListener> _listeners;
	
	private ElementsManager() {
		_activeElementSet = null;
		_listeners = new LinkedList<IElementSetChangeListener>();
	}
	
	public static ElementsManager getInstance() {
		
		if(_instance == null) {
			_instance = new ElementsManager();
		}
		
		return _instance;
	}
	
	public ElementSet getActiveElementSet() {
		return _activeElementSet;
	}
	
	public void setActiveElementSet(ElementSet elementSet) {
		if(_activeElementSet != elementSet) {
			_activeElementSet = elementSet;
			notifyElementSetChange();
		}
	}
	
	public void registerElementsSetChangeListener(IElementSetChangeListener listener) {
		_listeners.add(listener);
	}
	
	public void unregisterElementsSetChangeListener(IElementSetChangeListener listener) {
		_listeners.remove(listener);
	}
	
	public void notifyElementSetChange() {
		for(IElementSetChangeListener listener: _listeners) {
			listener.newActiveSetElementSet(_activeElementSet);
		}
	}

}
