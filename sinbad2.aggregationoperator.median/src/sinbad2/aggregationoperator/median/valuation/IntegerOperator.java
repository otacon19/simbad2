package sinbad2.aggregationoperator.median.valuation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sinbad2.core.validator.Validator;
import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.integer.IntegerValuation;

public class IntegerOperator {
	
	private IntegerOperator() {}
	
	public static Valuation aggregate(List<Valuation> valuations) {
		Valuation result = null;
		NumericIntegerDomain domain = null;
		List<Valuation> values = new LinkedList<Valuation>();
		
		for(Valuation valuation: valuations) {
			Validator.notIllegalElementType(valuation, new String[] {IntegerValuation.class.toString()});
			
			if(domain == null) {
				domain = (NumericIntegerDomain) valuation.getDomain();
			} else if(!domain.equals(valuation.getDomain())) {
				throw new IllegalArgumentException("Invalid domain");
			}
			
			values.add(valuation);
		}
		
		if(!values.isEmpty()) {
			int median = values.size() / 2;
			Collections.sort(values);
			result = values.get(median);
			result = (Valuation) result.clone();
			
			if((values.size() % 2) == 0) {
				Valuation auxValuation = values.get(median - 1);
				long value = (((IntegerValuation) result).getValue());
				value += (((IntegerValuation) auxValuation).getValue());
				((IntegerValuation) result).setValue(value);
			}
		}
		
		return result;
	}
	
}
