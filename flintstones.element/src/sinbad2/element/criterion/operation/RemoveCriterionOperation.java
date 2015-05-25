package sinbad2.element.criterion.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;

public class RemoveCriterionOperation extends UndoableOperation {
	
	private ProblemElementsSet _elementSet;
	private Criterion _removeCriterion;
	private Criterion _parent;
	

	public RemoveCriterionOperation(String label, Criterion removeCriterion, ProblemElementsSet elementSet) {
		super(label);
		
		_elementSet = elementSet;
		_removeCriterion = removeCriterion;
		_parent = _removeCriterion.getParent();
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		boolean hasParent = false;
		
		if(_parent == null) {
			_elementSet.removeCriterion(_removeCriterion, hasParent);;
		} else {
			hasParent = true;
			_parent.removeSubcriterion(_removeCriterion);
			_removeCriterion.setParent(_parent);
			_elementSet.removeCriterion(_removeCriterion, hasParent);
		}
			
		return Status.OK_STATUS;
		
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		boolean hasParent = false;
		
		if(_parent == null) {
			_elementSet.addCriterion(_removeCriterion, hasParent);
		} else {
			hasParent = true;
			_parent.addSubcriterion(_removeCriterion);
			_elementSet.addCriterion(_removeCriterion, hasParent);
			
		}
		
		return Status.OK_STATUS;
		
	}

}
