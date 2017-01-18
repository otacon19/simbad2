package sinbad2.aggregationoperator.geometric.valuation;

import java.util.List;

import sinbad2.core.validator.Validator;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.real.RealValuation;

public class RealOperator {
	
	private RealOperator() {}
	
	public static Valuation aggregate(List<Valuation> valuations) {
		RealValuation result = null;
		double value = 0, aux = 0;
		int size = valuations.size();
		NumericRealDomain domain = null;
		
		for(Valuation valuation: valuations) {
			Validator.notIllegalElementType(valuation, new String[] {RealValuation.class.toString()});
			
			if(domain == null) {
				domain = (NumericRealDomain) valuation.getDomain();
			} else if(!domain.equals(valuation.getDomain())) {
				throw new IllegalArgumentException("Invalid domain");
			}
			
			value *= ((RealValuation) valuation).getValue();
		}
		
		aux = Math.pow(value,1/size);
		value = aux;
		
		if(domain != null) {
			result = (RealValuation) valuations.get(0).clone();
			result.setValue(value);
		}
		
		return result;
	}
}
