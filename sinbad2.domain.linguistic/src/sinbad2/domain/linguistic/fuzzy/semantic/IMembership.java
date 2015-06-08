package sinbad2.domain.linguistic.fuzzy.semantic;

import sinbad2.domain.linguistic.fuzzy.function.FragmentFunction;
import sinbad2.domain.numeric.real.NumericRealDomain;

public interface IMembership extends Cloneable, Comparable<IMembership> {
	
	public FragmentFunction toFragmentFunction();
	
	public boolean isSymmetrical();
	
	public boolean isSymmetrical(IMembership other, double center);
	
	public NumericRealDomain getCenter();
	
	public NumericRealDomain getCoverage();
	
	public double getMembershipValue(double x);
	
	public double resumeValue();
	
	public Object clone();
}
