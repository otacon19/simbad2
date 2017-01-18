package sinbad2.aggregationoperator.weightedharmonic.valuation;

import java.util.LinkedList;
import java.util.List;

import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;

public class UnifiedValuationOperator {
	
	private UnifiedValuationOperator() {}

	public static Valuation aggregate(List<Valuation> valuations, List<Double> weights) {
		Valuation result = null;
		double increase;
		double measure;
		FuzzySet domain = null;
		List<Valuation> validValuations = new LinkedList<Valuation>();
		int cardinality = -1;

		for(Valuation valuation : valuations) {
			Validator.notIllegalElementType(valuation, new String[] { UnifiedValuation.class.toString() });

			if(domain == null) {
				domain = (FuzzySet) valuation.getDomain().clone();
				cardinality = domain.getLabelSet().getCardinality();
				for(int i = 0; i < cardinality; i++) {
					domain.setValue(i, 0d);
				}
			} else {
				for(int i = 0; i < cardinality; i++) {
					if(!domain.getLabelSet().getLabel(i).equals(((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(i))) {
						throw new IllegalArgumentException("Invalid domain");
					}
				}
			}
			validValuations.add(valuation);
		}

		UnifiedValuation valuation = null;
		if (domain != null) {
			int size = validValuations.size();
			double weight;
			for(int i = 0; i < size; i++) {
				valuation = (UnifiedValuation) validValuations.get(i);
				weight = weights.get(i);
				for(int j = 0; j < cardinality; j++) {
					increase = (((FuzzySet) (valuation).getDomain()).getValue(j) * weight);
					measure = domain.getValue(j);
					measure += increase;
					domain.setValue(j, measure);
				}
			}
		}

		if (domain != null) {
			result = new UnifiedValuation(domain);
		}

		return result;
	}

}
