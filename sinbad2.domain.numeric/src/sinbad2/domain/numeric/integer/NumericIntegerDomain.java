package sinbad2.domain.numeric.integer;

import sinbad2.domain.type.Numeric;

public class NumericIntegerDomain extends Numeric {
	
	public static final String ID = "flintstones.domain.numeric.integer";
	
	private int _min;
	private int _max;
	
	public NumericIntegerDomain() {
		super();
		_min = 0;
		_max = 0;
	}
	
	public void setMin(Integer min) {
		_min = min;
	}
	
	public int getMin() {
		return _min;
	}
	
	public void setMax(Integer max) {
		_max = max;
	}
	
	public int getMax() {
		return _max;
	}

	public void setMinMax(Integer min, Integer max) {
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
		NumericIntegerDomain result = null;
		
		result = (NumericIntegerDomain) super.clone();
		
		return result;
	}
	
	
	

}
