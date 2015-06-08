package sinbad2.domain.linguistic.fuzzy.label;

import java.util.LinkedList;
import java.util.List;

public class LabelSet implements Cloneable {

	protected List<Label> _labels;
	
	public LabelSet() {
		_labels = new LinkedList<Label>();
	}
	
	public LabelSet(List<Label> labels) {
		this();
		setLabels(labels);
	}
	
	public void setLabels(List<Label> labels) {
		//TODO validator
		for(Label label: labels) {
			addLabel(label);
		}
		
		_labels = labels;
	}
	
	public void addLabel(Label label) {
		addLabel(getCardinality(), label);
	}
	
	public void addLabel(int pos, Label label) {
		//TODO validator
		
		if(containsLabel(label.getName())) {
			throw new IllegalArgumentException("Duplicate label name");
		}
		
		_labels.add(pos, label);
	}
	
	public void removeLabel(int pos) {
		//TODO validator
		
		_labels.remove(pos);
	}
	
	public void removeLabel(Label label) {
		
		if(label == null) {
			return;
		}
		
		int pos = getPos(label);
		
		if(pos != -1) {
			removeLabel(pos);
		}
	}
	
	public boolean containsLabel(String name) {
		return (getPos(name) != -1);
	}
	
	public int getCardinality() {
		return _labels.size();
	}
	
	public Label getLabel(int pos) {
		//TODO validator
		
		return _labels.get(pos);
	}
	
	public Label getLabel(String name) {
		int pos = getPos(name);
		
		if(pos != -1) {
			return getLabel(pos);
		} else {
			return null;
		}
	}
	
	public int getPos(String name) {
		
		if(name == null) {
			return -1;
		}
		
		//TODO validator
		
		for(Label auxLabel: _labels) {
			if(auxLabel.getName().equals(name)) {
				return _labels.indexOf(auxLabel);
			}
		}
		
		return -1;
		
	}
	
	public int getPos(Label label) {
		return _labels.indexOf(label);
	}
	
	@Override
	public String toString() {
		String result = "";
		int cardinality = getCardinality();
		
		if(cardinality > 0) {
			for(int pos = 0; pos < cardinality; ++pos) {
				if(pos > 0) {
					result += ", ";
				}
				result += _labels.get(pos);
			}
		}
		
		return "{" + result + "}";
		
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this.equals(obj)) {
			return true;
		}
		
		if(obj == null || (this.getClass() != obj.getClass())) {
			return false;
		}
		
		//TODO equalsBuilder
		
		return false;
	}
	
	//TODO hashCode
	
	@Override
	public Object clone() {
		Object result = null;
		
		try {
			result = super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		List<Label> resultLabels = new LinkedList<Label>();
		for(Label label: _labels) {
			resultLabels.add((Label)label.clone()); 
		}
		
		((LabelSet) result)._labels = resultLabels;
		
		return result;
	}
	
}
