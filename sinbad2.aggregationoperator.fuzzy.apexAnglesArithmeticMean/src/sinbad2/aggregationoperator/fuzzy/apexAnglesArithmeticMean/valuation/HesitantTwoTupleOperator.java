package sinbad2.aggregationoperator.fuzzy.apexAnglesArithmeticMean.valuation;

import java.util.List;

import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.twoTuple.HesitantTwoTupleValuation;

public class HesitantTwoTupleOperator {

	private HesitantTwoTupleOperator() {}
	
	public static Valuation aggregate(List<Valuation> valuations) {
		HesitantTwoTupleValuation result = null;
		double a = 0, b = 0, c = 0, d = 0;
		double limits[] = new double[4];
		int size = valuations.size();
		
		TrapezoidalFunction tpf;
		
		double[] angle = computeAngle(valuations);
		
		for(Valuation valuation : valuations) {
			Validator.notIllegalElementType(valuation, new String[] { HesitantTwoTupleValuation.class.toString() });

			tpf = ((HesitantTwoTupleValuation) valuation).getFuzzyNumber();
				 
			limits = tpf.getLimits();
			
			a += limits[1] - Math.tan(angle[0]);
			b += limits[1];
			c += limits[2];
			d += limits[2] + Math.tan(angle[1]);
		}
		
		a /= size;
		b /= size;
		c /= size;
		d /= size;
		
		result = (HesitantTwoTupleValuation) valuations.get(0).clone();
		result.createRelation(new TrapezoidalFunction(new double[]{a, b, c, d}));
		
		return result;
	}

	private static double[] computeAngle(List<Valuation> valuations) {
		double[] angles = new double[2];
		angles[0] = 0;
		angles[1] = 0;
		
		FuzzySet domain = null;
		int size = valuations.size();
		double limits[] = new double[4];
		
		TrapezoidalFunction tpf;
	
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
				 
			limits = tpf.getLimits();
			
			angles[0] += Math.atan(limits[1] - limits[0]);
			angles[1] += Math.atan(limits[3] - limits[2]);
		}
		
		angles[0] /= size;
		angles[1] /= size;
		
		return angles;
	}
}
