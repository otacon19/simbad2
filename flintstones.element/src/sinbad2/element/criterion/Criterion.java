package sinbad2.element.criterion;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sinbad2.element.ProblemElement;

public class Criterion extends ProblemElement {
	
	private List<Criterion> _subcriteria;
	private Criterion _parent;
	private boolean _cost;
	
	public Criterion() {
		super();
		
		_subcriteria = null;
		_parent = null;
		_cost = false;
	}
	
	public Criterion(String id) {
		super(id);
	}
	
	@Override
	public String getFormatId() {
		String result = null;
		
		if(_parent != null) {
			result = _parent.getFormatId() + ":" + _id; //$NON-NLS-1$
		} else {
			result = _id;
		}
		
		return result;
	}
	
	public void setParent(Criterion parent) {
		_parent = parent;
	}
	
	public Criterion getParent() {
		return _parent;
	}
	
	public void setCost(Boolean cost) {
		_cost = cost;
	}
	
	public boolean getCost() {
		return _cost;
	}
	
	public void addSubcriterion(Criterion subcriterion) {
		//TODO validator
		
		if(_subcriteria == null) {
			_subcriteria = new LinkedList<Criterion>();
		} 
		
		subcriterion.setParent(this);
		_subcriteria.add(subcriterion);
		Collections.sort(_subcriteria);
	}
	
	public void removeSubcriterion(Criterion subcriterion) {
		
		if(_subcriteria != null) {
			_subcriteria.remove(subcriterion);
			subcriterion.setParent(null);
		}
		
		Collections.sort(_subcriteria);
	}
	
	public List<Criterion> getSubcriteria() {
		return _subcriteria;
	}
	
	public void setSubcriteria(List<Criterion> subcriteria) {
		_subcriteria = subcriteria;
	}
	
	public boolean hasSubcriterial() {
		
		if(_subcriteria != null) {
			if(!_subcriteria.isEmpty()) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(obj == null) {
			return false;
		}
		
		if(obj.getClass() != this.getClass()) {
			return false;
		}
		
		//TODO builder
		
		return false;
	}
	
	public static Criterion getCriterionByFormatId(List<Criterion> elements, String formatId) {
		
		//TODO validator
		
		if(formatId.contains(":")) {
			String parentID = formatId.split(":")[0];		
			for(Criterion criterion: elements) {
				if(criterion.getId().equals(parentID)) {
					return getCriterionByFormatId(criterion.getSubcriteria(), formatId.substring(parentID.length() + 1));
				}
			}
		} else {
			for(Criterion criterion: elements) {
				if(criterion.getId().equals(formatId)) {
					return criterion;
				}
			}
		}
		
		return null;
	}
	
	//TODO hashcode
	
	@Override
	public Object clone() {
		Criterion result = null;
		
		result = (Criterion) super.clone();
		result.setParent(_parent);
		result.setCost(_cost);
		
		if(hasSubcriterial()) {
			List<Criterion> subcriteria = new LinkedList<Criterion>();
			for(Criterion criterion: _subcriteria) {
				subcriteria.add((Criterion)criterion.clone());
			}
			result.setSubcriteria(subcriteria);
		}
		
		return result;
	}
}
