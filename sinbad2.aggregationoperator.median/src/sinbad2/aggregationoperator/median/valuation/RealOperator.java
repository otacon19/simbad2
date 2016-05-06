package sinbad2.aggregationoperator.median.valuation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sinbad2.aggregationoperator.median.nls.Messages;
import sinbad2.core.validator.Validator;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.real.RealValuation;

public class RealOperator {
	
	private RealOperator() {}
	
	public static Valuation aggregate(List<Valuation> valuations) {
		Valuation result = null;
		NumericRealDomain domain = null;
		List<Valuation> values = new LinkedList<Valuation>();
		
		for(Valuation valuation: valuations) {
			Validator.notIllegalElementType(valuation, new String[] {RealValuation.class.toString()});
			
			if(domain == null) {
				domain = (NumericRealDomain) valuation.getDomain();
			} else if(!domain.equals(valuation.getDomain())) {
				throw new IllegalArgumentException(Messages.RealOperator_Invalid_domain);
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
				double value = (((RealValuation) result).getValue());
				value += (((RealValuation) auxValuation).getValue());
				((RealValuation) result).setValue(value);
			}
		}
		
		return result;
	}
	
}
