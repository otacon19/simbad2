package sinbad2.element.expert.operation;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.expert.Expert;

public class RemoveMultipleExpertsOperation extends UndoableOperation {
	
	private ProblemElementsSet _elementSet;
	private List<Expert> _removeSeveralExperts;
	private Expert _firstParentOfRemoveExperts;

	public RemoveMultipleExpertsOperation(String label, List<Expert> expertsRemove, ProblemElementsSet elementSet) {
		super(label);
		
		_elementSet = elementSet;
		_removeSeveralExperts = expertsRemove;
		_firstParentOfRemoveExperts = _removeSeveralExperts.get(0).getParent();
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		boolean hasParent = false;
		
		if(_firstParentOfRemoveExperts == null) {
			_elementSet.removeSeveralExperts(_removeSeveralExperts, hasParent);
		} else {
			hasParent = true;
			_elementSet.removeSeveralExperts(_removeSeveralExperts, hasParent);
		}
				
		return Status.OK_STATUS;	
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		boolean hasParent = false;
		
		if(_firstParentOfRemoveExperts == null) {
			_elementSet.insertSeveralExperts(_removeSeveralExperts, hasParent);
		} else {
			hasParent = true;
			_elementSet.insertSeveralExperts(_removeSeveralExperts, hasParent);
		}
		
		return Status.OK_STATUS;
	}
	
}
