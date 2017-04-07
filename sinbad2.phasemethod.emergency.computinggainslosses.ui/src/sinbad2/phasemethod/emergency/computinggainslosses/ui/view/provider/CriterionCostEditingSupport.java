package sinbad2.phasemethod.emergency.computinggainslosses.ui.view.provider;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;

import sinbad2.element.criterion.Criterion;

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
		return ((Criterion) element).isCost();
	}

	@Override
	protected void setValue(Object element, Object value) {
	}

}
