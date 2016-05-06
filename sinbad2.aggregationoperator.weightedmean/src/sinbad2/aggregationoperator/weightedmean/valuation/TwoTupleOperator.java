package sinbad2.aggregationoperator.weightedmean.valuation;

import java.util.LinkedList;
import java.util.List;

import sinbad2.aggregationoperator.weightedmean.nls.Messages;
import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.twoTuple.TwoTuple;

public class TwoTupleOperator {
	
	private TwoTupleOperator() {}

	public static Valuation aggregate(List<Valuation> valuations, List<Double> weights) {
		TwoTuple result = null;
		double beta = 0;
		List<Double> measures = new LinkedList<Double>();
		FuzzySet domain = null;

		for(Valuation valuation : valuations) {
			Validator.notIllegalElementType(valuation, new String[] { TwoTuple.class.toString() });

			if (domain == null) {
				domain = (FuzzySet) valuation.getDomain();
			} else if (!domain.equals(valuation.getDomain())) {
				throw new IllegalArgumentException(Messages.TwoTupleOperator_Invalid_domain);
			}
			measures.add(((TwoTuple) valuation).calculateInverseDelta());
		}

		if (domain != null) {
			int size = measures.size();
			for (int i = 0; i < size; i++) {
				beta += weights.get(i) * measures.get(i);
			}

			result = (TwoTuple) valuations.get(0).clone();
			result.calculateDelta(beta);
		}
		return result;
	}
	
}
