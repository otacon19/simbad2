package sinbad2.element.ui.view.criteria.editing;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.criterion.operation.ModifyCriterionOperation;
import sinbad2.element.ui.nls.Messages;

public class CriterionCostEditingSupport extends EditingSupport {
	
	private final TreeViewer _treeViewer;
	private CheckboxCellEditor _cellEditor;

	public CriterionCostEditingSupport(TreeViewer treeViewer) {
		super(treeViewer);
		_treeViewer = treeViewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		_cellEditor = new CheckboxCellEditor(_treeViewer.getTree());
		return _cellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		return ((Criterion) element).getCost();
	}

	@Override
	protected void setValue(Object element, Object value) {
		boolean oldValue = (Boolean) ((Criterion) element).getCost();
		boolean newValue = (Boolean) value;
		
		if(newValue = oldValue) {
			return;
		}
		
		ProblemElementsManager elementManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementSet = elementManager.getActiveElementSet();
		
		Criterion criterion = (Criterion) element;
		
		IUndoableOperation operation = new ModifyCriterionOperation(Messages.CriterionCostEditingSupport_Modify_criterion, criterion, criterion.getId(), newValue, elementSet);
		IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
		
		operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
		try {
			operationHistory.execute(operation, null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
	}

}
