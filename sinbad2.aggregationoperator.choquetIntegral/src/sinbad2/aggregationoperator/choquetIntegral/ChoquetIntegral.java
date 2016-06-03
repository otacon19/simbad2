package sinbad2.aggregationoperator.choquetIntegral;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.aggregationoperator.choquetIntegral.valuation.IntegerOperator;
import sinbad2.aggregationoperator.choquetIntegral.valuation.RealOperator;
import sinbad2.aggregationoperator.choquetIntegral.valuation.TwoTupleOperator;
import sinbad2.aggregationoperator.choquetIntegral.valuation.UnifiedValuationOperator;
import sinbad2.core.validator.Validator;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.integer.IntegerValuation;
import sinbad2.valuation.real.RealValuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;

public class ChoquetIntegral extends WeightedAggregationOperator {

	@Override
	public Valuation aggregate(List<Valuation> valuations, List<Double> weights) {
		Map<Integer, Set<Integer>> rules = new HashMap<Integer, Set<Integer>>();
		List<Set<Integer>> assignments = new LinkedList<Set<Integer>>();
		List<Double> validWeights = new LinkedList<Double>();

		// Controlar evaluaciones nulas
		Validator.notNull(valuations);

		// Controlar pesos nulos
		Validator.notNull(weights);

		int valuationsSize = valuations.size();

		if(valuationsSize > 0) {
			HashSet<Integer> aux = null;
			boolean open = false;
			int numberOfElements = 0;
			int counter = 0;
			for(Double value : weights) {
				if(open) {
					if(counter == numberOfElements) {
						for(Integer i : aux) {
							if(!rules.containsKey(i)) {
								rules.put(i, new HashSet<Integer>());
							}
							rules.get(i).add(assignments.size());
						}
						assignments.add(aux);
						validWeights.add(value);
						open = false;
					} else {
						aux.add(value.intValue());
						counter++;
					}
				} else {
					numberOfElements = value.intValue();
					counter = 0;
					aux = new HashSet<Integer>();
					open = true;
				}
			}

			for(Valuation valuation : valuations) {
				if(valuation instanceof IntegerValuation) {
					return IntegerOperator.aggregate(valuations, validWeights, assignments, rules);
				} else if(valuation instanceof RealValuation) {
					return RealOperator.aggregate(valuations, validWeights, assignments, rules);
				} else if(valuation instanceof TwoTuple) {
					return TwoTupleOperator.aggregate(valuations, validWeights, assignments, rules);
				} else if(valuation instanceof UnifiedValuation) {
					return UnifiedValuationOperator.aggregate(valuations, validWeights, assignments, rules);
				} else {
					throw new IllegalArgumentException("Not supported type.");
				}
			}
		}

		// Si no existen evaluaciones devolvemos null
		return null;
	}

	public static List<Integer> getRanking(List<Double> values) {

		class MyObject implements Comparable<MyObject> {
			private Integer _pos;
			private Double _value;

			public MyObject(Integer pos, Double value) {
				_pos = pos;
				_value = value;
			}

			public Integer getPos() {
				return _pos;
			}

			public Double getValue() {
				return _value;
			}

			@Override
			public int compareTo(MyObject o) {
				return Double.compare(_value, o.getValue());
			}

		}

		List<Integer> result = new LinkedList<Integer>();
		List<MyObject> aux = new LinkedList<MyObject>();

		int size = values.size();

		for(int i = 0; i < size; i++) {
			aux.add(new MyObject(i, values.get(i)));
		}

		Collections.sort(aux);
		for(int i = 0; i < size; i++) {
			result.add(aux.get(i).getPos());
		}

		return result;
	}
	
	public static List<Double> getMeasures(List<Double> measures, List<Integer> ranking) {
		List<Double> result = new LinkedList<Double>();
		Double[] values = new Double[measures.size()];
		
		double last = 0;
		for(Integer i : ranking) {
			values[i] = measures.get(i) - last;
			last += values[i];
		}
		
		for(Double value : values) {
			result.add(value);
		}
		
		return result;
	}

	public static List<Double> getWeights(List<Integer> ranking, List<Double> weights, List<Set<Integer>> assignments, Map<Integer, Set<Integer>> rules) {

		List<Double> result = new LinkedList<Double>();
		Map<Integer, Double> auxResult = new HashMap<Integer, Double>();
		
		Set<Integer> currentSet = new HashSet<Integer>();
		for (int i = 0; i < ranking.size(); i++) {
			currentSet.add(i);
		}
		
		Set<Integer> currentAssignment;
		int assignmentsSize = assignments.size();
		boolean duplicate = false;
		Set<Integer> elementAssignments;
		
		// Iteramos sobre todos los elementos
		for(Integer currentElement : ranking) {
			
			elementAssignments = new HashSet<Integer>();
			
			// vamos añadiendo asignaciones una a una
			for(int assignmentPos = 0; assignmentPos < assignmentsSize; assignmentPos++) {
				currentAssignment = assignments.get(assignmentPos);
				
				if(currentSet.containsAll(currentAssignment)) {
					//Comprobamos si no exite una asignación con más elementos que la contenga
					duplicate = false;
					Iterator<Integer> iterator = elementAssignments.iterator();
					while(iterator.hasNext() && (!duplicate)) {
						if(assignments.get(iterator.next()).containsAll(currentAssignment)) {
							duplicate = true;
						}
					}
					
					if(!duplicate) {
						// Eliminamos asignaciones contenidas en esta
						List<Integer> toRemove = new LinkedList<Integer>();
						for(Integer testToRemove : elementAssignments) {
							if (currentAssignment.containsAll(assignments.get(testToRemove))) {
								toRemove.add(testToRemove);
							}
						}
						
						for(Integer removePos : toRemove) {
							elementAssignments.remove(removePos);
						}
						
						// Añadimos la asignación
						elementAssignments.add(assignmentPos);
					}
				}
			}

			// Calculamos la ponderación para cada elemento
			double weight = 0;
			for(Integer i : elementAssignments) {
				weight += weights.get(i);
			}
			auxResult.put(currentElement, weight);
			
			// Eliminamos el elemento del conjunto a calcular
			currentSet.remove(currentElement);
			
		}
		
		// Establecemos los pesos para cada elemento
		int size = auxResult.size();
		for(int pos = 0; pos < size; pos++) {
			result.add(auxResult.get(pos));
		}

		return result;
	}
}

