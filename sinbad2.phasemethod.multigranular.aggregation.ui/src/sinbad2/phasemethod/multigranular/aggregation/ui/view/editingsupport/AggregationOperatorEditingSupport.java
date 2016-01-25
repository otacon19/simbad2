package sinbad2.phasemethod.multigranular.aggregation.ui.view.editingsupport;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.AggregationOperatorsManager;
import sinbad2.aggregationoperator.EAggregationOperatorType;
import sinbad2.element.ProblemElement;
import sinbad2.phasemethod.multigranular.aggregation.AggregationPhase;

public class AggregationOperatorEditingSupport extends EditingSupport {

	private CellEditor _cellEditor;
	
	private AggregationPhase _aggregationPhase;
	private String[] _aggregationOperatorsIds;
	private String _type;
	private boolean _abort;
	private boolean _assignAll;

	private AggregationOperatorsManager _aggregationOperatorsManager;
	
	private final TreeViewer _viewer;

	public AggregationOperatorEditingSupport(AggregationPhase aggregationPhase, TreeViewer _expertsViewer, String type) {
		super(_expertsViewer);

		_type = type;
		_aggregationOperatorsIds = null;
		_aggregationPhase = aggregationPhase;
		_aggregationOperatorsManager = AggregationOperatorsManager.getInstance();

		_viewer = _expertsViewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		if (_aggregationOperatorsIds == null) {

			EAggregationOperatorType operatorType = null;
				
			_aggregationOperatorsIds = _aggregationOperatorsManager.getAggregationOperatorsIdByType(operatorType);
	
			for (int i = 0; i < _aggregationOperatorsIds.length; i++) {
				_aggregationOperatorsIds[i] = _aggregationOperatorsIds[i].substring(0, 1).toUpperCase() + _aggregationOperatorsIds[i].substring(1);
			}
		}

		_cellEditor = new ComboBoxCellEditor(_viewer.getTree(), _aggregationOperatorsIds);
		
		return _cellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		if(element instanceof ProblemElement) {
			if (AggregationPhase.EXPERTS.equals(_type) || AggregationPhase.CRITERIA.equals(_type)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	protected Object getValue(Object element) {
		return getPos(element);
	}

	private int getPos(Object element) {
		String auxItems[] = ((ComboBoxCellEditor) _cellEditor).getItems();
		String items[] = new String[auxItems.length];
		for (int i = 0; i < auxItems.length; i++) {
			if (auxItems[i].startsWith("(W) ")) { //$NON-NLS-1$
				items[i] = auxItems[i].substring(4);
			} else {
				items[i] = auxItems[i];
			}
			items[i] = items[i].toLowerCase();
		}
		int pos = 0;
		int length = auxItems.length;
		if (length == 0) {
			return -1;
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
		if (operator != null) {
			while (!items[pos].equals(operator.getId())) {
				pos++;
				if (pos == length) {
					return -1;
				}
			}
			return pos;
		} else {
			return -1;
		}

	}

	private ProblemElement[] setOperator(ProblemElement element, AggregationOperator aggregationOperator) {

		ProblemElement[] result = new ProblemElement[] {};

		AggregationOperator operator = null;
		operator = aggregationOperator;

		if (AggregationPhase.EXPERTS.equals(_type)) {
			result = _aggregationPhase.setExpertOperator(element, operator);
		} else {
			result = _aggregationPhase.setCriterionOperator(element, operator);
		}

		if (result.length > 0) {
			if (!_assignAll) {
				MessageDialog dg = new MessageDialog(Display.getCurrent().getActiveShell(), "Assign the operator to all elements", null, "Assign the operator to all elements",
						MessageDialog.QUESTION_WITH_CANCEL, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 0);

				switch (dg.open()) {
				case 0:
					// yes
					_assignAll = true;
					break;
				case 1:
					// no
					_abort = true;
					break;
				}
			}
		}

		return result;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if ((element == null) || (value == null)) {
			return;
		}

		int oldValue = (Integer) getPos(element);
		int newValue = (Integer) value;

		if (newValue == oldValue) {
			return;
		}

		ProblemElement pe = null;
		if (element instanceof ProblemElement) {
			pe = (ProblemElement) element;
		}

		String id = _aggregationOperatorsIds[newValue];
		if (id.startsWith("(W) ")) { //$NON-NLS-1$
			id = id.substring(4);
		}
		id = id.toLowerCase();
		AggregationOperator operator = _aggregationOperatorsManager.getAggregationOperator(id);

		_abort = false;
		_assignAll = false;
		for (ProblemElement son : setOperator(pe, operator)) {
			if (!_abort) {
				for (ProblemElement sonSon : setOperator(son, operator)) {
					if (!_abort) {
						setOperator(sonSon, operator);
					}
				}
			}
		}

	}
}

