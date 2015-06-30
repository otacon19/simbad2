package sinbad2.resolutionphase.frameworkstructuring.domainassignments.operation;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.domain.Domain;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignmentKey;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignments;

public class ApplyDomainAssignmentsOperation extends UndoableOperation {
	
	private DomainAssignments _domainAssignments;
	private Expert _expert;
	private Alternative _alternative;
	private Criterion _criterion;
	private Domain _domain;
	
	private ProblemElementsSet _elementSet;
	private Map<DomainAssignmentKey, Domain> _oldDomainAssignments;
	private Map<DomainAssignmentKey, Domain> _newDomainAssignments;

	public ApplyDomainAssignmentsOperation(DomainAssignments domainAssignments, Expert expert, Alternative alternative, Criterion criterion,
			Domain domain) {
		super(""); //$NON-NLS-1$
		
		_domainAssignments = domainAssignments;
		_expert = expert;
		_alternative = alternative;
		_criterion = criterion;
		_domain = domain;
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementSet = elementsManager.getActiveElementSet();
		
		_oldDomainAssignments = new HashMap<DomainAssignmentKey, Domain>();
		_newDomainAssignments = new HashMap<DomainAssignmentKey, Domain>();
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		setDomain(_expert, _alternative, _criterion, _domain);
		
		if(_newDomainAssignments.size() > 0) {
			_domainAssignments.setDomain(_oldDomainAssignments, _newDomainAssignments, _inUndoRedo);
		}
		
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
	
		if(_newDomainAssignments.size() > 0) {
			Domain domain;
			for(DomainAssignmentKey key: _newDomainAssignments.keySet()) {
				domain = _newDomainAssignments.get(key);
				_domainAssignments.setDomain(key.getExpert(), key.getAlternative(), key.getCriterion(), domain);
			}
			
			_domainAssignments.setDomain(_oldDomainAssignments, _newDomainAssignments, _inUndoRedo);
		}
		
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		if(_oldDomainAssignments.size() > 0) {
			Domain domain;
			for(DomainAssignmentKey key: _oldDomainAssignments.keySet()) {
				domain = _oldDomainAssignments.get(key);
				if(domain == null) {
					_domainAssignments.removeDomain(key.getExpert(), key.getAlternative(), key.getCriterion());
				} else {
					_domainAssignments.setDomain(key.getExpert(), key.getAlternative(), key.getCriterion(), domain);
				}
			}
			
			_domainAssignments.setDomain(_newDomainAssignments, _oldDomainAssignments, _inUndoRedo);
		}
		
		return Status.OK_STATUS;
	}
	
	private void setDomain(Expert expert, Alternative alternative, Criterion criterion, Domain domain) {
		boolean doit = true;
		
		if (expert == null) {
			for (Expert e : _elementSet.getExperts()) {
				setDomain(e, alternative, criterion, domain);
			}
			doit = false;

		} else if (expert.hasChildrens()) {
			for (Expert e : expert.getChildrens()) {
				setDomain(e, alternative, criterion, domain);
			}
			doit = false;

		} else if (alternative == null) {
			for (Alternative a : _elementSet.getAlternatives()) {
				setDomain(expert, a, criterion, domain);
			}
			doit = false;

		} else if (criterion == null) {
			for (Criterion c : _elementSet.getCriteria()) {
				setDomain(expert, alternative, c, domain);
			}
			doit = false;

		} else if (criterion.hasSubcriteria()) {
			for (Criterion c : criterion.getSubcriteria()) {
				setDomain(expert, alternative, c, domain);
			}
			doit = false;

		}

		if (doit) {
			Domain oldAssignment = _domainAssignments.getDomain(expert, alternative, criterion);
			_oldDomainAssignments.put(new DomainAssignmentKey(expert, alternative, criterion), oldAssignment);

			_domainAssignments.setDomain(expert, alternative, criterion, domain);
			_newDomainAssignments.put(new DomainAssignmentKey(expert, alternative, criterion), domain);
		}
	}
		

}
