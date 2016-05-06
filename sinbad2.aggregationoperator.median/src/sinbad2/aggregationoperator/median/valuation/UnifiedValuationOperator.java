package sinbad2.aggregationoperator.median.valuation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sinbad2.aggregationoperator.median.nls.Messages;
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

			Validator.notIllegalElementType(valuation, new String[] { UnifiedValuation.class.toString() });

			if(domain == null) {
				domain = (FuzzySet) valuation.getDomain().clone();
				cardinality = domain.getLabelSet().getCardinality();
				for (int i = 0; i < cardinality; i++) {
					domain.setValue(i, 0d);
				}
			} else {
				for(int i = 0; i < cardinality; i++) {
					if(!domain.getLabelSet().getLabel(i).equals(((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(i))) {
						throw new IllegalArgumentException(Messages.UnifiedValuationOperator_Invalid_domain);
					}
				}
			}
			values.add(valuation);
		}
		
		if(!values.isEmpty()) {
			Collections.sort(values);
			int pos = values.size() / 2;
			result = values.get(pos);
			result = (Valuation) result.clone();
			if((values.size() % 2) == 0) {
				Valuation auxValuation = values.get(pos - 1);
				List<Double> measures1 = ((FuzzySet) ((UnifiedValuation) result).getDomain()).getValues();
				List<Double> measures2 = ((FuzzySet) ((UnifiedValuation) auxValuation).getDomain()).getValues();
				for(int i = 0; i < cardinality; i++) {
					measures1.set(i, (measures1.get(i) + measures2.get(i)) / 2d);
				}
				((FuzzySet) ((UnifiedValuation) result).getDomain()).setValues(measures1);				
			}
		}
		return result;
	}
}
