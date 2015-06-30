package sinbad2.element.expert.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.expert.Expert;

public class RemoveExpertOperation extends UndoableOperation {
	
	private ProblemElementsSet _elementSet;
	private Expert _removeExpert;
	private Expert _parent;

	public RemoveExpertOperation(String label, Expert expert, ProblemElementsSet elementSet) {
		super(label);
		
		_elementSet = elementSet;
		_removeExpert = expert;
		_parent = _removeExpert.getParent();
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		boolean hasParent = false;
		
		if(_parent == null) {
			_elementSet.removeExpert(_removeExpert, hasParent, _inUndoRedo);
		} else {
			hasParent = true;
			_parent.removeChildren(_removeExpert);
			_removeExpert.setParent(_parent);
			_elementSet.removeExpert(_removeExpert, hasParent, _inUndoRedo);
		}
			
		return Status.OK_STATUS;
		
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		boolean hasParent = false;
		
		if(_parent == null) {
			_elementSet.addExpert(_removeExpert, hasParent, _inUndoRedo);
		} else {
			hasParent = true;
			_parent.addChildren(_removeExpert);
			_elementSet.addExpert(_removeExpert, hasParent, _inUndoRedo);
			
		}
		
		return Status.OK_STATUS;
		
	}

}
