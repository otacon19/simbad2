package sinbad2.resolutionphase.sensitivityanalysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.utils.Pair;
import sinbad2.core.workspace.WorkspaceContentPersistenceException;
import sinbad2.domain.Domain;
import sinbad2.element.ProblemElement;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.method.Method;
import sinbad2.method.MethodsManager;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.aggregation.AggregationPhase;
import sinbad2.phasemethod.todim.resolution.ResolutionPhase;
import sinbad2.phasemethod.topsis.selection.SelectionPhase;
import sinbad2.resolutionphase.IResolutionPhase;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.resolutionphase.io.XMLWriter;
import sinbad2.resolutionphase.sensitivityanalysis.nls.Messages;
import sinbad2.resolutionphase.state.EResolutionPhaseStateChange;
import sinbad2.resolutionphase.state.ResolutionPhaseStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.twoTuple.TwoTuple;

public class SensitivityAnalysis implements IResolutionPhase {

	public static final String ID = "flintstones.resolutionphase.sensitivityanalysis"; //$NON-NLS-1$

	private int _numAlternatives;
	private int _numCriteria;

	private Double[] _w;
	private Double[][] _decisionMatrix;

	private Double[] _alternativesFinalPreferences;
	private Double[][] _alternativesRatioFinalPreferences;

	private Integer[] _ranking;
	private Double[][][] _minimumAbsoluteChangeInCriteriaWeights;
	private Double[][][] _minimumPercentChangeInCriteriaWeights;
	private Double[][][] _absoluteThresholdValues;
	private Double[][][] _relativeThresholdValues;
	private List<Integer> _absoluteTop;
	private List<Integer> _absoluteAny;

	private ProblemElementsSet _elementsSet;

	private AggregationPhase _aggregationPhase;

	private EModel _model;
	private EProblem _problem;

	public List<ISensitivityAnalysisChangeListener> _listeners;

	public SensitivityAnalysis() {
		_w = null;

		_absoluteTop = new LinkedList<Integer>();
		_absoluteAny = new LinkedList<Integer>();

		_listeners = new LinkedList<ISensitivityAnalysisChangeListener>();

		_model = EModel.WEIGHTED_SUM;
		_problem = EProblem.MOST_CRITICAL_CRITERION;
	}

	public int getNumAlternatives() {
		return _numAlternatives;
	}

	public void setNumAlternatives(int numberOfAlternatives) {
		_numAlternatives = numberOfAlternatives;
	}

	public int getNumCriteria() {
		return _numCriteria;
	}

	public void setNumCriteria(int numberOfCriteria) {
		_numCriteria = numberOfCriteria;
	}

	public Double[] getWeights() {
		return _w;
	}

	public void setWeights(Double[] w) {
		_w = w;
	}

	public Double[][] getDecisionMatrix() {
		return _decisionMatrix;
	}

	public void setDecisionMatrix(Double[][] dm) {
		_decisionMatrix = dm;
	}

	public EModel getModel() {
		return _model;
	}

	public void setModel(EModel model) {
		_model = model;
	}

	public void setProblem(EProblem problem) {
		_problem = problem;
	}

	public EProblem getProblem() {
		return _problem;
	}

	public Double[] getAlternativesFinalPreferences() {
		return _alternativesFinalPreferences;
	}

	public void setAlternativesFinalPreferences(Double[] alternativesFinalPreferences) {
		_alternativesFinalPreferences = alternativesFinalPreferences;
	}

	public Double[][] getAlternativesRatioFinalPreferences() {
		return _alternativesRatioFinalPreferences;
	}

	public void setAlternativesRatioFinalPreferences(Double[][] alternativesRatioFinalPreferences) {
		_alternativesRatioFinalPreferences = alternativesRatioFinalPreferences;
	}

	public Integer[] getRanking() {
		return _ranking;
	}

	public void setRanking(Integer[] ranking) {
		_ranking = ranking;
	}

	public Double[][][] getMinimumAbsoluteChangeInCriteriaWeights() {
		return _minimumAbsoluteChangeInCriteriaWeights;
	}

	public void setMinimunAbsoluteChangeInCriteriaWeights(Double[][][] minimumAbsoluteChangeInCriteriaWeights) {
		_minimumAbsoluteChangeInCriteriaWeights = minimumAbsoluteChangeInCriteriaWeights;
	}

	public Double[][][] getMinimumPercentChangeInCriteriaWeights() {
		return _minimumPercentChangeInCriteriaWeights;
	}

	public void setMinimunPercentChangeInCriteriaWeights(Double[][][] minimumPercentChangeInCriteriaWeights) {
		_minimumPercentChangeInCriteriaWeights = minimumPercentChangeInCriteriaWeights;
	}

	public Double[][][] getAbsoluteThresholdValues() {
		return _absoluteThresholdValues;
	}

	public void setAbsoluteThresholdValues(Double[][][] absoluteThresholdValues) {
		_absoluteThresholdValues = absoluteThresholdValues;
	}

	public Double[][][] getRelativeThresholdValues() {
		return _relativeThresholdValues;
	}

	public void setRelativeThresholdValues(Double[][][] relativeThresholdValues) {
		_relativeThresholdValues = relativeThresholdValues;
	}

	public List<Integer> getAbsoluteTop() {
		return _absoluteTop;
	}

	public void setAbsoluteTop(List<Integer> absoluteTop) {
		_absoluteTop = absoluteTop;
	}

	public List<Integer> getAbsoluteAny() {
		return _absoluteAny;
	}

	public void setAbsoluteAny(List<Integer> absoluteAny) {
		_absoluteAny = absoluteAny;
	}

	public String[] getAlternativesIds() {
		String[] alternativesIds = new String[_elementsSet.getAlternatives().size()];

		int cont = 0;
		for (Alternative a : _elementsSet.getAlternatives()) {
			alternativesIds[cont] = a.getId();
			cont++;
		}

		return alternativesIds;
	}

	public String[] getCriteriaIds() {
		String[] criteriaIds = new String[_elementsSet.getAllSubcriteria().size()];

		int cont = 0;
		for (Criterion c : _elementsSet.getAllSubcriteria()) {
			criteriaIds[cont] = c.getCanonicalId();
			cont++;
		}

		return criteriaIds;
	}

	public void calculateDecisionMatrix(List<Double> weights) {
		_numAlternatives = _elementsSet.getAlternatives().size();
		_numCriteria = _elementsSet.getAllSubcriteria().size();
		
		String activatedMethodId = MethodsManager.getInstance().getActiveMethod().getId();
		if(activatedMethodId.contains("todim")) { //$NON-NLS-1$
			
			if(weights != null) {
				assignWeights(weights);
			}
			
			computeTODIM();
			computeMulticriteriaProblem("todim"); //$NON-NLS-1$
			
		} else if(activatedMethodId.contains("topsis")) { //$NON-NLS-1$
			
			computeWeights(weights);
			computeTOPSIS();
			computeMulticriteriaProblem("topsis"); //$NON-NLS-1$
				
		} else {
			computeWeights(weights);
			compute();
		}
	}

