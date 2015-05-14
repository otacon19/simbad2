package sinbad2.element;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sinbad2.element.expert.Expert;
import sinbad2.element.expert.listener.EExpertsChange;
import sinbad2.element.expert.listener.ExpertsChangeEvent;
import sinbad2.element.expert.listener.IExpertsChangeListener;

public class ProblemElementsSet implements Cloneable {
	
	private List<Expert> _experts;
	
	private List<IExpertsChangeListener> _expertsListener;

	
	public ProblemElementsSet(){
		_experts = new LinkedList<Expert>();
		
		_expertsListener = new LinkedList<IExpertsChangeListener>();
	}

	public List<Expert> getExperts() {
		return _experts;
	}
	
	public void setExperts(List<Expert> experts) {
		//TODO Clase validator
		
		_experts = experts;
		
		notifyExpertsChanges(new ExpertsChangeEvent(EExpertsChange.EXPERTS_CHANGES, null, _experts));
	}

	public void insertExpert(Expert newExpert) {
		_experts.add(newExpert);
		Collections.sort(_experts);
		
	}
	
	public void registerExpertsChangesListener(IExpertsChangeListener listener) {
		_expertsListener.add(listener);
	}
	
	public void unregisterExpertsChangeListener(IExpertsChangeListener listener) {
		_expertsListener.remove(listener);
	}
	
	public void notifyExpertsChanges(ExpertsChangeEvent event) {
		for(IExpertsChangeListener listener: _expertsListener) {
			listener.notifyExpertsChange(event);
		}
		
		//TODO workspace
	}
	
	public void clear() {
		if(!_experts.isEmpty()) {
			_experts.clear();
			notifyExpertsChanges(new ExpertsChangeEvent(EExpertsChange.EXPERTS_CHANGES, null, _experts));
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
		
		result._expertsListener = new LinkedList<IExpertsChangeListener>();
		for(IExpertsChangeListener listener: _expertsListener) {
			result._expertsListener.add(listener);
		}
		
		return result;
		
	}
	
}
