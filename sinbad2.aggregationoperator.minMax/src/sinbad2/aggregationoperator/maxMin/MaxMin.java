package sinbad2.aggregationoperator.maxMin;

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

		if(valuations.size() > 0) {
			for(Valuation valuation: valuations) {
				if(valuation instanceof HesitantValuation) {
					HesitantOperator.aggregate(valuations);
				} else {
					throw new IllegalArgumentException("Not supported type");
				}
			}
		}
		
		return null;	
	}
		
}
