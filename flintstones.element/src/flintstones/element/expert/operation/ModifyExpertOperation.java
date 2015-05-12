package flintstones.element.expert.operation;

import java.util.Collections;
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

public class ModifyExpertOperation extends AbstractOperation {
	
	private ElementSet _elementSet;
	private Expert _expert;
	private List<Expert> _brothers;
	private String _newId;
	private String _oldId;

	public ModifyExpertOperation(String label, Expert expert, String newId, ElementSet elementSet) {
		super(label);
		
		_elementSet = elementSet;
		_expert = expert;
		_newId = newId;
		_oldId = expert.getId();
		
		Expert parent = expert.getParent();
		if(parent != null) {
			_brothers = parent.getMembers();
		} else {
			_brothers = elementSet.getExperts();
		}
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		if(info != null) {
			Shell shell = (Shell) info.getAdapter(Shell.class);
			if(shell != null) {
				if(!MessageDialog.openQuestion(shell, "Modificar experto", "¿Quiéres modificar el experto?")) {
					return Status.CANCEL_STATUS;
				}
			}
		}
		
		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		Expert oldExpert = (Expert) _expert.clone();
		
		_expert.setId(_newId);
		Collections.sort(_brothers);
		
		notify(EExpertsChange.MODIFY_EXPERT, oldExpert, _expert);
		
		return Status.OK_STATUS;

	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		Expert oldExpert = (Expert) _expert.clone();
		
		_expert.setId(_oldId);
		Collections.sort(_brothers);
		
		notify(EExpertsChange.MODIFY_EXPERT, oldExpert, _expert);
		
		return Status.OK_STATUS;
		
	}
	
	public void notify(EExpertsChange change, Object oldValue, Object newValue) {
		_elementSet.notifyExpertsChanges(new ExpertsChangeEvent(change, oldValue, newValue));
	}

}
