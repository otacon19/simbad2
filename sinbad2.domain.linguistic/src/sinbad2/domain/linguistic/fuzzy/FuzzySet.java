package sinbad2.domain.linguistic.fuzzy;

import java.util.LinkedList;
import java.util.List;

import sinbad2.domain.linguistic.fuzzy.label.Label;
import sinbad2.domain.linguistic.fuzzy.label.LabelSet;
import sinbad2.domain.type.Linguistic;

public class FuzzySet extends Linguistic {
	
	public static final String ID = "flintstones.domain.linguistic";
	
	protected LabelSet _labelSet;
	protected List<Double> _values;
	
	//TODO unbalancedinfo
	
	public FuzzySet() {
		super();
		
		_labelSet = new LabelSet();
		_values = new LinkedList<Double>();
	}
	
	public FuzzySet(List<Label> labels) {
		_labelSet.setLabels(labels);
		addValues(labels);
	}
	
	public void setValues(List<Double> values) {
		//TODO validator
		
		_values = values;
	}
	
	public void setValue(int pos, double value) {
		//TODO validator
		
		_values.set(pos, value);
	}
	
	/*
	 * Nos hemos quedado en setMeasure(String name, double measure)
	 */
	
	public List<Double> getValues() {
		return _values;
	}
	
	public void setLabelSet(LabelSet labelSet) {
		_labelSet = labelSet;
	}
	
	public LabelSet getLabelSet() {
		return _labelSet;
	}
	
	public void addLabel(Label label) {
		int labels = _labelSet.getCardinality();
		
		if(labels == 0) {
			addLabel(labels, label);
		} else {
			boolean lower = false;
			int counter = 0;	
			do {
				if(label.compareTo(_labelSet.getLabels().get(counter)) <= 0) {
					lower = true;
				} else {
					counter++;
				}
			} while ((!lower) && (counter < labels));
			
			if(lower) {
				addLabel(counter, label);
				addLabel(labels, label);
			}
		}
	}
	
	public void addLabel(Label label, Double value) {
		addLabel(_labelSet.getCardinality(), label, value);
	}
	
	public void addLabel(int pos, Label label) {
		_labelSet.addLabel(pos, label);
		_values.add(pos, 0d);
	}
	
	public void addLabel(int pos, Label label, double value) {
		_labelSet.addLabel(pos, label);
		
		//TODO validator
		
		_values.add(pos, value);
	}
	
	public void removeLabel(int pos) {
		//TODO validator
		
		_labelSet.getLabels().remove(pos);
		_values.remove(pos);
	}
	
	public void removeLabel(String name) {
		
		if(name == null) {
			return;
		}
		
		int pos = _labelSet.getPos(name);
		
		if(pos != -1) {
			removeLabel(pos);
		}
	}
	
	public void removeLabel(Label label) {
		
		if(label == null) {
			return;
		}
		
		int pos = _labelSet.getPos(label);
		
		if(pos != - 1) {
			removeLabel(pos);
		}
	}
	
	public void addValues(List<Label> labels) {
		List<Double> values = new LinkedList<Double>();
		for(int element = 0; element < labels.size(); ++element) {
			values.add(0d);
		}
		
		_values = values;
	}

	@Override
	public double midpoint() {
		return 0;
	}

	@Override
	public String formatDescriptionDomain() {
		// TODO Mostrar las etiquetas del dominio linguistico
		return null;
	}
	
	
}
