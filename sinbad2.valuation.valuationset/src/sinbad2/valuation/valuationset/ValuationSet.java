package sinbad2.valuation.valuationset;

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
import sinbad2.domain.listener.DomainSetChangeEvent;
import sinbad2.domain.listener.EDomainSetChange;
import sinbad2.domain.listener.IDomainSetListener;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.resolutionphase.framework.Framework;
import sinbad2.resolutionphase.frameworkstructuring.FrameworkStructuring;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignmentKey;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignments;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignmentsManager;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.listener.DomainAssignmentsChangeEvent;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.listener.IDomainAssignmentsChangeListener;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.ValuationsManager;
import sinbad2.valuation.valuationset.listener.EValuationSetChange;
import sinbad2.valuation.valuationset.listener.IValuationSetChangeListener;
import sinbad2.valuation.valuationset.listener.ValuationSetChangeEvent;
import sinbad2.valuation.valuationset.operation.ERemoveValuation;
import sinbad2.valuation.valuationset.operation.RemoveValuationOperation;
import sinbad2.valuation.valuationset.operation.RemoveValuationOperationProvider;

public class ValuationSet implements IDomainSetListener, IDomainAssignmentsChangeListener {
	
	private DomainAssignmentsManager _domainAssignmentsManager;
	private DomainAssignments _domainAssignments;

	private Map<ValuationKey, Valuation> _valuations;
	private List<IValuationSetChangeListener> _listeners;

	public ValuationSet() {
		_valuations = new HashMap<ValuationKey, Valuation>();
		_listeners = new LinkedList<IValuationSetChangeListener>();

		_domainAssignmentsManager = DomainAssignmentsManager.getInstance();
		_domainAssignments = _domainAssignmentsManager.getActiveDomainAssignments();
	}

	public void setValuations(Map<ValuationKey, Valuation> valuations) {
		_valuations = valuations;
	}

	public Map<ValuationKey, Valuation> getValuations() {
		return _valuations;
	}

	public void setValuation(Expert expert, Alternative alternative, Criterion criterion, Valuation valuation) {
		_valuations.put(new ValuationKey(expert, alternative, criterion), valuation);

	}

	public Valuation getValuation(Expert expert, Alternative alternative, Criterion criterion) {
		return _valuations.get(new ValuationKey(expert, alternative, criterion));
	}
	
	public void modifySeveralValuations(Map<ValuationKey, Valuation> oldValuations, Map<ValuationKey, Valuation> newValuations, boolean inUndoRedo) {
		notifyValuationSetChange(new ValuationSetChangeEvent(EValuationSetChange.MODIFY_MULTIPLE_VALUATIONS, oldValuations, newValuations,
				inUndoRedo));	
	}
	
	public void modifyValuation(Valuation oldValuations, Valuation newValuations, boolean inUndoRedo) {
		notifyValuationSetChange(new ValuationSetChangeEvent(EValuationSetChange.MODIFY_VALUATION, oldValuations, newValuations,
				inUndoRedo));
		
	}

	public void removeValuation(Expert expert, Alternative alternative, Criterion criterion) {
		_valuations.remove(new ValuationKey(expert, alternative, criterion));
	}

	public void clear() {
		
		if (_valuations.size() > 0) {
			_valuations.clear();

			notifyValuationSetChange(new ValuationSetChangeEvent(EValuationSetChange.VALUATIONS_CHANGES, null, getValuations(), false));
		}
		_valuations.clear();
	}

	public void save(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("valuations"); //$NON-NLS-1$
		Valuation valuation;
		for (ValuationKey key : _valuations.keySet()) {
			valuation = _valuations.get(key);
			writer.writeStartElement(valuation.getId()); //$NON-NLS-1$
			writer.writeAttribute("domain-id", valuation.getDomain().getId()); //$NON-NLS-1$
			writer.writeAttribute("expert", key.getExpert().getCanonicalId()); //$NON-NLS-1$
			writer.writeAttribute("alternative", key.getAlternative().getCanonicalId()); //$NON-NLS-1$
			writer.writeAttribute("criterion", key.getCriterion().getCanonicalId()); //$NON-NLS-1$
			
			valuation.save(writer);
			writer.writeEndElement();
		}

		writer.writeEndElement();
	}

