package sinbad2.phasemethod.aggregation.ui.view.editingsupport;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.element.ProblemElement;
import sinbad2.phasemethod.aggregation.AggregationPhase;
import sinbad2.phasemethod.aggregation.ui.view.dialog.ParametersDialog;

public class ParametersEditingSupport extends EditingSupport {
	
	private CheckboxCellEditor _cellEditor;
	private TreeViewer _viewer;
	
	private String _type;
	
	private AggregationPhase _aggregationPhase;
	
	public ParametersEditingSupport(TreeViewer viewer, String type, AggregationPhase aggregationPhase) {
		super(viewer);

		_aggregationPhase = aggregationPhase;
		_type = type;
		_viewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		_cellEditor = new CheckboxCellEditor(_viewer.getTree());
		return _cellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		ProblemElement problemElement = null;
		if (element instanceof ProblemElement) {
			problemElement = (ProblemElement) element;
		}

		AggregationOperator operator;
		if (AggregationPhase.EXPERTS.equals(_type)) {
			operator = _aggregationPhase.getExpertOperator(problemElement);
		} else {
			operator = _aggregationPhase.getCriterionOperator(problemElement);
		}
		if (operator != null) {
			if (operator.hasParameters() && !operator.getName().equals("OWA")) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected Object getValue(Object element) {
		return true;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if ((element == null) || (value == null)) {
			return;
		}

		ProblemElement problemElement = null;
		if (element instanceof ProblemElement) {
			problemElement = (ProblemElement) element;
		}

		AggregationOperator operator;
		if (AggregationPhase.EXPERTS.equals(_type)) {
			operator = _aggregationPhase.getExpertOperator(problemElement);
		} else {
			operator = _aggregationPhase.getCriterionOperator(problemElement);
		}

		List<Double> parameters = operator.getParameters();
		ParametersDialog dialog = new ParametersDialog(Display.getCurrent().getActiveShell(), parameters);
		if(dialog.open() == ParametersDialog.SAVE) {
			operator.setParameters(dialog.getParameters());
		}	
	}
}
