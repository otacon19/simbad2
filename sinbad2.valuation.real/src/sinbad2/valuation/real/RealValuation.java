package sinbad2.valuation.real;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.domain.numeric.real.NumericRealDomain;
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
		//TODO validator
		_value = value;
	}
	
	public double getValue() {
		return _value;
	}
	
	@Override
	public Normalized normalize() {
		RealValuation result = (RealValuation) clone();
		double min, max, intervalSize;
		
		min = _domain.getMin();
		max = _domain.getMax();
		intervalSize = max - min;
		
		result._domain.setMinMax(0d, 1d);
		result._value = (_value - min) / intervalSize;
		
		return result;
		
	}
	
	@Override
	public Valuation negateValutation() {
		RealValuation result = (RealValuation) clone();
		double aux = _domain.getMin() + _domain.getMax();
		
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
		// TODO validator
		
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
	
	//TODO save and read

}
