package sinbad2.phasemethod.topsis.selection;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.AggregationOperatorsManager;
import sinbad2.aggregationoperator.UnweightedAggregationOperator;
import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.aggregationoperator.max.Max;
import sinbad2.aggregationoperator.min.Min;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.valuationset.ValuationKey;

public class SelectionPhase implements IPhaseMethod {
	
	public static final String ID = "flintstones.phasemethod.topsis.selection";
	
	private Map<ValuationKey, Valuation> _valuationsInTwoTuple;
	
	private List<Object[]> _decisionMatrix;
	private List<Object[]> _idealSolution;
	private List<Object[]> _noIdealSolution;
	
	private ProblemElementsSet _elementsSet;
	
	public SelectionPhase() {		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		_decisionMatrix = new LinkedList<Object[]>();
		_idealSolution = new LinkedList<Object[]>();
		_noIdealSolution = new LinkedList<Object[]>();
		
		_valuationsInTwoTuple = new HashMap<ValuationKey, Valuation>();
	}

	public Map<ValuationKey, Valuation> getUnificationValues() {
		return _valuationsInTwoTuple;
	}
	
	public void setUnificationValues(Map<ValuationKey, Valuation> valuationsInTwoTuple) {
		_valuationsInTwoTuple = valuationsInTwoTuple;
	}
	
	public List<Object[]> getDecisionMatrix() {
		return _decisionMatrix;
	}
	
	public void setDecisionMatrix(List<Object[]> decisionMatrix) {
		_decisionMatrix = decisionMatrix;
	}
	
	public List<Object[]> getIdealSolution() {
		return _idealSolution;
	}
	
	public void setIdealSolution(List<Object[]> idealSolution) {
		_idealSolution = idealSolution;
	}
	
	public List<Object[]> getNoIdealSolution() {
		return _noIdealSolution;
	}
	
	public void setNoIdealSolution(List<Object[]> noIdealSolution) {
		_noIdealSolution = noIdealSolution;
	}
	
	
	public List<Object[]> calculateDecisionMatrix(AggregationOperator operator, List<Double> weights) {
		
		_decisionMatrix.clear();
		
		for(Alternative a: _elementsSet.getAlternatives()) {
			for(Criterion c: _elementsSet.getAllCriteria()) {
				aggregateExperts(a, c, operator, weights);
			}
		}
		
		return _decisionMatrix;
	}
	
	private void aggregateExperts(Alternative alternative, Criterion criterion, AggregationOperator operator, List<Double> weights) {
		List<Valuation> valuations = new LinkedList<Valuation>();
		for(ValuationKey vk: _valuationsInTwoTuple.keySet()) {
			if(vk.getAlternative().equals(alternative) && vk.getCriterion().equals(criterion)) {
				valuations.add(_valuationsInTwoTuple.get(vk));
			}
		}
		
		Valuation compositeValuation = null;
		if(operator instanceof UnweightedAggregationOperator) {
			compositeValuation = ((UnweightedAggregationOperator) operator).aggregate(valuations);
		} else if(operator instanceof WeightedAggregationOperator) {
			compositeValuation = ((WeightedAggregationOperator) operator).aggregate(valuations, weights);
		}
		
		Object[] data = new Object[3];
		data[0] = alternative;
		data[1] = criterion;
		data[2] = compositeValuation;
		
		_decisionMatrix.add(data);
	}
	
	public List<Object[]> calculateIdealSolution() {
		AggregationOperatorsManager aggregationOperatorManager = AggregationOperatorsManager.getInstance();
		AggregationOperator max = aggregationOperatorManager.getAggregationOperator(Max.ID);
		
		_idealSolution.clear();
		
		List<Valuation> valuations;
		List<Criterion> criteria = new LinkedList<Criterion>();
		for(Object[] data: _decisionMatrix) {
			Criterion c = (Criterion) data[1];
			if(!criteria.contains(c)) {
				valuations = new LinkedList<Valuation>();
				criteria.add(c);
				valuations.add((Valuation) data[2]);
				for(Object[] data2: _decisionMatrix) {
					if(c.equals(data2[1]) && !data.equals(data2)) {
						valuations.add((Valuation) data2[2]);
					}
				}	
				
				if(!valuations.isEmpty()) {
					Object[] values = new Object[2];
					values[0] = c;
					values[1] = ((UnweightedAggregationOperator) max).aggregate(valuations);
					
					_idealSolution.add(values);
				}
			}
		}
		
		return _idealSolution;
		
	}

	public List<Object[]> calculateNoIdealSolution() {
		AggregationOperatorsManager aggregationOperatorManager = AggregationOperatorsManager.getInstance();
		AggregationOperator min = aggregationOperatorManager.getAggregationOperator(Min.ID);
		
		_idealSolution.clear();
		
		List<Valuation> valuations;
		List<Criterion> criteria = new LinkedList<Criterion>();
		for(Object[] data: _decisionMatrix) {
			Criterion c = (Criterion) data[1];
			if(!criteria.contains(c)) {
				valuations = new LinkedList<Valuation>();
				criteria.add(c);
				valuations.add((Valuation) data[2]);
				for(Object[] data2: _decisionMatrix) {
					if(c.equals(data2[1]) && !data.equals(data2)) {
						valuations.add((Valuation) data2[2]);
					}
				}	
				
				if(!valuations.isEmpty()) {
					Object[] values = new Object[2];
					values[0] = c;
					values[1] = ((UnweightedAggregationOperator) min).aggregate(valuations);
					
					_idealSolution.add(values);
				}
			}
		}
		
		return _idealSolution;
		
	}
	
	
	@Override
	public IPhaseMethod copyStructure() {
		return new SelectionPhase();
	}

	@Override
	public void copyData(IPhaseMethod iPhaseMethod) {
		SelectionPhase selectionPhase = (SelectionPhase) iPhaseMethod;
		
		clear();
		
		_decisionMatrix = selectionPhase.getDecisionMatrix();
		_valuationsInTwoTuple = selectionPhase.getUnificationValues();
		_idealSolution = selectionPhase.getIdealSolution();
		_noIdealSolution = selectionPhase.getNoIdealSolution();
	}

	@Override
	public void activate() {}

	@Override
	public boolean validate() {
		
		if (_elementsSet.getExperts().isEmpty()) {
			return false;
		}
		
		if(_elementsSet.getAlternatives().isEmpty()) {
			return false;
		}
		
		if(_elementsSet.getCriteria().isEmpty()) {
			return false;
		}

		return true;
	}
	
	@Override
	public IPhaseMethod clone() {
		SelectionPhase result = null;

		try {
			result = (SelectionPhase) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	@Override
	public void clear() {
		_decisionMatrix.clear();
		_valuationsInTwoTuple.clear();
		_idealSolution.clear();
		_noIdealSolution.clear();
	}

	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		if(event.getChange().equals(EPhaseMethodStateChange.ACTIVATED)) {
			activate();
		}
	}
}
