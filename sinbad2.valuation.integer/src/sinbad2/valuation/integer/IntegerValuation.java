package sinbad2.valuation.integer;

import sinbad2.domain.DomainsManager;
import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.valuation.Normalized;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.ValuationsManager;

public class IntegerValuation extends Normalized {
	
	public static final String ID = "flintstones.valuation.integer";
	
	public long _value;
	
	//TODO cambio
	protected NumericIntegerDomain _domain;
	
	public IntegerValuation() {
		super();
		_value = 0;
	}
	
	public void setValue(Long value) {
		//TODO validator
		_value = value;
	}
	
	public long getValue() {
		return _value;
	}
	
	@Override
	public Normalized normalize() {
		//TODO cambios
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
		
		//TODO builder
		
		return false;
	}
	
	//TODO hashCode
	
	@Override
	public int compareTo(Valuation other) {
		// TODO validator
		
		if(_domain.equals(other.getDomain())) {
			return Long.compare(_value, ((IntegerValuation) other)._value);
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
	
	//TODO save and read
	
	//TODO hecha por mi
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