	public void read(XMLRead reader, Framework framework, FrameworkStructuring frameworkStructuring) throws XMLStreamException {
		reader.goToStartElement("valuations"); //$NON-NLS-1$

		ValuationsManager manager = ValuationsManager.getInstance();
		DomainSet ds = framework.getDomainSet();
		ProblemElementsSet es = framework.getElementSet();

		XMLEvent event;
		
		String id = null;
		String domainId = null;
		String expertId = null;
		String alternativeId = null;
		String criterionId = null;
		String endtag = null;
		
		List<Expert> experts = es.getExperts();
		List<Criterion> criteria = es.getCriteria();
		List<Alternative> alternatives = es.getAlternatives();
		
		Valuation valuation = null;
		Expert expert = null;
		Criterion criterion = null;
		Alternative alternative = null;
		Domain domain = null;
		
		
		boolean end = false;
		while (reader.hasNext() && !end) {
			event = reader.next();

			if (event.isStartElement()) {
				id = reader.getStartElementLocalPart();
				valuation = manager.copyValuation(id);
				domainId = reader.getStartElementAttribute("domain-id"); //$NON-NLS-1$
				expertId = reader.getStartElementAttribute("expert"); //$NON-NLS-1$
				alternativeId = reader.getStartElementAttribute("alternative"); //$NON-NLS-1$
				criterionId = reader.getStartElementAttribute("criterion"); //$NON-NLS-1$
				
				domain = ds.getDomain(domainId);
				valuation.setDomain(domain);
				
				expert = Expert.getExpertByCanonicalId(experts, expertId);
				criterion = Criterion.getCriterionByCanonicalId(criteria, criterionId);
				for (Alternative a : alternatives) {
					if (a.getId().equals(alternativeId)) {
						alternative = a;
					}
				}
				
				try {
					valuation.read(reader);
				} catch (Exception e) {
					throw new XMLStreamException();
				}

			} else if (event.isEndElement()) {
				endtag = reader.getEndElementLocalPart();
				if (endtag.equals(id)) {
					setValuation(expert, alternative, criterion, valuation);
				} else if (endtag.equals("valuations")) { //$NON-NLS-1$
					end = true;
				}
			}
		}
	}

	@Override
	public int hashCode() {

		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		List<ValuationKey> keys = new LinkedList<ValuationKey>(_valuations.keySet());
		
		Collections.sort(keys);
		
		for (ValuationKey key : keys) {
			hcb.append(key);
			hcb.append(_valuations.get(key));
		}

		return hcb.toHashCode();
	}

	@Override
	public Object clone() {

		ValuationSet result = null;

		try {
			result = (ValuationSet) super.clone();
			for (ValuationKey key : _valuations.keySet()) {
				result._valuations.put(
						new ValuationKey(key.getExpert(), key.getAlternative(),
								key.getCriterion()), _valuations.get(key));
			}
		} catch (Exception e) {
		}

		return result;

	}

	public void registerValuationSetChangeListener(IValuationSetChangeListener listener) {
		_listeners.add(listener);
	}

	public void unregisterValuationSetChangeListener(IValuationSetChangeListener listener) {
		_listeners.remove(listener);
	}

	public void notifyValuationSetChange(ValuationSetChangeEvent event) {
		for (IValuationSetChangeListener listener : _listeners) {
			listener.notifyValuationSetChange(event);
		}
		Workspace.getWorkspace().updateHashCode();
	}
	
	@Override
	public void notifyDomainSetListener(DomainSetChangeEvent event) {

		if (!event.getInUndoRedo()) {
			EDomainSetChange change = event.getChange();

			if (EDomainSetChange.MODIFY_DOMAIN.equals(change)) {
				removeValuationsOperation(ERemoveValuation.DOMAIN, event.getOldValue());
			}
		}
	}
	
	@Override
	public void notifyNewActiveDomainAssignments(DomainAssignments domainAssignments) {
		if (_domainAssignments != domainAssignments) {
			_domainAssignments.unregisterDomainAssignmentsChangeListener(this);
			_domainAssignments = domainAssignments;
			_domainAssignments.registerDomainAssignmentsChangeListener(this);
			removeValuationsOperation(ERemoveValuation.ALL, null);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void notifyDomainAssignmentsChange(DomainAssignmentsChangeEvent event) {
		
		if (!event.getInUndoRedo()) {
			List<ValuationKey> valuations = new LinkedList<ValuationKey>();
			
			Map<DomainAssignmentKey, Domain> v = (Map<DomainAssignmentKey, Domain>) event.getOldValue();
			for(DomainAssignmentKey dk: v.keySet()) {
				ValuationKey vk = new ValuationKey(dk.getExpert(), dk.getAlternative(), dk.getCriterion());
				if(_valuations.get(vk) != null) {
					valuations.add(vk);
				}
			}
				
			removeValuationsOperation(ERemoveValuation.VALUATIONS, valuations);
		}
	}

	public void removeValuationsOperation(ERemoveValuation type, Object value) {
		Map<ValuationKey, Valuation> newValuations = new RemoveValuationOperationProvider(this, type, value).check();
		
		if (newValuations != null) {
			RemoveValuationOperation operation = new RemoveValuationOperation(this, newValuations);
			IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();

			operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
			try {
				operationHistory.execute(operation, null, null);
			} catch (ExecutionException e) {
			}
		}
	}

}
