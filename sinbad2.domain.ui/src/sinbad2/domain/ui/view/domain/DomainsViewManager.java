package sinbad2.domain.ui.view.domain;

import java.util.LinkedList;
import java.util.List;

import sinbad2.domain.Domain;

public class DomainsViewManager {
	
	public static DomainsViewManager _instance = null;
	
	private Domain _activeDomain;
	private Object _activeRanking;
	
	private List<IDisplayDomainChangeListener> _listeners;
	
	private DomainsViewManager() {
		_activeDomain = null;
		_activeRanking = null;
		
		_listeners = new LinkedList<IDisplayDomainChangeListener>();
	}
	
	public static DomainsViewManager getInstance() {
		
		if(_instance == null) {
			_instance = new DomainsViewManager();
		}
		
		return _instance;
	}
	
	public void setContent(Domain domain, Object ranking) {
		_activeDomain = domain;
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
