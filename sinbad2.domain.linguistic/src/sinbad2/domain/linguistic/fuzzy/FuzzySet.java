package sinbad2.domain.linguistic.fuzzy;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.function.FragmentFunction;
import sinbad2.domain.linguistic.fuzzy.function.IFragmentFunction;
import sinbad2.domain.linguistic.fuzzy.function.types.LinearPieceFunction;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.fuzzy.label.LabelSetLinguisticDomain;
import sinbad2.domain.linguistic.fuzzy.nls.Messages;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.domain.type.Linguistic;
import sinbad2.resolutionphase.io.XMLRead;

public class FuzzySet extends Linguistic {
	
	public static final String ID = "flintstones.domain.linguistic"; //$NON-NLS-1$
	
	protected LabelSetLinguisticDomain _labelSet;
	protected List<Double> _values;
	
	public FuzzySet() {
		super();
		
		_labelSet = new LabelSetLinguisticDomain();
		_values = new LinkedList<Double>();
	}
	
	public FuzzySet(List<LabelLinguisticDomain> labels) {
		_labelSet.setLabels(labels);
		addValues(labels);
	}
	
	public void clearFuzzySet() {
		_labelSet = new LabelSetLinguisticDomain();
		_values = new LinkedList<Double>();
	}
	
	public void setValues(List<Double> values) {
		Validator.notNull(values);
		int cardinality = _labelSet.getCardinality();
		Validator.notInvalidSize(values.size(), cardinality, cardinality);
		for(Double value: values) {
			Validator.notNull(value);
			Validator.notInvalidSize(value, 0.0, 1.0, "value"); //$NON-NLS-1$
		}
		
		_values = values;
	}
	
	public void setValue(int pos, Double value) {
		Validator.notEmpty(_labelSet.getLabels().toArray());
		Validator.notInvalidSize(pos, 0, _labelSet.getCardinality() - 1);
		
		Validator.notNull(value);
		Validator.notInvalidSize(value, 0.0, 1.0, "value"); //$NON-NLS-1$
		
		_values.set(pos, value);
	}
	
	public void setValue(String name, Double value) {
		Validator.notNull(name);
		Validator.notNull(value);
		Validator.notInvalidSize(value, 0.0, 1.0, "value"); //$NON-NLS-1$
		
		int pos = _labelSet.getPos(name);
		
		if(pos != -1) {
			setValue(pos, value);
		} else {
			throw new IllegalArgumentException(Messages.FuzzySet_Inexistent_element);
		}
	}
	
	public void setValue(LabelLinguisticDomain label, Double value) {
		Validator.notNull(label);
		Validator.notNegative(value);
		Validator.notInvalidSize(value, 0.0, 1.0, "value"); //$NON-NLS-1$
		
		int pos = _labelSet.getPos(label);
		
		if(pos != -1) {
			setValue(pos, value);
		} else {
			throw new IllegalArgumentException(Messages.FuzzySet_Inexistent_element);
		}
	}
	
	public Double getValue(int pos) {
		Validator.notEmpty(_labelSet.getLabels().toArray());
		Validator.notInvalidSize(pos, 0, _labelSet.getCardinality() - 1);
		
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
	
	public Double getValue(LabelLinguisticDomain label) {
		
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
	
	public void setLabelSet(LabelSetLinguisticDomain labelSet) {
		_labelSet = labelSet;
	}
	
	public LabelSetLinguisticDomain getLabelSet() {
		return _labelSet;
	}
	
	public void addLabel(LabelLinguisticDomain label) {
		
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
			} else {
				addLabel(labels, label);
			}
		}
	}
	
	public void addLabel(LabelLinguisticDomain label, Double value) {
		addLabel(_labelSet.getCardinality(), label, value);
	}
	
	public void addLabel(int pos, LabelLinguisticDomain label) {
		_labelSet.addLabel(pos, label);
		_values.add(pos, 0d);
	}
	
	public void addLabel(int pos, LabelLinguisticDomain label, Double value) {
		_labelSet.addLabel(pos, label);
		
		Validator.notNull(value);
		Validator.notInvalidSize(value, 0.0, 1.0, "value"); //$NON-NLS-1$
		

		_values.add(pos, value);
	}
	
