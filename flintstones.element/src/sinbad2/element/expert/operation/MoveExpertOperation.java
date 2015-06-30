package sinbad2.element.expert.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.expert.Expert;

public class MoveExpertOperation extends UndoableOperation {
	
	private ProblemElementsSet _elementSet;
	private Expert _moveExpert;
	private Expert _newParent;
	private Expert _oldParent;

	public MoveExpertOperation(String label, Expert moveExpert, Expert newParent, ProblemElementsSet elementSet) {
		super(label);
		
		_elementSet = elementSet;
		_moveExpert = moveExpert;
		_newParent = newParent;
		_oldParent = moveExpert.getParent();
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return move(_oldParent, _newParent);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return move(_newParent, _oldParent);
	}
	
	private IStatus move(Expert oldParent, Expert newParent) {
		
		_elementSet.moveExpert(_moveExpert, newParent, oldParent, _inUndoRedo);
	
		return Status.OK_STATUS;
		
	}

}
