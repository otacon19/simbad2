package sinbad2.element.expert;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sinbad2.element.ProblemElement;

public class Expert extends ProblemElement {
	
	private List<Expert> _childrens = null;
	private Expert _parent = null;

	public Expert() {
		super();
	}

	public Expert(String id) {
		super(id);
	}
	
	@Override
	public String getFormatId(){
		String result = null;
		
		if(_parent != null) {
			result = _parent.getFormatId() + ":" + _id; //$NON-NLS-1$
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
	
	public static Expert getExpertByFormatId(List<Expert> experts, String idExpertFormat) {
		//TODO validator
		
		if(idExpertFormat.contains(":")) { //$NON-NLS-1$
			String parentId = idExpertFormat.split(":")[0]; //$NON-NLS-1$
			for(Expert expert: experts) {
				if(expert.getId().equals(parentId)) {
					return getExpertByFormatId(expert.getChildrens(), idExpertFormat.substring(parentId.length() + 1));
				}
			}
		} else {
			for(Expert expert: experts) {
				if(expert.getId().equals(idExpertFormat)) {
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
}
