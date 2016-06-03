package sinbad2.aggregationoperator.choquetIntegral.valuation;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sinbad2.aggregationoperator.choquetIntegral.ChoquetIntegral;
import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;

public class UnifiedValuationOperator {

	private UnifiedValuationOperator() {};

	public static Valuation aggregate(List<Valuation> valuations, List<Double> weights, List<Set<Integer>> assignments, Map<Integer, Set<Integer>> rules) {

		Valuation result = null;
		FuzzySet domain = null;
		List<Valuation> validValuations = new LinkedList<Valuation>();
		List<Double> measures = new LinkedList<Double>();
		int cardinality = -1;

		for(Valuation valuation : valuations) {
			// Controlar evaluación de tipo inválido
			Validator.notIllegalElementType(valuation, new String[] { UnifiedValuation.class.toString() });

			// Comprobamos que la semántica del dominio sea igual
			if(domain == null) {
				domain = (FuzzySet) valuation.getDomain().clone();
				cardinality = domain.getLabelSet().getCardinality();
				for (int i = 0; i < cardinality; i++) {
					domain.setValue(i, 0.0);
				}
			} else {
				for(int i = 0; i < cardinality; i++) {
					if(!domain.getLabelSet().getLabel(i).equals(((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(i))) {
						throw new IllegalArgumentException("Invalid domain");
					}
				}
			}

			validValuations.add(valuation);
			measures.add(((UnifiedValuation) valuation).disunification((FuzzySet) valuation.getDomain()).calculateInverseDelta());

		}

		// Calculamos la evaluación
		UnifiedValuation valuation;
		if(domain != null) {
			//Calculamos el ranking
			List<Integer> ranking = ChoquetIntegral.getRanking(measures);
			
			// Calculamos los pesos para cada elemento
			List<Double> finalWeights = ChoquetIntegral.getWeights(ranking, weights, assignments, rules);
			
			// Agregamos la evaluación
			int size = validValuations.size();
			double[] labelWeight = new double[cardinality];
			for(int i = 0; i < cardinality; i++) {
				labelWeight[i] = 0;
			}
			
			double weight;
			for(int i = 0; i < size; i++) {
				valuation = (UnifiedValuation) validValuations.get(i);
				weight = finalWeights.get(i);
				for (int j = 0; j < cardinality; j++) {
					labelWeight[j] += (((FuzzySet) (valuation).getDomain()).getValue(j) * weight);
				}
			}
			
			for(int i = 0; i < cardinality; i++) {
				domain.setValue(i, labelWeight[i] / size);
			}
			
			result = new UnifiedValuation(domain);

		}

		return result;
	}
}
