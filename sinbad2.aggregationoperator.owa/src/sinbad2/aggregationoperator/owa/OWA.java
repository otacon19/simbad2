package sinbad2.aggregationoperator.owa;

import java.util.LinkedList;
import java.util.List;

import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.aggregationoperator.owa.valuation.IntegerOperator;
import sinbad2.aggregationoperator.owa.valuation.RealOperator;
import sinbad2.aggregationoperator.owa.valuation.TwoTupleOperator;
import sinbad2.aggregationoperator.owa.valuation.UnifiedValuationOperator;
import sinbad2.core.validator.Validator;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.integer.IntegerValuation;
import sinbad2.valuation.real.RealValuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;

public class OWA extends WeightedAggregationOperator {

	@Override
	public Valuation aggregate(List<Valuation> valuations, List<Double> weights) {
		Validator.notNull(valuations);

		List<Valuation> auxValuations = new LinkedList<Valuation>();
		for(Valuation valuation : valuations) {
			if (valuation != null) {
				auxValuations.add(valuation);
			}
		}

		if(auxValuations.size() != valuations.size()) {
			valuations = auxValuations;
		}

		if(valuations.size() > 0) {
			for(Valuation valuation : valuations) {
				if(valuation instanceof TwoTuple) {
				}
				if(valuation instanceof IntegerValuation) {
					return IntegerOperator.aggregate(valuations, weights.get(0), weights.get(1));
				} else if(valuation instanceof RealValuation) {
					return RealOperator.aggregate(valuations, weights.get(0), weights.get(1));
				} else if (valuation instanceof TwoTuple) {
					if(weights.get(0) == -1) {
						weights.remove(0);
						return TwoTupleOperator.aggregate(auxValuations, weights);
					} else {
						return TwoTupleOperator.aggregate(valuations, weights.get(0), weights.get(1));
					}
				} else if(valuation instanceof UnifiedValuation) {
					return UnifiedValuationOperator.aggregate(valuations, weights.get(0), weights.get(1));
				} else {
					throw new IllegalArgumentException("Not supported type.");
				}
			}
		}
		return null;
	}
}

