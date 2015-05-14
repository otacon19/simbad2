package sinbad2.element.expert.operation;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.element.ProblemElementsSet;
import sinbad2.element.expert.Expert;
import sinbad2.element.expert.listener.EExpertsChange;
import sinbad2.element.expert.listener.ExpertsChangeEvent;

public class RemoveExpertsOperation extends AbstractOperation {
	
	private ProblemElementsSet _elementSet;
	private List<Expert> _experts;
	private List<Expert> _removeSeveralExperts;
	private Expert _firstParentOfRemoveExperts;

	public RemoveExpertsOperation(String label, List<Expert> expertsRemove, ProblemElementsSet elementSet) {
		super(label);
		
		_elementSet = elementSet;
		_experts = _elementSet.getExperts();
		_removeSeveralExperts = expertsRemove;
		_firstParentOfRemoveExperts = _removeSeveralExperts.get(0).getParent();
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		for(Expert expert: _removeSeveralExperts) {
			if(_firstParentOfRemoveExperts == null) {
				_experts.remove(expert);
			} else {
				_firstParentOfRemoveExperts.removeChildren(expert);
				expert.setParent(_firstParentOfRemoveExperts);
			}
		
		}
		
		notify(EExpertsChange.REMOVE_SEVERAL_EXPERTS, _removeSeveralExperts, null);
			
		return Status.OK_STATUS;
		
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		for(Expert expert: _removeSeveralExperts) {
			if(_firstParentOfRemoveExperts == null) {
				_elementSet.insertExpert(expert);
			} else {
				_firstParentOfRemoveExperts.addChildren(expert);
			}
		}
		
		notify(EExpertsChange.ADD_SEVERAL_EXPERTS, null, _removeSeveralExperts);
		
		return Status.OK_STATUS;
	}
	
	private void notify(EExpertsChange change, Object oldValue, Object newValue) {
		_elementSet.notifyExpertsChanges(new ExpertsChangeEvent(change, oldValue, newValue));
		
	}
}
