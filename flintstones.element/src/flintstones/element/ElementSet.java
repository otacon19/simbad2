package flintstones.element;

import java.util.LinkedList;
import java.util.List;

import flintstones.element.expert.Expert;

public class ElementSet implements Cloneable {
	
	private List<Expert> _experts;

	public ElementSet(){
		_experts = new LinkedList<Expert>();
	}

	public List<Expert> get_experts() {
		return _experts;
	}

	public void set_experts(List<Expert> _experts) {
		this._experts = _experts;
	}
	
	public void inset_expert(Expert expert) {
		_experts.add(expert);
		
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		
		ElementSet result = null;
		
		result = (ElementSet) super.clone();
		
		result._experts = new LinkedList<Expert>();
		for(Expert expert: _experts){
			result._experts.add((Expert) expert.clone());
		}
		
		return result;
		
	}
	
}
