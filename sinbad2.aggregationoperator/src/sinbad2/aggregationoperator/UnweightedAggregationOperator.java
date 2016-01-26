package sinbad2.aggregationoperator;

import java.util.List;

import sinbad2.valuation.Valuation;

public abstract class UnweightedAggregationOperator extends AggregationOperator {
	
	public abstract Valuation aggregate(List<Valuation> valuations);
	
}
