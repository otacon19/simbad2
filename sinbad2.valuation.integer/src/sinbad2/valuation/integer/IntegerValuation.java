package sinbad2.valuation.integer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.validator.Validator;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.valuation.Normalized;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.ValuationsManager;

public class IntegerValuation extends Normalized {
	
	public static final String ID = "flintstones.valuation.integer";
	
	public long _value;

	protected NumericIntegerDomain _domain;
	
	public IntegerValuation() {
		super();
		_value = 0;
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
	
	public Normalized normalized() {
		ValuationsManager valuationsManager = ValuationsManager.getInstance();
		IntegerValuation result = (IntegerValuation) valuationsManager.copyValuation(IntegerValuation.ID);
		
		DomainsManager domainsManager = DomainsManager.getInstance();
		NumericIntegerDomain domain = (NumericIntegerDomain) domainsManager.copyDomain(NumericIntegerDomain.ID);

		domain.setMinMax(_domain.getMin(), _domain.getMax());
		domain.setInRange(_domain.getInRange());
		
		result.setDomain(domain);
		result.setValue(_value);
		
		return result.normalizeRange();
	}
	
	@Override
	public Valuation negateValutation() {
		IntegerValuation result = (IntegerValuation) clone();
		
		long aux = Math.round(_domain.getMin()) + Math.round(_domain.getMax());
		result.setValue(aux - _value);
		
		return result;
	}
	
	@Override
	public String toString() {
		return ("Integer(" + _value + ") in" + _domain.toString());
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(obj == null || (obj.getClass() != this.getClass())) {
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
		writer.writeAttribute("value", Long.toString(_value));
		
	}

	@Override
	public void read(XMLRead reader) throws XMLStreamException {
		_value = Long.parseLong(reader.getStartElementAttribute("value"));	
	}
	
	private Normalized normalizeRange() {
		IntegerValuation result = (IntegerValuation) clone();
		long min, max, intervalSize;
		
		min = Math.round(_domain.getMin());
		max = Math.round(_domain.getMax());
		intervalSize = max - min;
		
		result._domain.setMinMax(0, 1);
		result._value = (_value - min) / intervalSize;
		
		return result;
	}

}
