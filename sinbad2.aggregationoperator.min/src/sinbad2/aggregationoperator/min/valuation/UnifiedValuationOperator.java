package sinbad2.aggregationoperator.min.valuation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sinbad2.aggregationoperator.min.nls.Messages;
import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;

public class UnifiedValuationOperator {
	
	private UnifiedValuationOperator() {};

	public static Valuation aggregate(List<Valuation> valuations) {
		Valuation result = null;
		List<Valuation> values = new LinkedList<Valuation>();
		FuzzySet domain = (FuzzySet) valuations.get(0).getDomain().clone();
		int cardinality = domain.getLabelSet().getCardinality();

		for(int i = 0; i < cardinality; i++) {
			domain.setValue(i, 0d);
		}

		for(Valuation valuation : valuations) {

			Validator.notIllegalElementType(valuation,new String[] { UnifiedValuation.class.toString() });

			if(domain == null) {
				domain = (FuzzySet) valuation.getDomain().clone();
				cardinality = domain.getLabelSet().getCardinality();
				for(int i = 0; i < cardinality; i++) {
					domain.setValue(i, 0d);
				}
			} else {
				for(int i = 0; i < cardinality; i++) {
					if (!domain.getLabelSet().getLabel(i).equals(((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(i))) {
						throw new IllegalArgumentException(Messages.UnifiedValuationOperator_Invalid_domain);
					}
				}
			}
			values.add(valuation);
		}

		if(!values.isEmpty()) {
			Collections.sort(values);
			result = values.get(0);
			result = (Valuation) result.clone();
		}

		return result;
	}
}
