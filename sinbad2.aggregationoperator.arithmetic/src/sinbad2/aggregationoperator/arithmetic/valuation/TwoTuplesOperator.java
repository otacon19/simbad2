package sinbad2.aggregationoperator.arithmetic.valuation;

import java.util.List;

import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.linguistic.LinguisticValuation;

public class TwoTuplesOperator {

	private TwoTuplesOperator() {}
	
	public static Valuation aggregate(List<Valuation> valuations) {
		LinguisticValuation result = null;
		double beta = 0;
		FuzzySet domain = null;
		
		for(Valuation valuation: valuations) {
			Validator.notIllegalElementType(valuation, new String[] {LinguisticValuation.class.toString()});
			
			if(domain == null) {
				domain = (FuzzySet) valuation.getDomain();
			} else if(!domain.equals(valuation.getDomain())) {
				throw new IllegalArgumentException("Invalid domain");
			}
			
			LabelLinguisticDomain label = ((LinguisticValuation) valuation).getLabel();
			beta += domain.getLabelSet().getPos(label);	
		}
		
		beta /= domain.getLabelSet().getCardinality();
		
		if(domain != null) {
			result = (LinguisticValuation) valuations.get(0).clone();
			//Asignar resultado
		}
		
		return result;
	}
}