	private void computeTODIM() {
		Map<Criterion, Double> criteriaWeights = new HashMap<Criterion, Double>();
		criteriaWeights = transformWeightsMap(_w);
		
		ResolutionPhase todimPhase = (ResolutionPhase) PhasesMethodManager.getInstance().getPhaseMethod(ResolutionPhase.ID).getImplementation();
		todimPhase.setCriteriaWeights(criteriaWeights);
		todimPhase.calculateRelativeWeights();
		todimPhase.calculateDominanceDegreeByCriterionFuzzyNumber(1);
		todimPhase.calculateDominaceDegreeOverAlternatives();
		
		Map<Alternative, Double> globalDominance = todimPhase.calculateGlobalDominance();
		int alternative = 0;
		for (Alternative a : _elementsSet.getAlternatives()) {
			_alternativesFinalPreferences[alternative] = globalDominance.get(a);
			alternative++;
		}
	}
	
	private Map<Criterion, Double> transformWeightsMap(Double[] weights) {
		int numWeight = 0;
		
		Map<Criterion, Double> criteriaWeights = new HashMap<Criterion, Double>();
		for (Criterion c : _elementsSet.getAllCriteria()) {
			criteriaWeights.put(c, weights[numWeight]);
			numWeight++;
		}
		
		return criteriaWeights;
	}

	private void computeTOPSIS() {
		SelectionPhase topsisPhase = (SelectionPhase) PhasesMethodManager.getInstance().getPhaseMethod(SelectionPhase.ID).getImplementation();
		
		List<Double> criteriaWeights;
		criteriaWeights = transformWeightsList(_w);

		topsisPhase.calculateIdealSolution();
		topsisPhase.calculateNoIdealSolution();
		topsisPhase.calculateIdealEuclideanDistanceByCriterion(criteriaWeights);
		topsisPhase.calculateNoIdealEuclideanDistanceByCriterion(criteriaWeights);
		List<Object[]> coefficients = topsisPhase.calculateClosenessCoefficient();
		
		int alternative = 0;
		for(Alternative a: _elementsSet.getAlternatives()) {
 			for(Object[] coefficent: coefficients) {
 				if(a.equals(coefficent[1])) {
 					_alternativesFinalPreferences[alternative] = (Double) coefficent[4];
 				}
 			}
 			alternative++;
		}
	}
	
	private List<Double> transformWeightsList(Double[] weights) {
		int numWeight = 0;
		
		List<Double> ws = new LinkedList<Double>();
		for(int c = 0 ; c < _numCriteria; ++c) {
			ws.add(weights[numWeight]);
			numWeight++;
		}
		
		return ws;
	}
	
	@SuppressWarnings("unchecked")
	private void computeWeights(List<Double> weights) {
		
		if (weights != null) {
			assignWeights(weights);
		} else if (_w == null) {
			Map<ProblemElement, Object> criteriaOperatorWeights = _aggregationPhase.getCriteriaOperatorWeights();
			if (!criteriaOperatorWeights.isEmpty()) {
				if (criteriaOperatorWeights.get(null) == null) {
					createDefaultWeights();
				} else if (criteriaOperatorWeights.size() == 1) {
					assignWeights((List<Double>) criteriaOperatorWeights.get(null));
				} else {
					assignWeights(getSubcriteriaWeights());
				}
			} else {
				createDefaultWeights();
			}
		}	
	}
	
	@SuppressWarnings("unchecked")
	private List<Double> getSubcriteriaWeights() {
		List<Double> globalWeights = new LinkedList<Double>();
		Map<ProblemElement, Object> criteriaWeights = new HashMap<ProblemElement, Object>();

		criteriaWeights = _aggregationPhase.getCriteriaOperatorWeights();
		globalWeights = ((Map<Object, List<Double>>) criteriaWeights.get(null)).get(null);

		return calculateWeightsSubcriteria(globalWeights, criteriaWeights);
	}

	@SuppressWarnings("unchecked")
	private List<Double> calculateWeightsSubcriteria(List<Double> globalWeights, Map<ProblemElement, Object> elementWeights) {
		List<Double> subcriteriaWeights = new LinkedList<Double>();
		int numC = -1, cont = 0;
		double result;

		for (Criterion c : _elementsSet.getCriteria()) {
			numC++;
			if (c.hasSubcriteria()) {
				for (Criterion sc : c.getSubcriteria()) {
					result = globalWeights.get(numC);
					result *= ((Map<Object, List<Double>>) elementWeights.get(c)).get(null).get(cont);
					result *= recursiveWeightCriterion(sc, cont);
					cont++;

					subcriteriaWeights.add(Math.round(result * 1000d) / 1000d);
				}
			} else {
				result = globalWeights.get(numC);
				subcriteriaWeights.add(Math.round(result * 1000d) / 1000d);
			}
		}

		return subcriteriaWeights;
	}

	@SuppressWarnings("unchecked")
	private double recursiveWeightCriterion(Criterion c, int cont) {

		if (c.hasSubcriteria()) {
			for (Criterion sc : c.getSubcriteria()) {
				recursiveWeightCriterion(sc, cont);
			}
		}

		List<Double> weightsCriterion = null;
		if (_aggregationPhase.getCriteriaOperatorWeights().get(c) != null) {
			weightsCriterion = ((Map<Object, List<Double>>) _aggregationPhase.getCriteriaOperatorWeights().get(c)).get(null);
		}

		if (weightsCriterion != null) {
			return weightsCriterion.get(cont);
		} else {
			return 1;
		}
	}

	private void assignWeights(List<Double> weights) {
		_w = new Double[_numCriteria];
		for (int i = 0; i < weights.size(); ++i) {
			_w[i] = weights.get(i);
		}
	}

	private void createDefaultWeights() {
		_w = new Double[_numCriteria];
		double tempW = 1d / (double) _numCriteria;
		for (int i = 0; i < _numCriteria; i++) {
			_w[i] = tempW;
		}
	}

	private void setDefaultValues(Double[][][] matrix) {
		for (int i = 0; i < _numAlternatives; i++) {
			for (int j = 0; j < _numAlternatives; j++) {
				for (int k = 0; k < _numCriteria; k++) {
					matrix[i][j][k] = null;
				}
			}
		}
	}
	
