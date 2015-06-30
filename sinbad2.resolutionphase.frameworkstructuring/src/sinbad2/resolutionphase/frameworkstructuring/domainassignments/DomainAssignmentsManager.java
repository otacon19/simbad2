package sinbad2.resolutionphase.frameworkstructuring.domainassignments;

import java.util.LinkedList;
import java.util.List;

import sinbad2.core.workspace.Workspace;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.listener.IDomainAssignmentsChangeListener;

public class DomainAssignmentsManager {
	
	private static DomainAssignmentsManager _instance = null;
	private DomainAssignments _activeDomainAssignments = null;
	
	private List<IDomainAssignmentsChangeListener> _listeners;
	
	private DomainAssignmentsManager() {
		_listeners = new LinkedList<IDomainAssignmentsChangeListener>();
	}
	
	public static DomainAssignmentsManager getInstance() {
		
		if(_instance == null) {
			_instance = new DomainAssignmentsManager();
		}
		
		return _instance;
	}
	
	public DomainAssignments getActiveDomainAssignments() {
		return _activeDomainAssignments;
	}
	
	public void setActiveDomainAssignments(DomainAssignments domainAssignments) {
		ProblemElementsManager elementsManager = null;
		ProblemElementsSet elementSet = null;
		DomainsManager domainsManager = null;
		DomainSet domainSet = null;

		if (_activeDomainAssignments != domainAssignments) {
			elementsManager = ProblemElementsManager.getInstance();
			elementSet = elementsManager.getActiveElementSet();
			domainsManager = DomainsManager.getInstance();
			domainSet = domainsManager.getActiveDomainSet();

			if (_activeDomainAssignments != null) {
				elementsManager.unregisterElementsSetChangeListener(_activeDomainAssignments);
				elementSet.unregisterExpertsChangeListener(_activeDomainAssignments);
				elementSet.unregisterAlternativesChangeListener(_activeDomainAssignments);
				elementSet.unregisterCriteriaChangeListener(_activeDomainAssignments);
				domainsManager.unregisterDomainSetChangeListener(_activeDomainAssignments);
				domainSet.unregisterDomainsListener(_activeDomainAssignments);
			}
			_activeDomainAssignments = domainAssignments;
			if (_activeDomainAssignments != null) {
				elementsManager.registerElementsSetChangeListener(_activeDomainAssignments);
				elementSet.registerExpertsChangesListener(_activeDomainAssignments);
				elementSet.registerAlternativesChangesListener(_activeDomainAssignments);
				elementSet.registerCriteriaChangeListener(_activeDomainAssignments);
				domainsManager.registerDomainSetChangeListener(_activeDomainAssignments);
				domainSet.registerDomainsListener(_activeDomainAssignments);
			}

			notifyDomainAssignmentsChange();
		}
	}

	public void registerDomainAssignmentsChangeListener(IDomainAssignmentsChangeListener listener) {
		_listeners.add(listener);
	}

	public void unregisterDomainAssignmentsChangeListener(IDomainAssignmentsChangeListener listener) {
		_listeners.remove(listener);
	}

	public void notifyDomainAssignmentsChange() {
		
		for (IDomainAssignmentsChangeListener listener : _listeners) {
			listener.notifyNewActiveDomainAssignments(_activeDomainAssignments);
		}
		
		Workspace.getWorkspace().updateHashCode();
	}

}
