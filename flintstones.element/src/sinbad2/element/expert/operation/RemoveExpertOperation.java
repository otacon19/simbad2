package sinbad2.element.expert.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.element.ProblemElementsSet;
import sinbad2.element.expert.Expert;
import undoable.UndoableOperation;

public class RemoveExpertOperation extends UndoableOperation {
	
	private ProblemElementsSet _elementSet;
	private Expert _removeExpert;
	private Expert _parentOfRemoveExpert;

	public RemoveExpertOperation(String label, Expert expert, ProblemElementsSet elementSet) {
		super(label);
		
		_elementSet = elementSet;
		_removeExpert = expert;
		_parentOfRemoveExpert = _removeExpert.getParent();
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		boolean hasParent = false;
		
		if(_parentOfRemoveExpert == null) {
			_elementSet.removeExpert(_removeExpert, hasParent);
		} else {
			hasParent = true;
			_parentOfRemoveExpert.removeChildren(_removeExpert);
			_removeExpert.setParent(_parentOfRemoveExpert);
			_elementSet.removeExpert(_removeExpert, hasParent);
		}
			
		return Status.OK_STATUS;
		
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		boolean hasParent = false;
		
		if(_parentOfRemoveExpert == null) {
			_elementSet.insertExpert(_removeExpert, hasParent);
		} else {
			hasParent = true;
			_parentOfRemoveExpert.addChildren(_removeExpert);
			_elementSet.insertExpert(_removeExpert, hasParent);
			
		}
		
		return Status.OK_STATUS;
		
	}

}