	public Double[] computeAlternativesFinalPreferenceInferWeights(Double[] ws) {
		Double[] alternativeFinalPreferences = new Double[_numAlternatives];

		String activatedMethodId = MethodsManager.getInstance().getActiveMethod().getId();
		if (activatedMethodId.contains("todim")) { //$NON-NLS-1$
			alternativeFinalPreferences = computeTODIMWeightsInference(ws);
		} else if(activatedMethodId.contains("topsis")) { //$NON-NLS-1$
			alternativeFinalPreferences = computeTOPSISInference(ws);
		} else {
			for (int alternative = 0; alternative < _numAlternatives; ++alternative) {
				alternativeFinalPreferences[alternative] = 0d;
				for (int criterion = 0; criterion < _numCriteria; criterion++) {
					alternativeFinalPreferences[alternative] += _decisionMatrix[criterion][alternative] * ws[criterion];
				}
			}
		}
		
		return alternativeFinalPreferences;
	}
	
	private Double[] computeTODIMWeightsInference(Double[] weights) {
		Map<Criterion, Double> criteriaWeights = transformWeightsMap(weights);

		ResolutionPhase todimPhase = (ResolutionPhase) PhasesMethodManager.getInstance().getPhaseMethod(ResolutionPhase.ID).getImplementation();
		todimPhase.setCriteriaWeights(criteriaWeights);
		todimPhase.calculateRelativeWeights();
		todimPhase.calculateDominanceDegreeByCriterionFuzzyNumber(1);
		todimPhase.calculateDominaceDegreeOverAlternatives();
		Map<Alternative, Double> globalDominance = todimPhase.calculateGlobalDominance();
		
		int alternative = 0;
		Double[] alternativesFinalPreferences = new Double[_numAlternatives];
		for (Alternative a : _elementsSet.getAlternatives()) {
			alternativesFinalPreferences[alternative] = globalDominance.get(a);
			alternative++;
		}

		return alternativesFinalPreferences;
	}
	
	private Double[] computeTOPSISInference(Double[] weights) {
		SelectionPhase topsisPhase = (SelectionPhase) PhasesMethodManager.getInstance().getPhaseMethod(SelectionPhase.ID).getImplementation();

		List<Double> criteriaWeights;
		criteriaWeights = transformWeightsList(weights);

		topsisPhase.calculateIdealSolution();
		topsisPhase.calculateNoIdealSolution();
		topsisPhase.calculateIdealEuclideanDistanceByCriterion(criteriaWeights);
		topsisPhase.calculateNoIdealEuclideanDistanceByCriterion(criteriaWeights);
		List<Object[]> coefficients = topsisPhase.calculateClosenessCoefficient();
		
		int alternative = 0;
		Double[] alternativesFinalPreferences = new Double[_numAlternatives];
		for(Alternative a: _elementsSet.getAlternatives()) {
 			for(Object[] coefficent: coefficients) {
 				if(a.equals(coefficent[1])) {
 					alternativesFinalPreferences[alternative] = (Double) coefficent[4];
 				}
 			}
 			alternative++;
		}
		
		return alternativesFinalPreferences;
	}

	public Double[] computeAlternativesPreferenceInferAttenuationFactor(double attenuationFactor) {
		Map<Criterion, Double> criteriaWeights = new HashMap<Criterion, Double>();
		criteriaWeights = transformWeightsMap(_w);

		ResolutionPhase todimPhase = (ResolutionPhase) PhasesMethodManager.getInstance().getPhaseMethod(ResolutionPhase.ID).getImplementation();
		todimPhase.setCriteriaWeights(criteriaWeights);
		todimPhase.calculateRelativeWeights();
		todimPhase.calculateDominanceDegreeByCriterionFuzzyNumber(attenuationFactor);
		todimPhase.calculateDominaceDegreeOverAlternatives();
		Map<Alternative, Double> globalDominance = todimPhase.calculateGlobalDominance();
		
		int alternative = 0;
		Double[] alternativesFinalPreferences = new Double[_numAlternatives];
		for (Alternative a : _elementsSet.getAlternatives()) {
			alternativesFinalPreferences[alternative] = globalDominance.get(a);
			alternative++;
		}

		return alternativesFinalPreferences;
	}
	
	public double computeAlternativeRatioFinalPreferenceInferWeights(int alternativeIndex1, int alternativeIndex2, Double[] w) {
		double alternativeRatioFinalPreference = 1;

		for (int criterion = 0; criterion < _numCriteria; criterion++) {
			alternativeRatioFinalPreference *= Math.pow((_decisionMatrix[criterion][alternativeIndex1] / _decisionMatrix[criterion][alternativeIndex2]),
					w[criterion]);
		}

		return alternativeRatioFinalPreference;
	}

	public Double[] calculateInferWeights(Criterion criterion, double weightCriterionSelected) {
		List<Criterion> criteria = _elementsSet.getAllSubcriteria();
		Double[] inferWeights = new Double[criteria.size()];

		weightCriterionSelected = Math.round(weightCriterionSelected * 10000d) / 10000d;

		int indexCriterionSelected = criteria.indexOf(criterion);
		inferWeights[indexCriterionSelected] = weightCriterionSelected;
		for (int i = 0; i < criteria.size(); ++i) {
			if (i != indexCriterionSelected) {
				inferWeights[i] = ((1 - weightCriterionSelected) / (1 - _w[indexCriterionSelected])) * _w[i];
			}
		}
		
		return inferWeights;
	}

	public Map<Criterion, Map<Alternative, Double>> getMinimunPercentMCMByCriterion() {
		Map<Criterion, Map<Alternative, Double>> result = new LinkedHashMap<Criterion, Map<Alternative, Double>>();

		List<Alternative> alternatives = _elementsSet.getAlternatives();
		List<Criterion> criteria = _elementsSet.getAllSubcriteria();
		double min, max = Math.round(getMaximunPercentMCM());

		String numberS = Double.toString(max);
		numberS = numberS.substring(1, numberS.indexOf('.'));
		double units = Math.pow(10, numberS.length()) - Double.parseDouble(numberS);
		max += units;

		for (int c = 0; c < _numCriteria; ++c) {
			for (int a1 = 0; a1 < _numAlternatives; ++a1) {
				min = Double.MAX_VALUE;
				for (int a2 = 0; a2 < _numAlternatives; ++a2) {
					if (_relativeThresholdValues[a1][a2][c] != null) {
						if (min > Math.abs(_relativeThresholdValues[a1][a2][c])) {
							min = Math.abs(_relativeThresholdValues[a1][a2][c]);
						}
					} else {
						if (a1 != a2) {
							if (min == Double.MAX_VALUE) {
								min = max;
							}
						}
					}
				}
				if (min != Double.MAX_VALUE) {
					if (result.get(criteria.get(c)) == null) {
						Map<Alternative, Double> minimunAlternative = new LinkedHashMap<Alternative, Double>();
						minimunAlternative.put(alternatives.get(a1), min);
						result.put(criteria.get(c), minimunAlternative);
					} else {
						Map<Alternative, Double> minimunAlternative = result.get(criteria.get(c));
						minimunAlternative.put(alternatives.get(a1), min);
					}
				}
			}
		}

		return result;
	}

