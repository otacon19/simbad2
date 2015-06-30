package sinbad2.resolutionphase.frameworkstructuring.domainassignments.operation;

import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.domain.Domain;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignmentKey;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignments;

public class RemoveDomainAssignmentsOperation extends UndoableOperation {

	private DomainAssignments _domainAssignments;

	private Map<DomainAssignmentKey, Domain> _oldDomainAssignments;
	private Map<DomainAssignmentKey, Domain> _newDomainAssignments;

	private RemoveDomainAssignmentsOperation() {
		super(""); //$NON-NLS-1$
	}

	public RemoveDomainAssignmentsOperation(
			DomainAssignments domainAssignments,
			Map<DomainAssignmentKey, Domain> newDomainAssignments) {
		this();

		_domainAssignments = domainAssignments;
		_oldDomainAssignments = _domainAssignments.getAssignments();
		_newDomainAssignments = newDomainAssignments;
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return redo(monitor, info);

	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		_domainAssignments.setAssignments(_newDomainAssignments);
		_domainAssignments.setDomain(_oldDomainAssignments, _newDomainAssignments, _inUndoRedo);

		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		_domainAssignments.setAssignments(_oldDomainAssignments);
		_domainAssignments.setDomain(_newDomainAssignments, _oldDomainAssignments, _inUndoRedo);

		return Status.OK_STATUS;
	}

}
