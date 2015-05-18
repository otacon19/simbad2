package sinbad2.element.alternative.operation;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;

public class RemoveMultipleAlternativesOperation extends UndoableOperation {
	
	private ProblemElementsSet _elementSet;
	private List<Alternative> _removeAlternatives;

	public RemoveMultipleAlternativesOperation(String label, ProblemElementsSet elementSet, List<Alternative> removeAlternatives) {
		super(label);
		
		_elementSet = elementSet;
		_removeAlternatives = removeAlternatives;
		
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return null;
		
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return null;
	}

}