	public double getMaximunPercentMCM() {
		double max = Double.NEGATIVE_INFINITY;
		for (int c = 0; c < _numCriteria; ++c) {
			for (int a1 = 0; a1 < _numAlternatives; ++a1) {
				for (int a2 = 0; a2 < _numAlternatives; ++a2) {
					if (_relativeThresholdValues[a1][a2][c] != null) {
						if (max < Math.abs(_relativeThresholdValues[a1][a2][c])) {
							max = Math.abs(_relativeThresholdValues[a1][a2][c]);
						}
					}
				}
			}
		}

		return max;
	}

	public Map<Criterion, Map<Alternative, Double>> getMinimunPercentMCCByCriterion() {
		Map<Criterion, Map<Alternative, Double>> result = new LinkedHashMap<Criterion, Map<Alternative, Double>>();
		Map<Integer, Double> minimunValueAlternatives = new LinkedHashMap<Integer, Double>();

		List<Alternative> alternatives = _elementsSet.getAlternatives();
		List<Criterion> criteria = _elementsSet.getAllSubcriteria();
		double min, max = Math.round(getMaximunPercentMCC());

		String numberS = Double.toString(max);
		numberS = numberS.substring(1, numberS.indexOf('.'));
		double units = Math.pow(10, numberS.length()) - Double.parseDouble(numberS);
		max += units;

		for (int c = 0; c < _numCriteria; ++c) {
			minimunValueAlternatives.clear();
			for (int a1 = 0; a1 < _numAlternatives; ++a1) {
				min = Double.MAX_VALUE;
				for (int a2 = 0; a2 < _numAlternatives; ++a2) {
					if (_minimumPercentChangeInCriteriaWeights[a1][a2][c] != null) {
						if (min > Math.abs(_minimumPercentChangeInCriteriaWeights[a1][a2][c])) {
							min = Math.abs(_minimumPercentChangeInCriteriaWeights[a1][a2][c]);
						}
					} else {
						if (a1 != a2) {
							if (min == Double.MAX_VALUE) {
								if (_minimumPercentChangeInCriteriaWeights[a2][a1][c] == null) {
									min = max;
								} else {
									if (minimunValueAlternatives.get(a1) != null) {
										if (minimunValueAlternatives.get(a1) > Math.abs(_minimumPercentChangeInCriteriaWeights[a2][a1][c])) {
											min = Math.abs(_minimumPercentChangeInCriteriaWeights[a2][a1][c]);
										} else {
											min = minimunValueAlternatives.get(a1);
										}
									} else {
										min = Math.abs(_minimumPercentChangeInCriteriaWeights[a2][a1][c]);
									}
								}
							} else {
								if (_minimumPercentChangeInCriteriaWeights[a2][a1][c] != null) {
									if (Math.abs(_minimumPercentChangeInCriteriaWeights[a2][a1][c]) < min) {
										min = Math.abs(_minimumPercentChangeInCriteriaWeights[a2][a1][c]);
									}
								}
							}
						}
					}
				}

				if (min != Double.MAX_VALUE) {
					if (result.get(criteria.get(c)) == null) {
						Map<Alternative, Double> minimunAlternative = new LinkedHashMap<Alternative, Double>();
						minimunAlternative.put(alternatives.get(a1), min);
						result.put(criteria.get(c), minimunAlternative);
					} else {
						Map<Alternative, Double> minimunAlternative = result.get(criteria.get(c));
						minimunAlternative.put(alternatives.get(a1), min);
					}
					minimunValueAlternatives.put(a1, min);
				}
			}
		}

		return result;
	}

	public double[] getMinimumAbsolutePairAlternatives(int a1, int a2) {
		double[] absolute = new double[_numCriteria];

		switch (_problem) {
		case MOST_CRITICAL_CRITERION:
			for (int k = 0; k < _numCriteria; k++) {
				if (_minimumAbsoluteChangeInCriteriaWeights[a1][a2][k] != null) {
					absolute[k] = _minimumAbsoluteChangeInCriteriaWeights[a1][a2][k];
				}
			}
			break;
		case MOST_CRITICAL_MEASURE:
			for (int k = 0; k < _numCriteria; k++) {
				if (_absoluteThresholdValues[a1][a2][k] != null) {
					absolute[k] = _absoluteThresholdValues[a1][a2][k];
				}
			}
			break;
		}

		return absolute;
	}
	
	public double getMaximunPercentMCC() {
		double max = Double.NEGATIVE_INFINITY;
		for (int c = 0; c < _numCriteria; ++c) {
			for (int a1 = 0; a1 < _numAlternatives; ++a1) {
				for (int a2 = 0; a2 < _numAlternatives; ++a2) {
					if (_minimumPercentChangeInCriteriaWeights[a1][a2][c] != null) {
						if (max < Math.abs(_minimumPercentChangeInCriteriaWeights[a1][a2][c])) {
							max = Math.abs(_minimumPercentChangeInCriteriaWeights[a1][a2][c]);
						}
					}
				}
			}
		}

		return max;
	}

	public void compute() {

		readDecisionMatrix();

		switch (_model) {
		case WEIGHTED_SUM:
			if (_problem == EProblem.MOST_CRITICAL_CRITERION) {
				computeWeightedSumModelCriticalCriterion();
			} else {
				computeWeightedSumModelCriticalMeasure();
			}
			break;
		case WEIGHTED_PRODUCT:
			if (_problem == EProblem.MOST_CRITICAL_CRITERION) {
				computeWeightedProductModelCriticalCriterion();
			} else {
				computeWeightedProductModelCriticalMeasure();
			}
			break;
		case ANALYTIC_HIERARCHY_PROCESS:
			if (_problem == EProblem.MOST_CRITICAL_CRITERION) {
				computeAnalyticHierarchyProcessModelCriticalCriterion();
			} else {
				computeAnalyticHierarchyProcessModelCriticalMeasure();
			}
			break;
		}
	}

	public void computeMulticriteriaProblem(String multicriteriaProblem) {
		
		if(multicriteriaProblem.equals("topsis")) { //$NON-NLS-1$
			readDecisionMatrix();
		}

		if (_problem == EProblem.MOST_CRITICAL_CRITERION) {
			computeMulticriteriaProblemCriticalCriterion();
		} else {
			computeMulticriteriaProblemCriticalMeasure();
		}
	}
	
	public void computeWeightedSumModelCriticalCriterion() {
		normalizeDecisionMatrix();

		computeFinalPreferences();
		computeMinimumAbsoluteChangeInCriteriaWeights();
		computeMinimumPercentChangeInCriteriaWeights();
		computeAbsoluteTopCriticalCriterion();
		computeAbsoluteAnyCriticalCriterion();

		notifySensitivityAnalysisChange();
	}
	
