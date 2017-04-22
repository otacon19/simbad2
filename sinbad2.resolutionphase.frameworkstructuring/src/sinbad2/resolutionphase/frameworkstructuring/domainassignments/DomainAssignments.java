package sinbad2.resolutionphase.frameworkstructuring.domainassignments;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.OperationHistoryFactory;

import sinbad2.core.workspace.Workspace;
import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.listener.DomainSetChangeEvent;
import sinbad2.domain.listener.EDomainSetChange;
import sinbad2.domain.listener.IDomainSetChangeListener;
import sinbad2.domain.listener.IDomainSetListener;
import sinbad2.element.IProblemElementsSetChangeListener;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.alternative.listener.AlternativesChangeEvent;
import sinbad2.element.alternative.listener.EAlternativesChange;
import sinbad2.element.alternative.listener.IAlternativesChangeListener;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.criterion.listener.CriteriaChangeEvent;
import sinbad2.element.criterion.listener.ECriteriaChange;
import sinbad2.element.criterion.listener.ICriteriaChangeListener;
import sinbad2.element.expert.Expert;
import sinbad2.element.expert.listener.EExpertsChange;
import sinbad2.element.expert.listener.ExpertsChangeEvent;
import sinbad2.element.expert.listener.IExpertsChangeListener;
import sinbad2.resolutionphase.framework.Framework;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.listener.DomainAssignmentsChangeEvent;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.listener.EDomainAssignmentsChange;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.listener.IDomainAssignmentsChangeListener;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.operation.ERemoveDomainAssignments;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.operation.RemoveDomainAssignmentsOperation;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.operation.RemoveDomainAssignmentsOperationProvider;
import sinbad2.resolutionphase.io.XMLRead;

