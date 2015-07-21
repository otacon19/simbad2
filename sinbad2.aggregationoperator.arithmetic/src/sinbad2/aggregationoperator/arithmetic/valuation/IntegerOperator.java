package sinbad2.aggregationoperator.arithmetic.valuation;

import java.util.List;




import sinbad2.core.validator.Validator;
import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.integer.IntegerValuation;

public class IntegerOperator {
	
	private IntegerOperator() {}
	
	public static Valuation aggregate(List<Valuation> valuations) {
		IntegerValuation result = null;
		long value = 0;
		int size = valuations.size();
		NumericIntegerDomain domain = null;
		
		for(Valuation valuation: valuations) {
			Validator.notIllegalElementType(valuation, new String[] {IntegerValuation.class.toString()});
			
			if(domain == null) {
				domain = (NumericIntegerDomain) valuation.getDomain();
			} else if(!domain.equals(valuation.getDomain())) {
				throw new IllegalArgumentException("Invalid domain");
			}
			
			value += (((IntegerValuation) valuation).getValue()) / size;
		}
		
		if(domain != null) {
			result = (IntegerValuation) valuations.get(0).clone();
			result.setValue(value);
		}
		
		return result;
	}
	
}
