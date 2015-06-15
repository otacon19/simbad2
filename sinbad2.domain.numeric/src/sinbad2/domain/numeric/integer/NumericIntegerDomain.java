package sinbad2.domain.numeric.integer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.domain.type.Numeric;
import sinbad2.resolutionphase.io.XMLRead;

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
		
		final NumericIntegerDomain other = (NumericIntegerDomain) obj;
		
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(_inRange, other._inRange);
		eb.append(_max, other._max);
		eb.append(_min, other._min);
		
		return eb.isEquals();
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(super.hashCode());
		hcb.append(_inRange);
		hcb.append(_max);
		hcb.append(_min);
		return hcb.toHashCode();
	}
	
	@Override
	public Object clone() {
		NumericIntegerDomain result = null;
		
		result = (NumericIntegerDomain) super.clone();
		
		return result;
	}

	@Override
	public void save(XMLStreamWriter writer) throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void read(XMLRead reader) throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}
	
	
	

}
