package sinbad2.phasemethod.multigranular.aggregation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.core.validator.Validator;
import sinbad2.element.ProblemElement;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.phasemethod.multigranular.aggregation.listener.AggregationProcessListener;
import sinbad2.phasemethod.multigranular.aggregation.listener.AggregationProcessStateChangeEvent;
import sinbad2.phasemethod.multigranular.aggregation.listener.EAggregationProcessStateChange;

public class AggregationPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.multigranular.aggregation";
	public static final String CRITERIA = "CRITERIA";
	public static final String EXPERTS = "EXPERTS";

	private ProblemElementsManager _elementsManager;
	private ProblemElementsSet _elementsSet;
	
	private Map<ProblemElement, AggregationOperator> _expertsOperators;
	private Map<ProblemElement, AggregationOperator> _criteriaOperators;
	
	private String _aggregateBy;
	
	private List<AggregationProcessListener> _listeners;
	
	public AggregationPhase() {
		_elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = _elementsManager.getActiveElementSet();
		
		_expertsOperators = new HashMap<ProblemElement, AggregationOperator>();
		_criteriaOperators = new HashMap<ProblemElement, AggregationOperator>();
		
		_aggregateBy = "CRITERIA";
		
		initialization();
	}
	
	public ProblemElementsSet getElementSet() {
		return _elementsSet;
	}
	
	public String getAggregateBy() {
		return _aggregateBy;
	}
	
	private void initialization() {

		createElement(null, EXPERTS);
		for (Expert expert : _elementsSet.getExperts()) {
			if (expert.hasChildrens()) {
				createElement(expert, EXPERTS);
				for (Expert children : expert.getChildrens()) {
					if (children.hasChildrens()) {
						createElement(children, EXPERTS);
					}
				}
			}
		}

		createElement(null, CRITERIA);
		for (Criterion criterion : _elementsSet.getCriteria()) {
			if (criterion.hasSubcriteria()) {
				createElement(criterion, CRITERIA);
				for (Criterion subcriterion : criterion.getSubcriteria()) {
					if (subcriterion.hasSubcriteria()) {
						createElement(subcriterion, CRITERIA);
					}
				}
			}
		}

	}
	
	private void createElement(ProblemElement element, String elementType) {
		if (elementType.equals(CRITERIA)) {
			_criteriaOperators.put(element, null);
		} else {
			_expertsOperators.put(element, null);
		}
	}
	
	public void aggregateBy(String elementType) {
		Validator.notNull(elementType);

		if (elementType.equals(CRITERIA) || elementType.equals(EXPERTS)) {
			_aggregateBy = elementType;
			notifyAggregationProcessChange(new AggregationProcessStateChangeEvent(EAggregationProcessStateChange.AGGREGATION_PROCESS_CHANGE, null, null));
		} else {
			throw new IllegalArgumentException("Illegal element type.");
		}
	}
	
	private ProblemElement[] setOperator(ProblemElement element, AggregationOperator operator, Map<ProblemElement, AggregationOperator> operators) {
		operators.put(element, operator);

		List<ProblemElement> result = new LinkedList<ProblemElement>();
		if(element instanceof Expert) {
			if(((Expert) element).hasChildrens()) {
				for(Expert children: ((Expert) element).getChildrens()) {
					result.add(children);
				}
			}
			
		} else if(element instanceof Criterion) {
			if(((Criterion) element).hasSubcriteria()) {
				for(Criterion subcriterion: ((Criterion) element).getSubcriteria()) {
					result.add(subcriterion);
				}
			}
			
		}

		notifyAggregationProcessChange(new AggregationProcessStateChangeEvent(EAggregationProcessStateChange.AGGREGATION_PROCESS_CHANGE, null, null));
		
		return result.toArray(new ProblemElement[0]);
	}

	public ProblemElement[] setExpertOperator(ProblemElement expert, AggregationOperator operator) {
		return setOperator(expert, operator, _expertsOperators);
	}
	
	public ProblemElement[] setCriterionOperator(ProblemElement criterion, AggregationOperator operator) {
		return setOperator(criterion, operator, _criteriaOperators);
	}
	
	public AggregationOperator getExpertOperator(ProblemElement expert) {
		return _expertsOperators.get(expert);
	}

	public AggregationOperator getCriterionOperator(ProblemElement criterion) {
		return _criteriaOperators.get(criterion);
	}
	
	public boolean isValid() {
		AggregationOperator operator;
		
		if (_elementsSet.getExperts().size() > 1) {
			for (ProblemElement element : _expertsOperators.keySet()) {
				operator = _expertsOperators.get(element);
				if (operator == null) {
					return false;
				}
			}
		}
		
		if (_elementsSet.getCriteria().size() > 1) {
			for (ProblemElement element : _criteriaOperators.keySet()) {
				operator = _criteriaOperators.get(element);
				if (operator == null) {
					return false;
				}
			}
		}

		return true;
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
		_elementsSet.clear();
		
		_criteriaOperators.clear();
		_expertsOperators.clear();
		_aggregateBy = "CRITERIA";
	}

	public void addAggregationProcessListener(AggregationProcessListener listener) {
		_listeners.add(listener);
		listener.aggregationProcessChange(new AggregationProcessStateChangeEvent(EAggregationProcessStateChange.AGGREGATION_PROCESS_CHANGE, null, null));
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
			listener.aggregationProcessChange(event);
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
