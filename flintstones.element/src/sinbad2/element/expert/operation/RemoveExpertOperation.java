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

public class RemoveExpertOperation extends AbstractOperation {
	
	private ProblemElementsSet _elementSet;
	private List<Expert> _experts;
	private Expert _removeExpert;
	private Expert _removeExpertParent;

	public RemoveExpertOperation(String label, Expert expert, ProblemElementsSet elementSet) {
		super(label);
		
		_elementSet = elementSet;
		_experts = _elementSet.getExperts();
		_removeExpert = expert;
		_removeExpertParent = _removeExpert.getParent();
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		if(_removeExpertParent == null) {
			_experts.remove(_removeExpert);
		} else {
			_removeExpertParent.removeChildren(_removeExpert);
			_removeExpert.setParent(_removeExpertParent);
		}
		
		notify(EExpertsChange.REMOVE_EXPERT, _removeExpert, null);
		
		return Status.OK_STATUS;
		
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		if(_removeExpertParent == null) {
			_elementSet.insertExpert(_removeExpert);
		} else {
			_removeExpertParent.addChildren(_removeExpert);
		}
		
		notify(EExpertsChange.ADD_EXPERT, null, _removeExpert);
		
		return Status.OK_STATUS;
		
	}
	
	private void notify(EExpertsChange change, Object oldValue, Object newValue) {
		_elementSet.notifyExpertsChanges(new ExpertsChangeEvent(change, oldValue, newValue));
		
	}

}
