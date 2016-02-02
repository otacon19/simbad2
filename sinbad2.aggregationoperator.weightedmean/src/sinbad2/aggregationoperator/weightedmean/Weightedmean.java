package sinbad2.aggregationoperator.weightedmean;

import java.util.LinkedList;
import java.util.List;

import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.aggregationoperator.weightedmean.valuation.IntegerOperator;
import sinbad2.aggregationoperator.weightedmean.valuation.RealOperator;
import sinbad2.aggregationoperator.weightedmean.valuation.TwoTupleOperator;
import sinbad2.aggregationoperator.weightedmean.valuation.UnifiedValuationOperator;
import sinbad2.core.validator.Validator;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.integer.IntegerValuation;
import sinbad2.valuation.real.RealValuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;

public class Weightedmean extends WeightedAggregationOperator {

	@Override
	public Valuation aggregate(List<Valuation> valuations, List<Double> weights) {
		
		Validator.notNull(valuations);
		Validator.notNull(weights);
		
		int valuationsSize = valuations.size();
		int weightsSize = weights.size();

		if(valuationsSize > 0) {
			if(valuationsSize == weightsSize) {
				double sum = 0;
				for(Double weight : weights) {
					if(weight == null) {
						throw new IllegalArgumentException("Null weight");
					}
					sum += weight;
				}

				if(sum != 1) {
					List<Double> newWeights = new LinkedList<Double>();
					for(Double w : weights) {
						newWeights.add(w);
					}
					weights.clear();

					for(Double w : newWeights) {
						weights.add(w / sum);
					}
				}

				double remainder = 0;
				int nullValuations = 0;
				for(int i = 0; i < valuationsSize; i++) {
					if(valuations.get(i) == null) {
						remainder += weights.get(i);
						nullValuations++;
					}
				}

				if(nullValuations > 0) {
					List<Valuation> auxValuations = new LinkedList<Valuation>();
					List<Double> auxWeights = new LinkedList<Double>();

					remainder /= (double) (valuationsSize - nullValuations);
					if(remainder < 1) {
						remainder = 1;
					}
					for(int i = 0; i < valuationsSize; i++) {
						if(valuations.get(i) != null) {
							auxWeights.add(weights.get(i) * remainder);
							auxValuations.add(valuations.get(i));
						}
					}

					valuations = auxValuations;
					weights = auxWeights;
				}

				for(Valuation valuation : valuations) {
					if(valuation instanceof IntegerValuation) {
						return IntegerOperator.aggregate(valuations, weights);
					} else if(valuation instanceof RealValuation) {
						return RealOperator.aggregate(valuations, weights);
					} else if(valuation instanceof TwoTuple) {
						return TwoTupleOperator.aggregate(valuations, weights);
					} else if(valuation instanceof UnifiedValuation) {
						return UnifiedValuationOperator.aggregate(valuations, weights);
					} else {
						throw new IllegalArgumentException("Not supported type.");
					}
				}
			} else {
				throw new IllegalArgumentException("Illegal number of weights.");
			}
		}
		return null;
	}

}
