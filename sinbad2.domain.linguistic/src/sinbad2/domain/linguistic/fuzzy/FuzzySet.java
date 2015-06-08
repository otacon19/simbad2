package sinbad2.domain.linguistic.fuzzy;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.domain.linguistic.fuzzy.function.FragmentFunction;
import sinbad2.domain.linguistic.fuzzy.function.IFragmentFunction;
import sinbad2.domain.linguistic.fuzzy.function.types.LinearPieceFunction;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.label.Label;
import sinbad2.domain.linguistic.fuzzy.label.LabelSet;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembership;
import sinbad2.domain.numeric.real.NumericRealDomain;
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
	
	public void setValue(String name, double value) {
		//TODO validator
		
		int pos = _labelSet.getPos(name);
		
		if(pos != -1) {
			setValue(pos, value);
		} else {
			throw new IllegalArgumentException("Inexistent element");
		}
	}
	
	public void setValue(Label label, double value) {
		//TODO validator
		
		int pos = _labelSet.getPos(label);
		
		if(pos != -1) {
			setValue(pos, value);
		} else {
			throw new IllegalArgumentException("Inexistent element");
		}
	}
	
	public Double getValue(int pos) {
		//TODO validator
		
		return _values.get(pos);
	}
	
	public Double getValue(String name) {
		
		int pos = _labelSet.getPos(name);
		
		if(pos != -1) {
			return getValue(pos);
		} else {
			return null;
		}
	}
	
	public Double getValue(Label label) {
		
		int pos = _labelSet.getPos(label);
		
		if(pos != -1) {
			return getValue(pos);
		} else {
			return null;
		}
		
	}
	
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
	
	public double chi() {
		double result, valuesSum, sum;
		int pos;
		
		if(_labelSet.getCardinality() == 0) {
			return 0;
		}
		
		sum = valuesSum = pos = 0;
		
		for(Double value: _values) {
			sum += value * pos++;
			valuesSum += value;
		}
		
		result = sum / valuesSum;
		
		return result;
	}
	
	public boolean isFuzzy() {
		int cardinality = _labelSet.getCardinality();
		
		if(cardinality == 0) {
			return false;
		}
		
		FragmentFunction fragmentFunction = new FragmentFunction();
		FragmentFunction semantic;
		Map<NumericRealDomain, IFragmentFunction> pieces;
		for(Label label: _labelSet.getLabels()) {
			semantic = label.getSemantic().toFragmentFunction();
			pieces = semantic.getPieces();
			for(NumericRealDomain domain: pieces.keySet()) {
				fragmentFunction.addPiece(domain, pieces.get(domain));
				fragmentFunction.simplify();
			}
		}
		
		pieces = fragmentFunction.getPieces();
		if(pieces.size() == 1) {
			NumericRealDomain domain = new NumericRealDomain();
			domain.setMinMax(0d, 1d);
			LinearPieceFunction piece = (LinearPieceFunction) pieces.get(domain);
			if(piece != null) {
				if((piece.getSlope() == 0) && (piece.getCutOffY() == 1)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean isOdd() {
		return (_labelSet.getCardinality() % 2 != 0);
		
	}
	
	public boolean isTriangular() {
		
		if(_labelSet.getCardinality() == 0) {
			return false;
		}
		
		IMembership semantic;
		for(Label label: _labelSet.getLabels()) {
			semantic = label.getSemantic();
			if(semantic instanceof TrapezoidalFunction) {
				if(!((TrapezoidalFunction) semantic).isTriangular()) {
					return false;
				}
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isTOR() {
		
		if(isFuzzy()) {
			if(isOdd()) {
				return isTriangular();
			}
		}
		
		return false;
	}
	
	public boolean isSymmetrical() {
		//TODO revisar
		if(_labelSet.getCardinality() == 0) {
			return false;
		}
		
		double midPoint = midpoint();
		
		if(midPoint != -1){
			Label label1, label2;
			int centralPos = _labelSet.getCardinality() / 2;
		
			for(int i = 0; i < centralPos; ++i) {
				label1 = _labelSet.getLabels().get(i);
				label2 = _labelSet.getLabels().get((_labelSet.getCardinality() - 1) - i);
				if(!(label1.getSemantic().isSymmetrical(label2.getSemantic(), midPoint))) {
					return false;
				}
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isUniform() {
		
		int cardinality = _labelSet.getCardinality();
		
		if(cardinality <= 1) {
			return true;
		}
		
		Label label1 = _labelSet.getLabels().get(0);
		Label label2 = _labelSet.getLabels().get(1);
		
		double center1 = label1.getSemantic().getCenter().midpoint();
		double center2 = label2.getSemantic().getCenter().midpoint();
		double distance = center2 - center1;
		double error = (1 / Math.pow(10, Double.toString(distance).length() - 2)) * 1.5;
		double distanceLower = distance - error;
		double distanceUpper = distance + error;
		
		for(int i = 2; i < cardinality; ++i) {
			label1 = (Label) label2.clone();
			label2 = _labelSet.getLabels().get(i);
			center1 = center2;
			center2 = label2.getSemantic().getCenter().midpoint();
			distance = center2 - center1;
			if(!((distanceLower <= distance) && (distance <= distanceUpper))) {
				return false;
			}
		}
		
 		return true;
	}
	
	public boolean isBLTS() {
		
		if(isTOR()) {
			if(isSymmetrical()) {
				return isUniform();
			}
		}
		
		return false;
	}
	
	@Override
	public double midpoint() {
		Label label1, label2;
		int centralPos = _labelSet.getCardinality() / 2;
		double midPoint;
		
		if(isOdd()) {
			label1 = _labelSet.getLabels().get(centralPos);
			IMembership semantic = label1.getSemantic();
			if(!semantic.isSymmetrical()) {
				return -1;
			} else {
				midPoint = semantic.midPoint();
			}
		} else {
			label1 = _labelSet.getLabels().get(centralPos - 1);
			label2 = _labelSet.getLabels().get(centralPos);
			
			midPoint = (label2.getSemantic().midPoint() + 
					label1.getSemantic().midPoint()) / 2d;
		}
		
		return midPoint;
	}

	@Override
	public String formatDescriptionDomain() {
		String result = "";
		int cardinality;
		
		if((cardinality = _labelSet.getCardinality()) != 0){
			result += "(";
			for(int i = 0; i < cardinality - 1; ++i) {
				result += _labelSet.getLabels().get(i).getName() + ")";
			}
			result += _labelSet.getLabels().get(cardinality - 1).getName() + ")";
		}
		
		return result.toString();
	}
	
	@Override
	public String toString() {
		String result = "";
		int cardinality = _labelSet.getCardinality();
		
		if(cardinality > 0) {
			for(int i = 0; i < cardinality; ++i) {
				if(i > 0) {
					result += ", ";
				}
				result += "[" + _labelSet.getLabels().get(i) + ";" + _values.get(i) + "]";
			}
		}
		
		return "{" + result + "}";
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(obj == null || (this.getClass() != obj.getClass())) {
			return false;
		}
		
		//TODO equalsBuilder
		
		return false;
	}
	
	@Override
	public Object clone() {
		Object result;
		
		result = super.clone();
		
		List<Double> values = new LinkedList<Double>();
		for(Double value: _values) {
			values.add(new Double(value));
		}
		
		((FuzzySet) result)._values = values;
		
		return result;
	}
}
