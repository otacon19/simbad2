package sinbad2.phasemethod.todim.resolution.ui.view.provider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import sinbad2.core.utils.Pair;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.todim.resolution.ResolutionPhase;
import sinbad2.phasemethod.todim.resolution.ui.view.CalculateRanking;

public class ThresholdEditingSupport extends EditingSupport{

	private final TableViewer viewer;
	private final int col;

	private ResolutionPhase _resolutionPhase;
	
	private CalculateRanking _calculateRanking;
	
	private ProblemElementsSet _elementsSet;

	public ThresholdEditingSupport(TableViewer viewer, int col, ResolutionPhase resolutionPhase, ProblemElementsSet elementsSet, CalculateRanking calculateRanking) {
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

		Object[] row = getRow(element);
		setNewThresholds(row, newValue);
		
		_calculateRanking.refreshTODIMTables();
	}

	public Object[] getRow(Object element) {
		Object[] rowData = new Object[3];
		String keyExpert = (String) ((Object[]) element)[0];
		String keyCriterion = (String) ((Object[]) element)[1];

		int pos = 0;
		for(Expert e: _elementsSet.getExperts()) {
			if(e.getId().equals(keyExpert)) {
				pos = _elementsSet.getExperts().indexOf(e);
				rowData[0] = e;
			}
		}
		
		for (Criterion c : _elementsSet.getCriteria()) {
			if (c.getId().equals(keyCriterion)) {
				pos += _elementsSet.getAllCriteria().indexOf(c);
				rowData[1] = c;
			}
		}
		
		rowData[2] = pos;
		
		return rowData;
	}
	
	private Object setNewThresholds(Object[] row, double newValue) { 
		Pair<Expert, Criterion> pair = new Pair<>((Expert) row[0], (Criterion) row[1]);
		Map<Pair<Expert,Criterion>, Double> thresholds = _resolutionPhase.getThresholdValues();
		Map<Pair<Expert,Criterion>, Double> thresholdsAux = new HashMap<Pair<Expert, Criterion>, Double>();
		thresholdsAux.putAll(thresholds);
		
		for(Pair<Expert, Criterion> p: thresholdsAux.keySet()) {
			if(p.equals(pair)) {
				thresholds.remove(p);
				thresholds.put(pair, newValue);
			}
		}

		return thresholds;
	}
}