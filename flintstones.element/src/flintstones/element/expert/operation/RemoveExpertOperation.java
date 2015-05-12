package flintstones.element.expert.operation;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import flintstones.element.ElementSet;
import flintstones.element.expert.Expert;
import flintstones.element.expert.listener.EExpertsChange;
import flintstones.element.expert.listener.ExpertsChangeEvent;

public class RemoveExpertOperation extends AbstractOperation {
	
	private ElementSet _elementSet;
	private List<Expert> _experts;
	private Expert _expert;
	private Expert _parent;

	public RemoveExpertOperation(String label, Expert expert, ElementSet elementSet) {
		super(label);
		
		_elementSet = elementSet;
		_experts = _elementSet.getExperts();
		_expert = expert;
		_parent = _expert.getParent();
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		if(info != null) {
			Shell shell = (Shell) info.getAdapter(Shell.class);
			if(shell != null) {
				if(!MessageDialog.openQuestion(shell, "Borrar experto", "¿Quiéres eliminar el experto?")) {
					return Status.CANCEL_STATUS;
				}
			}
		}
		
		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		if(_parent == null) {
			_experts.remove(_expert);
		} else {
			_parent.removeMember(_expert);
			_expert.setParent(_parent);
		}
		
		notify(EExpertsChange.REMOVE_EXPERT, _expert, null);
		
		return Status.OK_STATUS;
		
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		if(_parent == null) {
			_elementSet.insertExpert(_expert);
		} else {
			_parent.addMember(_expert);
		}
		
		notify(EExpertsChange.ADD_EXPERT, null, _expert);
		
		return Status.OK_STATUS;
		
	}
	
	private void notify(EExpertsChange change, Object oldValue, Expert newValue) {
		_elementSet.notifyExpertsChanges(new ExpertsChangeEvent(change, oldValue, newValue));
		
	}

}
