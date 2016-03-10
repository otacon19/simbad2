package sinbad2.aggregationoperator.min.valuation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sinbad2.core.validator.Validator;
import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.integer.interval.IntegerIntervalValuation;

public class IntervalIntegerOperator {

	private IntervalIntegerOperator() {}
	
	public static Valuation aggregate(List<Valuation> valuations) {
		Valuation result = null;
		NumericIntegerDomain domain = null;
		List<Valuation> values = new LinkedList<Valuation>();
		
		for(Valuation valuation: valuations) {
			Validator.notIllegalElementType(valuation, new String[] {IntegerIntervalValuation.class.toString()});
			
			if(domain == null) {
				domain = (NumericIntegerDomain) valuation.getDomain();
			} else if(!domain.equals(valuation.getDomain())) {
				throw new IllegalArgumentException("Invalid domain");
			}
			
			values.add((IntegerIntervalValuation) valuation);
		}
		
		if(!values.isEmpty()) {
			Collections.sort(values);
			result = values.get(values.size() - 1);
			result = (Valuation) result.clone();
		}
		
		return result;
	}
	
}
