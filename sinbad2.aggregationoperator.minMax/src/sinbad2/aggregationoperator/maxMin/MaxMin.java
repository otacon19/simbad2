package sinbad2.aggregationoperator.maxMin;

import java.util.LinkedList;
import java.util.List;

import sinbad2.aggregationoperator.UnweightedAggregationOperator;
import sinbad2.aggregationoperator.maxMin.valuation.HesitantOperator;
import sinbad2.core.validator.Validator;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.HesitantValuation;

public class MaxMin extends UnweightedAggregationOperator {

	@Override
	public Valuation aggregate(List<Valuation> valuations) {
		
		Validator.notNull(valuations);

		List<Valuation> auxValuations = new LinkedList<Valuation>();
		for (Valuation valuation : valuations) {
			if (valuation != null) {
				auxValuations.add(valuation);
			}
		}

		if (auxValuations.size() != valuations.size()) {
			valuations = auxValuations;
		}
		
		if(valuations.size() > 0) {
			for(Valuation valuation: valuations) {
				if(valuation instanceof HesitantValuation) {
					return HesitantOperator.aggregate(valuations);
				} else {
					throw new IllegalArgumentException("Not supported type");
				}
			}
		}
		
		return null;	
	}
		
}
