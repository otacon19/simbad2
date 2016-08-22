package sinbad2.phasemethod.aggregation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.UnweightedAggregationOperator;
import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.core.utils.Pair;
import sinbad2.core.validator.Validator;
import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.unbalanced.Unbalanced;
import sinbad2.element.ProblemElement;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.aggregation.listener.AggregationProcessListener;
import sinbad2.phasemethod.aggregation.listener.AggregationProcessStateChangeEvent;
import sinbad2.phasemethod.aggregation.listener.EAggregationProcessStateChange;
import sinbad2.phasemethod.aggregation.nls.Messages;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class AggregationPhase implements IPhaseMethod {
	
	public static final String ID = "flintstones.phasemethod.aggregation"; //$NON-NLS-1$
	public static final String CRITERIA = "CRITERIA"; //$NON-NLS-1$
	public static final String EXPERTS = "EXPERTS"; //$NON-NLS-1$

	private Map<ProblemElement, AggregationOperator> _expertsOperators;
	private Map<ProblemElement, Object> _expertsOperatorsWeights;
	private Map<ProblemElement, AggregationOperator> _criteriaOperators;
	private Map<ProblemElement, Object> _criteriaOperatorsWeights;
	
	private Map<ValuationKey, Valuation> _unifiedValuations;
	private Map<ProblemElement, Valuation> _aggregatedValuations;
	private Map<Pair<Alternative, Criterion>, Valuation> _decisionMatrix;

	private String _aggregateBy;
	private Domain _unifiedDomain;
	
	private ProblemElementsSet _elementsSet;
	private ValuationSet _valuationSet;
	
	private List<AggregationProcessListener> _listeners;

	public AggregationPhase() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = valuationSetManager.getActiveValuationSet();

		_expertsOperators = new HashMap<ProblemElement, AggregationOperator>();
		_criteriaOperators = new HashMap<ProblemElement, AggregationOperator>();
		_expertsOperatorsWeights = new HashMap<ProblemElement, Object>();
		_criteriaOperatorsWeights = new HashMap<ProblemElement, Object>();

		_decisionMatrix = new HashMap<Pair<Alternative, Criterion>, Valuation>();
		
		_unifiedValuations = new HashMap<ValuationKey, Valuation>();
				
		_listeners = new LinkedList<AggregationProcessListener>();

		_aggregateBy = "CRITERIA"; //$NON-NLS-1$
	}
	
	public Map<ValuationKey, Valuation> getUnificationValues() {
		return _unifiedValuations;
	}
	
	public void setUnificationValues(Map<ValuationKey, Valuation> values) {
		_unifiedValuations = values;
	}
	
	public Map<ProblemElement, Valuation> getAggregatedValuations() {
		return _aggregatedValuations;
	}
	
	public void setAggregatedValuations(Map<ProblemElement, Valuation> aggregatedValuations) {
		_aggregatedValuations = aggregatedValuations;
	}
	
	public Domain getUnifiedDomain() {
		return _unifiedDomain;
	}
	
	public void setUnifiedDomain(Domain unifiedDomain) {
		_unifiedDomain = unifiedDomain;
	}
	
	public Map<ProblemElement, AggregationOperator> getExpertsOperators() {
		return _expertsOperators;
	}
	
	public void setExpertsOperators(Map<ProblemElement, AggregationOperator> expertsOperators) {
		_expertsOperators = expertsOperators;
	}
	
	public Map<ProblemElement, AggregationOperator> getCriteriaOperators() {
		return _criteriaOperators;
	}
	
	public void setCriteriaOperators(Map<ProblemElement, AggregationOperator> criteriaOperators) {
		_criteriaOperators = criteriaOperators;
	}
	
	public Map<ProblemElement, Object> getExpertsOperatorWeights() {
		return _expertsOperatorsWeights;
	}
	
	public void setExpertsOperatorWeights(Map<ProblemElement, Object> expertsOperatorWeights) {
		_expertsOperatorsWeights = expertsOperatorWeights;
	}
	
	public Map<ProblemElement, Object> getCriteriaOperatorWeights() {
		return _criteriaOperatorsWeights;
	}
	
	public void setCriteriaOperatorWeights(Map<ProblemElement, Object> criteriaOperatorWeights) {
		_criteriaOperatorsWeights = criteriaOperatorWeights;
	}
	
	public Map<Pair<Alternative, Criterion>, Valuation> getDecisionMatrix() {
		return _decisionMatrix;
	}
	
	public void setDecisionMatrix(Map<Pair<Alternative, Criterion>, Valuation> decisionMatrix) {
		_decisionMatrix = decisionMatrix;
	}
	
	public ProblemElement[] setExpertOperator(ProblemElement expert, AggregationOperator operator, Object weights) {
		return setOperator(expert, operator, weights, _expertsOperatorsWeights, _expertsOperators, "experts"); //$NON-NLS-1$
	}

	public ProblemElement[] setCriterionOperator(ProblemElement criterion, AggregationOperator operator, Object weights) {
		return setOperator(criterion, operator, weights, _criteriaOperatorsWeights, _criteriaOperators, "criteria"); //$NON-NLS-1$
	}

	public AggregationOperator getExpertOperator(ProblemElement expert) {
		return _expertsOperators.get(expert);
	}

	public Object getExpertOperatorWeights(ProblemElement expert) {
		return _expertsOperatorsWeights.get(expert);
	}
	
	public AggregationOperator getCriterionOperator(ProblemElement criterion) {
		return _criteriaOperators.get(criterion);
	}
	
	public Object getCriterionOperatorWeights(ProblemElement criterion) {
		return _criteriaOperatorsWeights.get(criterion);
	}
	
	public String getAggregateBy() {
		return _aggregateBy;
	}

	public void aggregateBy(String elementType) {
		Validator.notNull(elementType);

		if (elementType.equals(CRITERIA) || elementType.equals(EXPERTS)) {
			_aggregateBy = elementType;
			notifyAggregationProcessChange(new AggregationProcessStateChangeEvent(EAggregationProcessStateChange.AGGREGATION_PROCESS_CHANGE, null, null));
		} else {
			throw new IllegalArgumentException(Messages.AggregationPhase_Illegal_element_type);
		}
	}

	private ProblemElement[] setOperator(ProblemElement element, AggregationOperator operator, Object weights, Map<ProblemElement, Object> weightsMap, Map<ProblemElement, AggregationOperator> operators, String elementType) {
		operators.put(element, operator);
		weightsMap.put(element, weights);
		
		List<ProblemElement> result = new LinkedList<ProblemElement>();
		if(elementType.equals("experts")) { //$NON-NLS-1$
			List<Expert> children = _elementsSet.getAllExpertChildren((Expert) element);
			for(Expert child : children) {
				if(child.hasChildren()) {
					result.add(child);
				}
			}

		} else if (elementType.equals("criteria")) { //$NON-NLS-1$
			List<Criterion> subcriteria = _elementsSet.getAllCriterionSubcriteria((Criterion) element);
			for(Criterion subcriterion : subcriteria) {
				if(subcriterion.hasSubcriteria()) {
					result.add(subcriterion);
				}
			}
		}

		notifyAggregationProcessChange(new AggregationProcessStateChangeEvent(EAggregationProcessStateChange.AGGREGATION_PROCESS_CHANGE, null, null));

		return result.toArray(new ProblemElement[0]);
	}

	public boolean isValid() {
		AggregationOperator operator;
		Object weights;

		if (_elementsSet.getExperts().size() > 1) {
			for (ProblemElement element : _expertsOperators.keySet()) {
				operator = _expertsOperators.get(element);
				if (operator == null) {
					return false;
				} else if (operator instanceof WeightedAggregationOperator) {
					weights = _expertsOperatorsWeights.get(element);
					if (weights == null) {
						return false;
					}
				}
			}
		}

		if (_elementsSet.getCriteria().size() > 1) {
			for (ProblemElement element : _criteriaOperators.keySet()) {
				operator = _criteriaOperators.get(element);
				if (operator == null) {
					return false;
				} else if (operator instanceof WeightedAggregationOperator) {
					weights = _criteriaOperatorsWeights.get(element);
					if (weights == null) {
						return false;
					}
				}
			}
		}

		return true;
	}

	public Map<ProblemElement, Valuation> aggregateAlternatives(Set<ProblemElement> experts, Set<ProblemElement> alternatives, Set<ProblemElement> criteria) {
		_aggregatedValuations = new HashMap<ProblemElement, Valuation>();
		
		_decisionMatrix.clear();
		
		for (ProblemElement alternative : alternatives) {
			if (CRITERIA.equals(getAggregateBy())) {
				_aggregatedValuations.put(alternative, aggregateAlternativeByCriteria(alternative, experts, criteria));
			} else {
				_aggregatedValuations.put(alternative, aggregateAlternativeByExperts(alternative, experts, criteria));
			}
		}
		
		if(_unifiedDomain instanceof Unbalanced) {
			return UnbalancedUtils.transformUnbalanced(_aggregatedValuations, (Unbalanced) _unifiedDomain);
		} else if(_unifiedDomain == null)  {
			_unifiedDomain = _valuationSet.getValuations().get(new ValuationKey(_elementsSet.getExperts().get(0), _elementsSet.getAlternatives().get(0), 
					_elementsSet.getCriteria().get(0))).getDomain();
		}
		
		return _aggregatedValuations;
	}
	
	private Valuation aggregateAlternativeByCriteria(ProblemElement alternative, Set<ProblemElement> experts, Set<ProblemElement> criteria) {
		return aggregateElementByCriteria(null, alternative, null, experts, criteria);
	}

	private Valuation aggregateAlternativeByExperts(ProblemElement alternative, Set<ProblemElement> experts, Set<ProblemElement> criteria) {
		return aggregateElementByExperts(null, alternative, null, experts, criteria);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Valuation aggregateElementByCriteria(ProblemElement expertParent, ProblemElement alternative, ProblemElement criterionParent, Set<ProblemElement> experts, Set<ProblemElement> criteria) {
		AggregationOperator operator;
		List<Valuation> alternativeValuations, criterionValuations = null;
		List<Double> weights;
		Object aux;
		
		List<Criterion> criteria1 = new LinkedList<Criterion>();
		if(criterionParent != null) {
			if(((Criterion) criterionParent).hasSubcriteria()) {
				criteria1 = ((Criterion) criterionParent).getSubcriteria();
			} else {
				criteria1.add((Criterion) criterionParent);
			}
		} else {
			criteria1 = _elementsSet.getCriteria();
		}

		List<Expert> experts1 = new LinkedList<Expert>();
		if(expertParent != null) {
			if(((Expert) expertParent).hasChildren()) {
				experts1 = ((Expert) expertParent).getChildren();
			} else {
				experts1.add((Expert) expertParent);
			}
		} else {
			experts1 = _elementsSet.getExperts();
		}

		alternativeValuations = new LinkedList<Valuation>();
		for (ProblemElement criterion : criteria1) {
			if(criteria.contains(criterion)) {
				if(_elementsSet.getAllCriterionSubcriteria((Criterion) criterionParent).size() > 0) {
					alternativeValuations.add(aggregateElementByCriteria(expertParent, alternative, criterion, experts, criteria));
				} else {
					criterionValuations = new LinkedList<Valuation>();
					for (ProblemElement expert : experts1) {
						if(experts.contains(expert)) {
							if (_elementsSet.getAllExpertChildren((Expert) expertParent).size() > 0) {
								criterionValuations.add(aggregateElementByExperts(expert, alternative, criterion, experts, criteria));
							} else {
								if(!_unifiedValuations.isEmpty()) {
									criterionValuations.add(_unifiedValuations.get(new ValuationKey((Expert) expert, (Alternative) alternative, (Criterion) criterion)));
								} else {
									criterionValuations.add(_valuationSet.getValuations().get(new ValuationKey((Expert) expert, (Alternative) alternative, (Criterion) criterion)));
								}
							}
						} else {
							criterionValuations.add(null);
						}
					}

					if (criterionValuations.size() > 1) {
						operator = getExpertOperator(expertParent);
				
						if (operator instanceof UnweightedAggregationOperator) {
							Valuation v = ((UnweightedAggregationOperator) operator).aggregate(criterionValuations);
							alternativeValuations.add(v);
							if(v != null) {
								_decisionMatrix.put(new Pair(alternative, criterion), v);
							}
						} else {
							aux = getExpertOperatorWeights(expertParent);
							if(aux instanceof List<?>) {
								weights = (List<Double>) aux;
							} else if(aux instanceof Map<?, ?>) {
								weights = ((Map<ProblemElement, List<Double>>) aux).get(criterion.getId());
								if(weights == null) {
									weights = ((Map<ProblemElement, List<Double>>) aux).get(null);
								}
							} else {
								weights = null;
							}
							if(operator == null) {
								alternativeValuations.add(null);
							} else {
								if(weights != null) {
									Valuation v = ((WeightedAggregationOperator) operator).aggregate(criterionValuations, weights);
									alternativeValuations.add(v);
									if(v != null) {
										_decisionMatrix.put(new Pair(alternative, criterion), v);
									}
								} else {
									alternativeValuations.add(null);
								}
							}
						}
					} else {
						alternativeValuations.add(criterionValuations.get(0));
						if(criterionValuations.get(0) != null) {
							_decisionMatrix.put(new Pair(alternative, criterion), criterionValuations.get(0));
						}
					}
				}
			} else {
				alternativeValuations.add(null);
			}
		}
		
		if (alternativeValuations.size() > 1) {
			operator = getCriterionOperator(criterionParent);
			if(operator instanceof UnweightedAggregationOperator) {
				return ((UnweightedAggregationOperator) operator).aggregate(alternativeValuations);
			} else if(operator instanceof WeightedAggregationOperator){
				aux = getCriterionOperatorWeights(criterionParent);
				if (aux instanceof List<?>) {
					weights = (List<Double>) aux;
				} else if (aux instanceof Map<?, ?>) {
					weights = ((Map<ProblemElement, List<Double>>) aux).get(null);
				} else {
					weights = null;
					return null;
				}
				return ((WeightedAggregationOperator) operator).aggregate(alternativeValuations, weights);
			} else {
				return null;
			}
		} else {
			return alternativeValuations.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Valuation aggregateElementByExperts(ProblemElement expertParent, ProblemElement alternative, ProblemElement criterionParent, Set<ProblemElement> experts, Set<ProblemElement> criteria) {
		AggregationOperator operator;
		List<Valuation> alternativeValuations, expertValuations;
		List<Double> weights;
		Object aux;
		
		List<Criterion> criteria1 = new LinkedList<Criterion>();
		if(criterionParent != null) {
			if(((Criterion) criterionParent).hasSubcriteria()) {
				criteria1 = ((Criterion) criterionParent).getSubcriteria();
			} else {
				criteria1.add((Criterion) criterionParent);
			}
		} else {
			criteria1 = _elementsSet.getCriteria();
		}

		List<Expert> experts1 = new LinkedList<Expert>();
		if(expertParent != null) {
			if(((Expert) expertParent).hasChildren()) {
				experts1 = ((Expert) expertParent).getChildren();
			} else {
				experts1.add((Expert) expertParent);
			}
		} else {
			experts1 = _elementsSet.getExperts();
		}

		alternativeValuations = new LinkedList<Valuation>();
		for (ProblemElement expert : experts1) {
			if(experts.contains(expert)) {
				if (_elementsSet.getAllExpertChildren((Expert) expertParent).size() > 0) {
					alternativeValuations.add(aggregateElementByExperts(expert, alternative, criterionParent, experts, criteria));
				} else {
					expertValuations = new LinkedList<Valuation>();
					for (ProblemElement criterion : criteria1) {
						if(criteria.contains(criterion)) {
							if (_elementsSet.getAllCriterionSubcriteria((Criterion) criterionParent).size() > 0) {
								expertValuations.add(aggregateElementByCriteria(expert, alternative, criterion, experts, criteria));
							} else {
								if(!_unifiedValuations.isEmpty()) {
									expertValuations.add(_unifiedValuations.get(new ValuationKey((Expert) expert, (Alternative) alternative, (Criterion) criterion)));
								} else {
									expertValuations.add(_valuationSet.getValuations().get(new ValuationKey((Expert) expert, (Alternative) alternative, (Criterion) criterion)));
								}
							}
						} else {
							expertValuations.add(null);
						}
					}
					
					if (expertValuations.size() > 1) {
						operator = getCriterionOperator(criterionParent);
						if (operator instanceof UnweightedAggregationOperator) {
							alternativeValuations.add(((UnweightedAggregationOperator) operator).aggregate(expertValuations));
						} else {
							aux = getCriterionOperatorWeights(criterionParent);
							if(aux instanceof List<?>) {
								weights = (List<Double>) aux;
							} else if(aux instanceof Map<?, ?>) {
								weights = ((Map<ProblemElement, List<Double>>) aux).get(expert.getId());
								if(weights == null) {
									weights = ((Map<ProblemElement, List<Double>>) aux).get(null);
								}
							} else {
								weights = null;
							}
							
							if(operator == null) {
								alternativeValuations.add(null);
							} else {
								if(weights != null) {
									alternativeValuations.add(((WeightedAggregationOperator) operator).aggregate(expertValuations, weights));
								} else {
									alternativeValuations.add(null);
								}
							}
						}
					} else {
						alternativeValuations.add(expertValuations.get(0));
					}
				}
			} else {
				alternativeValuations.add(null);
			}
		}

		if (alternativeValuations.size() > 1) {
			operator = getExpertOperator(expertParent);
			if (operator instanceof UnweightedAggregationOperator) {
				return ((UnweightedAggregationOperator) operator).aggregate(alternativeValuations);
			} else if(operator instanceof WeightedAggregationOperator) {
				aux = getExpertOperatorWeights(expertParent);
				if (aux instanceof List<?>) {
					weights = (List<Double>) aux;
				} else if (aux instanceof Map<?, ?>) {
					weights = ((Map<ProblemElement, List<Double>>) aux).get(null);
				} else {
					weights = null;
					
					return null;
				}
				return ((WeightedAggregationOperator) operator).aggregate(alternativeValuations, weights);
			} else {
				return null;
			}
		} else {
			return alternativeValuations.get(0);
		} 
	}
	
	public Object[] getAggregatedValuationsPosAndAlpha() {
		int size = _aggregatedValuations.size();
		String[] alternatives = new String[size];
		int[] pos = new int[size];
		double[] alpha = new double[size];
		Valuation valuation = null, aux;
		
		int i = 0;
		for (ProblemElement alternative : _aggregatedValuations.keySet()) {
			alternatives[i] = alternative.getId();
			aux = _aggregatedValuations.get(alternative);
			if (aux instanceof UnifiedValuation) {
				valuation = ((UnifiedValuation) aux).disunification((FuzzySet) aux.getDomain());
			} else {
				valuation = aux;
			}

			if (valuation instanceof TwoTuple) {
				pos[i] = ((FuzzySet) _unifiedDomain).getLabelSet().getPos(((TwoTuple) valuation).getLabel());
				alpha[i] = ((TwoTuple) valuation).getAlpha();
				i++;
			}
		}
		
		Object[] data = new Object[2];
		data[0] = pos;
		data[1] = alpha;
		
		return data;
	}

	@Override
	public IPhaseMethod copyStructure() {
		return new AggregationPhase();
	}

	@Override
	public void copyData(IPhaseMethod iMethodPhase) {
		AggregationPhase aggregationPhase = (AggregationPhase) iMethodPhase;

		clear();
		
		_criteriaOperatorsWeights = aggregationPhase.getCriteriaOperatorWeights();
		_expertsOperatorsWeights = aggregationPhase.getExpertsOperatorWeights();
		_criteriaOperators = aggregationPhase.getCriteriaOperators();
		_expertsOperators = aggregationPhase.getExpertsOperators();
		_unifiedValuations = aggregationPhase.getUnificationValues();
		_unifiedDomain = aggregationPhase.getUnifiedDomain();
		_decisionMatrix = aggregationPhase.getDecisionMatrix();
	}

	@Override
	public void clear() {
		_criteriaOperatorsWeights.clear();
		_expertsOperatorsWeights.clear();
		_criteriaOperators.clear();
		_expertsOperators.clear();
		_unifiedValuations.clear();
		_unifiedDomain = null;
		_decisionMatrix.clear();
		_aggregateBy = "CRITERIA"; //$NON-NLS-1$
	}

	public void addAggregationProcessListener(AggregationProcessListener listener) {
		_listeners.add(listener);
		listener.notifyAggregationProcessChange(new AggregationProcessStateChangeEvent(EAggregationProcessStateChange.AGGREGATION_PROCESS_CHANGE, null, null));
	}

	public void removeAggregationProcessListener(AggregationProcessListener listener) {
		_listeners.remove(listener);
	}

	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		if (event.getChange().equals(EPhaseMethodStateChange.ACTIVATED)) {
			activate();
		}
	}

	private void notifyAggregationProcessChange(AggregationProcessStateChangeEvent event) {
		for (AggregationProcessListener listener : _listeners) {
			listener.notifyAggregationProcessChange(event);
		}
	}

	@Override
	public IPhaseMethod clone() {
		AggregationPhase result = null;

		try {
			result = (AggregationPhase) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public void activate() {}

	@Override
	public boolean validate() {
		
		if(_valuationSet.getValuations().isEmpty()) {
			return false;
		}

		return true;
	}

}
