package sinbad2.valuation.valuationset.operation;

import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;

public class RemoveValuationOperation extends UndoableOperation {
	
	private ValuationSet _valuationSet;

	private Map<ValuationKey, Valuation> _oldValuations;
	private Map<ValuationKey, Valuation> _newValuations;

	private RemoveValuationOperation() {
		super(""); //$NON-NLS-1$
	}

	public RemoveValuationOperation(ValuationSet valuationSet, Map<ValuationKey, Valuation> newValuations) {
		this();

		_valuationSet = valuationSet;
		_oldValuations = _valuationSet.getValuations();
		_newValuations = newValuations;
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return redo(monitor, info);

	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		_valuationSet.setValuations(_newValuations);
		_valuationSet.modifySeveralValuations(_oldValuations, _newValuations, _inUndoRedo);

		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		_valuationSet.setValuations(_oldValuations);
		_valuationSet.modifySeveralValuations(_newValuations, _oldValuations, _inUndoRedo);
		
		return Status.OK_STATUS;
	}

}
