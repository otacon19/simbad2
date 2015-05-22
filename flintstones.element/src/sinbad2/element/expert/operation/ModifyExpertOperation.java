package sinbad2.element.expert.operation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.expert.Expert;

public class ModifyExpertOperation extends UndoableOperation {
	
	private ProblemElementsSet _elementSet;
	private Expert _modifyExpert;
	private List<Expert> _childrensOrOthers;
	private String _newId;
	private String _oldId;

	public ModifyExpertOperation(String label, Expert expert, String newId, ProblemElementsSet elementSet) {
		super(label);
		
		_elementSet = elementSet;
		_modifyExpert = expert;
		_newId = newId;
		_oldId = expert.getId();
		
		_childrensOrOthers = new LinkedList<Expert>();
		
		Expert parent = _modifyExpert.getParent();
		
		if(parent != null) {
			_childrensOrOthers = parent.getChildrens();
		} else {
			_childrensOrOthers = elementSet.getExperts();
		}
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		_elementSet.modifyExpert(_modifyExpert, _newId);

		Collections.sort(_childrensOrOthers);
		
		return Status.OK_STATUS;

	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		_elementSet.modifyExpert(_modifyExpert, _oldId);
		
		Collections.sort(_childrensOrOthers);
		
		return Status.OK_STATUS;
		
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return redo(monitor,info);
	}

}
