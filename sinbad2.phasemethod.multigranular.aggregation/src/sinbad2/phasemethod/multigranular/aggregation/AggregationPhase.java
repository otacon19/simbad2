package sinbad2.phasemethod.multigranular.aggregation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.UnweightedAggregationOperator;
import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.core.validator.Validator;
import sinbad2.element.ProblemElement;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.phasemethod.multigranular.aggregation.listener.AggregationProcessListener;
import sinbad2.phasemethod.multigranular.aggregation.listener.AggregationProcessStateChangeEvent;
import sinbad2.phasemethod.multigranular.aggregation.listener.EAggregationProcessStateChange;
import sinbad2.phasemethod.multigranular.unification.UnificationPhase;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.valuationset.ValuationKey;

public class AggregationPhase implements IPhaseMethod {
	
	public static final String ID = "flintstones.phasemethod.multigranular.aggregation";
	public static final String CRITERIA = "CRITERIA";
	public static final String EXPERTS = "EXPERTS";

	private ProblemElementsManager _elementsManager;
	private ProblemElementsSet _elementsSet;

	private Map<ProblemElement, AggregationOperator> _expertsOperators;
	private Map<ProblemElement, Object> _expertsOperatorsWeights;
	
	private Map<ProblemElement, AggregationOperator> _criteriaOperators;
	private Map<ProblemElement, Object> _criteriaOperatorsWeights;

	private String _aggregateBy;

	private List<AggregationProcessListener> _listeners;

	public AggregationPhase() {
		_elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = _elementsManager.getActiveElementSet();

		_expertsOperators = new HashMap<ProblemElement, AggregationOperator>();
		_criteriaOperators = new HashMap<ProblemElement, AggregationOperator>();
		_expertsOperatorsWeights = new HashMap<ProblemElement, Object>();
		_criteriaOperatorsWeights = new HashMap<ProblemElement, Object>();

		_listeners = new LinkedList<AggregationProcessListener>();

		_aggregateBy = "CRITERIA";
	}

