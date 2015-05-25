package sinbad2.element.alternative.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;

public class RemoveAlternativeOperation extends UndoableOperation {
	
	private ProblemElementsSet _elementSet;
	private Alternative _removeAlternative;

	public RemoveAlternativeOperation(String label, ProblemElementsSet elementSet, Alternative removeAlternative) {
		super(label);

		_elementSet = elementSet;
		_removeAlternative = removeAlternative;
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		_elementSet.removeAlternative(_removeAlternative);
		
		return Status.OK_STATUS;
		
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		_elementSet.addAlternative(_removeAlternative);
		
		return Status.OK_STATUS;
		
	}

}
