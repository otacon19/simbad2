package sinbad2.element;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sinbad2.element.alternative.Alternative;
import sinbad2.element.alternative.listener.AlternativesChangeEvent;
import sinbad2.element.alternative.listener.EAlternativesChange;
import sinbad2.element.alternative.listener.IAlternativesChangeListener;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.criterion.listener.CriteriaChangeEvent;
import sinbad2.element.criterion.listener.ECriteriaChange;
import sinbad2.element.criterion.listener.ICriteriaChangeListener;
import sinbad2.element.expert.Expert;
import sinbad2.element.expert.listener.EExpertsChange;
import sinbad2.element.expert.listener.ExpertsChangeEvent;
import sinbad2.element.expert.listener.IExpertsChangeListener;

public class ProblemElementsSet implements Cloneable {
	
	private List<Expert> _experts;
	private List<Alternative> _alternatives;
	private List<Criterion> _criteria;
	
	private List<IExpertsChangeListener> _expertsListener;
	private List<IAlternativesChangeListener> _alternativesListener;
	private List<ICriteriaChangeListener> _criteriaListener;

	
	public ProblemElementsSet(){
		_experts = new LinkedList<Expert>();
		_alternatives = new LinkedList<Alternative>();
		_criteria = new LinkedList<Criterion>();
		
		_expertsListener = new LinkedList<IExpertsChangeListener>();
		_alternativesListener = new LinkedList<IAlternativesChangeListener>();
		_criteriaListener = new LinkedList<ICriteriaChangeListener>();
	}

	public List<Expert> getExperts() {
		return _experts;
	}
	
	public List<Alternative> getAlternatives() {
		return _alternatives;
	}
	
	public List<Criterion> getCriteria() {
		return _criteria;
	}
	
	public void setExperts(List<Expert> experts) {
		//TODO Clase validator
		
		_experts = experts;
		
		notifyExpertsChanges(new ExpertsChangeEvent(EExpertsChange.EXPERTS_CHANGES, null, _experts));
	}
	
	public void setAlternatives(List<Alternative> alternatives) {
		//TODO Clase validator
		
		_alternatives = alternatives;
		
		notifyAlternativesChanges(new AlternativesChangeEvent(EAlternativesChange.ALTERNATIVES_CHANGES, null, _alternatives));
	}
	
	public void setCriteria(List<Criterion> criteria) {
		//TODO Clase validator
		
		_criteria = criteria;
		
		notifyCriteriaChanges(new CriteriaChangeEvent(ECriteriaChange.CRITERIA_CHANGES, null, _criteria));
		
	}

	public void insertExpert(Expert expert, Boolean hasParent) {
		
		if(!hasParent) {
			_experts.add(expert);
			Collections.sort(_experts);
		}
		
		notifyExpertsChanges(new ExpertsChangeEvent(EExpertsChange.ADD_EXPERT, null, expert));
		
	}
	
	public void insertAlternative(Alternative alternative) {
		
		_alternatives.add(alternative);
		Collections.sort(_alternatives);
		
		notifyAlternativesChanges(new AlternativesChangeEvent(EAlternativesChange.ADD_ALTERNATIVE, null, alternative));
		
	}
	
	public void insertCriterion(Criterion criterion, Boolean hasParent) {
		
		if(!hasParent) {
			_criteria.add(criterion);
			Collections.sort(_criteria);
		}
		
		notifyCriteriaChanges(new CriteriaChangeEvent(ECriteriaChange.ADD_CRITERION, null, criterion));
		
	}
	
	public void insertSeveralExperts(List<Expert> insertExperts, Boolean hasParent) {
		Expert parent = insertExperts.get(0).getParent();
		
		for(Expert expert: insertExperts) {	
			if(!hasParent) {
				_experts.add(expert);
			} else {
				parent.addChildren(expert);
			}
		}
		
		notifyExpertsChanges(new ExpertsChangeEvent(EExpertsChange.REMOVE_SEVERAL_EXPERTS, null, insertExperts));
		
	}
	
