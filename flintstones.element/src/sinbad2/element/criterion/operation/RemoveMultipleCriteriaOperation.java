package sinbad2.element.criterion.operation;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;

public class RemoveMultipleCriteriaOperation extends UndoableOperation {
	
	private ProblemElementsSet _elementSet;
	private List<Criterion> _removeCriteria;
	private Criterion _parent;

	public RemoveMultipleCriteriaOperation(String label, List<Criterion> removeCriteria, ProblemElementsSet elementSet) {
		super(label);
		
		_elementSet = elementSet;
		_removeCriteria = removeCriteria;
		_parent = _removeCriteria.get(0).getParent();
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		boolean hasParent = false;
		
		if(_parent == null) {
			_elementSet.removeMultipleCriteria(_removeCriteria, hasParent);
		} else {
			hasParent = true;
			_elementSet.removeMultipleCriteria(_removeCriteria, hasParent);
		}
				
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		boolean hasParent = false;
		
		if(_parent == null) {
			_elementSet.addMultipleCriteria(_removeCriteria, hasParent);
		} else {
			hasParent = true;
			_elementSet.addMultipleCriteria(_removeCriteria, hasParent);
		}
		
		return Status.OK_STATUS;

	}

}
