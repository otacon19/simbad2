package sinbad2.aggregationoperator.choquetIntegral.valuation;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sinbad2.aggregationoperator.choquetIntegral.ChoquetIntegral;
import sinbad2.core.validator.Validator;
import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.integer.IntegerValuation;

public class IntegerOperator {

	private IntegerOperator() {};

	public static Valuation aggregate(List<Valuation> valuations, List<Double> weights, List<Set<Integer>> assignments, Map<Integer, Set<Integer>> rules) {

		IntegerValuation result = null;
		double measure = 0;
		List<Double> measures = new LinkedList<Double>();
		NumericIntegerDomain domain = null;

		for (Valuation valuation : valuations) {
			// Controlar evaluación de tipo inválido
			Validator.notIllegalElementType(valuation, new String[] { IntegerValuation.class.toString() });

			// Comprobamos que el dominio sea igual
			if(domain == null) {
				domain = (NumericIntegerDomain) valuation.getDomain();
			} else if(!domain.equals(valuation.getDomain())) {
				throw new IllegalArgumentException("Invalid domain");
			}

			// Agregamos la evaluación
			measures.add((double) ((IntegerValuation) valuation).getValue());

		}

		// Calculamos la evaluación
		if(domain != null) {
			// Calculamos el ranking
			List<Integer> ranking = ChoquetIntegral.getRanking(measures);

			// Calculamos los pesos para cada elemento
			List<Double> finalWeights = ChoquetIntegral.getWeights(ranking, weights, assignments, rules);
			
			// Calculamos las medidas finales
			List<Double> finalMeasures = ChoquetIntegral.getMeasures(measures, ranking);
			
			// Calculamos el resultado del operador
			int size = finalMeasures.size();
			for (int i = 0; i < size; i++) {
				measure += finalWeights.get(i) * finalMeasures.get(i);
			}

			// Establecemos la medida del resultado
			result = (IntegerValuation) valuations.get(0).clone();
			result.setValue((long) measure);
		}

		return result;
	}
}
