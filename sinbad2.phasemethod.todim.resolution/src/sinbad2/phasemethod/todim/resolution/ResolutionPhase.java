package sinbad2.phasemethod.todim.resolution;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.UnweightedAggregationOperator;
import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class ResolutionPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.todim.resolution";
	
	private Map<ValuationKey, Valuation> _valuationsInTwoTuple;
	
	private Valuation[][] _decisionMatrix;
	
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
	
	public ResolutionPhase() {
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = valuationSetManager.getActiveValuationSet();
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
	
		_valuationsInTwoTuple = new HashMap<ValuationKey, Valuation>();

		_numAlternatives = _elementsSet.getAlternatives().size();
		_numCriteria = _elementsSet.getCriteria().size();
		_decisionMatrix = new Valuation[_numAlternatives][_numCriteria];
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

	public Valuation[][] calculateDecisionMatrix(AggregationOperator operator, Map<String, List<Double>> weights) {
		
		_decisionMatrix = new Valuation[_numAlternatives][_numCriteria];
		
		for(int a = 0; a < _elementsSet.getAlternatives().size(); ++a) {
			for(int c = 0; c < _elementsSet.getAllCriteria().size(); ++c) {
				if(!_elementsSet.getCriteria().get(c).hasSubcriteria()) {
					aggregateExperts(a, c, operator, weights);
				}
			}
		}
		
		return _decisionMatrix;
	}
	
	private void aggregateExperts(int alternative, int criterion, AggregationOperator operator, Map<String, List<Double>> weights) {
		List<Alternative> alternatives = _elementsSet.getAlternatives();
		List<Criterion> criteria = _elementsSet.getCriteria();
		
		List<Double> globalWeights = new LinkedList<Double>();
		List<Double> criterionWeights = new LinkedList<Double>();
		if(weights.size() == 1) {
			globalWeights = weights.get(null);
		} else if(weights.size() > 1) {
			criterionWeights = weights.get(_elementsSet.getAllCriteria().get(criterion).getCanonicalId());
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
			if(!globalWeights.isEmpty()) {
				expertsColectiveValuation = ((WeightedAggregationOperator) operator).aggregate(valuations, globalWeights);
			} else {
				expertsColectiveValuation = ((WeightedAggregationOperator) operator).aggregate(valuations, criterionWeights);
			}
		}

		_decisionMatrix[alternative][criterion] = expertsColectiveValuation;
	}
	
	@SuppressWarnings("unchecked")
	public List<String[]> calculateDistance() {
		List<String[]> result = new LinkedList<String[]>();
		
		Map<ValuationKey, Valuation> valuations = _valuationSet.getValuations();
		for(ValuationKey vk: valuations.keySet()) {
			String[] data = new String[7];
			data[0] = vk.getExpert().getId();
			data[1] = vk.getAlternative().getId();
			data[2] = vk.getCriterion().getId();
			data[3] = valuations.get(vk).changeFormatValuationToString();
			Valuation aggregatedValuation = _decisionMatrix[_elementsSet.getAlternatives().indexOf(vk.getAlternative())]
					[_elementsSet.getCriteria().indexOf(vk.getCriterion())];
			
			if(aggregatedValuation == null) {
				data[4] = "";
			} else {
				data[4] = aggregatedValuation.changeFormatValuationToString();
			}
				
			data[5] = "";
			data[6] = "";
			
			result.add(data);
		}
		
		Collections.sort(result, new DataComparator());
		
		return result;
	}
	
	@Override
	public IPhaseMethod copyStructure() {
		return new ResolutionPhase();
	}

	@Override
	public void copyData(IPhaseMethod iPhaseMethod) {
		ResolutionPhase resolution = (ResolutionPhase) iPhaseMethod;
		
		clear();
		
		_decisionMatrix = resolution.getDecisionMatrix();
		_valuationsInTwoTuple = resolution.getValuationsTwoTuple();
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
	}
	
	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		if (event.getChange().equals(EPhaseMethodStateChange.ACTIVATED)) {
			activate();
		}
	}
	
	@Override
	public IPhaseMethod clone() {
		ResolutionPhase result = null;

		try {
			result = (ResolutionPhase) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return result;
	}
}
