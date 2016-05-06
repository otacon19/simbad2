package sinbad2.aggregationoperator.maxMin.valuation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sinbad2.aggregationoperator.maxMin.nls.Messages;
import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.HesitantValuation;

public class HesitantOperator {
	
	private HesitantOperator() {}
	
	public static Valuation aggregate(List<Valuation> valuations) {
		Valuation result = null;
		FuzzySet domain = null;
		List<Valuation> values = new LinkedList<Valuation>();
		List<Integer> max = new LinkedList<Integer>();
		
		for(Valuation valuation: valuations) {
			Validator.notIllegalElementType(valuation, new String[] {HesitantValuation.class.toString()});
			
			if(domain == null) {
				domain = (FuzzySet) valuation.getDomain();
			} else if(!domain.equals(valuation.getDomain())) {
				throw new IllegalArgumentException(Messages.HesitantOperator_Invalid_domain);
			}
			
			values.add(valuation);
		}
		
		if(!values.isEmpty()) {
			for(Valuation valuation: values) {
				if(valuation instanceof HesitantValuation) {
					max.add(((HesitantValuation) valuation).getEnvelopeIndex()[1]);
				}
			}
			Collections.sort(max);
			result = new HesitantValuation(domain);
			((HesitantValuation) result).setLabel(max.get(0));
		}
		
		return result;
	}

}
