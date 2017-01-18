package sinbad2.aggregationoperator.harmonic.valuation;

import java.util.List;




import sinbad2.core.validator.Validator;
import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.integer.IntegerValuation;

public class IntegerOperator {
	
	private IntegerOperator() {}
	
	public static Valuation aggregate(List<Valuation> valuations) {
		IntegerValuation result = null;
		double value = 0, aux = 0;
		int size = valuations.size();
		NumericIntegerDomain domain = null;
		
		for(Valuation valuation: valuations) {
			Validator.notIllegalElementType(valuation, new String[] {IntegerValuation.class.toString()});
			
			if(domain == null) {
				domain = (NumericIntegerDomain) valuation.getDomain();
			} else if(!domain.equals(valuation.getDomain())) {
				throw new IllegalArgumentException("Invalid domain");
			}
			
			value += (1d / ((IntegerValuation) valuation).getValue());
		}
		
		aux = size / value;
		value =  aux;
		
		if(domain != null) {
			result = (IntegerValuation) valuations.get(0).clone();
			result.setValue(value);
		}
		
		return result;
	}
	
}
