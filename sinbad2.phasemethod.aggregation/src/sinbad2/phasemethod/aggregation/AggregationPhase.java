package sinbad2.phasemethod.aggregation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.UnweightedAggregationOperator;
import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.core.validator.Validator;
import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.unbalanced.Unbalanced;
import sinbad2.domain.numeric.real.NumericRealDomain;
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
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class AggregationPhase implements IPhaseMethod {
	
	public static final String ID = "flintstones.phasemethod.aggregation";
	
	public static final String CRITERIA = "CRITERIA";
	public static final String EXPERTS = "EXPERTS";

	private static final int UNBALANCED_LEFT = 0;
	private static final int UNBALANCED_RIGHT = 1;

	private Map<ProblemElement, AggregationOperator> _expertsOperators;
	private Map<ProblemElement, Object> _expertsOperatorsWeights;
	
	private Map<ProblemElement, AggregationOperator> _criteriaOperators;
	private Map<ProblemElement, Object> _criteriaOperatorsWeights;

	private String _aggregateBy;

	private List<AggregationProcessListener> _listeners;

	private Map<ValuationKey, Valuation> _unificationValues;
	
	private Domain _unifiedDomain;
	
	private ProblemElementsSet _elementsSet;
	private ValuationSet _valuationSet;

	public AggregationPhase() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = valuationSetManager.getActiveValuationSet();

		_expertsOperators = new HashMap<ProblemElement, AggregationOperator>();
		_criteriaOperators = new HashMap<ProblemElement, AggregationOperator>();
		_expertsOperatorsWeights = new HashMap<ProblemElement, Object>();
		_criteriaOperatorsWeights = new HashMap<ProblemElement, Object>();

		_unificationValues = new HashMap<ValuationKey, Valuation>();
		
		_listeners = new LinkedList<AggregationProcessListener>();

		_aggregateBy = "CRITERIA";
	}
	
	public Map<ValuationKey, Valuation> getUnificationValues() {
		return _unificationValues;
	}
	
	public void setUnificationValues(Map<ValuationKey, Valuation> values) {
		_unificationValues = values;
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
	
	public ProblemElement[] setExpertOperator(ProblemElement expert, AggregationOperator operator, Object weights) {
		return setOperator(expert, operator, weights, _expertsOperatorsWeights, _expertsOperators, "experts");
	}

	public ProblemElement[] setCriterionOperator(ProblemElement criterion, AggregationOperator operator, Object weights) {
		return setOperator(criterion, operator, weights, _criteriaOperatorsWeights, _criteriaOperators, "criteria");
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
			notifyAggregationProcessChange(new AggregationProcessStateChangeEvent(
					EAggregationProcessStateChange.AGGREGATION_PROCESS_CHANGE, null, null));
		} else {
			throw new IllegalArgumentException("Illegal element type.");
		}
	}

	private ProblemElement[] setOperator(ProblemElement element, AggregationOperator operator, Object weights, Map<ProblemElement, Object> weightsMap, Map<ProblemElement, AggregationOperator> operators, String elementType) {
		operators.put(element, operator);
		weightsMap.put(element, weights);
		
		List<ProblemElement> result = new LinkedList<ProblemElement>();
		if(elementType.equals("experts")) {
			List<Expert> children = _elementsSet.getAllExpertChildren((Expert) element);
			for(Expert child : children) {
				if(child.hasChildren()) {
					result.add(child);
				}
			}

		} else if (elementType.equals("criteria")) {
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
		Map<ProblemElement, Valuation> results = new HashMap<ProblemElement, Valuation>();
		
		for (ProblemElement alternative : alternatives) {
			if (CRITERIA.equals(getAggregateBy())) {
				results.put(alternative, aggregateAlternativeByCriteria(alternative, experts, criteria));
			} else {
				results.put(alternative, aggregateAlternativeByExperts(alternative, experts, criteria));
			}
		}

		return results;
	}
	
	private Valuation aggregateAlternativeByCriteria(ProblemElement alternative, Set<ProblemElement> experts, Set<ProblemElement> criteria) {
		return aggregateElementByCriteria(null, alternative, null, experts, criteria);
	}

	private Valuation aggregateAlternativeByExperts(ProblemElement alternative, Set<ProblemElement> experts, Set<ProblemElement> criteria) {
		return aggregateElementByExperts(null, alternative, null, experts, criteria);
	}
	
	@SuppressWarnings("unchecked")
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
								criterionValuations.add(_unificationValues.get(new ValuationKey((Expert) expert, (Alternative) alternative, (Criterion) criterion)));
							}
						} else {
							criterionValuations.add(null);
						}
					}
					
					if (criterionValuations.size() > 1) {
						operator = getExpertOperator(expertParent);
						if (operator instanceof UnweightedAggregationOperator) {
							alternativeValuations.add(((UnweightedAggregationOperator) operator).aggregate(criterionValuations));
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
									alternativeValuations.add(((WeightedAggregationOperator) operator).aggregate(criterionValuations, weights));
								} else {
									alternativeValuations.add(null);
								}
							}
						}
					} else {
						alternativeValuations.add(criterionValuations.get(0));
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
								expertValuations.add(_unificationValues.get(new ValuationKey((Expert) expert, (Alternative) alternative, (Criterion) criterion)));
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
	
	public Map<ProblemElement, Valuation> transform(Map<ProblemElement, Valuation> problemResult, Unbalanced resultsDomain) {

		_unifiedDomain = resultsDomain;
		Map<ProblemElement, Valuation> results = null;

		if(resultsDomain != null) {

			results = new HashMap<ProblemElement, Valuation>();

			Valuation valuation;
			Unbalanced hgls = null;
			LabelLinguisticDomain label, labelTest = null;
			double alpha;
			Map<Integer, Integer> domains = null;
			int testPos;
			boolean find;
			Integer labelPos;
			Unbalanced domainTest;
			int domainPos;
			int domainSize;

			int size;
			Unbalanced[] auxDomains = null;
			LabelLinguisticDomain[] labels = null;
			double[] alphas = null;
			int[] sizes = null;

			int[] lh = resultsDomain.getLh();
			Map<Integer, Unbalanced> lhDomains = new HashMap<Integer, Unbalanced>();

			for(int i = 0; i < lh.length; i++) {
				lhDomains.put(lh[i], createDomain(lh[i]));
			}

			for(ProblemElement alternative : problemResult.keySet()) {
				valuation = problemResult.get(alternative);
				if(valuation != null) {
					if(hgls == null) {
						hgls = (Unbalanced) valuation.getDomain();
					}
					label = ((TwoTuple) valuation).getLabel();
					alpha = ((TwoTuple) valuation).getAlpha();

					find = false;

					domainPos = lh.length - 1;
					do {
						domainSize = lh[domainPos];
						domainTest = lhDomains.get(domainSize);
						testPos = domainTest.getLabelSet().getPos(label);
						labelPos = resultsDomain.labelPos(domainSize, testPos);
						if(labelPos != null) {
							find = true;
							labelTest = ((Unbalanced) _unifiedDomain).getLabelSet().getLabel(labelPos);
							domains = resultsDomain.getLabel(labelPos);
							size = domains.size();
							auxDomains = new Unbalanced[size];
							labels = new LabelLinguisticDomain[size];
							sizes = new int[size];
							alphas = new double[size];
							int i = 0;
							for(Integer auxSize : domains.keySet()) {
								auxDomains[i] = lhDomains.get(auxSize);
								labels[i] = auxDomains[i].getLabelSet().getLabel(domains.get(auxSize));
								sizes[i] = auxSize;
								if (labels[i] == label) {
									alphas[i] = alpha;
								} else {
									alphas[i] = ((TwoTuple) valuation).transform(auxDomains[i]).getAlpha();
								}
								i++;
							}
						} else {
							domainPos--;
							valuation = ((TwoTuple) valuation).transform(lhDomains.get(lh[domainPos]));
							label = ((TwoTuple) valuation).getLabel();
							alpha = ((TwoTuple) valuation).getAlpha();
						}
					} while (!find);

					if((domains.size() == 1) || (alpha == 0)) {
						valuation = transformToResultsDomain(labelTest, alphas[0]);
					} else {
						if(alpha > 0) {
							if(smallSide(labelTest) == UNBALANCED_RIGHT) {
								if (sizes[0] > sizes[1]) {
									valuation = transformToResultsDomain(labelTest, alphas[0]);
								} else {
									valuation = transformToResultsDomain(labelTest, alphas[1]);
								}
							} else {
								if(sizes[0] > sizes[1]) {
									valuation = transformToResultsDomain(labelTest, alphas[1]);
								} else {
									valuation = transformToResultsDomain(labelTest, alphas[0]);
								}
							}
						} else {
							if (smallSide(label) == UNBALANCED_RIGHT) {
								if (sizes[0] > sizes[1]) {
									valuation = transformToResultsDomain(labelTest, alphas[1]);
								} else {
									valuation = transformToResultsDomain(labelTest, alphas[0]);
								}
							} else {
								if (sizes[0] > sizes[1]) {
									valuation = transformToResultsDomain(labelTest, alphas[0]);
								} else {
									valuation = transformToResultsDomain(labelTest, alphas[1]);
								}
							}
						}
					}
				}

				results.put(alternative, valuation);
			}
		}

		return results;
	}
	
	private Unbalanced createDomain(int cardinality) {
		String[] labels = new String[cardinality];
		for(int i = 0; i < cardinality; i++) {
			labels[i] = Integer.toString(i);
		}
		
		Unbalanced domain = new Unbalanced();
		domain.createTrapezoidalFunction(labels);

		return domain;
	}
	
	private int smallSide(LabelLinguisticDomain l) {
		NumericRealDomain center = l.getSemantic().getCenter();
		NumericRealDomain coverage = l.getSemantic().getCoverage();

		double left = center.getMin() - coverage.getMin();
		double right = coverage.getMax() - center.getMax();

		if(left > right) {
			return UNBALANCED_RIGHT;
		} else {
			return UNBALANCED_LEFT;
		}
	}
	
	private Valuation transformToResultsDomain(LabelLinguisticDomain label, double alpha) {
		TwoTuple result = new TwoTuple((FuzzySet) _unifiedDomain, label, alpha);
		return result;
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
		_unificationValues = aggregationPhase.getUnificationValues();
		_unifiedDomain = aggregationPhase.getUnifiedDomain();
	}

	@Override
	public void clear() {
		_criteriaOperatorsWeights.clear();
		_expertsOperatorsWeights.clear();
		_criteriaOperators.clear();
		_expertsOperators.clear();
		_unificationValues.clear();
		_unifiedDomain = null;
		_aggregateBy = "CRITERIA";
	}

	public void addAggregationProcessListener(AggregationProcessListener listener) {
		_listeners.add(listener);
		listener.notifyAggregationProcessChange(new AggregationProcessStateChangeEvent(
				EAggregationProcessStateChange.AGGREGATION_PROCESS_CHANGE, null, null));
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