	public void removeLabel(int pos) {
		Validator.notEmpty(_labelSet.getLabels().toArray());
		Validator.notInvalidSize(pos, 0, _labelSet.getCardinality() - 1);
		
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
	
	public void removeLabel(LabelLinguisticDomain label) {
		
		if(label == null) {
			return;
		}
		
		int pos = _labelSet.getPos(label);
		
		if(pos != - 1) {
			removeLabel(pos);
		}
	}
	
	public void addValues(List<LabelLinguisticDomain> labels) {
		List<Double> values = new LinkedList<Double>();
		for(int element = 0; element < labels.size(); ++element) {
			values.add(0d);
		}
		
		_values = values;
	}
	
	public void createTrapezoidalFunction(String[] labels) {
		LabelLinguisticDomain currentLabel;
		IMembershipFunction semantic;
		
		clearFuzzySet();
		
		if(labels.length == 1) {
			semantic = new TrapezoidalFunction(new double[] {0, 0, 1, 1});
			currentLabel = new LabelLinguisticDomain(labels[0], semantic);
			addLabel(currentLabel);
		} else {
			int numLabels = labels.length;
			double lower, central, upper, increment = 1d / (numLabels  - 1), factor = 1e5;
			
			for(int i = 0; i < numLabels; ++i) {
				lower = (i == 0) ? 0 : increment * (i - 1);
				central = increment * i;
				upper = (i == (numLabels - 1)) ? 1: increment * (i + 1);
				
				lower = Math.round(lower * factor) / factor;
				central = Math.round(central * factor) / factor;
				upper = Math.round(upper * factor) / factor;
				
				semantic = new TrapezoidalFunction(new double[] {lower, central, upper});
				currentLabel = new LabelLinguisticDomain(labels[i], semantic);
				addLabel(currentLabel);
			}
		}
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
		for(LabelLinguisticDomain label: _labelSet.getLabels()) {
			semantic = label.getSemantic().toFragmentFunction();
			pieces = semantic.getPieces();
			for(NumericRealDomain domain: pieces.keySet()) {
				fragmentFunction.addPiece(domain, pieces.get(domain));
				fragmentFunction.simplify();
			}
		}
		
		pieces = fragmentFunction.getPieces();
		if(pieces.size() == 1) {
			Object[] values = pieces.values().toArray();
			LinearPieceFunction piece = (LinearPieceFunction) values[0];
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
		
		IMembershipFunction semantic;
		for(LabelLinguisticDomain label: _labelSet.getLabels()) {
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
	
		if(_labelSet.getCardinality() == 0) {
			return false;
		}
		
		double midPoint = midpoint();

		if(midPoint != -1){
			LabelLinguisticDomain label1, label2;
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
	
	@Override
	public double midpoint() {
		LabelLinguisticDomain label1, label2;
		int centralPos = _labelSet.getCardinality() / 2;
		double midPoint;
		
		if(isOdd()) {
			label1 = _labelSet.getLabels().get(centralPos);
			IMembershipFunction semantic = label1.getSemantic();
			if(!semantic.isSymmetrical()) {
				return -1;
			} else {
				midPoint = semantic.centroid();
			}
		} else {
			label1 = _labelSet.getLabels().get(centralPos - 1);
			label2 = _labelSet.getLabels().get(centralPos);
			
			midPoint = (label2.getSemantic().centroid() + label1.getSemantic().centroid()) / 2d;
		}
		
		return midPoint;
	}
	
	public boolean isUniform() {
		
		int cardinality = _labelSet.getCardinality();
		
		if(cardinality <= 1) {
			return true;
		}
		
		LabelLinguisticDomain label1 = _labelSet.getLabels().get(0);
		LabelLinguisticDomain label2 = _labelSet.getLabels().get(1);
		
		double center1 = label1.getSemantic().getCenter().midpoint();
		double center2 = label2.getSemantic().getCenter().midpoint();
		double distance = center2 - center1;
		double error = (1 / Math.pow(10, Double.toString(distance).length() - 2)) * 1.5;
		double distanceLower = distance - error;
		double distanceUpper = distance + error;
		
		for(int i = 2; i < cardinality; ++i) {
			label1 = (LabelLinguisticDomain) label2.clone();
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
	public String formatDescriptionDomain() {
		String result = ""; //$NON-NLS-1$
		int cardinality;
		
		if((cardinality = _labelSet.getCardinality()) != 0){
			result += "("; //$NON-NLS-1$
			for(int i = 0; i < cardinality - 1; ++i) {
				result += _labelSet.getLabels().get(i).getName() + ", "; //$NON-NLS-1$
			}
			result += _labelSet.getLabels().get(cardinality - 1).getName() + ")"; //$NON-NLS-1$
		}
		
		return result.toString();
	}
	
	@Override
	public String toString() {
		String result = ""; //$NON-NLS-1$
		int cardinality = _labelSet.getCardinality();
		
		if(cardinality > 0) {
			for(int i = 0; i < cardinality; ++i) {
				if(i > 0) {
					result += ", "; //$NON-NLS-1$
				}
				result += "[" + _labelSet.getLabels().get(i) + ";" + _values.get(i) + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
		
		return "{" + result + "}"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	public void save(XMLStreamWriter writer) throws XMLStreamException {
		
		writer.writeStartElement("values"); //$NON-NLS-1$
		for (int i = 0; i < _values.size(); ++i) {
			writer.writeStartElement("value"); //$NON-NLS-1$
			writer.writeAttribute("v", Double.toString(_values.get(i))); //$NON-NLS-1$
			writer.writeEndElement();
		}
		writer.writeEndElement();
		
		writer.writeStartElement("labelSet"); //$NON-NLS-1$
		_labelSet.save(writer);;
		writer.writeEndElement();
		
	}

	@Override
	public void read(XMLRead reader) throws XMLStreamException {
		XMLEvent event;
		String v, endtag = null;
		Double value = null;
		boolean end = false;
		
		_values = new LinkedList<Double>();
		
		reader.goToStartElement("values"); //$NON-NLS-1$
		
		while (reader.hasNext() && !end) {
			event = reader.next();
			if (event.isStartElement()) {
				if ("value".equals(reader.getStartElementLocalPart())) { //$NON-NLS-1$
					v = reader.getStartElementAttribute("v"); //$NON-NLS-1$
					value = new Double(v);
					_values.add(value);
				} else {
					_labelSet = new LabelSetLinguisticDomain();
					_labelSet.read(reader);
				}
			} else if (event.isEndElement()) {
				endtag = reader.getEndElementLocalPart();
				if (endtag.equals("labelSet")) { //$NON-NLS-1$
					end = true;
				}
			}
		}
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(super.hashCode());
		return hcb.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(obj == null) {
			return false;
		}
		
		if(this.getClass() != obj.getClass()) {
			return false;
		}
		
		final FuzzySet other = (FuzzySet) obj;
		
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(_values, other._values);
		
		return eb.isEquals();
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
