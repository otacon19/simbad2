package sinbad2.domain.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;

public class ModifyDomainOperation extends UndoableOperation {
	
	private DomainSet _domainSet;
	private Domain _newDomain;
	private Domain _oldDomain;

	public ModifyDomainOperation(String label, Domain newDomain, Domain oldDomain, DomainSet domainSet) {
		super(label);
		
		_domainSet = domainSet;
		_newDomain = newDomain;
		_oldDomain = oldDomain;
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		_domainSet.modifyDomain(_oldDomain, _newDomain);
		
		return Status.OK_STATUS;
		
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		_domainSet.modifyDomain(_newDomain, _oldDomain);
		
		return Status.OK_STATUS;
	}

}
