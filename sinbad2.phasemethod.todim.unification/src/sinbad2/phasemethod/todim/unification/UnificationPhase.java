package sinbad2.phasemethod.todim.unification;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.AggregationOperatorsManager;
import sinbad2.aggregationoperator.owa.OWA;
import sinbad2.aggregationoperator.owa.YagerQuantifiers;
import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.HesitantValuation;
import sinbad2.valuation.integer.IntegerValuation;
import sinbad2.valuation.integer.interval.IntegerIntervalValuation;
import sinbad2.valuation.linguistic.LinguisticValuation;
import sinbad2.valuation.real.RealValuation;
import sinbad2.valuation.real.interval.RealIntervalValuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class UnificationPhase implements IPhaseMethod {

public static final String ID = "flintstones.phasemethod.todim.unification"; //$NON-NLS-1$
	
	private Map<ValuationKey, Valuation> _unifiedValuationsResult;
	private Map<ValuationKey, Valuation> _twoTupleValuationsResult;
	private Map<ValuationKey, TrapezoidalFunction> _envelopeValuations;
	
	private ValuationSet _valutationSet;
	
	public UnificationPhase() {
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valutationSet = valuationSetManager.getActiveValuationSet();
		
		_unifiedValuationsResult = new HashMap<ValuationKey, Valuation>();
		_twoTupleValuationsResult = new LinkedHashMap<ValuationKey, Valuation>();
		_envelopeValuations = new HashMap<ValuationKey, TrapezoidalFunction>();
	}
	
	public Map<ValuationKey, Valuation> getUnifiedValuationsResult() {
		return _unifiedValuationsResult;
	}
	
	public void setUnifiedValuationsResult(Map<ValuationKey, Valuation> unifiedValuationsResult) {
		_unifiedValuationsResult = unifiedValuationsResult;
	}

	
	public Map<ValuationKey, Valuation> getTwoTupleValuationsResult() {
		return _twoTupleValuationsResult;
	}
	
	public void setTwoTupleValuationsResult(Map<ValuationKey, Valuation> twoTupleValuationsResult) {
		_twoTupleValuationsResult = twoTupleValuationsResult;
	}
	
	public Map<ValuationKey, TrapezoidalFunction> getEnvelopeValuations() {
		return _envelopeValuations;
	}
	
	public void setEnvelopeValuations(Map<ValuationKey, TrapezoidalFunction> envelopeValuations) {
		_envelopeValuations = envelopeValuations;
	}

	@Override
	public IPhaseMethod copyStructure() {
		return new UnificationPhase();
	}

	@Override
	public void copyData(IPhaseMethod iPhaseMethod) {
		UnificationPhase unification = (UnificationPhase) iPhaseMethod;

		clear();

		_unifiedValuationsResult = unification.getUnifiedValuationsResult();
		_twoTupleValuationsResult = unification.getTwoTupleValuationsResult();
		_envelopeValuations = unification.getEnvelopeValuations();
	}

	@Override
	public void clear() {
		_unifiedValuationsResult.clear();
		_twoTupleValuationsResult.clear();
		_envelopeValuations.clear();
	}

	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		if (event.getChange().equals(EPhaseMethodStateChange.ACTIVATED)) {
			activate();
		}
	}

	@Override
	public IPhaseMethod clone() {
		UnificationPhase result = null;

		try {
			result = (UnificationPhase) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public void activate() {}

	@Override
	public boolean validate() {

		if (_valutationSet.getValuations().isEmpty()) {
			return false;
		}

		return true;
	}

	public Map<ValuationKey, Valuation> unification(FuzzySet unifiedDomain) {
		_unifiedValuationsResult = new HashMap<ValuationKey, Valuation>();
		Map<String, List<Object[]>> toNormalize = new HashMap<String, List<Object[]>>();
		Map<String, Domain> domains = new HashMap<String, Domain>();
		List<Object[]> auxEvaluations;
		
		if (unifiedDomain != null) {
			Expert expert;
			Alternative alternative;
			Criterion criterion;
			Valuation valuation;
			FuzzySet fuzzySet;
			Boolean isCost;

			Map<ValuationKey, Valuation> valuations = _valutationSet.getValuations();
			for(ValuationKey vk : valuations.keySet()) {
				expert = vk.getExpert();
				alternative = vk.getAlternative();
				criterion = vk.getCriterion();
				valuation = valuations.get(vk);
				isCost = criterion.getCost();
				
				if(valuation instanceof IntegerValuation) {
					if(isCost) {
						valuation = valuation.negateValuation();
					}
					if(((NumericIntegerDomain) valuation.getDomain()).getInRange()) {
						fuzzySet = ((IntegerValuation) valuation).unification(unifiedDomain);
						valuation = new UnifiedValuation(fuzzySet);
						_unifiedValuationsResult.put(vk, valuation);
					} else {
						Object[] auxEvaluation = new Object[5];
						auxEvaluation[0] = expert;
						auxEvaluation[1] = alternative;
						auxEvaluation[2] = criterion;
						auxEvaluation[3] = valuation;
						auxEvaluation[4] = vk;

						Domain domain = valuation.getDomain();

						auxEvaluations = toNormalize.get(domain.getId());
						if(auxEvaluations == null) {
							auxEvaluations = new LinkedList<Object[]>();
							toNormalize.put(domain.getId(), auxEvaluations);
							domains.put(domain.getId(), domain);
						}
						auxEvaluations.add(auxEvaluation);
					}
				} else if(valuation instanceof RealValuation) {
					if(isCost) {
						valuation = valuation.negateValuation();
					}
					if(((NumericRealDomain) valuation.getDomain()).getInRange()) {
						fuzzySet = ((RealValuation) valuation).unification(unifiedDomain);
						valuation = new UnifiedValuation(fuzzySet);
						_unifiedValuationsResult.put(vk, valuation);
					} else {
						Object[] auxEvaluation = new Object[5];
						auxEvaluation[0] = expert;
						auxEvaluation[1] = alternative;
						auxEvaluation[2] = criterion;
						auxEvaluation[3] = valuation;
						auxEvaluation[4] = vk;

						Domain domain = valuation.getDomain();
						
						auxEvaluations = toNormalize.get(domain.getId());
						if(auxEvaluations == null) {
							auxEvaluations = new LinkedList<Object[]>();
							toNormalize.put(domain.getId(), auxEvaluations);
							domains.put(domain.getId(), domain);
						}

						auxEvaluations.add(auxEvaluation);
					}
				} else if(valuation instanceof IntegerIntervalValuation) {
					if(isCost) {
						valuation = valuation.negateValuation();
					}
					if (((NumericIntegerDomain) valuation.getDomain()).getInRange()) {
						fuzzySet = ((IntegerIntervalValuation) valuation).unification(unifiedDomain);
						valuation = new UnifiedValuation(fuzzySet);
						_unifiedValuationsResult.put(vk, valuation);
					} else {
						Object[] auxEvaluation = new Object[5];
						auxEvaluation[0] = expert;
						auxEvaluation[1] = alternative;
						auxEvaluation[2] = criterion;
						auxEvaluation[3] = valuation;
						auxEvaluation[4] = vk;

						Domain domain = valuation.getDomain();
						
						auxEvaluations = toNormalize.get(domain.getId());
						if(auxEvaluations == null) {
							auxEvaluations = new LinkedList<Object[]>();
							toNormalize.put(domain.getId(), auxEvaluations);
							domains.put(domain.getId(), domain);
						}

						auxEvaluations.add(auxEvaluation);
					}
				} else if(valuation instanceof RealIntervalValuation) {
					if(isCost) {
						valuation = valuation.negateValuation();
					}
					if (((NumericRealDomain) valuation.getDomain()).getInRange()) {
						fuzzySet = ((RealIntervalValuation) valuation).unification(unifiedDomain);
						valuation = new UnifiedValuation(fuzzySet);
						_unifiedValuationsResult.put(vk, valuation);
					} else {
						Object[] auxEvaluation = new Object[5];
						auxEvaluation[0] = expert;
						auxEvaluation[1] = alternative;
						auxEvaluation[2] = criterion;
						auxEvaluation[3] = valuation;
						auxEvaluation[4] = vk;

						Domain domain = valuation.getDomain();
						
						auxEvaluations = toNormalize.get(domain.getId());
						if(auxEvaluations == null) {
							auxEvaluations = new LinkedList<Object[]>();
							toNormalize.put(domain.getId(), auxEvaluations);
							domains.put(domain.getId(), domain);
						}

						auxEvaluations.add(auxEvaluation);
					}
				} else if(valuation instanceof UnifiedValuation) {
					Valuation auxValuation = ((UnifiedValuation) valuation).disunification((FuzzySet) valuation.getDomain());
					if(isCost) {
						auxValuation = auxValuation.negateValuation();
					}
					fuzzySet = ((TwoTuple) auxValuation).unification(unifiedDomain);
					valuation = new UnifiedValuation(fuzzySet);
					
					_unifiedValuationsResult.put(vk, valuation);
				} else if(valuation instanceof LinguisticValuation) {
					if(isCost) {
						valuation = ((LinguisticValuation) valuation).negateValuation();
					}
					fuzzySet = ((LinguisticValuation) valuation).unification(unifiedDomain);
					valuation = new UnifiedValuation(fuzzySet);
					
					_unifiedValuationsResult.put(vk, valuation);
				} else if(valuation instanceof HesitantValuation) {
					calculateFuzzyEnvelope(vk, (HesitantValuation) valuation, unifiedDomain);
				}
			}
		}
		
		if(toNormalize.size() > 0) {
			normalizeValuations(toNormalize, domains, unifiedDomain);
		}
		
		unifiedEvaluationToTwoTuple(unifiedDomain);
		
		return _unifiedValuationsResult;
	}
	
	private void normalizeValuations(Map<String, List<Object[]>> toNormalize, Map<String, Domain> domains, FuzzySet unifiedDomain) {
		NumericIntegerDomain auxNumericIntegerDomain;
		NumericRealDomain auxNumericRealDomain;
		Valuation auxValuation;
		FuzzySet fuzzySet;
		double min, max, measure, minMeasure, maxMeasure;
		
		int cont = 0;
		for(String domainID : toNormalize.keySet()) {
		
			Domain d = domains.get(domainID);
			
			if(d instanceof NumericIntegerDomain) {
				min = -1;
				max = -1;
				List<Object[]> values = toNormalize.get(d.getId());
				Valuation v = (Valuation) values.get(cont)[3];
				
				if(v != null) {
					if(v instanceof IntegerValuation) {					
						for(Object[] evaluation : toNormalize.get(d.getId())) {
							auxValuation = (Valuation) evaluation[3];
							measure = ((IntegerValuation) auxValuation).getValue();
							if(min == -1) {
								min = measure;
								max = measure;
							} else {
								if (measure < min) {
									min = measure;
								}
								if (measure > max) {
									max = measure;
								}
							}
						}
						if(min != -1) {
							auxNumericIntegerDomain = new NumericIntegerDomain();
							auxNumericIntegerDomain.setMinMax((int) min, (int) max);
							auxNumericIntegerDomain.setType(((NumericIntegerDomain) d).getType());
							for(Object[] evaluation : toNormalize.get(d.getId())) {
								auxValuation = (Valuation) evaluation[3];
								ValuationKey vk = (ValuationKey) evaluation[4];
								auxValuation = new IntegerValuation(auxNumericIntegerDomain, ((IntegerValuation) auxValuation).getValue());
		
								fuzzySet = ((IntegerValuation) auxValuation).unification(unifiedDomain);
								UnifiedValuation valuation = new UnifiedValuation(fuzzySet);
		
								_unifiedValuationsResult.put(vk, valuation);
							}
						}
					} else if(v instanceof IntegerIntervalValuation) {
						min = -1;
						max = -1;
						for(Object[] evaluation : toNormalize.get(d.getId())) {
							auxValuation = (Valuation) evaluation[3];
							minMeasure = ((IntegerIntervalValuation) auxValuation).getMin();
	
							if(min == -1) {
								min = minMeasure;
								max = minMeasure;
							}
							if(minMeasure < min) {
								min = minMeasure;
							}
							if(minMeasure > max) {
								max = minMeasure;
							}
							maxMeasure = ((IntegerIntervalValuation) auxValuation).getMax();
							if(maxMeasure < min) {
								min = maxMeasure;
							}
							if(maxMeasure > max) {
								max = maxMeasure;
							}
						}
						if(min != -1) {
							auxNumericIntegerDomain = new NumericIntegerDomain();
							auxNumericIntegerDomain.setMinMax((int) min, (int) max);
							auxNumericIntegerDomain.setType(((NumericIntegerDomain) d).getType());
							for(Object[] evaluation : toNormalize.get(d.getId())) {
								auxValuation = (Valuation) evaluation[3];
								ValuationKey vk = (ValuationKey) evaluation[4];
								auxValuation = new IntegerIntervalValuation(auxNumericIntegerDomain, ((IntegerIntervalValuation) auxValuation).getMin(), ((IntegerIntervalValuation) auxValuation).getMax());
	
								fuzzySet = ((IntegerIntervalValuation) auxValuation).unification(unifiedDomain);
								UnifiedValuation valuation = new UnifiedValuation(fuzzySet);
	
								_unifiedValuationsResult.put(vk, valuation);
							}
						}
					}
				}
			} else if(d instanceof NumericRealDomain) {
				min = -1;
				max = -1;
				
				List<Object[]> values = toNormalize.get(d.getId());
				Valuation v = (Valuation) values.get(cont)[3];
				
				if(v != null) {
					if(v instanceof RealValuation) {	
						for(Object[] evaluation : toNormalize.get(d.getId())) {
							auxValuation = (Valuation) evaluation[3];
							measure = ((RealValuation) auxValuation).getValue();
							if(min == -1) {
								min = measure;
								max = measure;
							} else {
								if (measure < min) {
									min = measure;
								}
								if (measure > max) {
									max = measure;
								}
							}
						}
						if(min != -1) {
							auxNumericRealDomain = new NumericRealDomain();
							auxNumericRealDomain.setMinMax(min, max);
							auxNumericRealDomain.setType(((NumericRealDomain) d).getType());
							for(Object[] evaluation : toNormalize.get(d.getId())) {
								auxValuation = (Valuation) evaluation[3];
								ValuationKey vk = (ValuationKey) evaluation[4];
								auxValuation = new RealValuation(auxNumericRealDomain, ((RealValuation) auxValuation).getValue());
		
								fuzzySet = ((RealValuation) auxValuation).unification(unifiedDomain);
								UnifiedValuation valuation = new UnifiedValuation(fuzzySet);
			
								_unifiedValuationsResult.put(vk, valuation);
							}
						}
					} else if(v instanceof RealIntervalValuation) {
						min = -1;
						max = -1;
						
						for(Object[] evaluation : toNormalize.get(d.getId())) {
							auxValuation = (Valuation) evaluation[3];
							minMeasure = ((RealIntervalValuation) auxValuation).getMin();
	
							if(min == -1) {
								min = minMeasure;
								max = minMeasure;
							}
							if(minMeasure < min) {
								min = minMeasure;
							}
							if(minMeasure > max) {
								max = minMeasure;
							}
							maxMeasure = ((RealIntervalValuation) auxValuation).getMax();
							if(maxMeasure < min) {
								min = maxMeasure;
							}
							if(maxMeasure > max) {
								max = maxMeasure;
							}
						}
						if(min != -1) {
							auxNumericRealDomain = new NumericRealDomain();
							auxNumericRealDomain.setMinMax(min, max);
							auxNumericRealDomain.setType(((NumericRealDomain) d).getType());
	
							for(Object[] evaluation : toNormalize.get(d.getId())) {
								auxValuation = (Valuation) evaluation[3];
								ValuationKey vk = (ValuationKey) evaluation[4];
								auxValuation = new RealIntervalValuation(auxNumericRealDomain, ((RealIntervalValuation) auxValuation).getMin(), ((RealIntervalValuation) auxValuation).getMax());
	
								fuzzySet = ((RealIntervalValuation) auxValuation).unification(unifiedDomain);
								UnifiedValuation valuation = new UnifiedValuation(fuzzySet);
	
								_unifiedValuationsResult.put(vk, valuation);
							}
						}
					}
				}
			}
			cont++;
		}
	}
	
	private void calculateFuzzyEnvelope(ValuationKey vk, HesitantValuation valuation, FuzzySet domain) {	
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
        
        TrapezoidalFunction tmf = new TrapezoidalFunction(new double[] {a, b, c, d});
        _envelopeValuations.put(vk, tmf);
        
        transformToTwoTuple(vk, tmf, domain);
	}
	
	private void transformToTwoTuple(ValuationKey vk, TrapezoidalFunction tmf, Domain domain) {
		IMembershipFunction function;
		FuzzySet result;

		result = (FuzzySet) ((FuzzySet) domain).clone();
		int g = result.getLabelSet().getCardinality();

		for(int i = 0; i < g; i++) {
			function = result.getLabelSet().getLabel(i).getSemantic();
			result.setValue(i, function.maxMin(tmf));
		}
		
		Valuation unifiedValuation = new UnifiedValuation(result);
		TwoTuple twoTuple = ((UnifiedValuation) unifiedValuation).disunification(result);
		
		_twoTupleValuationsResult.put(vk, twoTuple);
	}

	public Map<ValuationKey, Valuation> unifiedEvaluationToTwoTuple(FuzzySet unifiedDomain) {
		
		if(unifiedDomain != null) {
		
			Valuation valuation;

			for(ValuationKey key : _unifiedValuationsResult.keySet()) {
				valuation = _unifiedValuationsResult.get(key);
				if(valuation instanceof UnifiedValuation) {
					valuation = ((UnifiedValuation) valuation).disunification((FuzzySet) valuation.getDomain());
				} else if(!(valuation instanceof TwoTuple)) {
					valuation = null;
				}
				_twoTupleValuationsResult.put(key, valuation);
			}
		}

		return _twoTupleValuationsResult;
	}
	
}
