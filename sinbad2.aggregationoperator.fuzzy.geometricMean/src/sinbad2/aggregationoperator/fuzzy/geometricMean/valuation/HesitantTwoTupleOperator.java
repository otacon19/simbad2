package sinbad2.aggregationoperator.fuzzy.geometricMean.valuation;

import java.util.List;

import sinbad2.aggregationoperator.fuzzy.geometricMean.nls.Messages;
import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.twoTuple.HesitantTwoTupleValuation;

public class HesitantTwoTupleOperator {

private HesitantTwoTupleOperator() {}
	
	public static Valuation aggregate(List<Valuation> valuations) {
		HesitantTwoTupleValuation result = null;
		double a = 1, b = 1, c = 1, d = 1;
		double limits[] = new double[4];
		FuzzySet domain = null;
		int size = valuations.size();
		
		TrapezoidalFunction tpf;
		
		for(Valuation valuation : valuations) {
			Validator.notIllegalElementType(valuation, new String[] { HesitantTwoTupleValuation.class.toString() });

			if(domain == null) {
				domain = (FuzzySet) valuation.getDomain();
			} else if(!domain.equals(valuation.getDomain())) {
				throw new IllegalArgumentException(Messages.HesitantTwoTupleOperator_Invalid_domain);
			}
			
			if(((HesitantTwoTupleValuation) valuation).getFuzzyNumber() == null) {
				tpf = ((HesitantTwoTupleValuation) valuation).calculateFuzzyEnvelope(domain);
			} else {
				tpf = ((HesitantTwoTupleValuation) valuation).getFuzzyNumber();
			}
				 
			limits = tpf.getLimits();
			a *= limits[0];
			b *= limits[1];
			c *= limits[2];
			d *= limits[3];
		}

		a = Math.pow(a, 1d / size);
		b = Math.pow(b, 1d / size);
		c = Math.pow(c, 1d / size);
		d = Math.pow(d, 1d / size);
		
		result = (HesitantTwoTupleValuation) valuations.get(0).clone();
		result.createRelation(new TrapezoidalFunction(new double[]{a, b, c, d}));
		
		return result;
	}
	
}
