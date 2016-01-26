package sinbad2.aggregationoperator.median;

import java.util.List;

import sinbad2.aggregationoperator.UnweightedAggregationOperator;
import sinbad2.aggregationoperator.median.valuation.IntegerOperator;
import sinbad2.aggregationoperator.median.valuation.IntervalIntegerOperator;
import sinbad2.aggregationoperator.median.valuation.IntervalRealOperator;
import sinbad2.aggregationoperator.median.valuation.RealOperator;
import sinbad2.aggregationoperator.median.valuation.TwoTupleOperator;
import sinbad2.aggregationoperator.median.valuation.UnifiedValuationOperator;
import sinbad2.core.validator.Validator;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.integer.IntegerValuation;
import sinbad2.valuation.integer.interval.IntegerInterval;
import sinbad2.valuation.real.RealValuation;
import sinbad2.valuation.real.interval.RealInterval;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;

public class Median extends UnweightedAggregationOperator {

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
				}  else if(valuation instanceof UnifiedValuation) {
					UnifiedValuationOperator.aggregate(valuations);
				} else {
					throw new IllegalArgumentException("Not supported type");
				}
			}
		}
		
		return null;
			
	}
}
