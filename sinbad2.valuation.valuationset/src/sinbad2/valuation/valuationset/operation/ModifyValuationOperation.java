package sinbad2.valuation.valuationset.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;

public class ModifyValuationOperation extends UndoableOperation {
	
	private ValuationSet _valuationSet;
	private Valuation _newValuation;
	private Valuation _oldValuation;
	private ValuationKey _key;

	public ModifyValuationOperation(ValuationKey key, Valuation newValuation, Valuation oldValuation, ValuationSet valuationSet) {
		super(""); //$NON-NLS-1$

		_valuationSet = valuationSet;
		_key = key;
		_oldValuation = oldValuation;
		_newValuation = newValuation;
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		try {
			_valuationSet.removeValuation(_key.getExpert(), _key.getAlternative(), _key.getCriterion());
			_valuationSet.setValuation(_key.getExpert(), _key.getAlternative(), _key.getCriterion(), _newValuation);
			_valuationSet.modifyValuation(_oldValuation, _newValuation, _inUndoRedo);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		try {
			_valuationSet.removeValuation(_key.getExpert(), _key.getAlternative(), _key.getCriterion());
			_valuationSet.setValuation(_key.getExpert(), _key.getAlternative(), _key.getCriterion(), _oldValuation);
			_valuationSet.modifyValuation(_newValuation, _oldValuation, _inUndoRedo);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		return Status.OK_STATUS;
	}

}
