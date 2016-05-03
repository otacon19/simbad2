package sinbad2.aggregationoperator;

import java.util.List;

import sinbad2.valuation.Valuation;

public abstract class WeightedAggregationOperator extends AggregationOperator {

	public abstract Valuation aggregate(List<Valuation> valuations, List<Double> weights);
}
