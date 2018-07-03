package sinbad2.aggregationoperator.fuzzy.max.valuation;

import java.util.Collections;
import java.util.LinkedList;
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
		List<Double> a = new LinkedList<Double>(), b = new LinkedList<Double>(), c = new LinkedList<Double>(), d = new LinkedList<Double>();
		double limits[] = new double[4];
		FuzzySet domain = null;
		
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
			a.add(limits[0]);
			b.add(limits[1]);
			c.add(limits[2]);
			d.add(limits[3]);
		}
		
		Collections.sort(a);
		Collections.sort(b);
		Collections.sort(c);
		Collections.sort(d);
		
		int size = a.size();
		
		double maxa = a.get(size - 1);
		double maxb = b.get(size - 1);
		double maxc = c.get(size - 1);
		double maxd = d.get(size - 1);
		
		result = (ELICIT) valuations.get(0).clone();
		result.createRelation(new TrapezoidalFunction(new double[]{maxa, maxb, maxc, maxd}));
		
		return result;
	}
}
