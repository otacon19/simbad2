package sinbad2.aggregationoperator.min;

import java.util.List;

import sinbad2.aggregationoperator.UnweightedAggregationOperator;
import sinbad2.aggregationoperator.min.valuation.IntegerOperator;
import sinbad2.aggregationoperator.min.valuation.IntervalIntegerOperator;
import sinbad2.aggregationoperator.min.valuation.IntervalRealOperator;
import sinbad2.aggregationoperator.min.valuation.RealOperator;
import sinbad2.aggregationoperator.min.valuation.TwoTupleOperator;
import sinbad2.core.validator.Validator;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.integer.IntegerValuation;
import sinbad2.valuation.integer.interval.IntegerInterval;
import sinbad2.valuation.real.RealValuation;
import sinbad2.valuation.real.interval.RealInterval;
import sinbad2.valuation.twoTuple.TwoTuple;

public class Min extends UnweightedAggregationOperator {
	
	@Override
	public Valuation aggregate(List<Valuation> valuations) {
		Validator.notNull(valuations);
		
		if(valuations.size() > 0) {
			for(Valuation valuation: valuations) {
				if(valuation instanceof IntegerValuation) {
					IntegerOperator.aggregate(valuations);
				} else if(valuation instanceof RealValuation) {
					RealOperator.aggregate(valuations);
				} else if(valuation instanceof IntegerInterval){
					IntervalIntegerOperator.aggregate(valuations);
				} else if(valuation instanceof RealInterval) {
					IntervalRealOperator.aggregate(valuations);
				} else if(valuation instanceof TwoTuple) {
					TwoTupleOperator.aggregate(valuations);
				} else {
					throw new IllegalArgumentException("Not supported type");
				}
			}
		}
		
		return null;
			
	}
}
