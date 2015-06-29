package sinbad2.domain.linguistic.fuzzy.function.types;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.function.FragmentFunction;
import sinbad2.domain.linguistic.fuzzy.nls.Messages;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.valuation.real.interval.RealInterval;

public class TrapezoidalFunction implements IMembershipFunction {
	
	private final static double EPSILON = 0.000001;
	
	private double _a;
	private double _b;
	private double _c;
	private double _d;
	
	public TrapezoidalFunction() {
		_a = _b = _c = _d = 0d;
	}
	
	public TrapezoidalFunction(double[] limits) {
		this();
		
		Validator.notNull(limits);
		Validator.notInvalidSize(limits.length, 3, 4);
		
		for(double limit: limits) {
			Validator.notNegative(limit);
		}
		
		if(limits.length == 3) {
			_a = limits[0];
			_b = _c = limits[1];
			_d = limits[2];
		} else if(limits.length == 4) {
			_a = limits[0];
			_b = limits[1];
			_c = limits[2];
			_d = limits[3];
		}
	}

	@Override
	public FragmentFunction toFragmentFunction() {
		FragmentFunction result = new FragmentFunction();
		LinearPieceFunction piece;
		NumericRealDomain domain;
		double slope, cutoffY;
		
		if(_a != _b) {
			slope = 1d / (_b - _a);
			cutoffY = -(slope * _a);
			piece = new LinearPieceFunction(slope, cutoffY);
			domain = new NumericRealDomain();
			domain.setMinMax(_a, _b);
			result.addPiece(domain, piece);
		}
		
		if(_b != _c) {
			slope = 0;
			cutoffY = 1;
			piece = new LinearPieceFunction(slope, cutoffY);
			domain = new NumericRealDomain();
			domain.setMinMax(_b, _c);
			result.addPiece(domain, piece);
		}
		
		if(_c != _d) {
			slope = 1d / (_c - _d);
			cutoffY = -(slope * _d);
			piece = new LinearPieceFunction(slope, cutoffY);
			domain = new NumericRealDomain();
			domain.setMinMax(_c, _d);
			result.addPiece(domain, piece);
		}
		
		return result;
	}
	
	@Override
	public boolean isSymmetrical() {
		return (Math.abs((_b - _a) - (_d - _c)) < EPSILON);
	}
	
	@Override
	public boolean isSymmetrical(IMembershipFunction other, double center) {
		double displacement = (center - _d) * 2;
		
		TrapezoidalFunction clone = (TrapezoidalFunction) clone();
		
		clone._a = _d;
		clone._b = clone._a + (_d - _c);
		clone._c = clone._b + (_c - _b);
		clone._d = clone._c + (_b - _a);
		
		clone._a += displacement;
		clone._b += displacement;
		clone._c += displacement;
		clone._d += displacement;
		
		return clone.equals(other);
	}
	
	@Override
	public NumericRealDomain getCenter() {
		NumericRealDomain result = new NumericRealDomain();
		result.setMinMax(_b, _c);
		
		return result;
	}
	
	@Override
	public NumericRealDomain getCoverage() {
		NumericRealDomain result = new NumericRealDomain();
		result.setMinMax(_a, _d);
		
		return result;
	}
	
	@Override
	public double getMembershipValue(double x) {
		double result;
		
		if(x >= _b && x <= _c) {
			result = 1d;
		} else if(x <= _a || x >= _d) {
			result = 0d;
		} else if(x < _b) {
			result = (x - _a) / (_b - _a);
		} else {
			result = (x - _d) / (_c - _d);
		}
		
		return result;
	}
	
	public double centroid() {
		double centroidLeft, centroidCenter, centroidRight, areaLeft, areaCenter, areaRight,
			areaSum, result;
		
		centroidLeft = (_a + ( 2 * _b)) / 3.;
		centroidCenter = (_b + _c) / 2.;
		centroidRight = ((2 * _c) + _d) / 3.;
		
		areaLeft = (_b - _a) / 2.;
		areaCenter = (_c - _b);
		areaRight = (_d - _c) / 2;
		areaSum = areaLeft + areaCenter + areaRight;
		
		result = ((centroidLeft * areaLeft) + (centroidCenter + areaCenter)
				+ (centroidRight + areaRight)) / areaSum;
		
		return result;
	}
	
	public boolean isTriangular() {
		return (_b == _c);
	}
	
	@Override
	public double midPoint() {
		return centroid();
	}
	

	@Override
	public double maxMin(RealInterval interval) {
		
		Validator.notNull(interval);
		
		RealInterval normalized;
		double min, max;
		
		normalized = (RealInterval) interval.normalized();
		min = normalized.getMin();
		max = normalized.getMax();
		
		if(( max >= _b) && (min <= _c)) {
			return 1d;
		} else if(max < _b) {
			return getMembershipValue(max);
		} else {
			return getMembershipValue(min);
		}
	}

