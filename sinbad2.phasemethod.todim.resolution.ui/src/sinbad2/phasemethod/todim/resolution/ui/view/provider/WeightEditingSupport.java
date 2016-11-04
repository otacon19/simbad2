package sinbad2.phasemethod.todim.resolution.ui.view.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.phasemethod.todim.resolution.ResolutionPhase;
import sinbad2.phasemethod.todim.resolution.ui.view.CalculateRanking;

public class WeightEditingSupport extends EditingSupport{

	private final TableViewer viewer;
	private final int col;

	private ResolutionPhase _resolutionPhase;
	
	private CalculateRanking _calculateRanking;
	
	private ProblemElementsSet _elementsSet;

	public WeightEditingSupport(TableViewer viewer, int col, ResolutionPhase resolutionPhase, ProblemElementsSet elementsSet, CalculateRanking calculateRanking) {
		super(viewer);

		this.viewer = viewer;
		this.col = col;
		this._resolutionPhase = resolutionPhase;
		this._calculateRanking = calculateRanking;
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
		
		_calculateRanking.refreshTODIMTables();
	}

	public int getRow(Object element) {
		String key = (String) ((Object[]) element)[0];

		int pos = 0;
		for (Criterion c : _elementsSet.getAllCriteria()) {
			if (c.getCanonicalId().equals(key)) {
				pos = _elementsSet.getAllCriteria().indexOf(c);
			}
		}
		
		return pos;
	}
	
	private Object setNewWeights(int row, double newValue) {
		List<Double> weights = _resolutionPhase.transformWeightsToList();
		weights.remove(row);
		weights.add(row, newValue);
		
		Map<Criterion, Double> mapWeigths = new HashMap<Criterion, Double>();
		int numCriterion = 0;
		for(Criterion c: _elementsSet.getAllCriteria()) {
			mapWeigths.put(c, weights.get(numCriterion));
			numCriterion++;
		}
		
		_resolutionPhase.setCriteriaWeights(mapWeigths);
		
		return weights;
	}
}