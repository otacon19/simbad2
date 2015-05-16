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
	private List<Expert> _others;
	private List<Expert> _childrens;
	private String _newIdExpert;
	private String _oldIdExpert;

	public ModifyExpertOperation(String label, Expert expert, String newId, ProblemElementsSet elementSet) {
		super(label);
		
		_elementSet = elementSet;
		_modifyExpert = expert;
		_newIdExpert = newId;
		_oldIdExpert = expert.getId();
		
		_childrens = new LinkedList<Expert>();
		_others = new LinkedList<Expert>();
		
		Expert parent = _modifyExpert.getParent();
		
		if(parent != null) {
			_childrens = parent.getChildrens();
		} else {
			_others = elementSet.getExperts();
		}
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		_elementSet.modifyExpert(_modifyExpert, _newIdExpert);
		
		if(_childrens.size() == 0) {
			Collections.sort(_others);
		} else {
			Collections.sort(_childrens);
		}
		
		return Status.OK_STATUS;

	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		_elementSet.modifyExpert(_modifyExpert, _oldIdExpert);
		
		if(_childrens.size() == 0) {
			Collections.sort(_others);
		} else {
			Collections.sort(_childrens);
		}
		
		return Status.OK_STATUS;
		
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return redo(monitor,info);
	}

}
