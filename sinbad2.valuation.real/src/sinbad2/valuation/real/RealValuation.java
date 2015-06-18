package sinbad2.valuation.real;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.validator.Validator;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.valuation.Normalized;
import sinbad2.valuation.Valuation;

public class RealValuation extends Normalized {
	
	public static final String ID = "flintstones.valuation.real";
	
	public double _value;
	
	protected NumericRealDomain _domain;
	
	public RealValuation() {
		super();
		_value = 0;
	}
	
	public void setValue(Double value) {
		Validator.notNull(_domain);
		
		if(((NumericRealDomain)_domain).getInRange()) {
			Validator.inRange(value, ((NumericRealDomain)_domain).getMin(), ((NumericRealDomain)_domain).getMax());
			_value = value;
		} else {
			_value = value;
		}
	}
	
	public double getValue() {
		return _value;
	}
	
	public Normalized normalized() {
		RealValuation result = (RealValuation) clone();
		double min, max, intervalSize;
		
		min = ((NumericRealDomain)_domain).getMin();
		max = ((NumericRealDomain)_domain).getMax();
		intervalSize = max - min;
		
		((NumericRealDomain) result._domain).setMinMax(0d, 1d);
		result._value = (_value - min) / intervalSize;
		
		return result;
		
	}
	
	@Override
	public Normalized negateValutation() {
		RealValuation result = (RealValuation) clone();
		double aux = ((NumericRealDomain)_domain).getMin() + ((NumericRealDomain)_domain).getMax();
		
		result.setValue(aux - _value);
		
		return result;
	}
	
	@Override
	public String toString() {
		return "(Real(" + _value + ") in " + _domain.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(obj == null || (obj.getClass() != this.getClass())) {
			return false;
		}
		
		final RealValuation other = (RealValuation) obj;
		
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
		Validator.notIllegalElementType(other, new String[] { Integer.class.toString() });
		
		if(_domain.equals(other.getDomain())) {
			return Double.compare(_value, ((RealValuation) other)._value);
		}
				
		return 0;
	}
	
	@Override
	public Object clone() {
		RealValuation result = null;
		result = (RealValuation) super.clone();
		result._value = new Double(_value);
		
		return result;
	}
	
	@Override
	public String changeFormatValuationToString() {
		return Double.toString(_value);
	}

	@Override
	public void save(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeAttribute("value", Double.toString(_value));	
	}

	@Override
	public void read(XMLRead reader) throws XMLStreamException {
		_value = Double.parseDouble(reader.getStartElementAttribute("value"));
	}

}
