package sinbad2.aggregationoperator.arithmetic;

import java.util.List;

import sinbad2.aggregationoperator.UnweightedAggregationOperator;
import sinbad2.core.validator.Validator;
import sinbad2.valuation.Valuation;

public class Arithmetic extends UnweightedAggregationOperator {

	@Override
	public Valuation aggregate(List<Valuation> valuations) {
		Validator.notNull(valuations);
		
		if(valuations.size() > 0) {
			for(Valuation valuation: valuations) {
			}
		}
		
		return null;
	}

}
