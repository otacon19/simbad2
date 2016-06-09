package sinbad2.aggregationoperator.max.valuation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sinbad2.aggregationoperator.max.nls.Messages;
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
				throw new IllegalArgumentException(Messages.IntegerOperator_Invalid_domain);
			}
			
			values.add((IntegerValuation) valuation);
		}
		
		if(!values.isEmpty()) {
			Collections.sort(values);
			result = values.get(values.size() - 1);
			result = (Valuation) result.clone();
		}
		System.out.println(result);
		return result;
	}
}
