package sinbad2.valuation.valuationset;

import java.util.LinkedList;
import java.util.List;

import sinbad2.core.workspace.Workspace;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignments;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignmentsManager;
import sinbad2.valuation.valuationset.listener.IValuationSetChangeListener;

public class ValuationSetManager {
	
	private static ValuationSetManager _instance = null;

	private ValuationSet _activeValuationSet;
	private List<IValuationSetChangeListener> _listeners;

	private ValuationSetManager() {
		_activeValuationSet = null;

		_listeners = new LinkedList<IValuationSetChangeListener>();
	}

	public static ValuationSetManager getInstance() {
		if (_instance == null) {
			_instance = new ValuationSetManager();
		}
		return _instance;
	}

	public ValuationSet getActiveValuationSet() {
		return _activeValuationSet;
	}

	public void setActiveValuationSet(ValuationSet valuationSet) {
		
		DomainsManager domainsManager = null;
		DomainSet domainSet = null;
		DomainAssignmentsManager domainAssignmentsManager = null;
		DomainAssignments domainAssignments = null;

		if (_activeValuationSet != valuationSet) {
			domainsManager = DomainsManager.getInstance();
			domainSet = domainsManager.getActiveDomainSet();
			domainAssignmentsManager = DomainAssignmentsManager.getInstance();
			domainAssignments = domainAssignmentsManager.getActiveDomainAssignments();

			if (_activeValuationSet != null) {
				domainSet.unregisterDomainsListener(_activeValuationSet);
				domainAssignmentsManager.unregisterDomainAssignmentsChangeListener(_activeValuationSet);
				domainAssignments.unregisterDomainAssignmentsChangeListener(_activeValuationSet);
			}
			_activeValuationSet = valuationSet;
			if (_activeValuationSet != null) {
				domainSet.registerDomainsListener(_activeValuationSet);
				domainAssignmentsManager.registerDomainAssignmentsChangeListener(_activeValuationSet);
				domainAssignments.registerDomainAssignmentsChangeListener(_activeValuationSet);
			}

			notifyValuationSetChange();
		}
	}

	public void registerValuationSetChangeListener(
			IValuationSetChangeListener listener) {
		_listeners.add(listener);
	}

	public void unregisterValuationSetChangeListener(
			IValuationSetChangeListener listener) {
		_listeners.remove(listener);
	}

	public void notifyValuationSetChange() {
		
		for (IValuationSetChangeListener listener : _listeners) {
			listener.notifyNewActiveValuationSet(_activeValuationSet);
		}
		Workspace.getWorkspace().updateHashCode();
	}

}