	private void computeFinalPreferences() {

		_alternativesFinalPreferences = new Double[_numAlternatives];

		for (int alternative = 0; alternative < _numAlternatives; alternative++) {
			_alternativesFinalPreferences[alternative] = 0d;
			for (int criterion = 0; criterion < _numCriteria; criterion++) {
				_alternativesFinalPreferences[alternative] += _decisionMatrix[criterion][alternative] * _w[criterion];
			}
		}

		computeRanking();
	}

	private void computeRanking() {

		_ranking = new Integer[_numAlternatives];

		List<Double> preferences = new LinkedList<Double>();
		for (double preference : _alternativesFinalPreferences) {
			preferences.add(new Double(preference));
		}

		Collections.sort(preferences);
		Collections.reverse(preferences);

		int rankingPos = 0;
		double previousPreference = 0;

		for (double preference : preferences) {
			if (preference != previousPreference) {
				rankingPos++;
				for (int alternative = 0; alternative < _numAlternatives; alternative++) {
					if (_alternativesFinalPreferences[alternative] == preference) {
						_ranking[alternative] = rankingPos;
					}
				}
				previousPreference = preference;
			}
		}
	}

	private void computeMinimumAbsoluteChangeInCriteriaWeights() {
		_minimumAbsoluteChangeInCriteriaWeights = new Double[_numAlternatives][_numAlternatives][_numCriteria];

		setDefaultValues(_minimumAbsoluteChangeInCriteriaWeights);

		for (int i = 0; i < _numAlternatives - 1; i++) {
			for (int j = i + 1; j < _numAlternatives; j++) {
				for (int k = 0; k < _numCriteria; k++) {
					if (_decisionMatrix[k][j] - _decisionMatrix[k][i] == 0) {
						_minimumAbsoluteChangeInCriteriaWeights[i][j][k] = null;
					} else {
						_minimumAbsoluteChangeInCriteriaWeights[i][j][k] = (_alternativesFinalPreferences[j] - _alternativesFinalPreferences[i]) 
								/ (_decisionMatrix[k][j] - _decisionMatrix[k][i]);
						if (_minimumAbsoluteChangeInCriteriaWeights[i][j][k] > _w[k]) {
							_minimumAbsoluteChangeInCriteriaWeights[i][j][k] = null;
						}
					}
				}
			}
		}
	}
	
	private void computeMinimumPercentChangeInCriteriaWeights() {
		_minimumPercentChangeInCriteriaWeights = new Double[_numAlternatives][_numAlternatives][_numCriteria];

		for (int i = 0; i < _numAlternatives; i++) {
			for (int j = 0; j < _numAlternatives; j++) {
				for (int k = 0; k < _numCriteria; k++) {
					if (_minimumAbsoluteChangeInCriteriaWeights[i][j][k] != null) {
						if (_w[k] == 0) {
							_minimumPercentChangeInCriteriaWeights[i][j][k] = null;
						} else {
							_minimumPercentChangeInCriteriaWeights[i][j][k] = _minimumAbsoluteChangeInCriteriaWeights[i][j][k] * (100d / _w[k]);
						}
					} else {
						_minimumPercentChangeInCriteriaWeights[i][j][k] = null;
					}
				}
			}
		}
	}
	
	private void computeAbsoluteTopCriticalCriterion() {
		List<Integer> bestAlternatives = new LinkedList<Integer>();

		for (int i = 0; i < _numAlternatives; i++) {
			if (_ranking[i] == 1) {
				bestAlternatives.add(new Integer(i));
			}
		}

		Double minimum = null;
		Double aux = null;
		for (int i = 0; i < bestAlternatives.size(); i++) {
			for (int j = 0; j < _numAlternatives; j++) {
				for (int k = 0; k < _numCriteria; k++) {
					aux = _minimumAbsoluteChangeInCriteriaWeights[bestAlternatives.get(i)][j][k];
					if (aux != null) {
						aux = Math.abs(aux);
						if (minimum == null) {
							minimum = aux;
							_absoluteTop = new LinkedList<Integer>();
							_absoluteTop.add(new Integer(k));
						} else if (aux < minimum) {
							minimum = aux;
							_absoluteTop = new LinkedList<Integer>();
							_absoluteTop.add(new Integer(k));
						} else if (aux == minimum) {
							_absoluteTop.add(new Integer(k));
						}
					}
				}
			}

			for (int j = 0; j < _numAlternatives; j++) {
				for (int k = 0; k < _numCriteria; k++) {
					aux = _minimumAbsoluteChangeInCriteriaWeights[j][bestAlternatives.get(i)][k];
					if (aux != null) {
						aux = Math.abs(aux);
						if (minimum == null) {
							minimum = aux;
							_absoluteTop = new LinkedList<Integer>();
							_absoluteTop.add(new Integer(k));
						} else if (aux < minimum) {
							minimum = aux;
							_absoluteTop = new LinkedList<Integer>();
							_absoluteTop.add(new Integer(k));
						} else if (aux == minimum) {
							_absoluteTop.add(new Integer(k));
						}
					}
				}
			}
		}
	}

	private void computeAbsoluteAnyCriticalCriterion() {
		Double minimum = null;
		Double aux = null;

		for (int i = 0; i < _numAlternatives; i++) {
			for (int j = 0; j < _numAlternatives; j++) {
				for (int k = 0; k < _numCriteria; k++) {
					aux = _minimumAbsoluteChangeInCriteriaWeights[i][j][k];
					if (aux != null) {
						aux = Math.abs(aux);
						if (minimum == null) {
							minimum = aux;
							_absoluteAny = new LinkedList<Integer>();
							_absoluteAny.add(new Integer(k));
						} else if (aux < minimum) {
							minimum = aux;
							_absoluteAny = new LinkedList<Integer>();
							_absoluteAny.add(new Integer(k));
						} else if (aux == minimum) {
							_absoluteAny.add(new Integer(k));
						}
					}
				}
			}
		}
	}


	public void computeMulticriteriaProblemCriticalCriterion() {
		normalizeDecisionMatrix();

		computeRanking();
		
		_minimumAbsoluteChangeInCriteriaWeights = new Double[_numAlternatives][_numAlternatives][_numCriteria];

		for (int i = 0; i < _numAlternatives; i++) {
			for (int j = 0; j < _numAlternatives; j++) {
				for (int k = 0; k < _numCriteria; k++) {
					_minimumAbsoluteChangeInCriteriaWeights[i][j][k] = Double.MAX_VALUE;
				}
			}
		}
		
		_minimumPercentChangeInCriteriaWeights = new Double[_numAlternatives][_numAlternatives][_numCriteria];

		for (int i = 0; i < _numAlternatives; i++) {
			for (int j = 0; j < _numAlternatives; j++) {
				for (int k = 0; k < _numCriteria; k++) {
					_minimumPercentChangeInCriteriaWeights[i][j][k] = Double.MAX_VALUE;
				}
			}
		}
	
		notifySensitivityAnalysisChange();
	}

