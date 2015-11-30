package sinbad2.element;

import java.util.LinkedList;
import java.util.List;

public class ProblemElementsManager {
	
	private static ProblemElementsManager _instance = null;
	
	private ProblemElementsSet _activeElementSet;
	
	private List<IProblemElementsSetChangeListener> _listeners;
	
	private ProblemElementsManager() {
		_activeElementSet = null;
		_listeners = new LinkedList<IProblemElementsSetChangeListener>();
	}
	
	public static ProblemElementsManager getInstance() {
		if(_instance == null) {
			_instance = new ProblemElementsManager();
		}
		
		return _instance;
	}
	
	public ProblemElementsSet getActiveElementSet() {
		return _activeElementSet;
	}
	
	public void setActiveElementSet(ProblemElementsSet elementSet) {
		if(_activeElementSet != elementSet) {
			_activeElementSet = elementSet;
			notifyProblemElementsSetChange();
		}
	}
	
	public void registerElementsSetChangeListener(IProblemElementsSetChangeListener listener) {
		_listeners.add(listener);
	}
	
	public void unregisterElementsSetChangeListener(IProblemElementsSetChangeListener listener) {
		_listeners.remove(listener);
	}
	
	public void notifyProblemElementsSetChange() {
		for(IProblemElementsSetChangeListener listener: _listeners) {
			listener.notifyNewProblemElementsSet(_activeElementSet);
		}
	}

}
