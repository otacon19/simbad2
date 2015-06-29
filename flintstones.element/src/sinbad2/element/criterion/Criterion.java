package sinbad2.element.criterion;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.validator.Validator;
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
	public String getPathId() {
		String result = null;
		
		if(_parent != null) {
			result = _parent.getPathId() + ">" + _id; //$NON-NLS-1$
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
		Validator.notNull(subcriterion);
		Validator.notSameElement(this, subcriterion);
		
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
	
	public boolean hasSubcriteria() {
		
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
		
		final Criterion other = (Criterion) obj;
		
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(_parent, other._parent);
		eb.append(_id, other._id);
		eb.append(_subcriteria, other._subcriteria);
		eb.append(_cost, other._cost);
		
		return eb.isEquals();
	}
	
	public static Criterion getCriterionByFormatId(List<Criterion> criteria, String formatId) {
		
		Validator.notNull(criteria);
		Validator.notNull(formatId);
		
		if(formatId.contains(">")) { //$NON-NLS-1$
			String parentId = formatId.split(">")[0]; //$NON-NLS-1$
			return searchCriterionForPathId(parentId, formatId, criteria);
		} else {
			for(Criterion criterion: criteria) {
				if(criterion.getId().equals(formatId)) {
					return criterion;
				}
			}
		}
		
		return null;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(getPathId());
		if(hasSubcriteria()) {
			for(Criterion criterion: _subcriteria) {
				hcb.append(criterion);
			}
		}
		hcb.append(getCost());
		return hcb.toHashCode();
	}
	
	@Override
	public Object clone() {
		Criterion result = null;
		
		result = (Criterion) super.clone();
		result.setParent(_parent);
		result.setCost(_cost);
		
		if(hasSubcriteria()) {
			List<Criterion> subcriteria = new LinkedList<Criterion>();
			for(Criterion criterion: _subcriteria) {
				subcriteria.add((Criterion)criterion.clone());
			}
			result.setSubcriteria(subcriteria);
		}
		
		return result;
	}
	
	private static Criterion searchCriterionForPathId(String parentId, String formatId, List<Criterion> criteria) {
		
		for(Criterion criterion: criteria) {
			if(criterion.getId().equals(parentId)) {
				return getCriterionByFormatId(criterion.getSubcriteria(), formatId.substring(parentId.length() + 1));
			}
		}
		
		return null;
	}
}
