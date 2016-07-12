package sinbad2.phasemethod.todim.resolution;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.UnweightedAggregationOperator;
import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
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
		
		List<Double> globalWeights = new LinkedList<Double>();
		List<Double> criterionWeights = new LinkedList<Double>();
		if(weights.size() == 1) {
			globalWeights = weights.get(null);
		} else if(weights.size() > 1) {
			criterionWeights = weights.get(_elementsSet.getAllCriteria().get(criterion).getCanonicalId());
		}
		
		List<Valuation> valuations = new LinkedList<Valuation>();
		for(ValuationKey vk: _valuationsInTwoTuple.keySet()) {
			if(vk.getAlternative().equals(alternative) && vk.getCriterion().equals(criterion)) {
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
		_decisionMatrix = null;
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