	public void insertSeveralAlternatives(List<Alternative> insertAlternatives) {
		
		for(Alternative alternative: insertAlternatives) {	
			_alternatives.add(alternative);
		
		}
		
		Collections.sort(_alternatives);
		
		notifyAlternativesChanges(new AlternativesChangeEvent(EAlternativesChange.ADD_SEVERAL_ALTERNATIVES, null, insertAlternatives));
		
	}
	
	public void insertSeveralCriteria(List<Criterion> insertCriteria, Boolean hasParent) {
		Criterion parent = insertCriteria.get(0).getParent();
		
		for(Criterion criterion: insertCriteria) {	
			if(!hasParent) {
				_criteria.add(criterion);
			} else {
				parent.addSubcriterion(criterion);
			}
		}
		
		
		notifyCriteriaChanges(new CriteriaChangeEvent(ECriteriaChange.ADD_CRITERIA, null, insertCriteria));
		
	}
	
	public void removeExpert(Expert expert, Boolean hasParent) {
		
		if(!hasParent) {
			_experts.remove(expert);
			Collections.sort(_experts);
		}
		
		notifyExpertsChanges(new ExpertsChangeEvent(EExpertsChange.REMOVE_EXPERT, expert, null));
		
	}
	
	public void removeAlternative(Alternative alternative) {
		
		_alternatives.remove(alternative);
		Collections.sort(_alternatives);
		
		notifyAlternativesChanges(new AlternativesChangeEvent(EAlternativesChange.REMOVE_ALTERNATIVE, alternative, null));
		
	}
	
	public void removeCriterion(Criterion criterion, Boolean hasParent) {
		
		if(!hasParent) {
			_criteria.remove(criterion);
			Collections.sort(_criteria);
		}
		
		notifyCriteriaChanges(new CriteriaChangeEvent(ECriteriaChange.REMOVE_CRITERION, criterion, null));
	}
	
	public void removeSeveralExperts(List<Expert> removeExperts, Boolean hasParent) {
		Expert parent = removeExperts.get(0).getParent();
		
		for(Expert expert: removeExperts) {	
			if(!hasParent) {
				_experts.remove(expert);
			} else {
				parent.removeChildren(expert);
				expert.setParent(parent);
			}
		}
		
		notifyExpertsChanges(new ExpertsChangeEvent(EExpertsChange.REMOVE_SEVERAL_EXPERTS, removeExperts, null));
		
	}
	
	public void removeSeveralAlternatives(List<Alternative> removeAlternatives) {
		
		for(Alternative alternative: removeAlternatives) {
			_alternatives.remove(alternative);
			
		}
		
		Collections.sort(_alternatives);
		
		notifyAlternativesChanges(new AlternativesChangeEvent(EAlternativesChange.REMOVE_SEVERAL_ALTERNATIVES, removeAlternatives, null));	
	
	}
	
	public void removeSeveralCriteria(List<Criterion> removeCriteria, Boolean hasParent) {
		Criterion parent = removeCriteria.get(0).getParent();
		
		for(Criterion criterion: removeCriteria) {
			if(!hasParent) {
				_criteria.remove(criterion);
			} else {
				parent.removeSubcriterion(criterion);
				criterion.setParent(parent);
			}
		}
		
		notifyCriteriaChanges(new CriteriaChangeEvent(ECriteriaChange.REMOVE_CRITERIA, removeCriteria, null));
		
	}
	
	public void modifyExpert(Expert modifyExpert, String id) {
		Expert oldExpert = (Expert) modifyExpert.clone();
		modifyExpert.setId(id);
		
		
		notifyExpertsChanges(new ExpertsChangeEvent(EExpertsChange.MODIFY_EXPERT, oldExpert, modifyExpert));
		
	}
	
	public void modifyAlternative(Alternative modifyAlternative, String id) {
		Alternative oldAlternative = (Alternative) modifyAlternative.clone();
		modifyAlternative.setId(id);
		
		Collections.sort(_alternatives);
		
		notifyAlternativesChanges(new AlternativesChangeEvent(EAlternativesChange.MODIFY_ALTERNATIVE, oldAlternative, modifyAlternative));
		
	}
	
