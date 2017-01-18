package sinbad2.aggregationoperator.harmonic.valuation;

import java.util.List;

import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;

public class UnifiedValuationOperator {
	
	private UnifiedValuationOperator() {};

	public static Valuation aggregate(List<Valuation> valuations) {
		Valuation result = null;
		double increase, measure;
		FuzzySet domain = null;
		int cardinality = -1;
		//int size = valuations.size();

		for(Valuation valuation : valuations) {
			Validator.notIllegalElementType(valuation, new String[] { UnifiedValuation.class.toString() });

			if(domain == null) {
				domain = (FuzzySet) valuation.getDomain().clone();
				cardinality = domain.getLabelSet().getCardinality();
				for (int i = 0; i < cardinality; i++) {
					domain.setValue(i, 0d);
				}
			} else {
				for(int i = 0; i < cardinality; i++) {
					if (!domain.getLabelSet().getLabel(i).equals(((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(i))) {
						throw new IllegalArgumentException("Invalid domain");
					}
				}
			}

			for(int i = 0; i < cardinality; i++) {
				increase = (1 / ((FuzzySet) ((UnifiedValuation) valuation).getDomain()).getValue(i));
				measure = domain.getValue(i);
				measure += increase;
				domain.setValue(i, measure);
			}

		}

		if (domain != null) {
			result = new UnifiedValuation(domain);
		}

		return result;
	}	
}
