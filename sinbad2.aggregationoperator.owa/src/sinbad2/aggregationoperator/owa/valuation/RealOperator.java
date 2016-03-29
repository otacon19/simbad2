package sinbad2.aggregationoperator.owa.valuation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.core.validator.Validator;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.real.RealValuation;

public class RealOperator {

	public RealOperator() {}
	
	public static Valuation aggregate(List<Valuation> valuations, double alphaQ, double betaQ) {
		RealValuation result = null;
		double measure = 0;
		List<Double> measures = new LinkedList<Double>();
		NumericRealDomain domain = null;

		for(Valuation valuation : valuations) {
			Validator.notIllegalElementType(valuation, new String[] { RealValuation.class.toString() });

			if(domain == null) {
				domain = (NumericRealDomain) valuation.getDomain();
			} else if (!domain.equals(valuation.getDomain())) {
				throw new IllegalArgumentException("Invalid domain");
			}
			
			measures.add((double) ((RealValuation) valuation).getValue());

		}

		if(domain != null) {
			Collections.sort(measures);
			Collections.reverse(measures);

			int size = measures.size();
			double[] weights = WeightedAggregationOperator.Q(size, alphaQ, betaQ);

			for(int i = 0; i < size; i++) {
				measure += weights[i] * measures.get(i);
			}

			result = (RealValuation) valuations.get(0).clone();
			result.setValue((double) measure);
		}
		
		return result;
	}
	
}
