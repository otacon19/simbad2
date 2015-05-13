package flintstones.element.expert.operation;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import flintstones.element.ElementSet;
import flintstones.element.expert.Expert;
import flintstones.element.expert.listener.EExpertsChange;
import flintstones.element.expert.listener.ExpertsChangeEvent;

public class AddExpertOperation extends AbstractOperation {
	
	private List<Expert> _experts;
	private Expert _parent;
	private ElementSet _elementSet;
	private String _id;
	private Expert _expert;

	public AddExpertOperation(String label, String id, Expert parent, ElementSet elementSet) {
		super(label);
		
		_elementSet = elementSet;
		_experts = _elementSet.getExperts();
		_id = id;
		_parent = parent;
		_expert = new Expert(_id);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {

		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		if(_parent == null) {
			_elementSet.insertExpert(_expert);
		} else {
			_parent.addMember(_expert);
		}
		
		notify(EExpertsChange.ADD_EXPERT, null, _expert);
		
		return Status.OK_STATUS;
		
	}
	
	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		if(_parent == null) {
			_experts.remove(_expert);
		} else {
			_parent.removeMember(_expert);
			_expert.setParent(_parent);
		}
		
		notify(EExpertsChange.REMOVE_EXPERT, _expert, null);
		
		return Status.OK_STATUS;
		
	}

	private void notify(EExpertsChange change, Object oldValue, Expert newValue) {
		_elementSet.notifyExpertsChanges(new ExpertsChangeEvent(change, oldValue, newValue));
		
	}

}
