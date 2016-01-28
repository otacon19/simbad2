package sinbad2.aggregationoperator.max;

import java.util.List;

import sinbad2.aggregationoperator.UnweightedAggregationOperator;
import sinbad2.aggregationoperator.max.valuation.IntegerOperator;
import sinbad2.aggregationoperator.max.valuation.IntervalIntegerOperator;
import sinbad2.aggregationoperator.max.valuation.IntervalRealOperator;
import sinbad2.aggregationoperator.max.valuation.RealOperator;
import sinbad2.aggregationoperator.max.valuation.TwoTupleOperator;
import sinbad2.aggregationoperator.max.valuation.UnifiedValuationOperator;
import sinbad2.core.validator.Validator;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.integer.IntegerValuation;
import sinbad2.valuation.integer.interval.IntegerInterval;
import sinbad2.valuation.real.RealValuation;
import sinbad2.valuation.real.interval.RealInterval;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;

public class Max extends UnweightedAggregationOperator {

	@Override
	public Valuation aggregate(List<Valuation> valuations) {
		Validator.notNull(valuations);
		
		if(valuations.size() > 0) {
			for(Valuation valuation: valuations) {
				if(valuation instanceof IntegerValuation) {
					return IntegerOperator.aggregate(valuations);
				} else if(valuation instanceof RealValuation) {
					return RealOperator.aggregate(valuations);
				} else if(valuation instanceof IntegerInterval){
					return IntervalIntegerOperator.aggregate(valuations);
				} else if(valuation instanceof RealInterval) {
					return IntervalRealOperator.aggregate(valuations);
				} else if(valuation instanceof TwoTuple){
					return TwoTupleOperator.aggregate(valuations);
				}  else if(valuation instanceof UnifiedValuation) {
					return UnifiedValuationOperator.aggregate(valuations);
				} else {
					throw new IllegalArgumentException("Not supported type");
				}
			}
		}
		
		return null;
			
	}

}
