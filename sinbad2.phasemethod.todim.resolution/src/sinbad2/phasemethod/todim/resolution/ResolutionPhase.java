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
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class ResolutionPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.todim.resolution";
	
	private Map<ValuationKey, Valuation> _valuationsInTwoTuple;
	
	private double[][] _decisionMatrix;
	private int _numCriteria;
	private int _numAlternatives;
	
	private ValuationSet _valuationSet;
	private ProblemElementsSet _elementsSet;
	
	public ResolutionPhase() {
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = valuationSetManager.getActiveValuationSet();
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
	
		_valuationsInTwoTuple = new HashMap<ValuationKey, Valuation>();
	
		_numCriteria = _elementsSet.getCriteria().size();
		_numAlternatives = _elementsSet.getAlternatives().size();
		_decisionMatrix = new double[_numCriteria][_numAlternatives];
	}
	
	public void setDecisionMatrix(double[][] decisionMatrix) {
		_decisionMatrix = decisionMatrix;
	}
	
	public double[][] getDecisionMatrix() {
		return _decisionMatrix;
	}
	
	public void setValuationsTwoTuple(Map<ValuationKey, Valuation> valuationsInTwoTuple) {
		_valuationsInTwoTuple = valuationsInTwoTuple;
	}
	
	public Map<ValuationKey, Valuation> getValuationsTwoTuple() {
		return _valuationsInTwoTuple;
	}

	public double[][] calculateDecisionMatrix(AggregationOperator operator, List<Double> weights) {
		
		_decisionMatrix = new double[_numCriteria][_numAlternatives];
		
		for(int a = 0; a < _elementsSet.getAlternatives().size(); ++a) {
			for(int c = 0; c < _elementsSet.getAllCriteria().size(); ++c) {
				if(!_elementsSet.getCriteria().get(c).hasSubcriteria()) {
					aggregateExperts(a, c, operator, weights);
				}
			}
		}
		
		normalizeDecisionMatrix();
		
		return _decisionMatrix;
	}
	
	private void aggregateExperts(int alternative, int criterion, AggregationOperator operator, List<Double> weights) {
		
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
			expertsColectiveValuation = ((WeightedAggregationOperator) operator).aggregate(valuations, weights);
		}
		
		_decisionMatrix[criterion][alternative] = ((TwoTuple) expertsColectiveValuation).calculateInverseDelta();
	}
	
	private void normalizeDecisionMatrix() {
		
		if(!checkNormalizedMatrix()) {
			double acum, noStandarizedValue;
			for (int i = 0; i < _numCriteria; ++i) {
				acum = sumCriteria(i);
				for (int j = 0; j < _numAlternatives; ++j) {
					noStandarizedValue = _decisionMatrix[i][j];
					_decisionMatrix[i][j] = (double) Math.round((noStandarizedValue / acum) * 10000d) / 10000d;
				}
			}
		}
	}

	private boolean checkNormalizedMatrix() {
		
		for (int i = 0; i < _numCriteria; ++i) {
			for (int j = 0; j < _numAlternatives; ++j) {
				if(_decisionMatrix[i][j] > 1) {
					return false;
				}
			}
		}
		return true;
	}
	
	private double sumCriteria(int numCriterion) {
		double value = 0;
		for (int j = 0; j < _numAlternatives; ++j) {
			value += Math.pow(_decisionMatrix[numCriterion][j], 2);
		}
		return Math.sqrt(value);
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