	@Override
	public double maxMin(IMembershipFunction function) {
		TrapezoidalFunction trapezoidalFunction;
		
		Validator.notNull(function);
		
		if(function instanceof TrapezoidalFunction) {
			trapezoidalFunction = (TrapezoidalFunction) function;
		} else {
			throw new IllegalArgumentException(Messages.TrapezoidalFunction_Invalid_element_type);
		}
		
		double values[] = new double[5], result, slopeAB, slopeFunctionAB, slopeCD, 
				slopeFunctionCD;
		
		RealInterval interval = new RealInterval();
		NumericRealDomain domain = new NumericRealDomain();
		domain.setMinMax(0d, 1d);
		
		interval.setDomain(domain);
		interval.setMinMax(trapezoidalFunction._b, trapezoidalFunction._c);
		
		values[0] = maxMin(interval);
		
		if(values[0] == 1) {
			return 1d;
		}
		
		if(_b == _a) {
			values[1] = values[2] = trapezoidalFunction.getMembershipValue(_a);
		} else {
			slopeAB = 1d / (_b - _a);
			
			if(trapezoidalFunction._a == trapezoidalFunction._b) {
				values[1] = getMembershipValue(trapezoidalFunction._a);
			} else {
				slopeFunctionAB = 1d / (trapezoidalFunction._b - trapezoidalFunction._a);
				if(slopeAB == slopeFunctionAB) {
					values[1] = 0d;
				} else {
					values[1] = slopeFunctionAB * slopeAB * (_a - trapezoidalFunction._a) / (slopeAB - slopeFunctionAB);
				}
			}
			
			if(trapezoidalFunction._c == trapezoidalFunction._d) {
				values[2] = getMembershipValue(trapezoidalFunction._c);
			} else {
				slopeFunctionCD = 1d / (trapezoidalFunction._c - trapezoidalFunction._d);
				values[2] = slopeFunctionCD * slopeAB * (_a - trapezoidalFunction._a) / (slopeAB - slopeFunctionCD);
			}
		}
		
		if(_c == _d) {
			values[3] = values[4] = trapezoidalFunction.getMembershipValue(_c);
		} else {
			slopeCD = 1d / (_c - _d);
			
			if(trapezoidalFunction._a == trapezoidalFunction._b) {
				values[3] = getMembershipValue(trapezoidalFunction._a);
			} else {
				slopeFunctionAB = 1d / (trapezoidalFunction._b - trapezoidalFunction._a);
				values[3] = slopeFunctionAB * slopeCD * (_d - trapezoidalFunction._a) / (slopeCD - slopeFunctionAB);
			}
			
			if(trapezoidalFunction._c == trapezoidalFunction._d) {
				values[4] = getMembershipValue(trapezoidalFunction._c);
			} else {
				slopeFunctionCD = 1d / (trapezoidalFunction._c - trapezoidalFunction. _d);
				if(slopeCD == slopeFunctionCD) {
					values[4] = 0d;
				} else {
					values[4] = slopeFunctionCD * slopeCD * (_d - trapezoidalFunction._d) / (slopeCD - slopeFunctionCD);
				}
			}
		}
		
		for(int i = 0; i < values.length; ++i) {
			if(values[i] > 1) {
				values[i] = 0d;
			}
		}
		
		result = Math.max(values[0], Math.max(values[1], Math.max(values[2], Math.max(values[3], values[4]))));
		
		return result;
	}
	
	@Override
	public void save(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeAttribute("a",Double.toString(_a)); //$NON-NLS-1$
		writer.writeAttribute("b",Double.toString(_b)); //$NON-NLS-1$
		writer.writeAttribute("c",Double.toString(_c)); //$NON-NLS-1$
		writer.writeAttribute("d",Double.toString(_d)); //$NON-NLS-1$
	}
	
	@Override
	public void read(XMLRead reader) throws XMLStreamException {		
		_a = Double.parseDouble(reader.getStartElementAttribute("a")); //$NON-NLS-1$
		_b = Double.parseDouble(reader.getStartElementAttribute("b")); //$NON-NLS-1$
		_c = Double.parseDouble(reader.getStartElementAttribute("c")); //$NON-NLS-1$
		_d = Double.parseDouble(reader.getStartElementAttribute("d")); //$NON-NLS-1$
	}
	
	
	@Override
	public String toString() {
		
		if(_b == _c) {
			return ("Trapezoidal(" + _a + ", " + _b + "," + _d + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		} else {
			return ("Trapezoidal(" + _a + ", " + _b + "," + _c + ", " + _d + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(obj == null || (this.getClass() != obj.getClass())) {
			return false;
		}
		
		final TrapezoidalFunction other = (TrapezoidalFunction) obj;
		
		double aRound = Math.abs(new BigDecimal(_a).setScale(3, RoundingMode.HALF_UP).doubleValue());
		double bRound = Math.abs(new BigDecimal(_b).setScale(3, RoundingMode.HALF_UP).doubleValue());
		double cRound = Math.abs(new BigDecimal(_c).setScale(3, RoundingMode.HALF_UP).doubleValue());
		double dRound = Math.abs(new BigDecimal(_c).setScale(3, RoundingMode.HALF_UP).doubleValue());
		double aOtherRound = Math.abs(new BigDecimal(other._a).setScale(3, RoundingMode.HALF_UP).doubleValue());
		double bOtherRound = Math.abs(new BigDecimal(other._b).setScale(3, RoundingMode.HALF_UP).doubleValue());
		double cOtherRound = Math.abs(new BigDecimal(other._c).setScale(3, RoundingMode.HALF_UP).doubleValue());
		double dOtherRound = Math.abs(new BigDecimal(other._d).setScale(3, RoundingMode.HALF_UP).doubleValue());
		
		if(aRound == aOtherRound) {
			if(bRound == bOtherRound) {
				if(cRound == cOtherRound) {
					if(dRound == dOtherRound) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(_a);
		hcb.append(_b);
		hcb.append(_c);
		hcb.append(_d);
		return hcb.hashCode();
	}
	
	@Override
	public Object clone() {
		Object result = null;
		
		try {
			result = super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Override
	public int compareTo(IMembershipFunction other) {
		Validator.notNull(other);
		
		return Double.compare(this.midPoint(), other.midPoint());
	}
	
}
