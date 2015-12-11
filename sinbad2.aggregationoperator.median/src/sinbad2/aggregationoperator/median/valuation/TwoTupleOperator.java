package sinbad2.aggregationoperator.median.valuation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.twoTuple.TwoTuple;

public class TwoTupleOperator {
	
	private TwoTupleOperator() {}
	
	public static Valuation aggregate(List<Valuation> valuations) {
		Valuation result = null;
		FuzzySet domain = null;
		List<Valuation> values = new LinkedList<Valuation>();
		
		for(Valuation valuation: valuations) {
			Validator.notIllegalElementType(valuation, new String[] {TwoTuple.class.toString()});
			
			if(domain == null) {
				domain = (FuzzySet) valuation.getDomain();
			} else if(!domain.equals(valuation.getDomain())) {
				throw new IllegalArgumentException("Invalid domain");
			}
			
			values.add(valuation);
		}
		
		if(domain != null) {
			Collections.sort(values);
			int pos = values.size() / 2;
			result = values.get(pos);
			result = (Valuation) result.clone();
			if(values.size() % 2 == 0) {
				Valuation auxValuation = values.get(pos - 1);
				double beta = ((TwoTuple) result).calculateInverseDelta();
				beta += ((TwoTuple) auxValuation).calculateInverseDelta();
				beta /= 2.0;
				((TwoTuple) result).calculateDelta(beta);
			}
		}
		
		return result;
	}

}
