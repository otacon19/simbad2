package sinbad2.element.expert.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.expert.Expert;

public class AddExpertOperation extends UndoableOperation {
	
	private ProblemElementsSet _elementSet;
	private Expert _parent;
	private Expert _newExpert;
	private String _newExpertID;

	public AddExpertOperation(String label, String id, Expert parent, ProblemElementsSet elementSet) {
		super(label);
		
		_elementSet = elementSet;
		_newExpertID = id;
		_parent = parent;
		_newExpert = new Expert(_newExpertID);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		boolean hasParent = false;
		
		if(_parent == null) {
			_elementSet.insertExpert(_newExpert, hasParent);
		} else {
			hasParent = true;
			_parent.addChildren(_newExpert);
			_elementSet.insertExpert(_newExpert, hasParent);
		}

		return Status.OK_STATUS;
		
	}
	
	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		boolean hasParent = false;
		
		if(_parent == null) {
			_elementSet.removeExpert(_newExpert, hasParent);
		} else {
			hasParent = true;
			_parent.removeChildren(_newExpert);
			_newExpert.setParent(_parent);
			_elementSet.removeExpert(_newExpert, hasParent);
		}
		
		return Status.OK_STATUS;
		
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return redo(monitor, info);
	}

}
