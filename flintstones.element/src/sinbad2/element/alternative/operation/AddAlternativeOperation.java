package sinbad2.element.alternative.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;

public class AddAlternativeOperation extends UndoableOperation {
	
	private ProblemElementsSet _elementSet;
	private Alternative _addAlternative;
	private String _addAlternativeId;
	
	public AddAlternativeOperation(String label, ProblemElementsSet elementSet, String addAlternativeId) {
		super(label);
		
		_elementSet = elementSet;
		_addAlternativeId = addAlternativeId;
		_addAlternative = new Alternative(_addAlternativeId);
		
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return redo(monitor, info);
	}
	
	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		_elementSet.insertAlternative(_addAlternative);
		
		return Status.OK_STATUS;
	}
	
	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		_elementSet.removeAlternative(_addAlternative);
		
		return Status.OK_STATUS;
	}
	
	
	
}