	public ProblemElementsSet getElementSet() {
		return _elementsSet;
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

	private ProblemElement[] setOperator(ProblemElement element, AggregationOperator operator, Object weights, Map<ProblemElement, Object> weightsMap, Map<ProblemElement, AggregationOperator> operators) {
		operators.put(element, operator);
		weightsMap.put(element, weights);
		
		List<ProblemElement> result = new LinkedList<ProblemElement>();
		if(element instanceof Expert) {
			List<Expert> childrens = _elementsSet.getExpertChildren((Expert) element);
			for(Expert children : childrens) {
				if(children.hasChildrens()) {
					result.add(children);
				}
			}

		} else if (element instanceof Criterion) {
			List<Criterion> subcriteria = _elementsSet.getCriteriaSubcriteria((Criterion) element);
			for(Criterion subcriterion : subcriteria) {
				if(subcriterion.hasSubcriteria()) {
					result.add(subcriterion);
				}
			}
		}

		notifyAggregationProcessChange(new AggregationProcessStateChangeEvent(EAggregationProcessStateChange.AGGREGATION_PROCESS_CHANGE, null, null));

		return result.toArray(new ProblemElement[0]);
	}

	public ProblemElement[] setExpertOperator(ProblemElement expert, AggregationOperator operator, Object weights) {
		return setOperator(expert, operator, weights, _expertsOperatorsWeights, _expertsOperators);
	}

	public ProblemElement[] setCriterionOperator(ProblemElement criterion, AggregationOperator operator, Object weights) {
		return setOperator(criterion, operator, weights, _criteriaOperatorsWeights, _criteriaOperators);
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

	public Map<ProblemElement, Valuation> aggregateAlternatives() {
		Map<ProblemElement, Valuation> results = new HashMap<ProblemElement, Valuation>();
		
		for (ProblemElement alternative : _elementsSet.getAlternatives()) {
			if (CRITERIA.equals(getAggregateBy())) {
				results.put(alternative, aggregateAlternativeByCriteria(alternative));
			} else {
				results.put(alternative, aggregateAlternativeByExperts(alternative));
			}
		}

		return results;
	}
	
	private Valuation aggregateAlternativeByCriteria(ProblemElement alternative) {
		return aggregateElementByCriteria(null, alternative, null);
	}

	private Valuation aggregateAlternativeByExperts(ProblemElement alternative) {
		return aggregateElementByExperts(null, alternative, null);
	}
	
	private Valuation aggregateElementByCriteria(ProblemElement expertParent, ProblemElement alternative, ProblemElement criterionParent) {
		AggregationOperator operator;
		List<Valuation> alternativeValuations, criterionValuations;
		Map<ValuationKey, Valuation> valuationsResult = UnificationPhase.getValuationsResult();

		List<Criterion> criteria = _elementsSet.getCriteriaSubcriteria((Criterion) criterionParent);
		if (criteria.size() == 0) {
			criteria.add((Criterion) criterionParent);
		}
		List<Expert> experts = _elementsSet.getExpertChildren((Expert) expertParent);
		if (experts.size() == 0) {
			experts.add((Expert) expertParent);
		}

		alternativeValuations = new LinkedList<Valuation>();
		for (ProblemElement criterion : criteria) {
			if(((Criterion) criterion).hasSubcriteria()) {
				alternativeValuations.add(aggregateElementByCriteria(expertParent, alternative, criterion));
			} else {
				criterionValuations = new LinkedList<Valuation>();
				for (ProblemElement expert : experts) {
					if (((Expert) expert).hasChildrens()) {
						criterionValuations.add(aggregateElementByExperts(expert, alternative, criterion));
					} else {
						criterionValuations.add(valuationsResult.get(new ValuationKey((Expert) expert, (Alternative) alternative, (Criterion) criterion)));
					}
				}
				
				if (criterionValuations.size() > 1) {
					operator = getExpertOperator(expertParent);
					if (operator instanceof UnweightedAggregationOperator) {
						alternativeValuations.add(((UnweightedAggregationOperator) operator).aggregate(criterionValuations));
					}
				} else {
					alternativeValuations.add(criterionValuations.get(0));
				}
			}
		}

		if (alternativeValuations.size() > 1) {
			operator = getCriterionOperator(criterionParent);
			if(operator instanceof UnweightedAggregationOperator) { //Operador no ponderado
				return ((UnweightedAggregationOperator) operator).aggregate(alternativeValuations);
			} else {
				return null;
			}
		} else {
			return alternativeValuations.get(0);
		}
	}
	
	private Valuation aggregateElementByExperts(ProblemElement expertParent, ProblemElement alternative, ProblemElement criterionParent) {
		AggregationOperator operator;
		List<Valuation> alternativeValuations, expertValuations;
		Map<ValuationKey, Valuation> valuationsResult = UnificationPhase.getValuationsResult();

		List<Criterion> criteria = _elementsSet.getCriteriaSubcriteria((Criterion) criterionParent);
		if (criteria.size() == 0) {
			criteria.add((Criterion) criterionParent);
		}
		List<Expert> experts = _elementsSet.getExpertChildren((Expert) expertParent);
		if (experts.size() == 0) {
			experts.add((Expert) expertParent);
		}

		alternativeValuations = new LinkedList<Valuation>();
		for (ProblemElement expert : experts) {
			if (((Expert) expert).hasChildrens()) {
				alternativeValuations.add(aggregateElementByExperts(expert, alternative, criterionParent));
			} else {
				expertValuations = new LinkedList<Valuation>();
				for (ProblemElement criterion : criteria) {
					if (((Criterion) criterion).hasSubcriteria()) {
						expertValuations.add(aggregateElementByCriteria(expert, alternative, criterion));
					} else {
						expertValuations.add(valuationsResult.get(new ValuationKey((Expert) expert, (Alternative) alternative, (Criterion) criterion)));
					}
				}

				if (expertValuations.size() > 1) {
					operator = getCriterionOperator(criterionParent);
					if (operator instanceof UnweightedAggregationOperator) {
						alternativeValuations.add(((UnweightedAggregationOperator) operator).aggregate(expertValuations));
					} 
				} else {
					alternativeValuations.add(expertValuations.get(0));
				}
			}
		}

		if (alternativeValuations.size() > 1) {
			operator = getExpertOperator(expertParent);
			if (operator instanceof UnweightedAggregationOperator) {
				return ((UnweightedAggregationOperator) operator).aggregate(alternativeValuations);
			} else {
				return null;
			}
		} else {
			return alternativeValuations.get(0);
		}
	}

	@Override
	public IPhaseMethod copyStructure() {
		return new AggregationPhase();
	}

	@Override
	public void copyData(IPhaseMethod iMethodPhase) {
		AggregationPhase aggregationPhase = (AggregationPhase) iMethodPhase;

		clear();

		_elementsSet.setExperts(aggregationPhase.getElementSet().getExperts());
		_elementsSet.setAlternatives(aggregationPhase.getElementSet().getAlternatives());
		_elementsSet.setCriteria(aggregationPhase.getElementSet().getCriteria());
	}

	@Override
	public void clear() {
		_criteriaOperators.clear();
		_expertsOperators.clear();
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
	public void activate() {
		_elementsManager.setActiveElementSet(_elementsSet);
	}

	@Override
	public boolean validate() {
		if (_elementsSet.getAlternatives().isEmpty()) {
			return false;
		}

		if (_elementsSet.getExperts().isEmpty()) {
			return false;
		}

		if (_elementsSet.getCriteria().isEmpty()) {
			return false;
		}

		return true;
	}

}