	public void computeWeightedSumModelCriticalMeasure() {
		normalizeDecisionMatrix();

		computeFinalPreferences();
		computeAbsoluteThresholdValuesWeightedSumModel();
		computeRelativeThresholdValuesWeightedSumModel();

		notifySensitivityAnalysisChange();
	}
	
	private void computeAbsoluteThresholdValuesWeightedSumModel() {
		_absoluteThresholdValues = new Double[_numAlternatives][_numAlternatives][_numCriteria];

		for (int i = 0; i < _numAlternatives; i++) {
			for (int j = 0; j < _numAlternatives; j++) {
				for (int k = 0; k < _numCriteria; k++) {
					if (i != j) {
						if (_w[k] == 0) {
							_absoluteThresholdValues[i][j][k] = null;
						} else {
							_absoluteThresholdValues[i][j][k] = (_alternativesFinalPreferences[i] - _alternativesFinalPreferences[j]) / _w[k];

							if (_absoluteThresholdValues[i][j][k] > _w[k]) {
								_absoluteThresholdValues[i][j][k] = null;
							}
						}
					}
				}
			}
		}
	}
	
	private void computeRelativeThresholdValuesWeightedSumModel() {
		_relativeThresholdValues = new Double[_numAlternatives][_numAlternatives][_numCriteria];

		for (int i = 0; i < _numAlternatives; i++) {
			for (int j = 0; j < _numAlternatives; j++) {
				for (int k = 0; k < _numCriteria; k++) {
					if (_absoluteThresholdValues[i][j][k] != null) {
						if (_decisionMatrix[k][i] == 0) {
							_relativeThresholdValues[i][j][k] = null;
						} else {
							_relativeThresholdValues[i][j][k] = _absoluteThresholdValues[i][j][k] * (100d / _decisionMatrix[k][i]);
						}
					} else {
						_relativeThresholdValues[i][j][k] = null;
					}
				}
			}
		}
	}

	public void computeMulticriteriaProblemCriticalMeasure() {
		normalizeDecisionMatrix();

		computeRanking();
		computeAbsoluteThresholdValuesWeightedSumModel();
		computeRelativeThresholdValuesWeightedSumModel();

		notifySensitivityAnalysisChange();
	}

	public void computeAnalyticHierarchyProcessModelCriticalCriterion() {
		normalizeDecisionMatrixSumToOne();

		computeFinalPreferences();
		computeMinimumAbsoluteChangeInCriteriaWeights();
		computeMinimumPercentChangeInCriteriaWeights();
		computeAbsoluteTopCriticalCriterion();
		computeAbsoluteAnyCriticalCriterion();

		notifySensitivityAnalysisChange();
	}

	public void computeAnalyticHierarchyProcessModelCriticalMeasure() {
		normalizeDecisionMatrixSumToOne();

		computeFinalPreferences();
		computeAbsoluteThresholdValuesAnalyticHierarchyProcess();
		computeRelativeThresholdValuesAnalyticHierarchyProcess();

		notifySensitivityAnalysisChange();
	}
	
	private void computeAbsoluteThresholdValuesAnalyticHierarchyProcess() {
		_absoluteThresholdValues = new Double[_numAlternatives][_numAlternatives][_numCriteria];

		double denominator;
		for (int i = 0; i < _numAlternatives; i++) {
			for (int j = 0; j < _numAlternatives; j++) {
				for (int k = 0; k < _numCriteria; k++) {
					if (i != j) {
						denominator = _alternativesFinalPreferences[i] - _alternativesFinalPreferences[j] + _w[k] * 
								(_decisionMatrix[k][j] - _decisionMatrix[k][i] + 1);
						if (denominator == 0) {
							_absoluteThresholdValues[i][j][k] = null;
						} else {
							_absoluteThresholdValues[i][j][k] = (_alternativesFinalPreferences[i] - _alternativesFinalPreferences[j]) / denominator;
							if (_absoluteThresholdValues[i][j][k] > _w[k]) {
								_absoluteThresholdValues[i][j][k] = null;
							}
						}
					}
				}
			}
		}
	}
	
	private void computeRelativeThresholdValuesAnalyticHierarchyProcess() {
		_relativeThresholdValues = new Double[_numAlternatives][_numAlternatives][_numCriteria];

		for (int i = 0; i < _numAlternatives; i++) {
			for (int j = 0; j < _numAlternatives; j++) {
				for (int k = 0; k < _numCriteria; k++) {
					if (_absoluteThresholdValues[i][j][k] != null) {
						if (_decisionMatrix[k][i] == 0) {
							_relativeThresholdValues[i][j][k] = null;
						} else {
							_relativeThresholdValues[i][j][k] = _absoluteThresholdValues[i][j][k] * (100d / _decisionMatrix[k][i]);
						}
					} else {
						_relativeThresholdValues[i][j][k] = null;
					}
				}
			}
		}
	}

	public void computeWeightedProductModelCriticalCriterion() {
		normalizeDecisionMatrix();

		computeFinalPreferencesWeightedProduct();
		computeWeightedProductMinimumAbsoluteChangeInCriteriaWeights();
		computeWeightedProductMinimumPercentChangeInCriteriaWeights();
		computeAbsoluteTopCriticalCriterion();
		computeAbsoluteAnyCriticalCriterion();

		notifySensitivityAnalysisChange();
	}

	private void computeFinalPreferencesWeightedProduct() {

		_alternativesRatioFinalPreferences = new Double[_numAlternatives][_numAlternatives];

		for (int alternative1 = 0; alternative1 < _numAlternatives - 1; alternative1++) {
			for (int alternative2 = alternative1 + 1; alternative2 < _numAlternatives; alternative2++) {
				_alternativesRatioFinalPreferences[alternative1][alternative2] = 1d;
				for (int criterion = 0; criterion < _numCriteria; criterion++) {
					_alternativesRatioFinalPreferences[alternative1][alternative2] *= Math.pow(
							(_decisionMatrix[criterion][alternative1] / _decisionMatrix[criterion][alternative2]), _w[criterion]);
				}
			}
		}

		computeRankingWeightedProductModel();
	}

