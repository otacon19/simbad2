package sinbad2.domain.linguistic.fuzzy.semantic;

import sinbad2.domain.linguistic.fuzzy.function.FragmentFunction;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.valuation.real.interval.RealInterval;

public interface IMembershipFunction extends Cloneable, Comparable<IMembershipFunction> {
	
	public FragmentFunction toFragmentFunction();
	
	public boolean isSymmetrical();
	
	public boolean isSymmetrical(IMembershipFunction other, double center);
	
	public NumericRealDomain getCenter();
	
	public NumericRealDomain getCoverage();
	
	public double getMembershipValue(double x);
	
	public double midPoint();
	
	public double maxMin(RealInterval interval);
	
	public double maxMin(IMembershipFunction function);
	
	public Object clone();
}
