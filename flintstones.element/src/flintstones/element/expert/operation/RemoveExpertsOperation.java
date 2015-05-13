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

public class RemoveExpertsOperation extends AbstractOperation {
	
	private ElementSet _elementSet;
	private List<Expert> _experts;
	private List<Expert> _expertsRemove;
	private Expert _parent;

	public RemoveExpertsOperation(String label, List<Expert> expertsRemove, ElementSet elementSet) {
		super(label);
		
		_elementSet = elementSet;
		_experts = _elementSet.getExperts();
		_expertsRemove = expertsRemove;
		_parent = _expertsRemove.get(0).getParent();
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		for(Expert expert: _expertsRemove) {
			if(_parent == null) {
				_experts.remove(expert);
			} else {
				_parent.removeMember(expert);
				expert.setParent(_parent);
			}
		
		}
		
		notify(EExpertsChange.REMOVE_EXPERTS, _expertsRemove, null);
			
		return Status.OK_STATUS;
		
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		for(Expert expert: _expertsRemove) {
			if(_parent == null) {
				_elementSet.insertExpert(expert);
			} else {
				_parent.addMember(expert);
			}
		}
		
		notify(EExpertsChange.ADD_EXPERTS, null, _expertsRemove);
		
		return Status.OK_STATUS;
	}
	
	private void notify(EExpertsChange change, Object oldValue, Object newValue) {
		_elementSet.notifyExpertsChanges(new ExpertsChangeEvent(change, oldValue, newValue));
		
	}
}
