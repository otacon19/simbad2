package sinbad2.aggregationoperator.fuzzy.weightedmean.valuation;

import java.util.List;

import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.twoTuple.HesitantTwoTupleValuation;

public class HesitantTwoTupleOperator {

	public static Valuation aggregate(List<Valuation> valuations, List<Double> weights) {
		HesitantTwoTupleValuation result = null;
		double a = 0, b = 0, c = 0, d = 0, weight;
		double limits[] = new double[4];
		FuzzySet domain = null;
		
		TrapezoidalFunction tpf;

		int numWeight = 0;
		for(Valuation valuation : valuations) {
			Validator.notIllegalElementType(valuation, new String[] { HesitantTwoTupleValuation.class.toString() });

			if(domain == null) {
				domain = (FuzzySet) valuation.getDomain();
			} else if(!domain.equals(valuation.getDomain())) {
				throw new IllegalArgumentException("Invalid domain");
			}
			
			if(((HesitantTwoTupleValuation) valuation).getFuzzyNumber() == null) {
				tpf = ((HesitantTwoTupleValuation) valuation).calculateFuzzyEnvelope(domain);
			} else {
				tpf = ((HesitantTwoTupleValuation) valuation).getFuzzyNumber();
			}
				 
			weight = weights.get(numWeight);
			
			limits = tpf.getLimits();
			a += limits[0] * weight;
			b += limits[1] * weight;
			c += limits[2] * weight;
			d += limits[3] * weight;
			
			numWeight++;
		}
		
		result = (HesitantTwoTupleValuation) valuations.get(0).clone();
		result.createRelation(new TrapezoidalFunction(new double[]{a, b, c, d}));
		

		return result;
	}
}
