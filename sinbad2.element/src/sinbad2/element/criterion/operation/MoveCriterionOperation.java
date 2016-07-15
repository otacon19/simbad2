package sinbad2.element.criterion.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;

public class MoveCriterionOperation extends UndoableOperation {
	
	private ProblemElementsSet _elementSet;
	private Criterion _moveCriterion;
	private Criterion _newParent;
	private Criterion _oldParent;

	public MoveCriterionOperation(String label, Criterion moveCriterion, Criterion newParent, ProblemElementsSet elementSet) {
		super(label);
		
		_elementSet = elementSet;
		_moveCriterion = moveCriterion;
		_newParent = newParent;
		_oldParent = moveCriterion.getParent();
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
	
	private IStatus move(Criterion oldParent, Criterion newParent) {
		
		_elementSet.moveCriterion(_moveCriterion, newParent, oldParent, _inUndoRedo);
	
		return Status.OK_STATUS;
		
	}
	
}
