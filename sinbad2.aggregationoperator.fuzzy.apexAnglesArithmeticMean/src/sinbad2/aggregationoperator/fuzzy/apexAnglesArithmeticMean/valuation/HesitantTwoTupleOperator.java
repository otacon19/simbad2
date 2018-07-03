package sinbad2.aggregationoperator.fuzzy.apexAnglesArithmeticMean.valuation;

import java.util.List;

import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.elicit.ELICIT;

public class HesitantTwoTupleOperator {

	private HesitantTwoTupleOperator() {}
	
	public static Valuation aggregate(List<Valuation> valuations) {
		ELICIT result = null;
		double a = 0, b = 0, c = 0, d = 0;
		double limits[] = new double[4];
		int size = valuations.size();
		
		TrapezoidalFunction tpf;
		
		double[] angle = computeAngle(valuations);
		
		for(Valuation valuation : valuations) {
			Validator.notIllegalElementType(valuation, new String[] { ELICIT.class.toString() });

			tpf = ((ELICIT) valuation).getBeta();
				 
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
		
		result = (ELICIT) valuations.get(0).clone();
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
				 
			limits = tpf.getLimits();
			
			angles[0] += Math.atan(limits[1] - limits[0]);
			angles[1] += Math.atan(limits[3] - limits[2]);
		}
		
		angles[0] /= size;
		angles[1] /= size;
		
		return angles;
	}
}
