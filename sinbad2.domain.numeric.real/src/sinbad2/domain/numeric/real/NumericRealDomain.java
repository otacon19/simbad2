package sinbad2.domain.numeric.real;

import sinbad2.domain.type.Numeric;

public class NumericRealDomain extends Numeric {
	
public static final String ID = "flintstones.domain.numeric.real";
	
	private double _min;
	private double _max;
	
	public NumericRealDomain() {
		super();
		_min = 0;
		_max = 0;
	}
	
	public void setMin(Double min) {
		_min = min;
	}
	
	public double getMin() {
		return _min;
	}
	
	public void setMax(Double max) {
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
	public double midpoint() {
		return ((double) (_max + _min)) / 2d;
	}

	@Override
	public String formatDescriptionDomain() {
		String prefix = "(I) "; //$NON-NLS-1$
		
		if(!_inRange) {
			return prefix + "without range";
		} else {
			return prefix + toString();
		}
	}
	
	//TODO save read
	
	@Override
	public String toString() {
		return "[" + _min + ", " + _max + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(obj == null || (obj.getClass() != this.getClass()) || !super.equals(obj)) {
			return false;
		}
		
		
		//TODO builder
		
		return false;
	}
	
	//TODO hashCode
	
	@Override
	public Object clone() {
		NumericRealDomain result = null;
		
		result = (NumericRealDomain) super.clone();
		
		return result;
	}
	
	
}
