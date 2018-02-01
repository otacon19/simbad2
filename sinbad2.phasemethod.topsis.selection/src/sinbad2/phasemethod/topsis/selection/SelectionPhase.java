package sinbad2.phasemethod.topsis.selection;

import java.util.ArrayList;
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
import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.phasemethod.topsis.unification.UnificationPhase;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.valuationset.ValuationKey;

public class SelectionPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.topsis.selection"; //$NON-NLS-1$

	private Map<Expert, Valuation[][]> _decisionMatricesExperts;
	private Valuation[][] _decisionMatrix;

	private List<TwoTuple> _idealSolution;
	private List<TwoTuple> _noIdealSolution;

	private List<TwoTuple> _idealDistance;
	private List<TwoTuple> _noIdealDistance;

	private List<TwoTuple> _closenessCoefficient;
	
	private Map<Expert, LabelLinguisticDomain[]> _weightsExperts;
	private TwoTuple[] _collectiveWeights;
	
	private FuzzySet _unificationDomain;
	private FuzzySet _distanceDomain;
	private FuzzySet _similarityDomain;
	private FuzzySet _weightsDomain;

	private ProblemElementsSet _elementsSet;

	private static class RankingComparator implements Comparator<TwoTuple> {
		public int compare(TwoTuple cd1, TwoTuple cd2) {
			return cd1.compareTo(cd2);
		}
	}

	public SelectionPhase() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		_idealSolution = new LinkedList<TwoTuple>();
		_noIdealSolution = new LinkedList<TwoTuple>();
		
		_idealDistance = new LinkedList<TwoTuple>();
		_noIdealDistance = new LinkedList<TwoTuple>();
		
		_closenessCoefficient = new LinkedList<TwoTuple>();
		
		_distanceDomain = new FuzzySet();
		createDistanceLabels();
		
		_similarityDomain = new FuzzySet();
		createSimilarityLabels();
		
		_weightsDomain = new FuzzySet();
		createWeightsLabels();
		
		_weightsExperts = new HashMap<>();
		initializeWeightsExperts();
	}

	private void createDistanceLabels() {
		String[] labels = new String[]{"Equal", "Almost equal", "A bit close", "Neither close nor far", "A bit far", "Far", "Far away"};
		_distanceDomain.createTrapezoidalFunction(labels);
	}
	
	private void createSimilarityLabels() {
		String[] labels = new String[]{"Total dissimilar", "Almost total dissimilar", "A bit dissimilar", 
				"Neither dissimilar nor dissimilar", "A bit similar", "Almost similar", "Completely similar"};
		_similarityDomain.createTrapezoidalFunction(labels);
	}
	
	private void createWeightsLabels() {
		String[] labels = new String[]{"Very low", "Low", "Medium low", 
				"Medium", "Medium high", "High", "Very high"};
		_weightsDomain.createTrapezoidalFunction(labels);
	}

	private void initializeWeightsExperts() {
		for(Expert e: _elementsSet.getOnlyExpertChildren()) {
			LabelLinguisticDomain[] weights = new LabelLinguisticDomain[_elementsSet.getAllSubcriteria().size()];
			for(int i = 0; i < weights.length; ++i) {
				weights[i] = _weightsDomain.getLabelSet().getLabel((_weightsDomain.getLabelSet().getCardinality() - 1) / 2);
			}
			_weightsExperts.put(e, weights);
		}
	}
	
	@Override
	public Map<ValuationKey, Valuation> getTwoTupleValuations() {
		return new HashMap<ValuationKey, Valuation>();
	}
	
	public Valuation[][] getDecisionMatrix() {
		return _decisionMatrix;
	}

	public void setDecisionMatrix(TwoTuple[][] decisionMatrix) {
		_decisionMatrix = decisionMatrix;
	}

	public List<TwoTuple> getIdealSolution() {
		return _idealSolution;
	}

	public void setIdealSolution(List<TwoTuple> idealSolution) {
		_idealSolution = idealSolution;
	}

	public List<TwoTuple> getNoIdealSolution() {
		return _noIdealSolution;
	}

	public void setNoIdealSolution(List<TwoTuple> noIdealSolution) {
		_noIdealSolution = noIdealSolution;
	}

	public List<TwoTuple> getIdealDistances() {
		return _idealDistance;
	}

	public void setIdealDistanceByAlternatives(List<TwoTuple> idealDistance) {
		_idealDistance = idealDistance;
	}

	public List<TwoTuple> getNoIdealDistances() {
		return _noIdealDistance;
	}

	public void setNoIdealDistanceByAlternatives(List<TwoTuple> noIdealDistance) {
		_noIdealDistance = noIdealDistance;
	}

	public List<TwoTuple> getClosenessCoeficient() {
		return _closenessCoefficient;
	}

	public void setClosenessCoefficient(List<TwoTuple> closenessCoeficient) {
		_closenessCoefficient = closenessCoeficient;
	}

	@Override
	public Domain getUnifiedDomain() {
		return _unificationDomain;
	}
	
	@Override
	public void setUnifiedDomain(Domain domain) {
		_unificationDomain = (FuzzySet) domain;
	}
	
	public FuzzySet getDistanceDomain() {
		return _distanceDomain;
	}
	
	public void setDistanceDomain(FuzzySet domain) {
		_distanceDomain = domain;
	}
	
	public FuzzySet getSimilarityDomain() {
		return _similarityDomain;
	}
	
	public void setSimilarityDomain(FuzzySet domain) {
		_similarityDomain = domain;
	}
	
	public FuzzySet getWeightsDomain() {
		return _weightsDomain;
	}
	
	public void setWeightsDomain(FuzzySet domain) {
		_weightsDomain = domain;
	}
	
	public Map<Expert, LabelLinguisticDomain[]> getExpertsWeights() {
		return _weightsExperts;
	}
	
	public void setExpertsWeights(Map<Expert, LabelLinguisticDomain[]> weightsExperts) {
		_weightsExperts = weightsExperts;
	}
	
	public LabelLinguisticDomain getExpertWeight(Expert e, int crit) {
		return _weightsExperts.get(e)[crit];
	}

	public void setExpertWeight(Expert e, int crit, LabelLinguisticDomain weight) {
		LabelLinguisticDomain[] weights = _weightsExperts.get(e);
		weights[crit] = weight;
		_weightsExperts.put(e, weights);
	}
	
	private void calculateDecisionMatrix() {
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		UnificationPhase unificationPhase = (UnificationPhase) pmm.getPhaseMethod(UnificationPhase.ID).getImplementation();
		
		_unificationDomain = (FuzzySet) unificationPhase.getUnifiedDomain();

		computeWeights();
		computeDecisionMatricesExperts(unificationPhase);
		
		_decisionMatrix = new Valuation[_elementsSet.getAllSubcriteria().size()][_elementsSet.getAlternatives().size()];
		
		double acum = 0;
		for(int i = 0; i < _elementsSet.getAllSubcriteria().size(); ++i) {
			for(int j = 0; j < _elementsSet.getAlternatives().size(); ++j) {
				for(Expert e: _decisionMatricesExperts.keySet()) {
					Valuation[][] dm = _decisionMatricesExperts.get(e);
					acum += ((TwoTuple) dm[i][j]).calculateInverseDelta();
				}
				TwoTuple collective = new TwoTuple(_unificationDomain);
				 collective.calculateDelta(acum / _decisionMatricesExperts.size());
				_decisionMatrix[i][j] = collective;
				acum = 0;
			}
		}
		
		computeWeigthedDecisionMatrix();
	}

	private void computeWeights() {
		Map<Expert, TwoTuple[]> weightsTwoTuple = transformWeightsToTwoTuple();
		computeCollectiveWeights(weightsTwoTuple);	
	}

	private Map<Expert, TwoTuple[]> transformWeightsToTwoTuple() {
		Map<Expert, TwoTuple[]> result = new HashMap<>();
		for(Expert e: _weightsExperts.keySet()) {
			LabelLinguisticDomain[] weights = _weightsExperts.get(e);
			TwoTuple[] weights2T = new TwoTuple[weights.length];
			for(int i = 0; i < weights.length; ++i) {
				weights2T[i] = new TwoTuple(_weightsDomain, weights[i]);
			}
			result.put(e, weights2T);
		}
		return result;
	}
	
	private void computeCollectiveWeights(Map<Expert, TwoTuple[]> weightsTwoTuple) {
		_collectiveWeights = new TwoTuple[_elementsSet.getAllSubcriteria().size()];
		double acum;
		for(int i = 0; i < _elementsSet.getAllSubcriteria().size(); ++i) {
			acum = 0;
			for(Expert e: weightsTwoTuple.keySet()) {
				acum += weightsTwoTuple.get(e)[i].calculateInverseDelta();
			}
			TwoTuple weight = new TwoTuple(_weightsDomain);
			weight.calculateDelta(acum / weightsTwoTuple.size());
			_collectiveWeights[i] = weight;
		}
	}

	/**
	 * Creation of experts' decision matrices
	 * @param unificationPhase
	 * 			TOPSIS unification phase
	 */
	private void computeDecisionMatricesExperts(UnificationPhase unificationPhase) {
		_decisionMatricesExperts = new HashMap<Expert, Valuation[][]>();
		
		List<Expert> experts = _elementsSet.getOnlyExpertChildren();
		List<Criterion> criteria = _elementsSet.getAllSubcriteria();
		List<Alternative> alternatives = _elementsSet.getAlternatives();
		for(Expert e: experts) {
			Valuation[][] dm = new Valuation[criteria.size()][alternatives.size()]; 
			for(Criterion c: criteria) {
				for(Alternative a: alternatives) {
					for(ValuationKey vk: unificationPhase.getTwoTupleValuations().keySet()) {
						if(vk.getExpert().equals(e) && vk.getCriterion().equals(c) && vk.getAlternative().equals(a)){
							System.out.println(vk.getExpert().getId() + " - " + vk.getAlternative() + " - " + vk.getCriterion().getId() + 
									" - " + ((TwoTuple) unificationPhase.getTwoTupleValuations().get(vk)).prettyFormat());
							dm[criteria.indexOf(c)][alternatives.indexOf(a)] = unificationPhase.getTwoTupleValuations().get(vk);
						}
					}
				}
			}
			_decisionMatricesExperts.put(e, dm);
		}
	}
	
	private void computeWeigthedDecisionMatrix() {
		/*for(int i = 0; i < _decisionMatrix.length; ++i) {
			for(int j = 0; j < _decisionMatrix[i].length; ++j) {
				TwoTuple v = (TwoTuple) _decisionMatrix[i][j];
				TwoTuple weight = _collectiveWeights[i];
				TwoTuple result = new TwoTuple(_unificationDomain);
				result.calculateDelta(v.calculateInverseDelta() * weight.calculateInverseDelta());
				_decisionMatrix[i][j] = result;
			}
		}*/
	}

	private void calculateIdealSolution() {
		
		_idealSolution.clear();
		
		AggregationOperatorsManager aggregationOperatorManager = AggregationOperatorsManager.getInstance();
		AggregationOperator max = aggregationOperatorManager.getAggregationOperator(Max.ID);
		AggregationOperator min = aggregationOperatorManager.getAggregationOperator(Min.ID);
		
		Valuation idealSolutionValuation = null;
		Criterion cri;
		
		List<Valuation> valuationsByCriterion = new ArrayList<>();
		for(int i = 0; i < _decisionMatrix.length; ++i) {
			cri = _elementsSet.getAllSubcriteria().get(i);
			for(int j = 0; j < _decisionMatrix[i].length; ++j) {
				valuationsByCriterion.add(_decisionMatrix[i][j]);
			}
			
			if(cri.isCost()) {
				idealSolutionValuation = ((UnweightedAggregationOperator) min).aggregate(valuationsByCriterion);
			} else {
				idealSolutionValuation = ((UnweightedAggregationOperator) max).aggregate(valuationsByCriterion);
			}
			_idealSolution.add((TwoTuple) idealSolutionValuation);
		}
	}

	private void calculateNoIdealSolution() {

		_noIdealSolution.clear();
		
		AggregationOperatorsManager aggregationOperatorManager = AggregationOperatorsManager.getInstance();
		AggregationOperator max = aggregationOperatorManager.getAggregationOperator(Max.ID);
		AggregationOperator min = aggregationOperatorManager.getAggregationOperator(Min.ID);
		
		Valuation noIdealSolutionValuation = null;
		Criterion cri;
		
		List<Valuation> valuationsByCriterion = new ArrayList<>();
		for(int i = 0; i < _decisionMatrix.length; ++i) {
			cri = _elementsSet.getAllSubcriteria().get(i);
			for(int j = 0; j < _decisionMatrix[i].length; ++j) {
				valuationsByCriterion.add(_decisionMatrix[i][j]);
			}
			
			if(cri.isCost()) {
				noIdealSolutionValuation = ((UnweightedAggregationOperator) max).aggregate(valuationsByCriterion);
			} else {
				noIdealSolutionValuation = ((UnweightedAggregationOperator) min).aggregate(valuationsByCriterion);
			}
			_noIdealSolution.add((TwoTuple) noIdealSolutionValuation);
		}
	}

	private void calculateIdealEuclideanDistance() {
		_idealDistance.clear();

		TwoTuple collective, idealSolution;
		TwoTuple distance = null;
		
		int t = _unificationDomain.getLabelSet().getCardinality();
		int t_prima = _similarityDomain.getLabelSet().getCardinality();
		int t_prima_prima = _distanceDomain.getLabelSet().getCardinality();
		
		double acum = 0;
		for(int i = 0; i < _decisionMatrix[0].length; ++i) {
			for(int j = 0; j < _decisionMatrix.length; ++j) {
				collective = (TwoTuple) _decisionMatrix[j][i];
				idealSolution =  _idealSolution.get(j);
				distance = new TwoTuple(_distanceDomain);
				distance.calculateDelta((t_prima + 1) - (t_prima - ((Math.abs(collective.calculateInverseDelta() - idealSolution.calculateInverseDelta()) * (t_prima_prima - 1)) / (t - 1))));
				
				acum += distance.calculateInverseDelta();
			}
			
			acum /= _decisionMatrix.length;
			
			distance.calculateDelta(acum);
			_idealDistance.add(distance);
		}
	}

	private void calculateNoIdealEuclideanDistance() {
		_noIdealDistance.clear();

		TwoTuple collective, noIdealSolution;
		TwoTuple distance = null;
		
		int t = _unificationDomain.getLabelSet().getCardinality();
		int t_prima = _similarityDomain.getLabelSet().getCardinality();
		int t_prima_prima = _distanceDomain.getLabelSet().getCardinality();
		
		double acum = 0;
		for(int i = 0; i < _decisionMatrix[0].length; ++i) {
			for(int j = 0; j < _decisionMatrix.length; ++j) {
				collective = (TwoTuple) _decisionMatrix[j][i];
				noIdealSolution = (TwoTuple) _noIdealSolution.get(j);
				
				distance = new TwoTuple(_distanceDomain);
				distance.calculateDelta((t_prima + 1) - (t_prima - ((Math.abs(collective.calculateInverseDelta() - noIdealSolution.calculateInverseDelta()) * (t_prima_prima - 1)) / (t - 1))));
				
				acum += distance.calculateInverseDelta();
			}
			
			acum /= _decisionMatrix.length;
			
			distance.calculateDelta(acum);
			_noIdealDistance.add(distance);
		}
	}

	private void calculateClosenessCoefficient() {
		_closenessCoefficient.clear();

		int t_prima_prima = _distanceDomain.getLabelSet().getCardinality();
		
		TwoTuple idealDistance, noIdealDistance, coefficient; 
		for(int i = 0; i < _decisionMatrix[0].length; ++i) {
			idealDistance = (TwoTuple) _idealDistance.get(i);
			noIdealDistance = (TwoTuple) _noIdealDistance.get(i);
			
			coefficient = new TwoTuple(_distanceDomain);
			coefficient.calculateDelta(((((noIdealDistance.calculateInverseDelta() - 1) / 
					((idealDistance.calculateInverseDelta() - 1) + (noIdealDistance.calculateInverseDelta() - 1))) * t_prima_prima) + 1));
			
			_closenessCoefficient.add(coefficient);
		}
		
		computeRanking();
	}

	private void computeRanking() {
		Collections.sort(_closenessCoefficient, new RankingComparator());
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
		_idealSolution = selectionPhase.getIdealSolution();
		_noIdealSolution = selectionPhase.getNoIdealSolution();
		_idealDistance = selectionPhase.getIdealDistances();
		_noIdealDistance = selectionPhase.getNoIdealDistances();
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
		_idealSolution.clear();
		_noIdealSolution.clear();
		_idealDistance.clear();
		_noIdealDistance.clear();
		_closenessCoefficient.clear();
	}

	public void execute() {
		calculateDecisionMatrix();
		calculateIdealSolution();
		calculateNoIdealSolution();
		calculateIdealEuclideanDistance();
		calculateNoIdealEuclideanDistance();
		calculateClosenessCoefficient();
	}
	
	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		if (event.getChange().equals(EPhaseMethodStateChange.ACTIVATED)) {
			activate();
		}
	}
}
