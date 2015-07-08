package sinbad2.element.expert;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.validator.Validator;
import sinbad2.element.ProblemElement;

public class Expert extends ProblemElement {
	
	private List<Expert> _childrens;
	private Expert _parent;

	public Expert() {
		super();
		
		_childrens = null;
		_parent = null;
	}

	public Expert(String id) {
		super(id);
		
		_childrens = null;
		_parent = null;
	}
	
	@Override
	public String getCanonicalId(){
		String result = null;
		
		if(_parent != null) {
			result = _parent.getCanonicalId() + ">" + _id; //$NON-NLS-1$
		} else {
			result = _id;
		}
		
		return result;
	}
	
	public void setParent(Expert parent) {
		_parent = parent;
	}
	
	public Expert getParent() {
		return _parent;
	}

	public void addChildren(Expert children) {
		Validator.notNull(children);
		Validator.notSameElement(this, children);
		
		if(_childrens == null) {
			_childrens = new LinkedList<Expert>();
		}
		_childrens.add(children);
		children.setParent(this);
		
		Collections.sort(_childrens);
	}
	
	public void removeChildren(Expert children) {
		
		if(_childrens != null) {
			_childrens.remove(children);
			children.setParent(null);
		}
		
		Collections.sort(_childrens);
			
	}

	public List<Expert> getChildrens() {
		return _childrens;
	}
	
	public void setChildrens(List<Expert> childrens) {
		_childrens = childrens;
	}
	
	public boolean hasChildrens() {
		if(_childrens != null && !_childrens.isEmpty()) {
			return true;
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
		
		final Expert other = (Expert) obj;
		
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(_childrens, other._childrens);
		eb.append(_id, other._id);
		eb.append(_parent, other._parent);
		
		return eb.isEquals();
	}
	
	public static Expert getExpertByCanonicalId(List<Expert> experts, String formatId) {
		Validator.notNull(experts);
		Validator.notNull(formatId);
		
		if(formatId.contains(">")) { //$NON-NLS-1$
			String parentId = formatId.split(">")[0]; //$NON-NLS-1$
			return searchExpertForPathId(parentId, formatId, experts);
		} else {
			for(Expert expert: experts) {
				if(expert.getId().equals(formatId)) {
					return expert;
				}
			}
		}
		return null;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(getCanonicalId());
		if(hasChildrens()) {
			for(Expert expert: _childrens) {
				hcb.append(expert);
			}
		}
		return hcb.toHashCode();
	}
	
	@Override
	public Object clone() {
		
		Expert result = null;
		
		result = (Expert) super.clone();
		result.setParent(_parent);
		
		if(hasChildrens()) {
			List<Expert> childrens = new LinkedList<Expert>();
			for(Expert expert: _childrens){
				childrens.add((Expert) expert.clone());
			}
			result.setChildrens(childrens);
		}
		
		return result;
	}
	
	private static Expert searchExpertForPathId(String parentId, String formatId, List<Expert> experts) {
		
		for(Expert expert: experts) {
			if(expert.getId().equals(parentId)) {
				return getExpertByCanonicalId(expert.getChildrens(), formatId.substring(parentId.length() + 1));
			}
		}
		
		return null;
	}
}
