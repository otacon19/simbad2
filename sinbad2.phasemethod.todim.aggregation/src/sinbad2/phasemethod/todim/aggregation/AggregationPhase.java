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
	
	private Map<ValuationKey, Valuation> _valuationsInTwoTuple;
	
	private Valuation[][] _decisionMatrix;
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
		
		calculateValuationsDistance();
		
		Map<ValuationKey, Valuation> valuations = _valuationSet.getValuations();
		
		for(ValuationKey vk: _valuationsInTwoTuple.keySet()) {
			if(!vk.getExpert().getId().endsWith("fgc")) { //$NON-NLS-1$
				String[] data = new String[7];
				data[0] = vk.getExpert().getId();
				data[1] = vk.getAlternative().getId();
				data[2] = vk.getCriterion().getId();
				data[3] = _valuationsInTwoTuple.get(vk).changeFormatValuationToString();
				Valuation aggregatedValuation = _decisionMatrix[_elementsSet.getAlternatives().indexOf(vk.getAlternative())][_elementsSet.getCriteria().indexOf(vk.getCriterion())];
				
				if(aggregatedValuation == null) {
					data[4] = ""; //$NON-NLS-1$
				} else {
					data[4] = aggregatedValuation.changeFormatValuationToString();
				}
					
				data[5] = Double.toString(Math.round(_distances.get(vk) * 10000d) / 10000d);
		
				Expert e = new Expert();
				e.setId(vk.getExpert().getId() + "fgc");
				ValuationKey vkFGC = new ValuationKey(e, vk.getAlternative(), vk.getCriterion());
					
				data[6] = Double.toString(((RealValuation) valuations.get(vkFGC)).getValue());
				
				result.add(data);
			}
		}
		
		Collections.sort(result, new DataComparator());
		
		return result;
	}
	
	private void calculateValuationsDistance() {
		List<Alternative> alternatives = _elementsSet.getAlternatives();
		List<Criterion> criteria = _elementsSet.getAllCriteria();
		
		for(int a = 0; a < alternatives.size(); ++a) {
			for(int c = 0; c < criteria.size(); ++c) {
				for(ValuationKey vk: _valuationsInTwoTuple.keySet()) {
					if(alternatives.get(a).equals(vk.getAlternative()) && criteria.get(c).equals(vk.getCriterion())) {
						TwoTuple v = (TwoTuple) _valuationsInTwoTuple.get(vk);
						LabelLinguisticDomain label = v.getLabel();
						TrapezoidalFunction semantic = (TrapezoidalFunction) label.getSemantic();
		
						double[] limits = semantic.getLimits();
						double aLimit = limits[0];
						double bLimit = limits[1];
						double cLimit = limits[2];
						double dLimit = limits[3];
						
						TwoTuple overallValuation =  (TwoTuple) _decisionMatrix[a][c];
						double[] overallLimits = ((TrapezoidalFunction) overallValuation.getLabel().getSemantic()).getLimits();
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
