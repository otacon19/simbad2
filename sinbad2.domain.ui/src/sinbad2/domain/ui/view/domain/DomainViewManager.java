package sinbad2.domain.ui.view.domain;

import java.util.LinkedList;
import java.util.List;

import sinbad2.domain.Domain;

public class DomainViewManager {
	
	public static DomainViewManager _instance = null;
	
	private Domain _activeDomain;
	private Object _activeRanking;
	
	private List<IDisplayDomainChangeListener> _listeners;
	
	private DomainViewManager() {
		_activeDomain = null;
		_activeRanking = null;
		
		_listeners = new LinkedList<IDisplayDomainChangeListener>();
	}
	
	public static DomainViewManager getInstance() {
		
		if(_instance == null) {
			_instance = new DomainViewManager();
		}
		
		return _instance;
	}
	
	public Domain getActiveDomain() {
		return _activeDomain;
	}
	
	public void setContent(Domain domain, Object ranking) {
		_activeDomain = domain;
		_activeRanking = ranking;
		
		notifyDisplayDomainChange();
	}
	
	public void setRanking(Object ranking) {
		_activeRanking = ranking;
		
		notifyDisplayDomainChange();
	}
	
	public void registerDisplayDomainChangeListener(IDisplayDomainChangeListener listener) {
		_listeners.add(listener);
	}
	
	public void unregisterDisplayDomainChangeListener(IDisplayDomainChangeListener listener) {
		_listeners.remove(listener);
	}
	
	public void notifyDisplayDomainChange() {
		
		for(IDisplayDomainChangeListener listener: _listeners) {
			listener.displayDomainChangeListener(_activeDomain, _activeRanking);
		}
	}
}
