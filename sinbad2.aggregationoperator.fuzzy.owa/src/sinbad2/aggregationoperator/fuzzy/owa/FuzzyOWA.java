package sinbad2.aggregationoperator.fuzzy.owa;

import java.util.LinkedList;
import java.util.List;

import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.aggregationoperator.fuzzy.owa.valuation.HesitantTwoTupleOperator;
import sinbad2.core.validator.Validator;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.elicit.ELICIT;

public class FuzzyOWA extends WeightedAggregationOperator {

	public static final String ID = "flintstones.aggregationoperator.fuzzy.owa"; //$NON-NLS-1$
	
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
				if(valuation instanceof ELICIT) {
					if(weights.get(0) == -1) {
						weights.remove(0);
						return HesitantTwoTupleOperator.aggregate(auxValuations, weights);
					} else {
						return HesitantTwoTupleOperator.aggregate(valuations, weights.get(0), weights.get(1));
					}
				} else {
					throw new IllegalArgumentException("Not supported type");
				}
			}
		}
		return null;
	}
}


