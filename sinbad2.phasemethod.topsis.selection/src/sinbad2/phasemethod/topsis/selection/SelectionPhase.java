package sinbad2.phasemethod.topsis.selection;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.AggregationOperatorsManager;
import sinbad2.aggregationoperator.UnweightedAggregationOperator;
import sinbad2.aggregationoperator.max.Max;
import sinbad2.aggregationoperator.min.Min;
import sinbad2.core.utils.Pair;
import sinbad2.domain.Domain;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.aggregation.AggregationPhase;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.valuationset.ValuationKey;

public class SelectionPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.topsis.selection"; //$NON-NLS-1$

	private Map<ValuationKey, Valuation> _twoTupleValuations;
	
	private Map<Pair<Alternative, Criterion>, Valuation> _decisionMatrix;

	private List<Object[]> _idealSolution;
	private List<Object[]> _noIdealSolution;

	private List<Object[]> _idealDistanceByCriteria;
	private List<Object[]> _noIdealDistanceByCriteria;

	private List<Object[]> _idealDistanceByAlternatives;
	private List<Object[]> _noIdealDistanceByAlternatives;

	private List<Object[]> _closenessCoefficient;

	private AggregationPhase _aggregationPhase;

	private ProblemElementsSet _elementsSet;

	private static class CoefficientComparator implements Comparator<Object[]> {
		public int compare(Object[] cd1, Object[] cd2) {
			double coefficient1 = (double) cd1[3];
			double coefficient2 = (double) cd2[3];

			return Double.compare(coefficient2, coefficient1);
		}
	}

	public SelectionPhase() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();

		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		_aggregationPhase = (AggregationPhase) pmm.getPhaseMethod(AggregationPhase.ID).getImplementation();

		_decisionMatrix = new HashMap<Pair<Alternative, Criterion>, Valuation>();
		_idealSolution = new LinkedList<Object[]>();
		_noIdealSolution = new LinkedList<Object[]>();
		_idealDistanceByCriteria = new LinkedList<Object[]>();
		_noIdealDistanceByCriteria = new LinkedList<Object[]>();
		_idealDistanceByAlternatives = new LinkedList<Object[]>();
		_noIdealDistanceByAlternatives = new LinkedList<Object[]>();
		_closenessCoefficient = new LinkedList<Object[]>();

		_twoTupleValuations = new HashMap<ValuationKey, Valuation>();
	}

	@Override
	public Map<ValuationKey, Valuation> getTwoTupleValuations() {
		return _twoTupleValuations;
	}

	public void setTwoTupleValuations(Map<ValuationKey, Valuation> twoTupleValuations) {
		_twoTupleValuations = twoTupleValuations;
	}
	
	public Map<Pair<Alternative, Criterion>, Valuation> getDecisionMatrix() {
		return _decisionMatrix;
	}

	public void setDecisionMatrix(Map<Pair<Alternative, Criterion>, Valuation> decisionMatrix) {
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

	public List<Object[]> getIdealDistanceByCriteria() {
		return _idealDistanceByCriteria;
	}

	public void setIdealDistanceByCriteria(List<Object[]> idealDistance) {
		_idealDistanceByCriteria = idealDistance;
	}

	public List<Object[]> getNoIdealDistanceByCriteria() {
		return _noIdealDistanceByCriteria;
	}

	public void setNoIdealDistanceByCriteria(List<Object[]> noIdealDistance) {
		_noIdealDistanceByCriteria = noIdealDistance;
	}

	public List<Object[]> getIdealDistanceByAlternatives() {
		return _idealDistanceByAlternatives;
	}

	public void setIdealDistanceByAlternatives(List<Object[]> idealDistance) {
		_idealDistanceByAlternatives = idealDistance;
	}

	public List<Object[]> getNoIdealDistanceByAlternatives() {
		return _noIdealDistanceByAlternatives;
	}

	public void setNoIdealDistanceByAlternatives(List<Object[]> noIdealDistance) {
		_noIdealDistanceByAlternatives = noIdealDistance;
	}

	public List<Object[]> getClosenessCoeficient() {
		return _closenessCoefficient;
	}

	public void setClosenessCoefficient(List<Object[]> closenessCoeficient) {
		_closenessCoefficient = closenessCoeficient;
	}

	public Map<Pair<Alternative, Criterion>, Valuation> calculateDecisionMatrix() {

		_decisionMatrix = _aggregationPhase.getDecisionMatrix();
		
		return _decisionMatrix;
	}
	
	@Override
	public Domain getUnifiedDomain() {
		return null;
	}

	public List<Object[]> calculateIdealSolution() {
		AggregationOperatorsManager aggregationOperatorManager = AggregationOperatorsManager.getInstance();
		AggregationOperator max = aggregationOperatorManager.getAggregationOperator(Max.ID);

		_idealSolution.clear();

		List<Valuation> valuationsByCriterion;
		for (Criterion c : _elementsSet.getAllCriteria()) {
			if (!c.hasSubcriteria()) {
				valuationsByCriterion = new LinkedList<Valuation>();
				for (Pair<Alternative, Criterion> pair : _decisionMatrix.keySet()) {
					Criterion decisionMatrixCriterion = pair.getRight();
					if (c.equals(decisionMatrixCriterion)) {
						valuationsByCriterion.add(_decisionMatrix.get(pair));
					}
				}
				Valuation idealSolutionValuation = ((UnweightedAggregationOperator) max).aggregate(valuationsByCriterion);
				Object[] idealSolutionData = new Object[2];
				idealSolutionData[0] = c;
				idealSolutionData[1] = idealSolutionValuation;
				_idealSolution.add(idealSolutionData);
			}
		}

		return _idealSolution;

	}

	public List<Object[]> calculateNoIdealSolution() {
		AggregationOperatorsManager aggregationOperatorManager = AggregationOperatorsManager.getInstance();
		AggregationOperator min = aggregationOperatorManager.getAggregationOperator(Min.ID);

		_noIdealSolution.clear();

		List<Valuation> valuationsByCriterion;
		for (Criterion c : _elementsSet.getAllCriteria()) {
			if (!c.hasSubcriteria()) {
				valuationsByCriterion = new LinkedList<Valuation>();
				for (Pair<Alternative, Criterion> pair : _decisionMatrix.keySet()) {
					Criterion decisionMatrixCriterion = pair.getRight();
					if (c.equals(decisionMatrixCriterion)) {
						valuationsByCriterion.add(_decisionMatrix.get(pair));
					}
				}

				Valuation noIdealSolutionValuation = ((UnweightedAggregationOperator) min).aggregate(valuationsByCriterion);
				Object[] noIdealSolutionData = new Object[2];
				noIdealSolutionData[0] = c;
				noIdealSolutionData[1] = noIdealSolutionValuation;
				_noIdealSolution.add(noIdealSolutionData);
			}
		}

		return _noIdealSolution;
	}

	public List<Object[]> calculateIdealEuclideanDistanceByCriterion(List<Double> weights) {
		double beta;
		int numWeight = 0;

		_idealDistanceByCriteria.clear();

		Alternative alternative = _elementsSet.getAlternatives().get(0);
		for (Pair<Alternative, Criterion> pair : _decisionMatrix.keySet()) {
			beta = 0;
			
			if (!alternative.equals(pair.getLeft())) {
				numWeight = 0;
			}
			
			TwoTuple colectiveExpertsValuation = (TwoTuple) ((TwoTuple) _decisionMatrix.get(pair)).clone();
			for (Object[] idealSolutionData : _idealSolution) {
				if (pair.getRight().equals(idealSolutionData[0])) {
					TwoTuple idealValuation = (TwoTuple) ((TwoTuple) idealSolutionData[1]).clone();

					if (weights == null) {
						beta = Math.abs(colectiveExpertsValuation.calculateInverseDelta() - idealValuation.calculateInverseDelta());
					} else {
						beta = Math.abs(colectiveExpertsValuation.calculateInverseDelta() - idealValuation.calculateInverseDelta()) * weights.get(numWeight);
						numWeight++;
					}

					Object[] dataDistance = new Object[3];
					dataDistance[0] = pair.getLeft();
					dataDistance[1] = pair.getRight();
					dataDistance[2] = beta;
					_idealDistanceByCriteria.add(dataDistance);
				}
			}
		}

		calculateIdealEuclideanDistanceByAlternatives(weights);

		return _idealDistanceByCriteria;
	}

	private void calculateIdealEuclideanDistanceByAlternatives(List<Double> weights) {
		double beta;
		int numWeight;

		_idealDistanceByAlternatives.clear();

		for (Alternative a : _elementsSet.getAlternatives()) {
			beta = 0;
			numWeight = 0;
			for (Pair<Alternative, Criterion> pair : _decisionMatrix.keySet()) {
				if (pair.getLeft().equals(a)) {
					for (Object[] idealSolutionData : _idealSolution) {
						if (pair.getRight().equals(idealSolutionData[0])) {
							TwoTuple expertsColectiveValuation = (TwoTuple) ((TwoTuple) _decisionMatrix.get(pair)).clone();
							TwoTuple idealSolutionValuation = (TwoTuple) ((TwoTuple) idealSolutionData[1]).clone();
							if (weights == null) {
								beta += Math.abs(expertsColectiveValuation.calculateInverseDelta() - idealSolutionValuation.calculateInverseDelta());
							} else {
								beta += Math.abs(expertsColectiveValuation.calculateInverseDelta() - idealSolutionValuation.calculateInverseDelta())
										* weights.get(numWeight);
								numWeight++;
							}
						}
					}
				}
			}
			Object[] dataDistance = new Object[2];
			dataDistance[0] = a;
			dataDistance[1] = beta;
			_idealDistanceByAlternatives.add(dataDistance);
		}
	}

	public List<Object[]> calculateNoIdealEuclideanDistanceByCriterion(List<Double> weights) {
		double beta;
		int numWeight = 0;

		_noIdealDistanceByCriteria.clear();

		Alternative alternative = _elementsSet.getAlternatives().get(0);
		for (Pair<Alternative, Criterion> pair : _decisionMatrix.keySet()) {
			beta = 0;
			if (!alternative.equals(pair.getLeft())) {
				numWeight = 0;
			}
			TwoTuple colectiveExpertsValuation = (TwoTuple) ((TwoTuple) _decisionMatrix.get(pair)).clone();
			for (Object[] noIdealSolutionData : _noIdealSolution) {
				if (pair.getRight().equals(noIdealSolutionData[0])) {
					TwoTuple noIdealValuation = (TwoTuple) ((TwoTuple) noIdealSolutionData[1]).clone();

					if (weights == null) {
						beta = Math.abs(colectiveExpertsValuation.calculateInverseDelta()
								- noIdealValuation.calculateInverseDelta());
					} else {
						beta = Math.abs(colectiveExpertsValuation.calculateInverseDelta()
								- noIdealValuation.calculateInverseDelta()) * weights.get(numWeight);
						numWeight++;
					}

					Object[] dataDistance = new Object[3];
					dataDistance[0] = pair.getLeft();
					dataDistance[1] = pair.getRight();
					dataDistance[2] = beta;
					_noIdealDistanceByCriteria.add(dataDistance);
				}
			}
		}

		calculateNoIdealEuclideanDistanceByAlternatives(weights);

		return _noIdealDistanceByCriteria;
	}

	private void calculateNoIdealEuclideanDistanceByAlternatives(List<Double> weights) {
		double beta;
		int numWeight;

		_noIdealDistanceByAlternatives.clear();

		for (Alternative a : _elementsSet.getAlternatives()) {
			beta = 0;
			numWeight = 0;
			for (Pair<Alternative, Criterion> pair : _decisionMatrix.keySet()) {
				if (pair.getLeft().equals(a)) {
					for (Object[] noIdealSolutionData : _noIdealSolution) {
						if (pair.getRight().equals(noIdealSolutionData[0])) {
							TwoTuple expertsColectiveValuation = (TwoTuple) ((TwoTuple) _decisionMatrix.get(pair))
									.clone();
							TwoTuple noIdealSolutionValuation = (TwoTuple) ((TwoTuple) noIdealSolutionData[1]).clone();
							if (weights == null) {
								beta += Math.abs(expertsColectiveValuation.calculateInverseDelta()
										- noIdealSolutionValuation.calculateInverseDelta());
							} else {
								beta += Math
										.abs(expertsColectiveValuation.calculateInverseDelta()
												- noIdealSolutionValuation.calculateInverseDelta())
										* weights.get(numWeight);
								numWeight++;
							}
						}
					}
				}
			}
			Object[] dataDistance = new Object[2];
			dataDistance[0] = a;
			dataDistance[1] = beta;
			_noIdealDistanceByAlternatives.add(dataDistance);
		}

	}

	public List<Object[]> calculateClosenessCoefficient() {

		_closenessCoefficient.clear();

		for (int i = 0; i < _idealDistanceByAlternatives.size(); ++i) {
			Object[] dataIdealAlternative = _idealDistanceByAlternatives.get(i);
			Object[] dataNoIdealAlternative = _noIdealDistanceByAlternatives.get(i);
			double ideal = (double) dataIdealAlternative[1];
			double noIdeal = (double) dataNoIdealAlternative[1];

			double coefficient = noIdeal / (ideal + noIdeal);

			Object[] coefficientData = new Object[5];
			coefficientData[1] = dataIdealAlternative[0];
			coefficientData[2] = dataIdealAlternative[1];
			coefficientData[3] = dataNoIdealAlternative[1];
			coefficientData[4] = coefficient;
			_closenessCoefficient.add(coefficientData);
		}

		Collections.sort(_closenessCoefficient, new CoefficientComparator());

		int ranking = 1;
		for (Object[] coefficientData : _closenessCoefficient) {
			coefficientData[0] = Integer.toString(ranking);
			ranking++;
		}

		return _closenessCoefficient;
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
		_twoTupleValuations = selectionPhase.getTwoTupleValuations();
		_idealSolution = selectionPhase.getIdealSolution();
		_noIdealSolution = selectionPhase.getNoIdealSolution();
		_idealDistanceByCriteria = selectionPhase.getIdealDistanceByCriteria();
		_noIdealDistanceByCriteria = selectionPhase.getNoIdealDistanceByCriteria();
		_idealDistanceByAlternatives = selectionPhase.getIdealDistanceByAlternatives();
		_noIdealDistanceByAlternatives = selectionPhase.getNoIdealDistanceByAlternatives();
		_closenessCoefficient = selectionPhase.getClosenessCoeficient();
	}

	@Override
	public void activate() {
	}

	@Override
	public boolean validate() {

		if (_elementsSet.getExperts().isEmpty()) {
			return false;
		}

		if (_elementsSet.getAlternatives().isEmpty()) {
			return false;
		}

		if (_elementsSet.getCriteria().isEmpty()) {
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
		_twoTupleValuations.clear();
		_idealSolution.clear();
		_noIdealSolution.clear();
		_idealDistanceByCriteria.clear();
		_noIdealDistanceByCriteria.clear();
		_idealDistanceByAlternatives.clear();
		_noIdealDistanceByAlternatives.clear();
		_closenessCoefficient.clear();
	}

	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		if (event.getChange().equals(EPhaseMethodStateChange.ACTIVATED)) {
			activate();
		}
	}
}
