package sinbad2.aggregationoperator.fuzzy.weightedmean;

import java.util.LinkedList;
import java.util.List;

import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.aggregationoperator.fuzzy.weightedmean.valuation.HesitantTwoTupleOperator;
import sinbad2.core.validator.Validator;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.elicit.ELICIT;

public class FuzzyWeightedMean extends WeightedAggregationOperator {
	
	public static final String ID = "flintstones.aggregationoperator.fuzzy.weightedmean";
	
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
					if(valuation instanceof ELICIT) {
						return HesitantTwoTupleOperator.aggregate(valuations, weights);
					} else {
						throw new IllegalArgumentException("Not supported type");
					}
				}
			} else {
				throw new IllegalArgumentException("Illegal number of weights");
			}
		}
		return null;
	}	
}
