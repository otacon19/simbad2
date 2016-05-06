package sinbad2.aggregationoperator.median.valuation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sinbad2.aggregationoperator.median.nls.Messages;
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
				throw new IllegalArgumentException(Messages.IntervalRealOperator_Invalid_domain);
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
				double minValue = (((RealIntervalValuation) result).getMin()) + (((RealIntervalValuation) auxValuation).getMin());
				double maxValue = (((RealIntervalValuation) result).getMax()) + (((RealIntervalValuation) auxValuation).getMax());
				minValue /= 2;
				maxValue /= 2;
				((RealIntervalValuation) result).setMinMax(minValue, maxValue);
			}
		}
		
		return result;
	}
	
}