	private void computeRankingWeightedProductModel() {

		_ranking = new Integer[_numAlternatives];

		for (int i = 0; i < _ranking.length; ++i) {
			_ranking[i] = i + 1;
		}

		double ratio = 0;
		for (int alternative1 = 0; alternative1 < _numAlternatives - 1; ++alternative1) {
			for (int alternative2 = (alternative1 + 1); alternative2 < _numAlternatives; ++alternative2) {
				if (alternative2 > alternative1) {
					ratio = _alternativesRatioFinalPreferences[alternative1][alternative2];
					if (ratio < 1) {
						if (_ranking[alternative2] > _ranking[alternative1]) {
							if (_ranking[alternative2] - 1 == _ranking[alternative1]) {
								_ranking[alternative2] = _ranking[alternative1];
								_ranking[alternative1] += 1;
							} else {
								_ranking[alternative2] = _ranking[alternative1];
								incrementPosRanking(alternative2);
							}
						}
					}
				}
			}
		}
	}

	private void incrementPosRanking(int alternative2) {
		int rank = _ranking[alternative2];

		for (int alternative = 0; alternative < _ranking.length; ++alternative) {
			if (_ranking[alternative] >= rank && _ranking[alternative] < _ranking.length) {
				if (alternative != alternative2) {
					_ranking[alternative] += 1;
				}
			}
		}
	}
	
	private void computeWeightedProductMinimumAbsoluteChangeInCriteriaWeights() {
		_minimumAbsoluteChangeInCriteriaWeights = new Double[_numAlternatives][_numAlternatives][_numCriteria];

		setDefaultValues(_minimumAbsoluteChangeInCriteriaWeights);

		double numerator, denominator, total;
		for (int i = 0; i < _numAlternatives - 1; i++) {
			for (int j = i + 1; j < _numAlternatives; j++) {
				for (int k = 0; k < _numCriteria; k++) {
					numerator = Math.log(_alternativesRatioFinalPreferences[i][j]);
					if (_decisionMatrix[k][j] == 0) {
						_minimumAbsoluteChangeInCriteriaWeights[i][j][k] = null;
					} else {
						denominator = Math.log(_decisionMatrix[k][i] / _decisionMatrix[k][j]);
						if (denominator != 0) {
							total = numerator / denominator;
							if (total > _w[k]) {
								_minimumAbsoluteChangeInCriteriaWeights[i][j][k] = null;
							} else {
								_minimumAbsoluteChangeInCriteriaWeights[i][j][k] = total;
								if (_minimumAbsoluteChangeInCriteriaWeights[i][j][k] > _w[k]) {
									_minimumAbsoluteChangeInCriteriaWeights[i][j][k] = null;
								}
							}
						} else {
							_minimumAbsoluteChangeInCriteriaWeights[i][j][k] = null;
						}
					}
				}
			}
		}
	}

	private void computeWeightedProductMinimumPercentChangeInCriteriaWeights() {
		_minimumPercentChangeInCriteriaWeights = new Double[_numAlternatives][_numAlternatives][_numCriteria];

		for (int i = 0; i < _numAlternatives; i++) {
			for (int j = 0; j < _numAlternatives; j++) {
				for (int k = 0; k < _numCriteria; k++) {
					if (_minimumAbsoluteChangeInCriteriaWeights[i][j][k] != null) {
						if (_w[k] == 0) {
							_minimumPercentChangeInCriteriaWeights[i][j][k] = null;
						} else {
							_minimumPercentChangeInCriteriaWeights[i][j][k] = _minimumAbsoluteChangeInCriteriaWeights[i][j][k] * (100d / _w[k]);
						}
					} else {
						_minimumPercentChangeInCriteriaWeights[i][j][k] = null;
					}
				}
			}
		}
	}
	
	public void computeWeightedProductModelCriticalMeasure() {
		normalizeDecisionMatrix();

		computeFinalPreferencesWeightedProduct();
		computeAbsoluteThresholdValuesWeightedProductModel();
		computeRelativeThresholdValuesWeightedProductModel();

		notifySensitivityAnalysisChange();
	}
	
	private void computeAbsoluteThresholdValuesWeightedProductModel() {
		Double[][] auxAlternativesRatioPreferences;
		auxAlternativesRatioPreferences = computeAuxAlternativesRatioPreferences();

		_absoluteThresholdValues = new Double[_numAlternatives][_numAlternatives][_numCriteria];

		for (int i = 0; i < _numAlternatives; i++) {
			for (int j = 0; j < _numAlternatives; j++) {
				for (int k = 0; k < _numCriteria; k++) {
					if (i != j) {
						if (_w[k] == 0) {
							_absoluteThresholdValues[i][j][k] = null;
						} else {
							_absoluteThresholdValues[i][j][k] = 1 - Math.pow(auxAlternativesRatioPreferences[j][i], 1d / _w[k]);

							if (_absoluteThresholdValues[i][j][k] > _w[k]) {
								_absoluteThresholdValues[i][j][k] = null;
							}
						}
					}
				}
			}
		}
	}
	
	private Double[][] computeAuxAlternativesRatioPreferences() {
		Double[][] auxAlternativesRatioPreferences = new Double[_numAlternatives][_numAlternatives];
		
		for (int alternative1 = 0; alternative1 < _numAlternatives; alternative1++) {
			for (int alternative2 = 0; alternative2 < _numAlternatives; alternative2++) {
				if (alternative1 != alternative2) {
					auxAlternativesRatioPreferences[alternative1][alternative2] = 1d;
					for (int criterion = 0; criterion < _numCriteria; criterion++) {
						auxAlternativesRatioPreferences[alternative1][alternative2] *= Math.pow(
								(_decisionMatrix[criterion][alternative1] / _decisionMatrix[criterion][alternative2]), _w[criterion]);
					}
				}
			}
		}
		
		return auxAlternativesRatioPreferences;
	}
	
	private void computeRelativeThresholdValuesWeightedProductModel() {
		_relativeThresholdValues = new Double[_numAlternatives][_numAlternatives][_numCriteria];

		for (int i = 0; i < _numAlternatives; i++) {
			for (int j = 0; j < _numAlternatives; j++) {
				for (int k = 0; k < _numCriteria; k++) {
					if (_absoluteThresholdValues[i][j][k] == null) {
						_relativeThresholdValues[i][j][k] = null;
					} else {
						_relativeThresholdValues[i][j][k] = _absoluteThresholdValues[i][j][k] * 100d;
					}
				}
			}
		}
	}
	
