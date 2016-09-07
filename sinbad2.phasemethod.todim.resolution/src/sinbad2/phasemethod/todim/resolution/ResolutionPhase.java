package sinbad2.phasemethod.todim.resolution;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.element.ProblemElement;
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
import sinbad2.valuation.hesitant.HesitantValuation;
import sinbad2.valuation.linguistic.LinguisticValuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class ResolutionPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.todim.resolution"; //$NON-NLS-1$

	private static final Integer ATTENUATION_FACTOR = 1;
	private static final int P = 2;

	private Map<ValuationKey, Valuation> _twoTupleValuations;
	
	private Map<Pair<Alternative, Criterion>, Valuation> _decisionMatrix;
	private Map<ValuationKey, Double> _distances;
	private Object[][] _consensusMatrix;
	private String[][] _trapezoidalConsensusMatrix;
	
	private int _numAlternatives;
	private int _numCriteria;
	
	private Criterion _referenceCriterion;
	
	private Map<Criterion, Double> _criteriaWeights;
	private Map<String, Double> _relativeWeights;
	
	private Map<Criterion, Map<Pair<Alternative, Alternative>, Double>> _dominanceDegreeByCriterion;
	private Map<Pair<Alternative, Alternative>, Double> _dominanceDegreeAlternatives;
	private Map<Alternative, Double> _globalDominance;

	private ProblemElementsSet _elementsSet;
	private ValuationSet _valuationSet;
	
	private AggregationPhase _aggregationPhase;

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
	
	private static class MapUtil {
		
		public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
			List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
			
			Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
				
				public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
					return (o1.getValue()).compareTo(o2.getValue());
				}
			});

			Map<K, V> result = new LinkedHashMap<K, V>();
			for (Map.Entry<K, V> entry : list) {
				result.put(entry.getKey(), entry.getValue());
				
			}
			
			return result;
		}
	}

	public ResolutionPhase() {		
		_aggregationPhase = (AggregationPhase) PhasesMethodManager.getInstance().getPhaseMethod(AggregationPhase.ID).getImplementation();
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = valuationSetManager.getActiveValuationSet();

		_twoTupleValuations = new HashMap<ValuationKey, Valuation>();
		
		_numAlternatives = _elementsSet.getAlternatives().size();
		_numCriteria = _elementsSet.getAllCriteria().size();

		_consensusMatrix = new Object[_numAlternatives][_numCriteria];
		_trapezoidalConsensusMatrix = new String[_numAlternatives][_numCriteria];
		
		initializeConsesusMatrix();
		
		_decisionMatrix = new HashMap<Pair<Alternative, Criterion>, Valuation>();
		_distances = new HashMap<ValuationKey, Double>();

		_criteriaWeights = new HashMap<Criterion, Double>();
		_relativeWeights = new HashMap<String, Double>();
		
		_dominanceDegreeByCriterion = new HashMap<Criterion, Map<Pair<Alternative, Alternative>, Double>>();
		_dominanceDegreeAlternatives = new HashMap<Pair<Alternative, Alternative>, Double>();
		_globalDominance = new HashMap<Alternative, Double>();
		
		_referenceCriterion = null;
	}
	
	public void setTwoTupleValuations(Map<ValuationKey, Valuation> twoTupleValuations) {
		_twoTupleValuations = twoTupleValuations;
	}
	
	@Override
	public Map<ValuationKey, Valuation> getTwoTupleValuations() {
		return _twoTupleValuations;
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

	public void setConsensusMatrix(Object[][] consensusMatrix) {
		_consensusMatrix = consensusMatrix;
	}

	public Object[][] getConsensusMatrix() {
		return _consensusMatrix;
	}
	
	public void setTrapezoidalConsensusMatrix(String[][] consensusMatrix) {
		_trapezoidalConsensusMatrix = consensusMatrix;
	}

	public String[][] getTrapezoidalConsensusMatrix() {
		return _trapezoidalConsensusMatrix;
	}

	public void setCriteriaWeights(Map<Criterion, Double> criteriaWeights) {
		_criteriaWeights = criteriaWeights;
	}

	public Map<Pair<Alternative, Criterion>, Valuation> calculateDecisionMatrix() {
		_decisionMatrix = _aggregationPhase.getDecisionMatrix();
		
		return _decisionMatrix;
	}
	
	@SuppressWarnings("unchecked")
	public List<Double> getExpertsWeights() {
		return (List<Double>) ((Map<ProblemElement, Object>) _aggregationPhase.getExpertsOperatorWeights().get(null)).get(null);
	}

	@Override
	public Domain getUnifiedDomain() {
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String[]> calculateDistance() {
		List<String[]> result = new LinkedList<String[]>();
		
		_twoTupleValuations = _aggregationPhase.getTwoTupleValuations();
		
		calculateValuationsDistance();
		
		Map<ValuationKey, Valuation> valuations = _valuationSet.getValuations();
		
		for(ValuationKey vk: _twoTupleValuations.keySet()) {
			if(!vk.getExpert().getId().endsWith("flintstones_gathering_cloud") && vk.getAlternative() != null) { //$NON-NLS-1$
				String[] data = new String[7];
				data[0] = vk.getExpert().getId();
				data[1] = vk.getAlternative().getId();
				data[2] = vk.getCriterion().getId();
				data[3] = _twoTupleValuations.get(vk).changeFormatValuationToString();
				Valuation aggregatedValuation = _decisionMatrix.get(new Pair(vk.getAlternative(), vk.getCriterion()));
				
				if(aggregatedValuation == null) {
					data[4] = ""; //$NON-NLS-1$
				} else {
					data[4] = aggregatedValuation.changeFormatValuationToString();
				}
					
				data[5] = Double.toString(Math.round(_distances.get(vk) * 10000d) / 10000d);
		
				Alternative aFGC = new Alternative("null_threshold");
				ValuationKey vkFGC = new ValuationKey(vk.getExpert(), aFGC, vk.getCriterion());
				LinguisticValuation v = (LinguisticValuation) valuations.get(vkFGC);
				FuzzySet knowledgeDomain = (FuzzySet) v.getDomain();
				
				double knowledge = 0;
				if(knowledgeDomain.getLabelSet().getPos(v.getLabel()) == 0) {
					knowledge = 0.013;
				} else if(knowledgeDomain.getLabelSet().getPos(v.getLabel()) == 1) {
					knowledge = 0.011;
				} else if(knowledgeDomain.getLabelSet().getPos(v.getLabel()) == 2) {
					knowledge = 0.009;
				} else if(knowledgeDomain.getLabelSet().getPos(v.getLabel()) == 3) {
					knowledge = 0.007;
				} else if(knowledgeDomain.getLabelSet().getPos(v.getLabel()) == 4) {
					knowledge = 0.005;
				} else if(knowledgeDomain.getLabelSet().getPos(v.getLabel()) == 5) {
					knowledge = 0.003;
				} else {
					knowledge = 0.001;
				}
				
				data[6] = Double.toString(knowledge);
				
				result.add(data);
			}
		}
		
		Collections.sort(result, new DataComparator());
		
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void calculateValuationsDistance() {
		List<Alternative> alternatives = _elementsSet.getAlternatives();
		List<Criterion> criteria = _elementsSet.getAllCriteria();
		
		for(int a = 0; a < alternatives.size(); ++a) {
			for(int c = 0; c < criteria.size(); ++c) {
				for(ValuationKey vk: _twoTupleValuations.keySet()) {
					if(alternatives.get(a).equals(vk.getAlternative()) && criteria.get(c).equals(vk.getCriterion())) {
						TwoTuple v = (TwoTuple) _twoTupleValuations.get(vk);
						LabelLinguisticDomain label = v.getLabel();
						TrapezoidalFunction semantic = (TrapezoidalFunction) label.getSemantic();
		
						double[] limits = semantic.getLimits();
						double aLimit = limits[0];
						double bLimit = limits[1];
						double cLimit = limits[2];
						double dLimit = limits[3];
						
						TwoTuple overallValuation =  (TwoTuple) _decisionMatrix.get(new Pair(alternatives.get(a), criteria.get(c)));
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

	public Map<Criterion, Double> getImportanceCriteriaWeights() {
		Map<Criterion, List<TrapezoidalFunction>> expertsEnvelopeWeightsForEachCriterion = new HashMap<Criterion, List<TrapezoidalFunction>>();
		List<TrapezoidalFunction> envelopeWeights;
		
		if(_criteriaWeights.isEmpty()) {
			ValuationSetManager vsm = ValuationSetManager.getInstance();
			ValuationSet vs = vsm.getActiveValuationSet();
			
			Valuation v = null;
			Map<ValuationKey, Valuation> valuations = vs.getValuations();
			for(ValuationKey vk: valuations.keySet()) {
				if(vk.getAlternative().getId().equals("null_importance")) {
					if(expertsEnvelopeWeightsForEachCriterion.get(vk.getCriterion()) != null) {
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

	private TrapezoidalFunction calculateFuzzyEnvelope(ValuationKey vk, HesitantValuation valuation, FuzzySet domain) {	
		double a, b, c, d;
		int g = domain.getLabelSet().getCardinality();
		Boolean lower = null;
		
		AggregationOperatorsManager aggregationOperatorManager = AggregationOperatorsManager.getInstance();
		AggregationOperator owa = aggregationOperatorManager.getAggregationOperator(OWA.ID);
		
        if(valuation.isPrimary()) {
            IMembershipFunction semantic = valuation.getLabel().getSemantic();
            a = semantic.getCoverage().getMin();
            b = semantic.getCenter().getMin();
            c = semantic.getCenter().getMax();
            d = semantic.getCoverage().getMax();
        } else {
            int envelope[] = valuation.getEnvelopeIndex();
            if(valuation.isUnary()) {
                switch(valuation.getUnaryRelation()) {
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
			for(Double weight : auxWeights) {
				weights.add(weight);
			}
            
            if(lower == null) {
                a = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[0]).getSemantic().getCoverage().getMin();
                d = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[1]).getSemantic().getCoverage().getMax();
                if(envelope[0] + 1 == envelope[1]) {
                    b = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[0]).getSemantic().getCenter().getMin();
                    c = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[1]).getSemantic().getCenter().getMax();
                } else {
                    int sum = envelope[1] + envelope[0];
                    int top;
                    if(sum % 2 == 0) {
                        top = sum / 2;
                    } else {
                        top = (sum - 1) / 2;
                    }
                    List<Valuation> valuations = new LinkedList<Valuation>();
                    for(int i = envelope[0]; i <= top; i++) {
                        valuations.add(new TwoTuple(domain, domain.getLabelSet().getLabel(i)));
                    }

                    Valuation aux = ((OWA) owa).aggregate(valuations, weights);
                    b = ((TwoTuple) aux).calculateInverseDelta() / ((double) g - 1);
                    c = 2D * domain.getLabelSet().getLabel(top).getSemantic().getCenter().getMin() - b;
                }
            } else {
                List<Valuation> valuations = new LinkedList<Valuation>();
                for(int i = envelope[0]; i <= envelope[1]; i++) {
                    valuations.add(new TwoTuple(domain, domain.getLabelSet().getLabel(i)));
                }

                Valuation aux = ((OWA) owa).aggregate(valuations, weights);
                if(lower.booleanValue()) {
                    a = 0.0D;
                    b = 0.0D;
                    c = ((TwoTuple) aux).calculateInverseDelta() / ((double) g - 1);
                    d = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[1]).getSemantic().getCoverage().getMax();
                } else {
                    a = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[0]).getSemantic().getCoverage().getMin();
                    b = ((TwoTuple) aux).calculateInverseDelta() / ((double) g - 1);
                    c = 1.0D;
                    d = 1.0D;
                }
            }
        }
        
        return new TrapezoidalFunction(new double[] {a, b, c, d});
    
	}

	private void calculateWeights(Map<Criterion, List<TrapezoidalFunction>> expertsEnvelopeWeightsForEachCriterion) {
		double acum;
		
		for(Criterion c: expertsEnvelopeWeightsForEachCriterion.keySet()) {
			acum = 0;
			List<TrapezoidalFunction> envelopeFunctions = expertsEnvelopeWeightsForEachCriterion.get(c);
			for(TrapezoidalFunction envelope: envelopeFunctions) {
				acum += envelope.centroid();
			}
			_criteriaWeights.put(c, Math.round((acum / envelopeFunctions.size()) * 1000d) / 1000d);
		}
		
		normalizeWeights();
	}
	
	private void normalizeWeights() {

		double sum = 0;

		for(Criterion c: _criteriaWeights.keySet()) {
			sum += _criteriaWeights.get(c);
		}

		for(Criterion c: _criteriaWeights.keySet()) {
			_criteriaWeights.put(c, Math.round((_criteriaWeights.get(c) / sum) * 1000d) / 1000d);
		}
	}
	
	public void setReferenceCriterion(Criterion referenceCriterion) {
		_referenceCriterion = referenceCriterion;
	}

	public Criterion getReferenceCriterion() {
		return _referenceCriterion;
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
	
	private void initializeConsesusMatrix() {
		
		for(int a = 0; a < _numAlternatives; ++a) {
			for(int c = 0; c < _numCriteria; ++c) {
				_consensusMatrix[a][c] = "(a,b,c,d)"; //$NON-NLS-1$
			}
		}
	}
	
	public Map<String, Double> calculateRelativeWeights() {
		_relativeWeights = new HashMap<String, Double>();
		
		if (_referenceCriterion != null) {
			Double weightReference = _criteriaWeights.get(_referenceCriterion);
			List<Criterion> criteria = _elementsSet.getAllCriteria();
			for (int i = 0; i < criteria.size(); ++i) {
				_relativeWeights.put(criteria.get(i).getCanonicalId(), Math.round((_criteriaWeights.get(criteria.get(i)) / weightReference) * 1000) / 1000d);
			}
		}
		return _relativeWeights;
	}

	public Map<Criterion, Map<Pair<Alternative, Alternative>, Double>> calculateDominanceDegreeByCriterionCOG() {
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
						condition = (Double) _consensusMatrix[a1Index][criterionIndex] - (Double) _consensusMatrix[a2Index][criterionIndex];
						if (condition > 0) {
							dominance = Math.sqrt((condition * _relativeWeights.get(c.getCanonicalId())) / acumSumRelativeWeights);
						} else if (condition < 0) {
							double inverse = (Double) _consensusMatrix[a2Index][criterionIndex] - (Double) _consensusMatrix[a1Index][criterionIndex];
							dominance = (-1d / ATTENUATION_FACTOR);
							dominance *= Math.sqrt((inverse * acumSumRelativeWeights) / _relativeWeights.get(c.getCanonicalId()));
						} else {
							dominance = 0;
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

	public Map<Criterion, Map<Pair<Alternative, Alternative>, Double>> calculateDominanceDegreeByCriterionFuzzy() {
		_dominanceDegreeByCriterion = new HashMap<Criterion, Map<Pair<Alternative, Alternative>, Double>>();

		double acumSumRelativeWeights = getAcumSumRelativeWeights();

		int criterionIndex = 0, a1Index = 0, a2Index = 0;
		double dominance = 0, condition = 0, aT1, bT1, cT1, dT1, aT2, bT2, cT2, dT2;
		double[] limits1, limits2;
		String trapezoidalNumber1, trapezoidalNumber2;
		String[] limits;
		TrapezoidalFunction tpf1, tpf2;
		
		for (Criterion c : _elementsSet.getAllCriteria()) {
			a1Index = 0;
			for (Alternative a1 : _elementsSet.getAlternatives()) {
				a2Index = 0;
				for (Alternative a2 : _elementsSet.getAlternatives()) {
					
					if (a1 != a2) {
						
						trapezoidalNumber1 = (String) _consensusMatrix[a1Index][criterionIndex];
						trapezoidalNumber1 = trapezoidalNumber1.replace("(", ""); //$NON-NLS-1$ //$NON-NLS-2$
						trapezoidalNumber1 = trapezoidalNumber1.replace(")", ""); //$NON-NLS-1$ //$NON-NLS-2$
						limits = trapezoidalNumber1.split(","); //$NON-NLS-1$
						aT1 = Double.parseDouble(limits[0]);
						bT1 = Double.parseDouble(limits[1]);
						cT1 = Double.parseDouble(limits[2]);
						dT1 = Double.parseDouble(limits[3]);
						limits1 = new double[]{aT1, bT1, cT1, dT1};
						tpf1 = new TrapezoidalFunction(limits1); 
						
						trapezoidalNumber2 = (String) _consensusMatrix[a2Index][criterionIndex];
						trapezoidalNumber2 = trapezoidalNumber2.replace("(", ""); //$NON-NLS-1$ //$NON-NLS-2$
						trapezoidalNumber2 = trapezoidalNumber2.replace(")", ""); //$NON-NLS-1$ //$NON-NLS-2$
						limits = trapezoidalNumber2.split(","); //$NON-NLS-1$
						aT2 = Double.parseDouble(limits[0]);
						bT2 = Double.parseDouble(limits[1]);
						cT2 = Double.parseDouble(limits[2]);
						dT2 = Double.parseDouble(limits[3]);
						limits2 = new double[]{aT2, bT2, cT2, dT2};
						tpf2 = new TrapezoidalFunction(limits2); 
						
						condition = tpf1.getSimpleDefuzzifiedValue() - tpf2.getSimpleDefuzzifiedValue();
						if (condition > 0) {
							dominance = Math.sqrt((tpf1.distance(tpf2) * _relativeWeights.get(c.getCanonicalId())) / acumSumRelativeWeights);
						} else if (condition < 0) {
							dominance = (-1d / ATTENUATION_FACTOR);
							dominance *= Math.sqrt((tpf1.distance(tpf2) * acumSumRelativeWeights) / _relativeWeights.get(c.getCanonicalId()));
						} else {
							dominance = 0;
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

	public Map<Pair<Alternative, Alternative>, Double> calculateDominaceDegreeAlternatives() {

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

	public Map<Alternative, Double> calculateGlobalDominance() {
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

		MapUtil.sortByValue(_globalDominance);
		
		return _globalDominance;
	}

	public Double[][] calculateCOG() {
		Double[][] result = new Double[_numAlternatives][_numCriteria];
		double a, b, c, d, cog;
		String trapezoidalNumber, limits[];
		TrapezoidalFunction semantic;
		
		if(_consensusMatrix != null && _consensusMatrix[0][0] instanceof String) {
			for(int al = 0; al < _numAlternatives; ++al) {
				for(int cr = 0; cr < _numCriteria; ++cr) {
					trapezoidalNumber = (String) _consensusMatrix[al][cr];
					trapezoidalNumber = trapezoidalNumber.replace("(", ""); //$NON-NLS-1$ //$NON-NLS-2$
					trapezoidalNumber = trapezoidalNumber.replace(")", ""); //$NON-NLS-1$ //$NON-NLS-2$
					limits = trapezoidalNumber.split(","); //$NON-NLS-1$
					a = Double.parseDouble(limits[0]);
					b = Double.parseDouble(limits[1]);
					c = Double.parseDouble(limits[2]);
					d = Double.parseDouble(limits[3]);
					semantic = new TrapezoidalFunction(new double[]{a, b, c, d});
					cog = semantic.centroid();
					result[al][cr] = Math.round(cog * 1000d) / 1000d;
				}
			}
			
			_consensusMatrix = result;
		}
		
		return (Double[][]) _consensusMatrix;
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
		_twoTupleValuations = resolution.getTwoTupleValuations();
		_distances = resolution.getDistances();
		_consensusMatrix = resolution.getConsensusMatrix();
		_criteriaWeights = resolution.getImportanceCriteriaWeights();
		_referenceCriterion = resolution.getReferenceCriterion();
		_relativeWeights = resolution.getRelativeWeights();
		_dominanceDegreeByCriterion = resolution.getDominanceDegreeByCriterion();
		_dominanceDegreeAlternatives = resolution.getDominanceDegreeAlternatives();
		_globalDominance = resolution.getGlobalDominance();
		_trapezoidalConsensusMatrix = resolution.getTrapezoidalConsensusMatrix();
	}

	@Override
	public void clear() {
		_decisionMatrix.clear();
		_twoTupleValuations.clear();
		_distances.clear();
		_consensusMatrix = new Object[_numAlternatives][_numCriteria];
		initializeConsesusMatrix();
		_trapezoidalConsensusMatrix = new String[_numAlternatives][_numCriteria];
		
		_criteriaWeights.clear();
		_referenceCriterion = null;
		_relativeWeights.clear();
		_dominanceDegreeByCriterion.clear();
		_dominanceDegreeAlternatives.clear();
		_globalDominance.clear();
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
