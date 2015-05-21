package sinbad2.element.criterion.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;

public class AddCriterionOperation extends UndoableOperation {
	
	private ProblemElementsSet _elementSet;
	private Criterion _newCriterion;
	private Criterion _parent;
	private String _newCriterionId;
	private boolean _cost;

	public AddCriterionOperation(String label, String newCriterionId, boolean cost, Criterion parent, ProblemElementsSet elementSet) {
		super(label);
		
		_elementSet = elementSet;
		_newCriterionId = newCriterionId;
		_cost = cost;
		_parent = parent;
		
		_newCriterion = new Criterion(_newCriterionId);
		_newCriterion.setCost(_cost);
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		boolean hasParent = false;
		
		if(_parent == null) {
			_elementSet.insertCriterion(_newCriterion, hasParent);
		} else {
			hasParent = true;
			_parent.addSubcriterion(_newCriterion);
			_elementSet.insertCriterion(_newCriterion, hasParent);
		}

		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		boolean hasParent = false;
		
		if(_parent == null) {
			_elementSet.removeCriterion(_newCriterion, hasParent);
		} else {
			hasParent = true;
			_parent.removeSubcriterion(_newCriterion);
			_newCriterion.setParent(_parent);
			_elementSet.removeCriterion(_newCriterion, hasParent);
		}
		
		return Status.OK_STATUS;
	}

}
