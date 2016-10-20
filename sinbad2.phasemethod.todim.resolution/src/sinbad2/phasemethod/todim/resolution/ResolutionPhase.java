package sinbad2.phasemethod.todim.resolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.AggregationOperatorsManager;
import sinbad2.aggregationoperator.owa.OWA;
import sinbad2.aggregationoperator.owa.YagerQuantifiers;
import sinbad2.core.utils.Pair;
import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.phasemethod.todim.unification.UnificationPhase;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.HesitantValuation;
import sinbad2.valuation.linguistic.LinguisticValuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class ResolutionPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.todim.resolution"; //$NON-NLS-1$

	private static final int P = 2;

	private int _numAlternatives;
	private int _numCriteria;

	private Map<Pair<Alternative, Criterion>, Valuation> _decisionMatrix;
	private Double[][] _centerOfGravityConsensusMatrix;
	private String[][] _trapezoidalConsensusMatrix;
	private Map<ValuationKey, Double> _distances;
	private Map<Pair<Expert, Criterion>, Double> _thresholdValues;

	private Map<Criterion, Double> _criteriaWeights;
	private Map<String, Double> _relativeWeights;

	private Map<Criterion, Map<Pair<Alternative, Alternative>, Double>> _dominanceDegreeByCriterion;
	private Map<Pair<Alternative, Alternative>, Double> _dominanceDegreeAlternatives;
	private Map<Alternative, Double> _globalDominance;

	private ProblemElementsSet _elementsSet;
	private ValuationSet _valuationSet;

	private UnificationPhase _unificationPhase;

	public LinkedHashMap<Alternative, Double> sortHashMapByValues(HashMap<Alternative, Double> passedMap) {
		List<Alternative> mapKeys = new ArrayList<>(passedMap.keySet());
		List<Double> mapValues = new ArrayList<>(passedMap.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);

		LinkedHashMap<Alternative, Double> sortedMap = new LinkedHashMap<>();

		Iterator<Double> valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			Double val = valueIt.next();
			Iterator<Alternative> keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				Alternative key = keyIt.next();
				Double comp1 = passedMap.get(key);
				Double comp2 = val;

				if (comp1.equals(comp2)) {
					keyIt.remove();
					sortedMap.put(key, val);
					break;
				}
			}
		}
		return sortedMap;
	}

	public ResolutionPhase() {
		_unificationPhase = (UnificationPhase) PhasesMethodManager.getInstance().getPhaseMethod(UnificationPhase.ID)
				.getImplementation();

		initializeConsesusMatrix();

		_decisionMatrix = new HashMap<Pair<Alternative, Criterion>, Valuation>();
		_trapezoidalConsensusMatrix = new String[_numAlternatives][_numCriteria];
		_distances = new HashMap<ValuationKey, Double>();

		_criteriaWeights = new HashMap<Criterion, Double>();
		_relativeWeights = new HashMap<String, Double>();

		_dominanceDegreeByCriterion = new HashMap<Criterion, Map<Pair<Alternative, Alternative>, Double>>();
		_dominanceDegreeAlternatives = new HashMap<Pair<Alternative, Alternative>, Double>();
		_globalDominance = new HashMap<Alternative, Double>();
		_thresholdValues = new HashMap<Pair<Expert, Criterion>, Double>();
	}

	private void initializeConsesusMatrix() {
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();
		_valuationSet = ValuationSetManager.getInstance().getActiveValuationSet();

		_numAlternatives = _elementsSet.getAlternatives().size();
		_numCriteria = _elementsSet.getAllCriteria().size();

		_centerOfGravityConsensusMatrix = new Double[_numAlternatives][_numCriteria];
		_trapezoidalConsensusMatrix = new String[_numAlternatives][_numCriteria];

		for (int a = 0; a < _numAlternatives; ++a) {
			for (int c = 0; c < _numCriteria; ++c) {
				_trapezoidalConsensusMatrix[a][c] = "(a,b,c,d)"; //$NON-NLS-1$
			}
		}
	}

	@Override
	public Map<ValuationKey, Valuation> getTwoTupleValuations() {
		return null;
	}

	public Map<ValuationKey, TrapezoidalFunction> getFuzzyValuations() {
		return _unificationPhase.getFuzzyValuations();
	}

	public void setDecisionMatrix(Map<Pair<Alternative, Criterion>, Valuation> decisionMatrix) {
		_decisionMatrix = decisionMatrix;
	}

	public Map<Pair<Alternative, Criterion>, Valuation> getDecisionMatrix() {
		return _decisionMatrix;
	}

	public void setDistances(Map<ValuationKey, Double> distances) {
		_distances = distances;
	}

	public Map<ValuationKey, Double> getDistances() {
		return _distances;
	}

	public void setCenterOfGravityConsensusMatrix(Double[][] consensusMatrix) {
		_centerOfGravityConsensusMatrix = consensusMatrix;
	}

	public Double[][] getCenterOfGravityConsesusMatrix() {
		return _centerOfGravityConsensusMatrix;
	}

	public void setTrapezoidalConsensusMatrix(String[][] consensusMatrix) {
		_trapezoidalConsensusMatrix = consensusMatrix;
	}

	public String[][] getTrapezoidalConsensusMatrix() {
		return _trapezoidalConsensusMatrix;
	}

	public Map<Criterion, Double> getCriteriaWeights() {
		return _criteriaWeights;
	}

	public void setCriteriaWeights(Map<Criterion, Double> criteriaWeights) {
		_criteriaWeights = criteriaWeights;
	}

	public Map<Pair<Expert, Criterion>, Double> getThresholdValues() {
		return _thresholdValues;
	}

	public void setThresholdValues(Map<Pair<Expert, Criterion>, Double> thresholdValues) {
		_thresholdValues = thresholdValues;
	}

	public void setRelativeWeights(Map<String, Double> relativeWeights) {
		_relativeWeights = relativeWeights;
	}

	public Map<String, Double> getRelativeWeights() {
		return _relativeWeights;
	}

	public void setDominanceDegreeByCriterion(
			Map<Criterion, Map<Pair<Alternative, Alternative>, Double>> dominanceDegreeByCriterion) {
		_dominanceDegreeByCriterion = dominanceDegreeByCriterion;
	}

	public Map<Criterion, Map<Pair<Alternative, Alternative>, Double>> getDominanceDegreeByCriterion() {
		return _dominanceDegreeByCriterion;
	}

	public void setDominanceDegreeAlternatives(
			Map<Pair<Alternative, Alternative>, Double> dominanceDegreeAlternatives) {
		_dominanceDegreeAlternatives = dominanceDegreeAlternatives;
	}

	public Map<Pair<Alternative, Alternative>, Double> getDominanceDegreeAlternatives() {
		return _dominanceDegreeAlternatives;
	}

	public void setGlobalDominance(Map<Alternative, Double> globalDominance) {
		_globalDominance = globalDominance;
	}

	public Map<Alternative, Double> getGlobalDominance() {
		return _globalDominance;
	}

	@Override
	public Domain getUnifiedDomain() {
		return null;
	}

	@Override
	public void setUnifiedDomain(Domain domain) {
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<Pair<Expert, Criterion>, Double> calculateThresholdValues() {

		_thresholdValues.clear();

		Map<ValuationKey, Valuation> valuations = _valuationSet.getValuations();
		Map<ValuationKey, TrapezoidalFunction> fuzzyValuations = getFuzzyValuations();
		for (ValuationKey vk : fuzzyValuations.keySet()) {

			Alternative aFGC = new Alternative("null_threshold");
			ValuationKey vkFGC = new ValuationKey(vk.getExpert(), aFGC, vk.getCriterion());
			LinguisticValuation v = (LinguisticValuation) valuations.get(vkFGC);
			FuzzySet knowledgeDomain = (FuzzySet) v.getDomain();

			double knowledge = 0;
			if (knowledgeDomain.getLabelSet().getPos(v.getLabel()) == 0) {
				knowledge = 1.3;
			} else if (knowledgeDomain.getLabelSet().getPos(v.getLabel()) == 1) {
				knowledge = 1.1;
			} else if (knowledgeDomain.getLabelSet().getPos(v.getLabel()) == 2) {
				knowledge = 0.9;
			} else if (knowledgeDomain.getLabelSet().getPos(v.getLabel()) == 3) {
				knowledge = 0.7;
			} else if (knowledgeDomain.getLabelSet().getPos(v.getLabel()) == 4) {
				knowledge = 0.5;
			} else if (knowledgeDomain.getLabelSet().getPos(v.getLabel()) == 5) {
				knowledge = 0.3;
			} else {
				knowledge = 0.1;
			}

			_thresholdValues.put(new Pair(vk.getExpert(), vk.getCriterion()), knowledge);
		}
		
		return _thresholdValues;
	}

	public Double[][] calculateConsensusMatrixCenterOfGravity() {
		;
		Double cog;
		String trapezoidalNumber;

		if (_centerOfGravityConsensusMatrix != null && _trapezoidalConsensusMatrix != null) {
			for (int al = 0; al < _numAlternatives; ++al) {
				for (int cr = 0; cr < _numCriteria; ++cr) {
					trapezoidalNumber = (String) _trapezoidalConsensusMatrix[al][cr];
					cog = transformToTrapezoidalFunction(trapezoidalNumber).centroid();
					_centerOfGravityConsensusMatrix[al][cr] = Math.round(cog * 1000d) / 1000d;
				}
			}
		}

		return (Double[][]) _centerOfGravityConsensusMatrix;
	}

	public Double[][] calculateConsensusMatrixCenterOfGravity(Double[][] result) {
		double cog;
		String trapezoidalNumber;

		if (_centerOfGravityConsensusMatrix != null && _trapezoidalConsensusMatrix != null) {
			for (int al = 0; al < _numAlternatives; ++al) {
				for (int cr = 0; cr < _numCriteria; ++cr) {
					trapezoidalNumber = (String) _trapezoidalConsensusMatrix[al][cr];
					cog = transformToTrapezoidalFunction(trapezoidalNumber).centroid();
					result[al][cr] = Math.round(cog * 1000d) / 1000d;
				}
			}
		}

		return (Double[][]) result;
	}

	public Map<Criterion, Double> getImportanceCriteriaWeights() {
		Map<Criterion, List<TrapezoidalFunction>> expertsEnvelopeWeightsForEachCriterion = new HashMap<Criterion, List<TrapezoidalFunction>>();
		List<TrapezoidalFunction> envelopeWeights;

		if (_criteriaWeights.isEmpty()) {
			ValuationSetManager vsm = ValuationSetManager.getInstance();
			ValuationSet vs = vsm.getActiveValuationSet();

			Valuation v = null;
			Map<ValuationKey, Valuation> valuations = vs.getValuations();
			for (ValuationKey vk : valuations.keySet()) {
				if (vk.getAlternative().getId().equals("null_importance")) {
					if (expertsEnvelopeWeightsForEachCriterion.get(vk.getCriterion()) != null) {
						envelopeWeights = expertsEnvelopeWeightsForEachCriterion.get(vk.getCriterion());
					} else {
						envelopeWeights = new LinkedList<TrapezoidalFunction>();
					}
					v = valuations.get(vk);
					envelopeWeights.add(calculateFuzzyEnvelope(vk, (HesitantValuation) v, (FuzzySet) v.getDomain()));
					expertsEnvelopeWeightsForEachCriterion.put(vk.getCriterion(), envelopeWeights);
				}
			}
		}

		calculateWeights(expertsEnvelopeWeightsForEachCriterion);

		return _criteriaWeights;
	}

	private void calculateWeights(Map<Criterion, List<TrapezoidalFunction>> expertsEnvelopeWeightsForEachCriterion) {
		double acum;

		for (Criterion c : expertsEnvelopeWeightsForEachCriterion.keySet()) {
			acum = 0;
			List<TrapezoidalFunction> envelopeFunctions = expertsEnvelopeWeightsForEachCriterion.get(c);
			for (TrapezoidalFunction envelope : envelopeFunctions) {
				acum += envelope.centroid();
			}
			_criteriaWeights.put(c, Math.round((acum / envelopeFunctions.size()) * 1000d) / 1000d);
		}

		normalizeWeights();
	}

	private void normalizeWeights() {

		double sum = 0;

		for (Criterion c : _criteriaWeights.keySet()) {
			sum += _criteriaWeights.get(c);
		}

		for (Criterion c : _criteriaWeights.keySet()) {
			_criteriaWeights.put(c, Math.round((_criteriaWeights.get(c) / sum) * 1000d) / 1000d);
		}
	}

	public Map<String, Double> calculateRelativeWeights() {
		_relativeWeights = new HashMap<String, Double>();

		double weightReference = Double.MIN_VALUE, weight;
		for (Criterion c : _criteriaWeights.keySet()) {
			weight = _criteriaWeights.get(c);
			if (weight > weightReference) {
				weightReference = weight;
			}
		}

		List<Criterion> criteria = _elementsSet.getAllCriteria();
		for (int i = 0; i < criteria.size(); ++i) {
			_relativeWeights.put(criteria.get(i).getCanonicalId(),
					Math.round((_criteriaWeights.get(criteria.get(i)) / weightReference) * 1000d) / 1000d);
		}

		return _relativeWeights;
	}

	private TrapezoidalFunction calculateFuzzyEnvelope(ValuationKey vk, HesitantValuation valuation, FuzzySet domain) {
		double a, b, c, d;
		int g = domain.getLabelSet().getCardinality();
		Boolean lower = null;

		AggregationOperatorsManager aggregationOperatorManager = AggregationOperatorsManager.getInstance();
		AggregationOperator owa = aggregationOperatorManager.getAggregationOperator(OWA.ID);

		if (valuation.isPrimary()) {
			IMembershipFunction semantic = valuation.getLabel().getSemantic();
			a = semantic.getCoverage().getMin();
			b = semantic.getCenter().getMin();
			c = semantic.getCenter().getMax();
			d = semantic.getCoverage().getMax();
		} else {
			int envelope[] = valuation.getEnvelopeIndex();
			if (valuation.isUnary()) {
				switch (valuation.getUnaryRelation()) {
				case LowerThan:
					lower = Boolean.valueOf(true);
					break;
				case AtMost:
					lower = Boolean.valueOf(true);
					break;
				default:
					lower = Boolean.valueOf(false);
					break;
				}
			} else {
				lower = null;
			}

			YagerQuantifiers.NumeredQuantificationType nqt = YagerQuantifiers.NumeredQuantificationType.FilevYager;
			List<Double> weights = new LinkedList<Double>();
			double[] auxWeights = YagerQuantifiers.QWeighted(nqt, g - 1, envelope, lower);

			weights.add(new Double(-1));
			for (Double weight : auxWeights) {
				weights.add(weight);
			}

			if (lower == null) {
				a = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[0]).getSemantic().getCoverage()
						.getMin();
				d = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[1]).getSemantic().getCoverage()
						.getMax();
				if (envelope[0] + 1 == envelope[1]) {
					b = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[0]).getSemantic().getCenter()
							.getMin();
					c = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[1]).getSemantic().getCenter()
							.getMax();
				} else {
					int sum = envelope[1] + envelope[0];
					int top;
					if (sum % 2 == 0) {
						top = sum / 2;
					} else {
						top = (sum - 1) / 2;
					}
					List<Valuation> valuations = new LinkedList<Valuation>();
					for (int i = envelope[0]; i <= top; i++) {
						valuations.add(new TwoTuple(domain, domain.getLabelSet().getLabel(i)));
					}

					Valuation aux = ((OWA) owa).aggregate(valuations, weights);
					b = ((TwoTuple) aux).calculateInverseDelta() / ((double) g - 1);
					c = 2D * domain.getLabelSet().getLabel(top).getSemantic().getCenter().getMin() - b;
				}
			} else {
				List<Valuation> valuations = new LinkedList<Valuation>();
				for (int i = envelope[0]; i <= envelope[1]; i++) {
					valuations.add(new TwoTuple(domain, domain.getLabelSet().getLabel(i)));
				}

				Valuation aux = ((OWA) owa).aggregate(valuations, weights);
				if (lower.booleanValue()) {
					a = 0.0D;
					b = 0.0D;
					c = ((TwoTuple) aux).calculateInverseDelta() / ((double) g - 1);
					d = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[1]).getSemantic()
							.getCoverage().getMax();
				} else {
					a = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[0]).getSemantic()
							.getCoverage().getMin();
					b = ((TwoTuple) aux).calculateInverseDelta() / ((double) g - 1);
					c = 1.0D;
					d = 1.0D;
				}
			}
		}

		return new TrapezoidalFunction(new double[] { a, b, c, d });

	}

	public Map<Criterion, Map<Pair<Alternative, Alternative>, Double>> calculateDominanceDegreeByCriterionCenterOfGravity(
			double attenuationFactor) {
		_dominanceDegreeByCriterion = new HashMap<Criterion, Map<Pair<Alternative, Alternative>, Double>>();

		double acumSumRelativeWeights = getAcumSumRelativeWeights();

		int criterionIndex = 0, a1Index = 0, a2Index = 0;
		double dominance = 0, condition = 0;
		for (Criterion c : _elementsSet.getAllCriteria()) {
			a1Index = 0;
			for (Alternative a1 : _elementsSet.getAlternatives()) {
				a2Index = 0;
				for (Alternative a2 : _elementsSet.getAlternatives()) {
					if (a1 != a2) {
						condition = (Double) _centerOfGravityConsensusMatrix[a1Index][criterionIndex]
								- (Double) _centerOfGravityConsensusMatrix[a2Index][criterionIndex];
						if (c.isCost()) {
							if (condition > 0) {
								double inverse = (Double) _centerOfGravityConsensusMatrix[a2Index][criterionIndex]
										- (Double) _centerOfGravityConsensusMatrix[a1Index][criterionIndex];
								dominance = (-1d / attenuationFactor);
								dominance *= Math.sqrt(
										(inverse * acumSumRelativeWeights) / _relativeWeights.get(c.getCanonicalId()));
							} else if (condition < 0) {
								dominance = Math.sqrt((condition * _relativeWeights.get(c.getCanonicalId()))
										/ acumSumRelativeWeights);
							} else {
								dominance = 0;
							}
						} else {
							if (condition > 0) {
								dominance = Math.sqrt((condition * _relativeWeights.get(c.getCanonicalId()))
										/ acumSumRelativeWeights);
							} else if (condition < 0) {
								double inverse = (Double) _centerOfGravityConsensusMatrix[a2Index][criterionIndex]
										- (Double) _centerOfGravityConsensusMatrix[a1Index][criterionIndex];
								dominance = (-1d / attenuationFactor);
								dominance *= Math.sqrt(
										(inverse * acumSumRelativeWeights) / _relativeWeights.get(c.getCanonicalId()));
							} else {
								dominance = 0;
							}
						}

						Pair<Alternative, Alternative> pairAlternatives = new Pair<Alternative, Alternative>(a1, a2);
						Map<Pair<Alternative, Alternative>, Double> pairAlternativesDominance;
						if (_dominanceDegreeByCriterion.get(c) != null) {
							pairAlternativesDominance = _dominanceDegreeByCriterion.get(c);
						} else {
							pairAlternativesDominance = new HashMap<Pair<Alternative, Alternative>, Double>();
						}
						pairAlternativesDominance.put(pairAlternatives, dominance);
						_dominanceDegreeByCriterion.put(c, pairAlternativesDominance);

					}
					a2Index++;
				}
				a1Index++;
			}
			criterionIndex++;
		}

		return _dominanceDegreeByCriterion;
	}

	private double getAcumSumRelativeWeights() {
		double acum = 0;

		for (Criterion c : _elementsSet.getAllCriteria()) {
			acum += _relativeWeights.get(c.getCanonicalId());
		}
		return acum;
	}

	public Map<Criterion, Map<Pair<Alternative, Alternative>, Double>> calculateDominanceDegreeByCriterionFuzzyNumber(
			double attenuationFactor) {
		_dominanceDegreeByCriterion = new HashMap<Criterion, Map<Pair<Alternative, Alternative>, Double>>();

		double acumSumRelativeWeights = getAcumSumRelativeWeights();

		int criterionIndex = 0, a1Index = 0, a2Index = 0;
		double dominance = 0, condition = 0;
		String trapezoidalNumber1, trapezoidalNumber2;
		TrapezoidalFunction tpf1, tpf2;

		for (Criterion c : _elementsSet.getAllCriteria()) {
			a1Index = 0;
			for (Alternative a1 : _elementsSet.getAlternatives()) {
				a2Index = 0;
				for (Alternative a2 : _elementsSet.getAlternatives()) {

					if (a1 != a2) {

						trapezoidalNumber1 = (String) _trapezoidalConsensusMatrix[a1Index][criterionIndex];
						trapezoidalNumber2 = (String) _trapezoidalConsensusMatrix[a2Index][criterionIndex];

						tpf1 = transformToTrapezoidalFunction(trapezoidalNumber1);
						tpf2 = transformToTrapezoidalFunction(trapezoidalNumber2);

						condition = tpf1.getSimpleDefuzzifiedValue() - tpf2.getSimpleDefuzzifiedValue();
						if (c.isCost()) {
							if (condition > 0) {
								dominance = (-1d / attenuationFactor);
								dominance *= Math.sqrt((tpf1.distance(tpf2, P) * acumSumRelativeWeights)
										/ _relativeWeights.get(c.getCanonicalId()));
							} else if (condition <= 0) {
								dominance = Math
										.sqrt((tpf1.distance(tpf2, P) * _relativeWeights.get(c.getCanonicalId()))
												/ acumSumRelativeWeights);
							}
						} else {
							if (condition >= 0) {
								dominance = Math
										.sqrt((tpf1.distance(tpf2, P) * _relativeWeights.get(c.getCanonicalId()))
												/ acumSumRelativeWeights);
							} else if (condition < 0) {
								dominance = (-1d / attenuationFactor);
								dominance *= Math.sqrt((tpf1.distance(tpf2, P) * acumSumRelativeWeights)
										/ _relativeWeights.get(c.getCanonicalId()));
							}
						}

						Pair<Alternative, Alternative> pairAlternatives = new Pair<Alternative, Alternative>(a1, a2);
						Map<Pair<Alternative, Alternative>, Double> pairAlternativesDominance;
						if (_dominanceDegreeByCriterion.get(c) != null) {
							pairAlternativesDominance = _dominanceDegreeByCriterion.get(c);
						} else {
							pairAlternativesDominance = new HashMap<Pair<Alternative, Alternative>, Double>();
						}
						pairAlternativesDominance.put(pairAlternatives, dominance);
						_dominanceDegreeByCriterion.put(c, pairAlternativesDominance);
					}
					a2Index++;
				}
				a1Index++;
			}
			criterionIndex++;
		}

		return _dominanceDegreeByCriterion;
	}

	private TrapezoidalFunction transformToTrapezoidalFunction(String trapezoidalNumber1) {
		trapezoidalNumber1 = trapezoidalNumber1.replace("(", ""); //$NON-NLS-1$ //$NON-NLS-2$
		trapezoidalNumber1 = trapezoidalNumber1.replace(")", ""); //$NON-NLS-1$ //$NON-NLS-2$
		String[] limits = trapezoidalNumber1.split(","); //$NON-NLS-1$
		double aT1 = Double.parseDouble(limits[0]);
		double bT1 = Double.parseDouble(limits[1]);
		double cT1 = Double.parseDouble(limits[2]);
		double dT1 = Double.parseDouble(limits[3]);
		double[] limits1 = new double[] { aT1, bT1, cT1, dT1 };

		return new TrapezoidalFunction(limits1);
	}

	public Map<Pair<Alternative, Alternative>, Double> calculateDominaceDegreeOverAlternatives() {

		_dominanceDegreeAlternatives = new HashMap<Pair<Alternative, Alternative>, Double>();

		double acum;
		for (Alternative a1 : _elementsSet.getAlternatives()) {
			for (Alternative a2 : _elementsSet.getAlternatives()) {
				acum = 0;
				if (a1 != a2) {
					for (Criterion c : _dominanceDegreeByCriterion.keySet()) {
						Map<Pair<Alternative, Alternative>, Double> pairAlternativesDominance = _dominanceDegreeByCriterion
								.get(c);
						for (Pair<Alternative, Alternative> pair : pairAlternativesDominance.keySet()) {
							if (a1.equals(pair.getLeft()) && a2.equals(pair.getRight())) {
								acum += pairAlternativesDominance.get(pair);
							}
						}
					}
					_dominanceDegreeAlternatives.put(new Pair<Alternative, Alternative>(a1, a2), acum);
				}
			}
		}
		return _dominanceDegreeAlternatives;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public LinkedHashMap<Alternative, Double> calculateGlobalDominance() {
		Map<Alternative, Double> acumDominanceDegreeAlternatives = new HashMap<Alternative, Double>();

		double acum, max = Double.MIN_VALUE, min = Double.MAX_VALUE;
		for (Alternative a : _elementsSet.getAlternatives()) {
			acum = 0;
			for (Pair<Alternative, Alternative> pairAlternatives : _dominanceDegreeAlternatives.keySet()) {
				if (a.equals(pairAlternatives.getLeft())) {
					acum += _dominanceDegreeAlternatives.get(pairAlternatives);
				}
			}

			if (acum < min) {
				min = acum;
			}

			if (acum > max) {
				max = acum;
			}

			acumDominanceDegreeAlternatives.put(a, acum);
		}

		double dominance;
		for (Alternative a : acumDominanceDegreeAlternatives.keySet()) {
			dominance = (acumDominanceDegreeAlternatives.get(a) - min) / (max - min);
			_globalDominance.put(a, dominance);
		}

		return sortHashMapByValues((HashMap) _globalDominance);
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
		_distances = resolution.getDistances();
		_centerOfGravityConsensusMatrix = resolution.getCenterOfGravityConsesusMatrix();
		_criteriaWeights = resolution.getImportanceCriteriaWeights();
		_relativeWeights = resolution.getRelativeWeights();
		_dominanceDegreeByCriterion = resolution.getDominanceDegreeByCriterion();
		_dominanceDegreeAlternatives = resolution.getDominanceDegreeAlternatives();
		_globalDominance = resolution.getGlobalDominance();
		_thresholdValues = resolution.getThresholdValues();
		_trapezoidalConsensusMatrix = resolution.getTrapezoidalConsensusMatrix();
	}

	@Override
	public void clear() {
		_decisionMatrix.clear();
		_distances.clear();
		_centerOfGravityConsensusMatrix = new Double[_numAlternatives][_numCriteria];
		_trapezoidalConsensusMatrix = new String[_numAlternatives][_numCriteria];
		initializeConsesusMatrix();

		_criteriaWeights.clear();
		_relativeWeights.clear();
		_dominanceDegreeByCriterion.clear();
		_dominanceDegreeAlternatives.clear();
		_globalDominance.clear();
		_thresholdValues.clear();
	}

	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		if (event.getChange().equals(EPhaseMethodStateChange.ACTIVATED)) {
			activate();
		}
	}

	@Override
	public void activate() {
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
