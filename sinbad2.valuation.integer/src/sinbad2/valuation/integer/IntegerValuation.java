package sinbad2.valuation.integer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.validator.Validator;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.ValuationsManager;

public class IntegerValuation extends Valuation {
	
	public static final String ID = "flintstones.valuation.integer"; //$NON-NLS-1$
	
	public long _value;
	
	public IntegerValuation() {
		super();
		_value = 0;
	}
	
	public IntegerValuation(NumericIntegerDomain domain, long value) {
		_domain = domain;
		_value = value;
	}
	
	public void setValue(Long value) {
		Validator.notNull(_domain);
		
		if(((NumericIntegerDomain) _domain).getInRange()) {
			Validator.inRange(value, ((NumericIntegerDomain) _domain).getMin(), 
					((NumericIntegerDomain) _domain).getMax());
			_value = value;
		} else {
			_value = value;
		}
	}
	
	public long getValue() {
		return _value;
	}
	
	public Valuation normalized() {
		ValuationsManager valuationsManager = ValuationsManager.getInstance();
		IntegerValuation result = (IntegerValuation) valuationsManager.copyValuation(IntegerValuation.ID);
		
		DomainsManager domainsManager = DomainsManager.getInstance();
		NumericIntegerDomain domain = (NumericIntegerDomain) domainsManager.copyDomain(NumericIntegerDomain.ID);

		domain.setMinMax(((NumericIntegerDomain)_domain).getMin(), ((NumericIntegerDomain) _domain).getMax());
		domain.setInRange(((NumericIntegerDomain)_domain).getInRange());
		
		result.setDomain(domain);
		result.setValue(_value);
		
		return result.normalizeRange();
	}
	
	public Valuation negateValutation() {
		IntegerValuation result = (IntegerValuation) clone();
		
		long aux = Math.round(((NumericIntegerDomain) _domain).getMin()) + Math.round(((NumericIntegerDomain) _domain).getMax());
		result.setValue(aux - _value);
		
		return result;
	}
	
	public FuzzySet unification(FuzzySet fuzzySet) {

		Validator.notNull(fuzzySet);
		
		if (!fuzzySet.isBLTS()) {
			throw new IllegalArgumentException("Not BLTS fuzzy set.");
		}

		int cardinality;
		IntegerValuation normalized;
		IMembershipFunction function;

		FuzzySet result = (FuzzySet) fuzzySet.clone();
		cardinality = fuzzySet.getLabelSet().getCardinality();
		normalized = (IntegerValuation) normalized();

		for(int i = 0; i < cardinality; i++) {
			function = result.getLabelSet().getLabel(i).getSemantic();
			result.setValue(i, function.getMembershipValue(normalized.getValue()));
		}

		return result;
	}

	
	@Override
	public String toString() {
		return ("Integer(" + _value + ") in" + _domain.toString()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(obj == null) {
			return false;
		}
		
		if(obj.getClass() != this.getClass()) {
			return false;
		}
		
		final IntegerValuation other = (IntegerValuation) obj;
		
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(_value, other._value);
		eb.append(_domain, other._domain);
		
		return eb.isEquals();
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(_value);
		hcb.append(_domain);
		return hcb.toHashCode();
	}
	
	@Override
	public int compareTo(Valuation other) {
		Validator.notNull(other);
		Validator.notIllegalElementType(other, 
				new String[] { Integer.class.toString() });
		
		if(_domain.equals(other.getDomain())) {
			return Long.valueOf(_value).compareTo(Long.valueOf(((IntegerValuation) other)._value));
		}
		
		return 0;
	}
	
	@Override
	public Object clone() {
		IntegerValuation result = null;
		result = (IntegerValuation) super.clone();
		result._value = new Long(_value);
		
		return result;
	}

	@Override
	public String changeFormatValuationToString() {
		return Long.toString(_value);
	}
	
	@Override
	public void save(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeAttribute("value", Long.toString(_value)); //$NON-NLS-1$
	}

	@Override
	public void read(XMLRead reader) throws XMLStreamException {
		_value = Long.parseLong(reader.getStartElementAttribute("value"));	 //$NON-NLS-1$
	}
	
	private Valuation normalizeRange() {
		IntegerValuation result = (IntegerValuation) clone();
		long min, max, intervalSize;
		
		min = Math.round(((NumericIntegerDomain) _domain).getMin());
		max = Math.round(((NumericIntegerDomain)_domain).getMax());
		intervalSize = max - min;
		
		((NumericIntegerDomain) result. _domain).setMinMax(0, 1);
		result._value = (_value - min) / intervalSize;
		
		return result;
	}
}