	public void modifyCriterion(Criterion modifyCriterion, String id, Boolean newCost) {
		Criterion oldCriterion = (Criterion) modifyCriterion.clone();
		modifyCriterion.setId(id);
		modifyCriterion.setCost(newCost);
		
		notifyCriteriaChanges(new CriteriaChangeEvent(ECriteriaChange.MODIFY_CRITERION, oldCriterion, modifyCriterion));
	}
	
	public void registerExpertsChangesListener(IExpertsChangeListener listener) {
		_expertsListener.add(listener);
	}
	
	public void unregisterExpertsChangeListener(IExpertsChangeListener listener) {
		_expertsListener.remove(listener);
	}
	
	public void registerAlternativesChangesListener(IAlternativesChangeListener listener) {
		_alternativesListener.add(listener);
	}
	
	public void unregisterAlternativesChangeListener(IAlternativesChangeListener listener) {
		_alternativesListener.remove(listener);
	}
	
	public void registerCriteriaChangeListener(ICriteriaChangeListener listener) {
		_criteriaListener.add(listener);
	}
	
	public void unregisterCriteriaChangeListener(ICriteriaChangeListener listener) {
		_criteriaListener.remove(listener);
	}
	
	public void notifyExpertsChanges(ExpertsChangeEvent event) {
		for(IExpertsChangeListener listener: _expertsListener) {
			listener.notifyExpertsChange(event);
		}
		
		//TODO workspace
	}
	
	public void notifyAlternativesChanges(AlternativesChangeEvent event) {
		for(IAlternativesChangeListener listener: _alternativesListener) {
			listener.notifyAlternativesChange(event);
		}
		
		//TODO workspace
	}
	
	public void notifyCriteriaChanges(CriteriaChangeEvent event) {
		for(ICriteriaChangeListener listener: _criteriaListener) {
			listener.notifyCriteriaChange(event);
		}
		
		//TODO workspace
	}
	
	public void clear() {
		if(!_experts.isEmpty()) {
			_experts.clear();
			notifyExpertsChanges(new ExpertsChangeEvent(EExpertsChange.EXPERTS_CHANGES, null, _experts));
		}
		
		if(!_alternatives.isEmpty()) {
			_alternatives.clear();
			notifyAlternativesChanges(new AlternativesChangeEvent(EAlternativesChange.ALTERNATIVES_CHANGES, null, _alternatives));
		}
		
		if(!_criteria.isEmpty()) {
			_criteria.clear();
			notifyCriteriaChanges(new CriteriaChangeEvent(ECriteriaChange.CRITERIA_CHANGES, null, _criteria));
		}
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		
		ProblemElementsSet result = null;
		
		result = (ProblemElementsSet) super.clone();
		
		result._experts = new LinkedList<Expert>();
		for(Expert expert: _experts){
			result._experts.add((Expert) expert.clone());
		}
		
		result._alternatives = new LinkedList<Alternative>();
		for(Alternative alternative: _alternatives){
			result._alternatives.add((Alternative) alternative.clone());
		}
		
		result._criteria = new LinkedList<Criterion>();
		for(Criterion criterion: _criteria){
			result._criteria.add((Criterion) criterion.clone());
		}
		
		result._expertsListener = new LinkedList<IExpertsChangeListener>();
		for(IExpertsChangeListener listener: _expertsListener) {
			result._expertsListener.add(listener);
		}
		
		result._alternativesListener = new LinkedList<IAlternativesChangeListener>();
		for(IAlternativesChangeListener listener: _alternativesListener) {
			result._alternativesListener.add(listener);
		}
		
		result._criteriaListener = new LinkedList<ICriteriaChangeListener>();
		for(ICriteriaChangeListener listener: _criteriaListener) {
			result._criteriaListener.add(listener);
		}
		
		return result;
		
	}
	
}