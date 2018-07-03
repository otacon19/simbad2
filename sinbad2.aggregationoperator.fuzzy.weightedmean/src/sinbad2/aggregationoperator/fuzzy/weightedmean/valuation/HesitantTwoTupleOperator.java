package sinbad2.aggregationoperator.fuzzy.weightedmean.valuation;

import java.util.List;

import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.elicit.ELICIT;

public class HesitantTwoTupleOperator {

	public static Valuation aggregate(List<Valuation> valuations, List<Double> weights) {
		ELICIT result = null;
		double a = 0, b = 0, c = 0, d = 0, weight;
		double limits[] = new double[4];
		FuzzySet domain = null;
		
		TrapezoidalFunction tpf;

		int numWeight = 0;
		for(Valuation valuation : valuations) {
			Validator.notIllegalElementType(valuation, new String[] { ELICIT.class.toString() });

			if(domain == null) {
				domain = (FuzzySet) valuation.getDomain();
			} else if(!domain.equals(valuation.getDomain())) {
				throw new IllegalArgumentException("Invalid domain");
			}
			
			if(((ELICIT) valuation).getBeta() == null) {
				tpf = ((ELICIT) valuation).calculateFuzzyEnvelopeEquivalentCLE(domain);
			} else {
				tpf = ((ELICIT) valuation).getBeta();
			}
				 
			weight = weights.get(numWeight);
			
			limits = tpf.getLimits();
			a += limits[0] * weight;
			b += limits[1] * weight;
			c += limits[2] * weight;
			d += limits[3] * weight;
			
			numWeight++;
		}
		
		result = (ELICIT) valuations.get(0).clone();
		result.createRelation(new TrapezoidalFunction(new double[]{a, b, c, d}));
		

		return result;
	}
}
