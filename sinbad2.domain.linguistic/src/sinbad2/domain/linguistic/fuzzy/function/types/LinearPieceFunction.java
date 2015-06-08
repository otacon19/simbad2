package sinbad2.domain.linguistic.fuzzy.function.types;


import sinbad2.domain.linguistic.fuzzy.function.IFragmentFunction;

public class LinearPieceFunction implements IFragmentFunction {
	
	private final static double EPSILON = 0.00001;
	
	private double _slope;
	private double _cutOff;
	
	public LinearPieceFunction(Double slope, Double cutOff) {
		_slope = slope;
		_cutOff = cutOff;
	}
	
	public double getSlope() {
		return _slope;
	}
	
	public double getCutOff() {
		return _cutOff;
	}
	
	@Override
	public IFragmentFunction sumFunctions(IFragmentFunction other) {
		//TODO validator
		
		return new LinearPieceFunction(_slope + ((LinearPieceFunction) other)._slope, _cutOff + ((LinearPieceFunction) other)._cutOff);
	}
	
	@Override
	public String toString() {
		return (_cutOff < 0) ? (_slope + "x " + _cutOff) : (_slope + "x + " + _cutOff);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(obj == null || (obj.getClass() != this.getClass())) {
			return false;
		}
		
		final LinearPieceFunction other = (LinearPieceFunction) obj;
		
		if(Math.abs(_slope - other._slope) < EPSILON) {
			if(Math.abs(_cutOff - other._cutOff) < EPSILON) {
				return true;
			}
		}
		
		return false;
	}
	
	//TODO hashCode

}
