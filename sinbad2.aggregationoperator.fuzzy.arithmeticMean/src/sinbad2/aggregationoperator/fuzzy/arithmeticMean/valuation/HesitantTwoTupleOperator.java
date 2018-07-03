package sinbad2.aggregationoperator.fuzzy.arithmeticMean.valuation;

import java.util.List;

import sinbad2.aggregationoperator.fuzzy.arithmeticMean.nls.Messages;
import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.twoTuple.HesitantTwoTupleValuation;

public class HesitantTwoTupleOperator {

private HesitantTwoTupleOperator() {}
	
	public static Valuation aggregate(List<Valuation> valuations) {
		HesitantTwoTupleValuation result = null;
		FuzzySet domain = null;
		
		TrapezoidalFunction tpf, tpfResult;
		HesitantTwoTupleValuation v =  (HesitantTwoTupleValuation) valuations.get(0);
		if(v.getBeta() == null) {
			tpfResult = ((HesitantTwoTupleValuation) v).calculateFuzzyEnvelopeEquivalentCLE(domain);
		} else {
			tpfResult = ((HesitantTwoTupleValuation) v).getBeta();
		}
		
		int size = valuations.size();
		for(int i = 1; i < size; ++i) {
			v = (HesitantTwoTupleValuation) valuations.get(i);
			Validator.notIllegalElementType(v, new String[] { HesitantTwoTupleValuation.class.toString() });
			
			if(domain == null) {
				domain = (FuzzySet) v.getDomain();
			} else if(!domain.equals(v.getDomain())) {
				throw new IllegalArgumentException(Messages.HesitantTwoTupleOperator_Invalid_domain);
			}
			
			if(((HesitantTwoTupleValuation) v).getBeta() == null) {
				tpf = ((HesitantTwoTupleValuation) v).calculateFuzzyEnvelopeEquivalentCLE(domain);
			} else {
				tpf = ((HesitantTwoTupleValuation) v).getBeta();
			}
			
			tpfResult = tpfResult.additionAlphaCuts(tpf);
		}

		tpfResult = tpfResult.divisionScalar(size);
		
		result = (HesitantTwoTupleValuation) valuations.get(0).clone();
		result.createRelation(tpfResult);
		return result;
	}
}
