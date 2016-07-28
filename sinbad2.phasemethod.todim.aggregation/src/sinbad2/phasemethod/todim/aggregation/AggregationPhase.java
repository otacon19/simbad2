package sinbad2.phasemethod.todim.aggregation;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.UnweightedAggregationOperator;
import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.real.RealValuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class AggregationPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.todim.aggregation"; //$NON-NLS-1$
	
	private static final int P = 2;
	private static final int M = 2;
	
	private Map<ValuationKey, Valuation> _valuationsInTwoTuple;
	
	private Valuation[][] _decisionMatrix;
	private Map<Alternative, Map<Criterion, TrapezoidalFunction>> _aggregatedFuzzyNumbers;
	private Map<Alternative, Map<Criterion, TrapezoidalFunction>> _overallOpinions;
	private Map<ValuationKey, Double> _distances;
	
	private List<Double> _globalWeights;
	private Map<String, List<Double>> _criteriaWeights;
	
	private int _numAlternatives;
	private int _numCriteria;
	
	private ValuationSet _valuationSet;
	private ProblemElementsSet _elementsSet;
	
	@SuppressWarnings("rawtypes")
	public static class DataComparator implements Comparator {
		@Override
		public int compare(Object d1, Object d2) {
			String e1 = ((String[]) d1)[0];
			String e2 = ((String[]) d2)[0];
			String a1 = ((String[]) d1)[1];
			String a2 = ((String[]) d2)[1];
			String c1 = ((String[]) d1)[2];
			String c2 = ((String[]) d2)[2];
			
			int expertComparation = e1.compareTo(e2);
			if(expertComparation != 0) {
				return expertComparation;
			} else if(a1.compareTo(a2) != 0){
				return a1.compareTo(a2);
			} else {
				return c1.compareTo(c2);
			}
		}
	 }
	
	public AggregationPhase() {
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = valuationSetManager.getActiveValuationSet();
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
	
		_valuationsInTwoTuple = new HashMap<ValuationKey, Valuation>();

		_numAlternatives = _elementsSet.getAlternatives().size();
		_numCriteria = _elementsSet.getCriteria().size();
		_decisionMatrix = new Valuation[_numAlternatives][_numCriteria];
		
		_criteriaWeights = new HashMap<String, List<Double>>();
		_globalWeights = new LinkedList<Double>();
		
		_aggregatedFuzzyNumbers = new HashMap<Alternative, Map<Criterion, TrapezoidalFunction>>();
		_overallOpinions = new HashMap<Alternative, Map<Criterion, TrapezoidalFunction>>();
		_distances = new HashMap<ValuationKey, Double>();
	}
	
	public void setDecisionMatrix(Valuation[][] decisionMatrix) {
		_decisionMatrix = decisionMatrix;
	}
	
	public Valuation[][] getDecisionMatrix() {
		return _decisionMatrix;
	}
	
	public void setValuationsTwoTuple(Map<ValuationKey, Valuation> valuationsInTwoTuple) {
		_valuationsInTwoTuple = valuationsInTwoTuple;
	}
	
	public Map<ValuationKey, Valuation> getValuationsTwoTuple() {
		return _valuationsInTwoTuple;
	}
	
	public void setExpertsWeights(Map<String, List<Double>> expertsWeights) {
		_criteriaWeights = expertsWeights;
	}
	
	public Map<String, List<Double>> getExpertsWeights() {
		return _criteriaWeights;
	}
	
	public void setExpertsWeights(List<Double> globalWeights) {
		_globalWeights = globalWeights;
	}
	
	public List<Double> getGlobalWeights() {
		return _globalWeights;
	}
	
	public void setAggregatedFuzzyNumber(Map<Alternative, Map<Criterion, TrapezoidalFunction>> aggregatedFuzzyNumber) {
		_aggregatedFuzzyNumbers = aggregatedFuzzyNumber;
	}
	
	public Map<Alternative, Map<Criterion, TrapezoidalFunction>> getAggregatedFuzzyNumber() {
		return _aggregatedFuzzyNumbers;
	}
	
	public void setOverallOpinions(Map<Alternative, Map<Criterion, TrapezoidalFunction>> overallOpinions) {
		_overallOpinions = overallOpinions;
	}
	
	public Map<Alternative, Map<Criterion, TrapezoidalFunction>> getOverallOpinons() {
		return _overallOpinions;
	}
	
	public void setDistances(Map<ValuationKey, Double> distances) {
		_distances = distances;
	}
	
	public Map<ValuationKey, Double> getDistances() {
		return _distances;
	}
	
	public Valuation[][] calculateDecisionMatrix(AggregationOperator operator, Map<String, List<Double>> weights) {
		
		_numAlternatives = _elementsSet.getAlternatives().size();
		_numCriteria = _elementsSet.getCriteria().size();
		_decisionMatrix = new Valuation[_numAlternatives][_numCriteria];
		
		_globalWeights = new LinkedList<Double>();
		_criteriaWeights = new HashMap<String, List<Double>>();
		
		if(weights.isEmpty()) {
			setDefaultWeights();
		} else if(weights.size() == 1) {
			_globalWeights = weights.get(null);
		}
		
		for(int a = 0; a < _elementsSet.getAlternatives().size(); ++a) {
			for(int c = 0; c < _elementsSet.getAllCriteria().size(); ++c) {
				if(!_elementsSet.getCriteria().get(c).hasSubcriteria()) {
					aggregateExperts(a, c, operator, weights);
				}
			}
		}
		
		return _decisionMatrix;
	}

	private void setDefaultWeights() {		
		List<Expert> experts = _elementsSet.getAllExperts();
		double weight = 1d / experts.size();
		for(int i = 0; i < experts.size(); ++i) {
			_globalWeights.add(weight);
		}
	}

	private void aggregateExperts(int alternative, int criterion, AggregationOperator operator, Map<String, List<Double>> weights) {
		List<Alternative> alternatives = _elementsSet.getAlternatives();
		List<Criterion> criteria = _elementsSet.getCriteria();
		
		List<Double> criterionWeights = new LinkedList<Double>();
		if(weights.size() > 1) {
			criterionWeights = weights.get(_elementsSet.getAllCriteria().get(criterion).getCanonicalId());
			_criteriaWeights.put(_elementsSet.getAllCriteria().get(criterion).getCanonicalId(), criterionWeights);
		}
		
		List<Valuation> valuations = new LinkedList<Valuation>();
		for(ValuationKey vk: _valuationsInTwoTuple.keySet()) {
			if(vk.getAlternative().equals(alternatives.get(alternative)) && vk.getCriterion().equals(criteria.get(criterion))) {
				valuations.add(_valuationsInTwoTuple.get(vk));
			}
		}
		
		Valuation expertsColectiveValuation = null;
		if(operator instanceof UnweightedAggregationOperator) {
			expertsColectiveValuation = ((UnweightedAggregationOperator) operator).aggregate(valuations);
		} else if(operator instanceof WeightedAggregationOperator) {
			if(!_globalWeights.isEmpty()) {
				expertsColectiveValuation = ((WeightedAggregationOperator) operator).aggregate(valuations, _globalWeights);
			} else {
				expertsColectiveValuation = ((WeightedAggregationOperator) operator).aggregate(valuations, criterionWeights);
			}
		}

		_decisionMatrix[alternative][criterion] = expertsColectiveValuation;
	}
	
	@SuppressWarnings("unchecked")
	public List<String[]> calculateDistance() {
		List<String[]> result = new LinkedList<String[]>();
		
		Map<String, Double> square = calculateSquareValues();
		calculateAggregatedFuzzyNumber(square);
		calculateOverallOpinion(square);
		calculateValuationsDistance();
		
		Map<ValuationKey, Valuation> valuations = _valuationSet.getValuations();
		Map<ValuationKey, Valuation> confidencesValuations = getConfidencesValuations(valuations);
		for(ValuationKey vk: valuations.keySet()) {
			if(!valuations.get(vk).getDomain().getId().equals("confidence")) { //$NON-NLS-1$
				String[] data = new String[7];
				data[0] = vk.getExpert().getId();
				data[1] = vk.getAlternative().getId();
				data[2] = vk.getCriterion().getId();
				data[3] = valuations.get(vk).changeFormatValuationToString();
				Valuation aggregatedValuation = _decisionMatrix[_elementsSet.getAlternatives().indexOf(vk.getAlternative())][_elementsSet.getCriteria().indexOf(vk.getCriterion())];
				
				if(aggregatedValuation == null) {
					data[4] = ""; //$NON-NLS-1$
				} else {
					data[4] = aggregatedValuation.changeFormatValuationToString();
				}
					
				data[5] = Double.toString(Math.round(_distances.get(vk) * 10000d) / 10000d);
				
				if(confidencesValuations.isEmpty()) {
					data[6] = "0"; //$NON-NLS-1$
				} else {
					data[6] = Double.toString(((RealValuation) confidencesValuations.get(vk)).getValue());
				}
				
				result.add(data);
			}
		}
		
		Collections.sort(result, new DataComparator());
		
		return result;
	}

	private Map<ValuationKey, Valuation> getConfidencesValuations(Map<ValuationKey, Valuation> valuations) {
		Map<ValuationKey, Valuation> confidencesValuations = new HashMap<ValuationKey, Valuation>();
		for(ValuationKey vk: valuations.keySet()) {
			Valuation v = valuations.get(vk);
			if(v.getDomain().getId().equals("confidence")) { //$NON-NLS-1$
				confidencesValuations.put(vk, v);
			}
		}
		
		return confidencesValuations;
	}

	private Map<String, Double> calculateSquareValues() {
		Map<String, Double> result = new HashMap<String, Double>();
		int numExpert = 0;
		
		List<Expert> experts = _elementsSet.getAllExperts();
 		for(Expert expert: experts) {
			result.put(expert.getCanonicalId(), Math.pow(_globalWeights.get(numExpert), M));
			numExpert++;
		}
 		
 		return result;
	}
	
	private void calculateAggregatedFuzzyNumber(Map<String, Double> square) {
		
		double acumA = 0, acumB = 0, acumC = 0, acumD = 0;
		for(Alternative a: _elementsSet.getAlternatives()) {
			for(Criterion c: _elementsSet.getAllCriteria()) {
				acumA = 0;
				acumB = 0;
				acumC = 0;
				acumD = 0;
				for(ValuationKey vk: _valuationsInTwoTuple.keySet()) {
					if(a.equals(vk.getAlternative()) && c.equals(vk.getCriterion())) {
						TwoTuple v = (TwoTuple) _valuationsInTwoTuple.get(vk);
						LabelLinguisticDomain label = v.getLabel();
						TrapezoidalFunction semantic = (TrapezoidalFunction) label.getSemantic();
						
						double[] limits = semantic.getLimits();
						acumA += limits[0] * square.get(vk.getExpert().getId());
						acumB += limits[1] * square.get(vk.getExpert().getId());
						acumC += limits[2] * square.get(vk.getExpert().getId());
						acumD += limits[3] * square.get(vk.getExpert().getId());	
					}
				}
				
				double newLimits[] = new double[4];
				newLimits[0] = acumA;
				newLimits[1] = acumB;
				newLimits[2] = acumC;
				newLimits[3] = acumD;
				TrapezoidalFunction result = new TrapezoidalFunction(newLimits);
				
				Map<Criterion, TrapezoidalFunction> fuzzyNumberCriterion;
				if(_aggregatedFuzzyNumbers.get(a) !=  null) {
					fuzzyNumberCriterion = _aggregatedFuzzyNumbers.get(a);
				} else {
					fuzzyNumberCriterion = new HashMap<Criterion, TrapezoidalFunction>();
				}
				fuzzyNumberCriterion.put(c, result);
				_aggregatedFuzzyNumbers.put(a, fuzzyNumberCriterion);
			}
		}
	}
	
	private void calculateOverallOpinion(Map<String, Double> square) {
		
		double squareAcum = 0;
		for(String expert: square.keySet()) {
			squareAcum += square.get(expert);
		}

		for(Alternative a: _elementsSet.getAlternatives()) {
			Map<Criterion, TrapezoidalFunction> fuzzyNumberCriterion = _aggregatedFuzzyNumbers.get(a);
			for(Criterion c: _elementsSet.getAllCriteria()) {
				
				Map<Criterion, TrapezoidalFunction> newFuzzyNumberCriterion;
				if(_overallOpinions.get(a) != null) {
					newFuzzyNumberCriterion = _overallOpinions.get(a);
				} else {
					newFuzzyNumberCriterion = new HashMap<Criterion, TrapezoidalFunction>();
				}
				
				TrapezoidalFunction fuzzyNumber = fuzzyNumberCriterion.get(c);
				double newLimits[] = new double[4];
				double limits[] = fuzzyNumber.getLimits();
				newLimits[0] = limits[0] * squareAcum;
				newLimits[1]= limits[1] * squareAcum;
				newLimits[2] = limits[2] * squareAcum;
				newLimits[3] = limits[3] * squareAcum;
				
				
				TrapezoidalFunction newFuzzyNumber = new TrapezoidalFunction(newLimits);
				newFuzzyNumberCriterion.put(c, newFuzzyNumber);
				_overallOpinions.put(a, newFuzzyNumberCriterion);
			}
		}
		
	}
	
	private void calculateValuationsDistance() {
		
		for(Alternative a: _elementsSet.getAlternatives()) {
			for(Criterion c: _elementsSet.getAllCriteria()) {
				for(ValuationKey vk: _valuationsInTwoTuple.keySet()) {
					if(a.equals(vk.getAlternative()) && c.equals(vk.getCriterion())) {
						TwoTuple v = (TwoTuple) _valuationsInTwoTuple.get(vk);
						LabelLinguisticDomain label = v.getLabel();
						TrapezoidalFunction semantic = (TrapezoidalFunction) label.getSemantic();
						
						double[] limits = semantic.getLimits();
						double aLimit = limits[0];
						double bLimit = limits[1];
						double cLimit = limits[2];
						double dLimit = limits[3];
						
						double[] overallLimits = _overallOpinions.get(a).get(c).getLimits();
						double aOverallLimit = overallLimits[0];
						double bOverallLimit = overallLimits[1];
						double cOverallLimit = overallLimits[2];
						double dOverallLimit = overallLimits[3];
						
						double distance = Math.pow(aLimit - aOverallLimit, P) + Math.pow(bLimit - bOverallLimit, P) + 
								Math.pow(cLimit - cOverallLimit, P) + Math.pow(dLimit - dOverallLimit, P);
						
						_distances.put(vk, distance);
					}
				}
			}
		}
	}

	@Override
	public IPhaseMethod copyStructure() {
		return new AggregationPhase();
	}

	@Override
	public void copyData(IPhaseMethod iPhaseMethod) {
		AggregationPhase aggregation = (AggregationPhase) iPhaseMethod;
		
		clear();
		
		_decisionMatrix = aggregation.getDecisionMatrix();
		_valuationsInTwoTuple = aggregation.getValuationsTwoTuple();
		_criteriaWeights = aggregation.getExpertsWeights();
		_globalWeights = aggregation.getGlobalWeights();
		_aggregatedFuzzyNumbers = aggregation.getAggregatedFuzzyNumber();
		_overallOpinions = aggregation.getOverallOpinons();
		_distances = aggregation.getDistances();
	}

	@Override
	public boolean validate() {
		if(_valuationSet.getValuations().isEmpty()) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public void activate() {}

	@Override
	public void clear() {
		_decisionMatrix = new Valuation[_numAlternatives][_numCriteria];
		_valuationsInTwoTuple.clear();
		_criteriaWeights.clear();
		_globalWeights.clear();
		_aggregatedFuzzyNumbers.clear();
		_overallOpinions.clear();
		_distances.clear();
	}
	
	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		if (event.getChange().equals(EPhaseMethodStateChange.ACTIVATED)) {
			activate();
		}
	}
	
	@Override
	public IPhaseMethod clone() {
		AggregationPhase result = null;

		try {
			result = (AggregationPhase) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return result;
	}
}
