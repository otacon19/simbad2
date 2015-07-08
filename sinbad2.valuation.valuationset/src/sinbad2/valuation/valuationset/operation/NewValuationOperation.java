package sinbad2.valuation.valuationset.operation;

import java.util.HashMap;
import java.util.Map;


import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;

public class NewValuationOperation extends UndoableOperation {
	
	private ValuationSet _valuationSet;
	private Expert _expert;
	private Alternative _alternative;
	private Criterion _criterion;
	private Valuation _valuation;
	
	private ProblemElementsSet _elementSet;
	private Map<ValuationKey, Valuation> _oldValuations;
	private Map<ValuationKey, Valuation> _newValuations;

	public NewValuationOperation(ValuationSet valuationSet, Expert expert, Alternative alternative, Criterion criterion, Valuation valuation) {
		super("");
		
		_valuationSet = valuationSet;
		_expert = expert;
		_alternative = alternative;
		_criterion = criterion;
		_valuation = valuation;
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementSet = elementsManager.getActiveElementSet();
		
		_oldValuations = new HashMap<ValuationKey, Valuation>();
		_newValuations = new HashMap<ValuationKey, Valuation>();
		
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		setValuation(_expert, _alternative, _criterion, _valuation);
		
		if(_newValuations.size() > 0) {
			_valuationSet.modifySeveralValuations(_oldValuations, _newValuations, _inUndoRedo);
		}
		
		return Status.OK_STATUS;
		
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		if(_newValuations.size() > 0) {
			Valuation valuation;
			for(ValuationKey key: _newValuations.keySet()) {
				valuation = _newValuations.get(key);
				_valuationSet.setValuation(key.getExpert(), key.getAlternative(), key.getCriterion(), valuation);
			}
			
			_valuationSet.modifySeveralValuations(_oldValuations, _newValuations, _inUndoRedo);
		}
		
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		
		if(_oldValuations.size() > 0) {
			Valuation valuation;
			for(ValuationKey key: _oldValuations.keySet()) {
				valuation = _oldValuations.get(key);
				if(valuation == null) {
					_valuationSet.removeValuation(key.getExpert(), key.getAlternative(), key.getCriterion());
				} else {
					_valuationSet.setValuation(key.getExpert(), key.getAlternative(), key.getCriterion(), valuation);
				}
			}
			_valuationSet.modifySeveralValuations(_newValuations, _oldValuations, _inUndoRedo);
		}
		
		return Status.OK_STATUS;
	}
	
	private void setValuation(Expert expert, Alternative alternative, Criterion criterion, Valuation valuation) {
		boolean doit = true;
		
		if(expert == null) {
			for(Expert e: _elementSet.getExperts()) {
				setValuation(e, alternative, criterion, valuation);
			}
			doit = false;
		} else if(expert.hasChildrens()) {
			for(Expert e: expert.getChildrens()) {
				setValuation(e, alternative, criterion, valuation);
			}
			doit = false;
		} else if(alternative == null) {
			for(Alternative a: _elementSet.getAlternatives()) {
				setValuation(expert, a, criterion, valuation);
			}
			doit = false;
		} else if(criterion == null) {
			for(Criterion c: _elementSet.getCriteria()) {
				setValuation(expert, alternative, c, valuation);
			}
			doit = false;
		} else if(criterion.hasSubcriteria()) {
			for(Criterion c: criterion.getSubcriteria()) {
				setValuation(expert, alternative, c, valuation);
			}
			doit = false;
		}
		
		if(doit) {
			Valuation oldValuation = _valuationSet.getValuation(expert, alternative, criterion);
			_oldValuations.put(new ValuationKey(expert, alternative, criterion), oldValuation);
			
			_valuationSet.setValuation(expert, alternative, criterion, valuation);
			_newValuations.put(new ValuationKey(expert, alternative, criterion), valuation);
		}
	}

}
