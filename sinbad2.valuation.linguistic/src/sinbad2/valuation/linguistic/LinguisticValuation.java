package sinbad2.valuation.linguistic;

import sinbad2.valuation.Normalized;
import sinbad2.valuation.Valuation;

public class LinguisticValuation extends Normalized {
	
	private FuzzySet _domain;
	
	public static final String ID = "flintstones.valuation.linguistic";

	@Override
	public int compareTo(Valuation arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Valuation negateValutation() {
		LinguisticValuation result = (LinguisticValuation) clone();
		
		return null;
	}

	@Override
	public String changeFormatValuationToString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Normalized normalize() {
		// TODO Auto-generated method stub
		return null;
	}

}
