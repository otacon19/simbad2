package sinbad2.valuation.real.interval;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.validator.Validator;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.valuation.Normalized;
import sinbad2.valuation.Valuation;

public class RealInterval extends Normalized {
	
	public static final String ID = "flintstones.valuation.real.interval";

	public double _min;
	public double _max;
	
	protected NumericRealDomain _domain;
	
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
		
		max = (_max - min) / intervalSize;
		min = (_min - min) / intervalSize;
		
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
		
		final RealInterval other = (RealInterval) obj;
		
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(_max, other._max);
		eb.append(_min, other._min);
		eb.append(_domain, other._domain);
		
		return eb.isEquals();
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(_max);
		hcb.append(_min);
		hcb.append(_domain);
		return hcb.toHashCode();
	}
	
	@Override
	public int compareTo(Valuation other) {
		Validator.notNull(other);
		Validator.notIllegalElementType(other, new String[] {Integer.class.toString()});
		
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

	@Override
	public void save(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeAttribute("min", Double.toString(_min));
		writer.writeAttribute("max", Double.toString(_max));	
	}

	@Override
	public void read(XMLRead reader) throws XMLStreamException {
		_min = Double.parseDouble(reader.getStartElementAttribute("min"));
		_max = Double.parseDouble(reader.getStartElementAttribute("max"));
	}

}
