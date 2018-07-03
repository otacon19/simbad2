package sinbad2.aggregationoperator.fuzzy.owa.valuation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sinbad2.aggregationoperator.owa.YagerQuantifiers;
import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.twoTuple.HesitantTwoTupleValuation;

public class HesitantTwoTupleOperator {
	
	public static Valuation aggregate(List<Valuation> valuations, double alphaQ, double betaQ) {
		HesitantTwoTupleValuation result = null;
		double a = 0, b = 0, c = 0, d = 0;
		List<HesitantTwoTupleValuation> measures = new LinkedList<HesitantTwoTupleValuation>();
		FuzzySet domain = null;

		for(Valuation valuation : valuations) {
			Validator.notIllegalElementType(valuation, new String[] { HesitantTwoTupleValuation.class.toString() });

			if(domain == null) {
				domain = (FuzzySet) valuation.getDomain();
			} else if (!domain.equals(valuation.getDomain())) {
				throw new IllegalArgumentException("Invalid domain");
			}
			
			measures.add((HesitantTwoTupleValuation) valuation);
		}

		if(domain != null) {
			Collections.sort(measures);
			Collections.reverse(measures);

			int size = measures.size();
			double[] weights = YagerQuantifiers.Q(size, alphaQ, betaQ), limits = new double[4];
			
			Valuation valuation;
			TrapezoidalFunction tpf;
			for(int i = 0; i < size; i++) {
				valuation = measures.get(i);
				if(((HesitantTwoTupleValuation) valuation).getBeta() == null) {
					tpf = ((HesitantTwoTupleValuation) valuation).calculateFuzzyEnvelopeEquivalentCLE(domain);
				} else {
					tpf = ((HesitantTwoTupleValuation) valuation).getBeta();
				}
		
				limits = tpf.getLimits();
				
				a += weights[i] * limits[0];
				b += weights[i] * limits[1];
				c += weights[i] * limits[2];
				d += weights[i] * limits[3];
			}

			result = (HesitantTwoTupleValuation) valuations.get(0).clone();
			result.createRelation(new TrapezoidalFunction(new double[]{a, b, c, d}));
		}
		
		return result;
	}

	public static Valuation aggregate(List<Valuation> valuations, List<Double> weights) {
		HesitantTwoTupleValuation result = null;
		double a = 0, b = 0, c = 0, d = 0;
		List<HesitantTwoTupleValuation> measures = new LinkedList<HesitantTwoTupleValuation>();
		FuzzySet domain = null;

		for(Valuation valuation : valuations) {
			Validator.notIllegalElementType(valuation, new String[] { HesitantTwoTupleValuation.class.toString() });

			if(domain == null) {
				domain = (FuzzySet) valuation.getDomain();
			} else if (!domain.equals(valuation.getDomain())) {
				throw new IllegalArgumentException("Invalid domain");
			}
			
			measures.add((HesitantTwoTupleValuation) valuation);
		}

		if(domain != null) {
			Collections.sort(measures);
			Collections.reverse(measures);

			int size = measures.size();
			double[] limits = new double[4];
			
			Valuation valuation;
			TrapezoidalFunction tpf;
			for(int i = 0; i < size; i++) {
				valuation = measures.get(i);
				if(((HesitantTwoTupleValuation) valuation).getBeta() == null) {
					tpf = ((HesitantTwoTupleValuation) valuation).calculateFuzzyEnvelopeEquivalentCLE(domain);
				} else {
					tpf = ((HesitantTwoTupleValuation) valuation).getBeta();
				}
		
				limits = tpf.getLimits();
				
				a += weights.get(i) * limits[0];
				b += weights.get(i) * limits[1];
				c += weights.get(i) * limits[2];
				d += weights.get(i) * limits[3];
			}

			result = (HesitantTwoTupleValuation) valuations.get(0).clone();
			result.createRelation(new TrapezoidalFunction(new double[]{a, b, c, d}));
		}
		
		return result;
	}
	
}
