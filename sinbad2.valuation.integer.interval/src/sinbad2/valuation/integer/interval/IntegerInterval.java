package sinbad2.valuation.integer.interval;

import sinbad2.domain.DomainsManager;
import sinbad2.domain.numeric.NumericIntegerDomain;
import sinbad2.valuation.Normalized;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.ValuationsManager;

public class IntegerInterval extends Normalized {

	public static final String ID = "flintstones.valuation.integer.interval";
	
	public long _min;
	public long _max;
	
	protected NumericIntegerDomain _domain;
	
	public IntegerInterval() {
		super();
		_min = 0;
		_max = 0;
	}
	
	public void setMin(Long min) {
		_min = min;
	}
	
	public long getMin() {
		return _min;
	}
	
	public void setMax(Long max) {
		_max = max;
	}
	
	public long getMax() {
		return _max;
	}
	
	public void setMinMax(Long min, Long max) {
		//TODO validator
		
		_min = min;
		_max = max;
	}
	
	@Override
	public Normalized normalize() {
		//TODO cambios hechos
		ValuationsManager valuationsManager = ValuationsManager.getInstance();
		IntegerInterval result = (IntegerInterval) valuationsManager.copyValuation(ID);
		
		DomainsManager domainsManager = DomainsManager.getInstance();
		NumericIntegerDomain domain = (NumericIntegerDomain) domainsManager.copyDomain(NumericIntegerDomain.ID);
		
		domain.setMinMax(_domain.getMin(), _domain.getMax());
		domain.setInRange(_domain.getInRange());
		
		result.setDomain(domain);
		result.setMinMax(_min, _max);
		
		return result.normalizeInterval();
	}
	
	@Override
	public Valuation negateValutation() {
		IntegerInterval result = (IntegerInterval) clone();
		
		long aux = Math.round(_domain.getMin()) + Math.round(_domain.getMax());
		result.setMinMax(aux - _max, aux - _min);
		
		return result;
	}
	
	@Override
	public String toString() {
		return ("Integer interval[" + _min + "," + _max + "] in" + _domain.toString());
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		} else {
			if(obj == null || (obj.getClass() != this.getClass())) {
				return false;
			}
		}
		
		//TODO builder
		
		return false;
	}
	
	//TODO hascode
	
	@Override
	public int compareTo(Valuation other) {
		//Validator
		
		if(_domain.equals(other.getDomain())) {
			long middle = (_max + _min) / 2l;
			long otherMidle = (((IntegerInterval) other)._max + ((IntegerInterval) other)._min) / 2l;
			return Long.compare(middle, otherMidle);	
		} else {
			throw new IllegalArgumentException("Differents domains");
		}
	}
	
	@Override
	public Object clone() {
		IntegerInterval result = null;
		result = (IntegerInterval) super.clone();
		result._min = new Long(_min);
		result._max = new Long(_max);
		
		return result;
	}

	@Override
	public String changeFormatValuationToString() {
		return "[" + Long.toString(_min) + ", " + Long.toString(_max) + "]";
	}
	
	//TODO save y read
	
	private Normalized normalizeInterval() {
		//TODO función hecha por mi
		IntegerInterval result = (IntegerInterval) clone();
		
		long min, max, intervalSize;
		
		min = Math.round(_domain.getMin());
		max = Math.round(_domain.getMax());
		intervalSize = max - min;
		
		max = ((long) (_max - min) / intervalSize);
		min = ((long) (_min - min) / intervalSize);
		
		result._domain.setMinMax(0, 1);
		
		result._min = min;
		result._max = max;
		
		return result;
	}
}
