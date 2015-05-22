package sinbad2.element.criterion.operation;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;

public class ModifyCriterionOperation extends UndoableOperation {
	
	private ProblemElementsSet _elementSet;
	private Criterion _modifyCriterion;
	private List<Criterion> _subcriteriaOrOthers;
	private String _newId;
	private String _oldId;
	private boolean _newCost;
	private boolean _oldCost;
	

	public ModifyCriterionOperation(String label, Criterion modifyCriterion, String newId, Boolean newCost, ProblemElementsSet elementSet) {
		super(label);
		
		_elementSet = elementSet;
		_modifyCriterion = modifyCriterion;
		_newId = newId;
		_newCost = newCost;
		_oldId = _modifyCriterion.getId();
		_oldCost = modifyCriterion.getCost();
		
		_subcriteriaOrOthers = null;
		
		Criterion parent = _modifyCriterion.getParent();
		if(parent != null) {
			_subcriteriaOrOthers = parent.getSubcriteria();
		} else {
			_subcriteriaOrOthers = _elementSet.getCriteria();
		}
	}
	
	public ModifyCriterionOperation(String label, Criterion modifyCriterion, String newId, ProblemElementsSet elementSet) {
		this(label, modifyCriterion, newId, modifyCriterion.getCost(), elementSet);
	}
	
	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		_elementSet.modifyCriterion(_modifyCriterion, _newId, _newCost);
		
		if(!_newId.equals(_oldId)) {
			Collections.sort(_subcriteriaOrOthers);
			
		}
		
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		_elementSet.modifyCriterion(_modifyCriterion, _oldId, _oldCost);
		
		if(!_oldId.equals(_newId)) {
			Collections.sort(_subcriteriaOrOthers);
		}
		
		return Status.OK_STATUS;
	}

}
