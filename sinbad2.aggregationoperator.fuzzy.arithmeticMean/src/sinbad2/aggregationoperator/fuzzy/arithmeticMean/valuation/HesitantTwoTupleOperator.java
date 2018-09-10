package sinbad2.aggregationoperator.fuzzy.arithmeticMean.valuation;

import java.util.List;

import sinbad2.aggregationoperator.fuzzy.arithmeticMean.nls.Messages;
import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.elicit.ELICIT;

public class HesitantTwoTupleOperator {

private HesitantTwoTupleOperator() {}
	
	public static Valuation aggregate(List<Valuation> valuations) {
		ELICIT v = null;
		FuzzySet domain = null;
		
		TrapezoidalFunction tpf, tpfResult = new TrapezoidalFunction(0, 0, 0, 0);
		
		int size = valuations.size();
		for(int i = 0; i < size; ++i) {
			v = (ELICIT) valuations.get(i);
			Validator.notIllegalElementType(v, new String[] { ELICIT.class.toString() });
			
			if(domain == null) {
				domain = (FuzzySet) v.getDomain();
			} else if(!domain.equals(v.getDomain())) {
				throw new IllegalArgumentException(Messages.HesitantTwoTupleOperator_Invalid_domain);
			}
			
			if(((ELICIT) v).getBeta() == null) {
				tpf = ((ELICIT) v).calculateFuzzyEnvelope();
			} else {
				tpf = ((ELICIT) v).getBeta();
			}
			
			tpfResult = tpfResult.additionAlphaCuts(tpf);
		}

		tpfResult = tpfResult.divisionScalar(size);
		
		ELICIT aggregatedElicit = new ELICIT();
		aggregatedElicit.setDomain(valuations.get(0).getDomain());
		aggregatedElicit.createRelation(tpfResult);
		
		return aggregatedElicit;
	}
}