public class DomainAssignments implements Cloneable, IExpertsChangeListener, IAlternativesChangeListener, ICriteriaChangeListener, 
	IProblemElementsSetChangeListener, IDomainSetChangeListener, IDomainSetListener {
	
	private ProblemElementsManager _elementsManager;
	private ProblemElementsSet _elementSet;
	private DomainsManager _domainsManager;
	private DomainSet _domainSet;
	private Map<DomainAssignmentKey, Domain> _assignments;
	
	private List<IDomainAssignmentsChangeListener> _listeners;
	
	public DomainAssignments() {
		_assignments = new HashMap<DomainAssignmentKey, Domain>();
		_listeners = new LinkedList<IDomainAssignmentsChangeListener>();

		_elementsManager = ProblemElementsManager.getInstance();
		_elementSet = _elementsManager.getActiveElementSet();
		_domainsManager = DomainsManager.getInstance();
		_domainSet = _domainsManager.getActiveDomainSet();
	}

	public void setAssignments(Map<DomainAssignmentKey, Domain> assignments) {
		_assignments = assignments;
	}

	public Map<DomainAssignmentKey, Domain> getAssignments() {
		return _assignments;
	}

	public void setDomain(Expert expert, Alternative alternative, Criterion criterion, Domain domain) {
		_assignments.put(new DomainAssignmentKey(expert, alternative, criterion), domain);		
	}
	
	public void setDomain(Map<DomainAssignmentKey, Domain> oldDomainAssignment, Map<DomainAssignmentKey, Domain> newDomainAssignment,
			boolean inUndoRedo) {
		notifyDomainAssignmentsChange(new DomainAssignmentsChangeEvent(EDomainAssignmentsChange.MODIFY_ASSIGNMENTS, oldDomainAssignment, 
				newDomainAssignment, inUndoRedo));
	}
	
	public Domain getDomain(Expert expert, Alternative alternative, Criterion criterion) {
		return _assignments.get( new DomainAssignmentKey(expert, alternative, criterion));
	}

	public void removeDomain(Expert expert, Alternative alternative, Criterion criterion) {
		_assignments.remove(new DomainAssignmentKey(expert, alternative, criterion));
	}

	public void clear() {
		_assignments.clear();
	}

	public void save(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("domain-assignments"); //$NON-NLS-1$

		for (DomainAssignmentKey key : _assignments.keySet()) {
			writer.writeStartElement("assignment"); //$NON-NLS-1$
			writer.writeAttribute("expert", key.getExpert().getCanonicalId()); //$NON-NLS-1$
			writer.writeAttribute("alternative", key.getAlternative().getCanonicalId()); //$NON-NLS-1$
			writer.writeAttribute("criterion", key.getCriterion().getCanonicalId()); //$NON-NLS-1$
			writer.writeAttribute("domain", _assignments.get(key).getId()); //$NON-NLS-1$
			writer.writeEndElement();
		}

		writer.writeEndElement();
	}

	public void read(XMLRead reader, Framework framework)
			throws XMLStreamException {
		reader.goToStartElement("domain-assignments"); //$NON-NLS-1$

		ProblemElementsSet elementSet = framework.getElementSet();
		DomainSet domainSet = framework.getDomainSet();

		XMLEvent event;
		String endtag;
		String id;
		Expert expert = null;
		Alternative alternative = null;
		Criterion criterion = null;
		Domain domain = null;
		boolean end = false;
		while (reader.hasNext() && !end) {
			event = reader.next();

			if (event.isStartElement()) {
				id = reader.getStartElementAttribute("expert"); //$NON-NLS-1$
				expert = Expert.getExpertByCanonicalId(elementSet.getExperts(),id);

				id = reader.getStartElementAttribute("criterion"); //$NON-NLS-1$
				criterion = Criterion.getCriterionByCanonicalId(elementSet.getCriteria(), id);

				id = reader.getStartElementAttribute("alternative"); //$NON-NLS-1$
				for (Alternative a : elementSet.getAlternatives()) {
					if (id.equals(a.getId())) {
						alternative = a;
					}
				}

				id = reader.getStartElementAttribute("domain"); //$NON-NLS-1$
				domain = domainSet.getDomain(id);

			} else if (event.isEndElement()) {
				endtag = reader.getEndElementLocalPart();
				if (endtag.equals("assignment")) { //$NON-NLS-1$
					setDomain(expert, alternative, criterion, domain);
				} else if (endtag.equals("domain-assignments")) { //$NON-NLS-1$
					end = true;
				}
			}
		}
	}

	@Override
	public Object clone() {

		DomainAssignments result = null;

		try {
			result = (DomainAssignments) super.clone();
			for (DomainAssignmentKey key : _assignments.keySet()) {
				result._assignments.put(
						new DomainAssignmentKey(key.getExpert(), key
								.getAlternative(), key.getCriterion()),
						_assignments.get(key));
			}
		} catch (Exception e) {
		}

		return result;

	}

	@Override
	public int hashCode() {

		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);

		List<DomainAssignmentKey> keys = new LinkedList<DomainAssignmentKey>(
				_assignments.keySet());
		Collections.sort(keys);
		for (DomainAssignmentKey key : keys) {
			hcb.append(key);
			hcb.append(_assignments.get(key));
		}

		return hcb.hashCode();
	}

	public void registerDomainAssignmentsChangeListener(
			IDomainAssignmentsChangeListener listener) {
		_listeners.add(listener);
	}

	public void unregisterDomainAssignmentsChangeListener(
			IDomainAssignmentsChangeListener listener) {
		_listeners.remove(listener);
	}

	public void notifyDomainAssignmentsChange(DomainAssignmentsChangeEvent event) {
		for (IDomainAssignmentsChangeListener listener : _listeners) {
			listener.notifyDomainAssignmentsChange(event);
		}
		Workspace.getWorkspace().updateHashCode();
	}

	@Override
	public void notifyNewProblemElementsSet(ProblemElementsSet elementSet) {
		
		if (_elementSet != elementSet) {
			_elementSet.unregisterExpertsChangeListener(this);
			_elementSet.unregisterAlternativesChangeListener(this);
			_elementSet.unregisterCriteriaChangeListener(this);
			_elementSet = elementSet;
			_elementSet.registerExpertsChangesListener(this);
			_elementSet.registerAlternativesChangesListener(this);
			_elementSet.registerCriteriaChangesListener(this);
			
			removeDomainAssignmentsOperation(ERemoveDomainAssignments.ALL, null);
		}
	}

	@Override
	public void notifyCriteriaChange(CriteriaChangeEvent event) {
		if (!event.getInUndoRedo()) {
			ECriteriaChange change = event.getChange();

			if (ECriteriaChange.REMOVE_CRITERION.equals(change)) {
				removeDomainAssignmentsOperation(ERemoveDomainAssignments.CRITERION, event.getOldValue());

			} else if (ECriteriaChange.REMOVE_CRITERIA.equals(change)) {
				removeDomainAssignmentsOperation(ERemoveDomainAssignments.CRITERIA, event.getOldValue());

			} else if (ECriteriaChange.CRITERIA_CHANGES.equals(change)) {
				removeDomainAssignmentsOperation(ERemoveDomainAssignments.ALL, null);

			} else if (ECriteriaChange.ADD_CRITERION.equals(change)) {
				Criterion criterion = (Criterion) event.getNewValue();
				if (criterion.getParent() != null) {
					removeDomainAssignmentsOperation(ERemoveDomainAssignments.CRITERION, criterion.getParent());
				}

			} else if (ECriteriaChange.ADD_CRITERIA.equals(change)) {
				@SuppressWarnings("unchecked")
				Criterion criterion = ((List<Criterion>) event.getNewValue()).get(0);
				if (criterion.getParent() != null) {
					removeDomainAssignmentsOperation(ERemoveDomainAssignments.CRITERION,criterion.getParent());
				}
			} else if (ECriteriaChange.MOVE_CRITERION.equals(change)) {
				Criterion criterion = (Criterion) event.getOldValue();
				if (criterion != null) {
					removeDomainAssignmentsOperation(ERemoveDomainAssignments.CRITERION, criterion);
				}
			}
		}

	}

	@Override
	public void notifyAlternativesChange(AlternativesChangeEvent event) {

		if (!event.getInUndoRedo()) {
			EAlternativesChange change = event.getChange();

			if (EAlternativesChange.REMOVE_ALTERNATIVE.equals(change)) {
				removeDomainAssignmentsOperation(ERemoveDomainAssignments.ALTERNATIVE, event.getOldValue());

			} else if (EAlternativesChange.REMOVE_MULTIPLE_ALTERNATIVES.equals(change)) {
				removeDomainAssignmentsOperation(ERemoveDomainAssignments.ALTERNATIVES, event.getOldValue());

			} else if (EAlternativesChange.ALTERNATIVES_CHANGES.equals(change)) {
				removeDomainAssignmentsOperation(ERemoveDomainAssignments.ALL, null);
			}
		}

	}

	@Override
	public void notifyExpertsChange(ExpertsChangeEvent event) {

		if (!event.getInUndoRedo()) {

			EExpertsChange change = event.getChange();

			if (EExpertsChange.REMOVE_EXPERT.equals(change)) {
				removeDomainAssignmentsOperation(ERemoveDomainAssignments.EXPERT, event.getOldValue());

			} else if (EExpertsChange.REMOVE_MULTIPLE_EXPERTS.equals(change)) {
				removeDomainAssignmentsOperation(ERemoveDomainAssignments.EXPERTS, event.getOldValue());

			} else if (EExpertsChange.EXPERTS_CHANGES.equals(change)) {
				removeDomainAssignmentsOperation(ERemoveDomainAssignments.ALL, null);

			} else if (EExpertsChange.ADD_EXPERT.equals(change)) {
				Expert expert = (Expert) event.getNewValue();
				if (expert.getParent() != null) {
					removeDomainAssignmentsOperation(ERemoveDomainAssignments.EXPERT, expert.getParent());
				}

			} else if (EExpertsChange.ADD_MULTIPLE_EXPERTS.equals(change)) {
				@SuppressWarnings("unchecked")
				Expert expert = ((List<Expert>) event.getNewValue()).get(0);
				if (expert.getParent() != null) {
					removeDomainAssignmentsOperation(ERemoveDomainAssignments.EXPERT, expert.getParent());
				}

			} else if (EExpertsChange.MOVE_EXPERT.equals(change)) {
				Expert expert = (Expert) event.getOldValue();
				if (expert != null) {
					removeDomainAssignmentsOperation(ERemoveDomainAssignments.EXPERT, expert);
				}
			}
		}

	}

	@Override
	public void notifyDomainSetListener(DomainSetChangeEvent event) {

		if (!event.getInUndoRedo()) {
			EDomainSetChange change = event.getChange();

			if (EDomainSetChange.REMOVE_DOMAIN.equals(change)) {
				removeDomainAssignmentsOperation(ERemoveDomainAssignments.DOMAIN, event.getOldValue());

			} else if (EDomainSetChange.REMOVE_DOMAINS.equals(change)) {
				removeDomainAssignmentsOperation(ERemoveDomainAssignments.DOMAINS, event.getOldValue());

			} else if (EDomainSetChange.DOMAINS_CHANGES.equals(change)) {
				removeDomainAssignmentsOperation(ERemoveDomainAssignments.ALL, null);
			}
		}
	}

	@Override
	public void notifyNewActiveDomainSet(DomainSet domainSet) {
		
		if (_domainSet != domainSet) {
			_domainSet.unregisterDomainsListener(this);
			_domainSet = domainSet;
			domainSet.registerDomainsListener(this);
			
			removeDomainAssignmentsOperation(ERemoveDomainAssignments.ALL, null);
		}
	}

	private void removeDomainAssignmentsOperation(ERemoveDomainAssignments type, Object value) {
		Map<DomainAssignmentKey, Domain> newAssignments = new RemoveDomainAssignmentsOperationProvider(this, type, value).check();
		if(newAssignments != null) {
			RemoveDomainAssignmentsOperation operation = new RemoveDomainAssignmentsOperation(this, newAssignments);
			IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
			operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
			try {
				operationHistory.execute(operation, null, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

}
