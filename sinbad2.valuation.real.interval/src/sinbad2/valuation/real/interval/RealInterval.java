package sinbad2.valuation.real.interval;

import sinbad2.domain.numeric.NumericDomain;
import sinbad2.valuation.Normalized;
import sinbad2.valuation.Valuation;

public class RealInterval extends Normalized {
	
	public static final String ID = "flintstones.valuation.real.interval";

	public double _min;
	public double _max;
	
	protected NumericDomain _domain;
	
	public RealInterval() {
		super();
		_min = 0;
		_max = 0;
	}
	
	public void setMin(Long min) {
		_min = min;
	}
	
	public double getMin() {
		return _min;
	}
	
	public void setMax(Long max) {
		_max = max;
	}
	
	public double getMax() {
		return _max;
	}
	
	public void setMinMax(Double min, Double max) {
		//TODO validator
		
		_min = min;
		_max = max;
	}
	
	@Override
	public Normalized normalize() {
		//TODO cambios hechos
		RealInterval result = (RealInterval) clone();
		double min, max, intervalSize;
		
		min = _domain.getMin();
		max = _domain.getMax();
		intervalSize = max - min;
		
		max = ((double) (_max - min)) / intervalSize;
		min = ((double) (_min - min)) / intervalSize;
		
		result._domain.setMinMax(0d, 1d);
		result.setMinMax(min, max);
		
		return result;
	}
	
	@Override
	public Valuation negateValutation() {
		RealInterval result = (RealInterval) clone();
		
		double aux = _domain.getMin() + _domain.getMax();
		result.setMinMax(aux - _max, aux - _min);
		
		return result;
	}
	
	@Override
	public String toString() {
		return ("Real interval[" + _min + "," + _max + "] in" + _domain.toString());
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
			double middle = (_max + _min) / 2l;
			double otherMidle = (((RealInterval) other)._max + ((RealInterval) other)._min) / 2d;
			return Double.compare(middle, otherMidle);	
		} else {
			throw new IllegalArgumentException("Differents domains");
		}
	}
	
	@Override
	public Object clone() {
		RealInterval result = null;
		result = (RealInterval) super.clone();
		result._min = new Double(_min);
		result._max = new Double(_max);
		
		return result;
	}

	@Override
	public String changeFormatValuationToString() {
		return "[" + Double.toString(_min) + ", " + Double.toString(_max) + "]";
	}
	
	//TODO save y read
}
