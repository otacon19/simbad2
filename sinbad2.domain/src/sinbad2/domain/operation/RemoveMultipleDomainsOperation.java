package sinbad2.domain.operation;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;

public class RemoveMultipleDomainsOperation extends UndoableOperation {
	
	private DomainSet _domainSet;
	private List<Domain> _removeDomains;
	
	public RemoveMultipleDomainsOperation(String label, List<Domain> removeDomains, DomainSet domainSet) {
		super(label);
		
		_domainSet = domainSet;
		_removeDomains = removeDomains;
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		_domainSet.removeMultipleDomains(_removeDomains);
		
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		_domainSet.addMultipleDomains(_removeDomains);
		
		return Status.OK_STATUS;
	}

}
