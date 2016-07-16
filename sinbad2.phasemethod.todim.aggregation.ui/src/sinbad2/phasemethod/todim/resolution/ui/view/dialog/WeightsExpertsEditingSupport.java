package sinbad2.phasemethod.todim.resolution.ui.view.dialog;

import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import sinbad2.element.expert.Expert;

public class WeightsExpertsEditingSupport extends EditingSupport {

	private final TableViewer viewer;
	
	private Map<String, Double> _weights;
	private List<Expert> _experts;

	private PropertyChangeSupport _changeSupport;
	
	public WeightsExpertsEditingSupport(TableViewer viewer, List<Expert> experts, PropertyChangeSupport changeSupport) {
		super(viewer);
		
		this.viewer = viewer;
		
		_experts = experts;
		
		_weights = new HashMap<String, Double>();
		for(Expert e: _experts) {
			_weights.put(e.getCanonicalId(), new Double(0));
		}
		
		_changeSupport = changeSupport;
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
		Double result = (Double) ((Object[]) element)[1];
		return result.toString();
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(value == null) {
			return;
		} else if(value instanceof String) {
			if (((String) value).isEmpty()) {
				return;
			}
		} else {
			return;
		}

		double newValue;

		try {
			newValue = (Double.parseDouble((String) value));
			if((newValue < 0) || (newValue > 1)) {
				MessageDialog.openError(null, "Invalid_range", "Invalid_range");
				return;
			}
		} catch (Exception e) {
			MessageDialog.openError(null, "Invalid_value", "Invalid_value");
			return;
		}

		_weights.put((String) ((Object[]) element)[0], newValue);
		
		setInput();
	}
	
	public boolean validate() {
		
		double acum = 0;
		for(String expert: _weights.keySet()) {
			acum += round(_weights.get(expert));
		}

		if(acum == 1 && _weights.size() == _experts.size()) {
			return true;
		}
		
		return false;
	}
	
	private double round(double value) {
		double error = 0.00001;

		double result = value + error;
		result *= 10000;
		result = Math.round(result);
		result /= 10000;

		return result;
	}
	
	public Map<String, Double> getWeights() {
		return _weights;
	}

	public void setInput() {
		List<Object[]> input = new LinkedList<Object[]>();
		
		for(String expert: _weights.keySet()) {
			Object[] data = new Object[2];
			data[0] = expert;
			data[1] = _weights.get(expert);
			
			input.add(data);
 		}
		
		viewer.setInput(input);
		
		_changeSupport.firePropertyChange("weight", null, null);
	}
}
