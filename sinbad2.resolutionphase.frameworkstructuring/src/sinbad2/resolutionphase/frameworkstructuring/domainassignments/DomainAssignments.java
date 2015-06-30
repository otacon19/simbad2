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
		
		_elementsManager = ProblemElementsManager.getInstance();
		_elementSet = _elementsManager.getActiveElementSet();
		_domainsManager = DomainsManager.getInstance();
		_domainSet = _domainsManager.getActiveDomainSet();
		
		_listeners = new LinkedList<IDomainAssignmentsChangeListener>();
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
		return _assignments.get(new DomainAssignmentKey(expert, alternative, criterion));
	}
	
	public void removeDomain(Expert expert, Alternative alternative, Criterion criterion) {
		_assignments.remove(new DomainAssignmentKey(expert, alternative, criterion));
	}
	
	public void clear() {
		_assignments.clear();
	}
	
	public void save(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("domain-assignments"); //$NON-NLS-1$
		
		for(DomainAssignmentKey key: _assignments.keySet()) {
			writer.writeStartElement("assignment"); //$NON-NLS-1$
			writer.writeAttribute("expert", key.getExpert().getPathId()); //$NON-NLS-1$
			writer.writeAttribute("alternative", key.getAlternative().getPathId()); //$NON-NLS-1$
			writer.writeAttribute("criterion", key.getCriterion().getPathId()); //$NON-NLS-1$
			writer.writeAttribute("domain", _assignments.get(key).getId()); //$NON-NLS-1$
			writer.writeEndElement();
		}
		writer.writeEndElement();
	}
	
	public void read(XMLRead reader, Framework framework) throws XMLStreamException {
		reader.goToStartElement("domain-assignments"); //$NON-NLS-1$
		
		ProblemElementsSet elementsSet = framework.getElementSet();
		DomainSet domainSet = framework.getDomainSet();
		
		XMLEvent event;
		String endtag, id;
		Expert expert = null;
		Alternative alternative = null;
		Criterion criterion = null;
		Domain domain = null;
		
		boolean end = false;
		while(reader.hasNext() && !end) {
			event = reader.next();
			if(event.isStartElement()) {
				id = reader.getStartElementAttribute("expert"); //$NON-NLS-1$
				expert = Expert.getExpertByFormatId(elementsSet.getExperts(), id);
				
				id = reader.getStartElementAttribute("alternative"); //$NON-NLS-1$
				for(Alternative a: elementsSet.getAlternatives()) {
					if(id.equals(a.getId())) {
						alternative = a;
					}
				}
				
				id = reader.getStartElementAttribute("criterion"); //$NON-NLS-1$
				criterion = Criterion.getCriterionByFormatId(elementsSet.getCriteria(), id);
				
				id = reader.getStartElementAttribute("domain"); //$NON-NLS-1$
				domain = domainSet.getDomain(id);
				
			} else if(event.isEndElement()) {
				endtag = reader.getEndElementLocalPart();
				if(endtag.equals("assignment")) { //$NON-NLS-1$
					setDomain(expert, alternative, criterion, domain);
				} else if(endtag.equals("domain-assignments")) { //$NON-NLS-1$
					end = true;
				}
			}
		}
	}
	
	@Override
	public Object clone()  {
		DomainAssignments result = null;
		
		try {
			result = (DomainAssignments) super.clone();
			for(DomainAssignmentKey key: _assignments.keySet()) {
				result._assignments.put(new DomainAssignmentKey(key.getExpert(), key.getAlternative(), key.getCriterion()), 
						_assignments.get(key));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		
		List<DomainAssignmentKey> keys = new LinkedList<DomainAssignmentKey>(_assignments.keySet());
		Collections.sort(keys);
		
		for(DomainAssignmentKey key: keys) {
			hcb.append(key);
			hcb.append(_assignments.get(key));
		}
		
		return hcb.hashCode();
	}
	
	public void registerDomainAssignmentsChangeListener(IDomainAssignmentsChangeListener listener) {
		_listeners.add(listener);
	}
	
	public void unregisterDomainAssignmentsChangeListener(IDomainAssignmentsChangeListener listener) {
		_listeners.remove(listener);
	}
	
	public void notifyDomainAssignmentsChange(DomainAssignmentsChangeEvent event) {
		
		for(IDomainAssignmentsChangeListener listener: _listeners) {
			listener.notifyDomainAssignmentsChange(event);
		}
		
		Workspace.getWorkspace().updateHashCode();
	}
	
	@Override
	public void notifyDomainSetListener(DomainSetChangeEvent event) {
		
		if(!event.getInUndoRedo()) {
			EDomainSetChange change = event.getChange();
			switch(change) {
				case REMOVE_DOMAIN:
					removeDomainAssignmentsOperation(ERemoveDomainAssignments.DOMAIN, event.getOldValue());
					break;
				case REMOVE_DOMAINS:
					removeDomainAssignmentsOperation(ERemoveDomainAssignments.DOMAINS, event.getOldValue());
					break;
				case DOMAINS_CHANGES:
					removeDomainAssignmentsOperation(ERemoveDomainAssignments.ALL, null);
					break;
					
				default: break;
			}
		}
		
	}

	@Override
	public void notifyNewDomainSet(DomainSet domainSet) {

		if(_domainSet != domainSet) {
			_domainSet.unregisterDomainsListener(this);
			_domainSet = domainSet;
			_domainSet.registerDomainsListener(this);
			removeDomainAssignmentsOperation(ERemoveDomainAssignments.ALL, null);
		}
		
	}

	@Override
	public void notifyNewProblemElementsSet(ProblemElementsSet elementSet) {
		
		if(_elementSet != elementSet) {
			_elementSet.unregisterExpertsChangeListener(this);
			_elementSet.unregisterAlternativesChangeListener(this);
			_elementSet.unregisterCriteriaChangeListener(this);
			_elementSet = elementSet;
			_elementSet.registerExpertsChangesListener(this);
			_elementSet.registerAlternativesChangesListener(this);
			_elementSet.registerCriteriaChangeListener(this);
			removeDomainAssignmentsOperation(ERemoveDomainAssignments.ALL, null);
		}
		
	}

	@Override
	public void notifyCriteriaChange(CriteriaChangeEvent event) {
		
		if(!event.getInUndoRedo()) {
			ECriteriaChange change = event.getChange();
			switch(change) {
				case REMOVE_CRITERION:
					removeDomainAssignmentsOperation(ERemoveDomainAssignments.CRITERION, event.getOldValue());
					break;
				case REMOVE_CRITERIA:
					removeDomainAssignmentsOperation(ERemoveDomainAssignments.CRITERIA, event.getOldValue());
					break;
				case CRITERIA_CHANGES:
					removeDomainAssignmentsOperation(ERemoveDomainAssignments.ALL, null);
					break;
				case ADD_CRITERION:
					Criterion criterion = (Criterion) event.getNewValue();
					if(criterion.getParent() != null) {
						removeDomainAssignmentsOperation(ERemoveDomainAssignments.CRITERION, criterion.getParent());
					}
					break;
				case ADD_CRITERIA:
					@SuppressWarnings("unchecked")
					Criterion criterion2 = ((List<Criterion>) event.getNewValue()).get(0);
					if(criterion2.getParent() != null) {
						removeDomainAssignmentsOperation(ERemoveDomainAssignments.CRITERION, criterion2.getParent());
					}
					break;
				case MOVE_CRITERION:
					Criterion criterion3 = (Criterion) event.getOldValue();
					if(criterion3 != null) {
						removeDomainAssignmentsOperation(ERemoveDomainAssignments.CRITERION, criterion3);
					}
					break;
					
					default: break;
			}
		}
	}

	@Override
	public void notifyAlternativesChange(AlternativesChangeEvent event) {
		
		if(!event.getInUndoRedo()) {
			EAlternativesChange change = event.getChange();
			switch(change) {
				case REMOVE_ALTERNATIVE:
					removeDomainAssignmentsOperation(ERemoveDomainAssignments.ALTERNATIVE, event.getOldValue());
					break;
				case REMOVE_MULTIPLE_ALTERNATIVES:
					removeDomainAssignmentsOperation(ERemoveDomainAssignments.ALTERNATIVES, event.getOldValue());
					break;
				case ALTERNATIVES_CHANGES:
					removeDomainAssignmentsOperation(ERemoveDomainAssignments.ALL, null);
					break;
					
				default: break;
			}
		}
		
	}

	@Override
	public void notifyExpertsChange(ExpertsChangeEvent event) {
		
		if(!event.getInUndoRedo()) {
			EExpertsChange change = event.getChange();
			switch(change) {
				case REMOVE_EXPERT:
					removeDomainAssignmentsOperation(ERemoveDomainAssignments.EXPERT, event.getOldValue());
					break;
				case REMOVE_MULTIPLE_EXPERTS:
					removeDomainAssignmentsOperation(ERemoveDomainAssignments.EXPERTS, event.getOldValue());
					break;
				case EXPERTS_CHANGES:
					removeDomainAssignmentsOperation(ERemoveDomainAssignments.ALL, null);
					break;
				case ADD_EXPERT:
					Expert expert = (Expert) event.getNewValue();
					if(expert.getParent() != null) {
						removeDomainAssignmentsOperation(ERemoveDomainAssignments.EXPERT, expert.getParent());
					}
					break;
				case ADD_MULTIPLE_EXPERTS:
					@SuppressWarnings("unchecked")
					Expert expert2 = ((List<Expert>) event.getNewValue()).get(0);
					if(expert2.getParent() != null) {
						removeDomainAssignmentsOperation(ERemoveDomainAssignments.EXPERT, expert2.getParent());
					}
					break;
				case MOVE_EXPERT:
					Expert expert3 = (Expert) event.getOldValue();
					if(expert3 != null) {
						removeDomainAssignmentsOperation(ERemoveDomainAssignments.EXPERT, expert3);
					}
					break;
					
					default: break;
			}
		}
		
	}
	
	private void removeDomainAssignmentsOperation(ERemoveDomainAssignments all, Object object) {
		//TODO
		
	}

}