	public double[] getMinimumPercentPairAlternatives(int a1, int a2) {
		double[] percents = new double[_numCriteria];

		switch (_problem) {
		case MOST_CRITICAL_CRITERION:
			for (int k = 0; k < _numCriteria; k++) {
				if (_minimumPercentChangeInCriteriaWeights[a1][a2][k] != null) {
					percents[k] = _minimumPercentChangeInCriteriaWeights[a1][a2][k];
				}
			}
			break;
		case MOST_CRITICAL_MEASURE:
			for (int k = 0; k < _numCriteria; k++) {
				if (_relativeThresholdValues[a1][a2][k] != null) {
					percents[k] = _relativeThresholdValues[a1][a2][k];
				}
			}
			break;
		}

		return percents;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void readDecisionMatrix() {

		_decisionMatrix = new Double[_numCriteria][_numAlternatives];
		
		aggregateAlternativesNewWeights();
		
		Map<Pair<Alternative, Criterion>, Valuation> decisionMatrix = _aggregationPhase.getDecisionMatrix();
		int i = 0, j = 0;
		for (Alternative a : _elementsSet.getAlternatives()) {
			j = 0;
			for (Criterion c : _elementsSet.getAllCriteria()) {
				_decisionMatrix[j][i] = ((TwoTuple) decisionMatrix.get(new Pair(a, c))).calculateInverseDelta();
				j++;
			}
			i++;
		}
	}

	private void aggregateAlternativesNewWeights() {
		
		List<Double> w = new LinkedList<Double>();
		for(int i = 0; i < _elementsSet.getAllCriteria().size(); ++i) {
			w.add(_w[i]);
		}
		
		Map<ProblemElement, Object> criteriaOperatorWeights = new HashMap<ProblemElement, Object>();
		Map<Object, List<Double>> weights = new HashMap<Object, List<Double>>();
		weights.put(null, w);
		criteriaOperatorWeights.put(null, weights);
		_aggregationPhase.setCriteriaOperatorWeights(criteriaOperatorWeights);
		
		Set<ProblemElement> experts = new HashSet<ProblemElement>(), alternatives = new HashSet<ProblemElement>(), criteria = new HashSet<ProblemElement>();
		experts.addAll(_elementsSet.getAllExperts());
		alternatives.addAll(_elementsSet.getAlternatives());
		criteria.addAll(_elementsSet.getAllCriteria());
		_aggregationPhase.aggregateAlternatives(experts, alternatives, criteria);
	}

	private void normalizeDecisionMatrix() {
		if (!checkNormalizedMatrix()) {
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
				if (_decisionMatrix[i][j] > 1) {
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

	private void normalizeDecisionMatrixSumToOne() {
		double rowAcum;

		for (int c = 0; c < _numCriteria; ++c) {
			rowAcum = 0;
			for (int a = 0; a < _numAlternatives; ++a) {
				rowAcum += _decisionMatrix[c][a];
			}
			if (rowAcum != 1) {
				normalizeRow(c, Math.round(rowAcum * 10000d) / 10000d);
			}
		}
	}

	private void normalizeRow(int criterion, double rowAcum) {
		double value;
		for (int a = 0; a < _numAlternatives; ++a) {
			value = Math.round((_decisionMatrix[criterion][a] / rowAcum) * 10000d) / 10000d;
			_decisionMatrix[criterion][a] = value;
		}
	}

	public Domain getDomain() {
		return _aggregationPhase.getUnifiedDomain();
	}

	public void registerSensitivityAnalysisChangeListener(ISensitivityAnalysisChangeListener listener) {
		_listeners.add(listener);
	}

	public void unregisterSensitivityAnalysisChangeListener(ISensitivityAnalysisChangeListener listener) {
		_listeners.remove(listener);
	}

	public void notifySensitivityAnalysisChange() {
		for (ISensitivityAnalysisChangeListener listener : _listeners) {
			listener.notifySensitivityAnalysisChange();
		}
	}

	@Override
	public void notifyResolutionPhaseStateChange(ResolutionPhaseStateChangeEvent event) {

		if (event.getChange().equals(EResolutionPhaseStateChange.ACTIVATED)) {
			activate();
		}
	}

	@Override
	public IResolutionPhase copyStructure() {
		return new SensitivityAnalysis();
	}

	@Override
	public void copyData(IResolutionPhase iResolutionPhase) {
		SensitivityAnalysis sa = (SensitivityAnalysis) iResolutionPhase;

		clear();

		_absoluteAny = sa.getAbsoluteAny();
		_absoluteTop = sa.getAbsoluteTop();
		_alternativesFinalPreferences = sa.getAlternativesFinalPreferences();
		_decisionMatrix = sa.getDecisionMatrix();
		_minimumAbsoluteChangeInCriteriaWeights = sa.getMinimumAbsoluteChangeInCriteriaWeights();
		_minimumPercentChangeInCriteriaWeights = sa.getMinimumPercentChangeInCriteriaWeights();
		_numAlternatives = sa.getNumAlternatives();
		_numCriteria = sa.getNumCriteria();
		_ranking = sa.getRanking();
		_w = sa.getWeights();
	}

	@Override
	public void clear() {
		_absoluteAny.clear();
		_absoluteTop.clear();
		_alternativesFinalPreferences = null;
		_decisionMatrix = null;
		_minimumAbsoluteChangeInCriteriaWeights = null;
		_minimumPercentChangeInCriteriaWeights = null;
		_numAlternatives = -1;
		_numCriteria = -1;
		_ranking = null;
		_w = null;
	}

	@Override
	public void save(XMLWriter writer) throws WorkspaceContentPersistenceException {
		@SuppressWarnings("unused")
		XMLStreamWriter streamWriter = writer.getStreamWriter();
	}

	@Override
	public void read(XMLRead reader, Map<String, IResolutionPhase> buffer) throws WorkspaceContentPersistenceException {
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);

		return hcb.toHashCode();
	}

	@Override
	public IResolutionPhase clone() {
		SensitivityAnalysis result = null;

		try {
			result = (SensitivityAnalysis) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public boolean validate() {
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		_aggregationPhase = (AggregationPhase) pmm.getPhaseMethod(AggregationPhase.ID).getImplementation();

		if (_elementsSet.getAlternatives().isEmpty()) {
			return false;
		}

		if (_elementsSet.getCriteria().isEmpty()) {
			return false;
		}

		if (_elementsSet.getExperts().isEmpty()) {
			return false;
		}
		
		Method activatedMethod = MethodsManager.getInstance().getActiveMethod();
		if(activatedMethod != null) {
			String categoryMethod = activatedMethod.getCategory();
			if(categoryMethod.contains(Messages.SensitivityAnalysis_Multi_criteria_decision_analysis)) {
				if (_alternativesFinalPreferences == null) {
					return false;
				} else {
					return true;
				}
			}
		}
		
		if (_aggregationPhase.getCriteriaOperators().isEmpty() && _aggregationPhase.getExpertsOperators().isEmpty()) {
			return false;
		}

		Map<Pair<Alternative, Criterion>, Valuation> decisionMatrixAggregation = _aggregationPhase.getDecisionMatrix();
		for (Pair<Alternative, Criterion> pair : decisionMatrixAggregation.keySet()) {
			if (!(decisionMatrixAggregation.get(pair) instanceof TwoTuple)) {
				return false;
			}
		}


		return true;
	}

	@Override
	public void activate() {
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();
	}
}
