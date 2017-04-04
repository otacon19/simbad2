package sinbad2.phasemethod.emergency.aggregation.ui.view.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import sinbad2.element.ProblemElementsSet;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.emergency.aggregation.AggregationPhase;
import sinbad2.phasemethod.emergency.aggregation.ui.view.AggregationProcess;

public class WeightEditingSupport extends EditingSupport {

	private final TableViewer viewer;
	private final int col;
	
	private AggregationProcess _aggregationProcess;
	
	private AggregationPhase _aggregationPhase;
	private ProblemElementsSet _elementsSet;

	public WeightEditingSupport(TableViewer viewer, int col, AggregationPhase aggregationPhase, ProblemElementsSet elementsSet, AggregationProcess aggregationProcess) {
		super(viewer);

		this.viewer = viewer;
		this.col = col;
		this._aggregationProcess = aggregationProcess;
		this._aggregationPhase = aggregationPhase;
		this._elementsSet = elementsSet;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new TextCellEditor(viewer.getTable());
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		return ((Object[]) element)[col];
	}

	@Override
	protected void setValue(Object element, Object value) {

		if (value == null) {
			return;
		} else if (value instanceof String) {
			if (((String) value).isEmpty()) {
				return;
			}
		} else {
			return;
		}

		double newValue;

		try {
			newValue = (Double.parseDouble((String) value));
			if ((newValue < 0) || (newValue > 1)) {
				MessageDialog.openError(null, "Invalid range", "Invalid range");
				return;
			}
		} catch (Exception e) {
			MessageDialog.openError(null, "Invalid value", "Invalid value");
			return;
		}

		int row = getRow(element);
		setNewWeights(row, newValue);
	}

	public int getRow(Object element) {
		String key = (String) ((Object[]) element)[0];

		int pos = 0;
		for (Expert e : _elementsSet.getAllExperts()) {
			if(!e.hasChildren() && !e.getId().equals("predefined_effective_control")) {
				if (e.getCanonicalId().equals(key)) {
					pos = _elementsSet.getAllExperts().indexOf(e) - 1;
				}
			}
		}
		
		return pos;
	}
	
	private void setNewWeights(int row, double newValue) {
		Double[] weights = _aggregationPhase.getExpertsWeights();
		weights[row] = newValue;
		
		_aggregationPhase.setExpertsWeights(weights);
		
		refresh(weights);
	}

	private void refresh(Double[] weights) {
		int numExpert = 0;

		List<String[]> result = new LinkedList<String[]>();
		for(Expert e: _elementsSet.getAllExperts()) {
			if(!e.hasChildren() && !e.getId().equals("predefined_effective_control")) {
				String[] data = new String[2];
				data[0] = e.getCanonicalId();
				data[1] = Double.toString(weights[numExpert]);
				result.add(data);
				
				numExpert++;
			}
		}
		
		viewer.setInput(result);
		viewer.refresh();
		
		_aggregationProcess.setGRPValues();
	}
}
