package sinbad2.aggregationoperator.min.valuation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sinbad2.core.validator.Validator;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.real.interval.RealIntervalValuation;

public class IntervalRealOperator {
	
	private IntervalRealOperator() {}
	
	public static Valuation aggregate(List<Valuation> valuations) {
		Valuation result = null;
		NumericRealDomain domain = null;
		List<Valuation> values = new LinkedList<Valuation>();
		
		for(Valuation valuation: valuations) {
			Validator.notIllegalElementType(valuation, new String[] {RealIntervalValuation.class.toString()});
			
			if(domain == null) {
				domain = (NumericRealDomain) valuation.getDomain();
			} else if(!domain.equals(valuation.getDomain())) {
				throw new IllegalArgumentException("Invalid domain");
			}
			
			values.add((RealIntervalValuation) valuation);
		}
		
		if(!values.isEmpty()) {
			Collections.sort(values);
			result = values.get(0);
			result = (Valuation) result.clone();
		}
		
		return result;
	}
}
