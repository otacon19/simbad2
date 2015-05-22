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
	private Criterion _addCriterion;
	private Criterion _parent;
	private String _id;
	private boolean _cost;

	public AddCriterionOperation(String label, String id, boolean cost, Criterion parent, ProblemElementsSet elementSet) {
		super(label);
		
		_elementSet = elementSet;
		_id = id;
		_cost = cost;
		_parent = parent;
		
		_addCriterion = new Criterion(_id);
		_addCriterion.setCost(_cost);
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		boolean hasParent = false;
		
		if(_parent == null) {
			_elementSet.insertCriterion(_addCriterion, hasParent);
		} else {
			hasParent = true;
			_parent.addSubcriterion(_addCriterion);
			_elementSet.insertCriterion(_addCriterion, hasParent);
		}

		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		boolean hasParent = false;
		
		if(_parent == null) {
			_elementSet.removeCriterion(_addCriterion, hasParent);
		} else {
			hasParent = true;
			_parent.removeSubcriterion(_addCriterion);
			_addCriterion.setParent(_parent);
			_elementSet.removeCriterion(_addCriterion, hasParent);
		}
		
		return Status.OK_STATUS;
	}

}
