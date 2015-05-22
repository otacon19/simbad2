package sinbad2.element.expert;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
	public String getPathId(){
		String result = null;
		
		if(_parent != null) {
			result = _parent.getPathId() + ">" + _id; //$NON-NLS-1$
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
		//TODO validar member
		
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
	public boolean equals(Object object) {
		if(this == object) {
			return true;
		}
		
		if(object == null || object.getClass() != this.getClass()) {
			return false;
		}
		
		//TODO builder
		
		return false;
	}
	
	public static Expert getExpertByFormatId(List<Expert> experts, String formatId) {
		//TODO validator
		
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
	
	//TODO hashcode
	
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
				return getExpertByFormatId(expert.getChildrens(), formatId.substring(parentId.length() + 1));
			}
		}
		
		return null;
	}
}
