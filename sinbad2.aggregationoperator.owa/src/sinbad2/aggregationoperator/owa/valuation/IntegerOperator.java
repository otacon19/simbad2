package sinbad2.aggregationoperator.owa.valuation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sinbad2.aggregationoperator.owa.YagerQuantifiers;
import sinbad2.core.validator.Validator;
import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.integer.IntegerValuation;

public class IntegerOperator {

	public IntegerOperator() {}
	
	public static Valuation aggregate(List<Valuation> valuations, double alphaQ, double betaQ) {
		IntegerValuation result = null;
		double measure = 0;
		List<Double> measures = new LinkedList<Double>();
		NumericIntegerDomain domain = null;

		for(Valuation valuation : valuations) {
			Validator.notIllegalElementType(valuation, new String[] { IntegerValuation.class.toString() });

			if(domain == null) {
				domain = (NumericIntegerDomain) valuation.getDomain();
			} else if (!domain.equals(valuation.getDomain())) {
				throw new IllegalArgumentException("Invalid domain");
			}
			
			measures.add((double) ((IntegerValuation) valuation).getValue());

		}

		if(domain != null) {
			Collections.sort(measures);
			Collections.reverse(measures);

			int size = measures.size();
			double[] weights = YagerQuantifiers.Q(size, alphaQ, betaQ);

			for(int i = 0; i < size; i++) {
				measure += weights[i] * measures.get(i);
			}

			result = (IntegerValuation) valuations.get(0).clone();
			result.setValue((long) measure);
		}
		
		return result;
	}
}
