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

public class AddExpertOperation extends AbstractOperation {
	
	private ProblemElementsSet _elementSet;
	private List<Expert> _experts;
	private Expert _newExpertParent;
	private Expert _newExpert;
	private String _newExpertID;

	public AddExpertOperation(String label, String id, Expert parent, ProblemElementsSet elementSet) {
		super(label);
		
		_elementSet = elementSet;
		_experts = _elementSet.getExperts();
		_newExpertID = id;
		_newExpertParent = parent;
		_newExpert = new Expert(_newExpertID);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {

		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		if(_newExpertParent == null) {
			_elementSet.insertExpert(_newExpert);
		} else {
			_newExpertParent.addChildren(_newExpert);
		}
		
		notify(EExpertsChange.ADD_EXPERT, null, _newExpert);
		
		return Status.OK_STATUS;
		
	}
	
	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		if(_newExpertParent == null) {
			_experts.remove(_newExpert);
		} else {
			_newExpertParent.removeChildren(_newExpert);
			_newExpert.setParent(_newExpertParent);
		}
		
		notify(EExpertsChange.REMOVE_EXPERT, _newExpert, null);
		
		return Status.OK_STATUS;
		
	}

	private void notify(EExpertsChange change, Object oldValue, Expert newValue) {
		_elementSet.notifyExpertsChanges(new ExpertsChangeEvent(change, oldValue, newValue));
		
	}

}
