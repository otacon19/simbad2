package flintstones.element.ui.handler.expert.modify;

import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;

import flintstones.element.ElementSet;
import flintstones.element.expert.Expert;

public class ModifyExpertInputValidator implements IInputValidator {
	
	private Expert _parent;
	private ElementSet _elementSet;
	private List<Expert> _brothers;
	private String _id;
	
	public ModifyExpertInputValidator() {
		super();
	}
	
	public ModifyExpertInputValidator(Expert parent, ElementSet elementSet, String id) {
		_parent = parent;
		_elementSet = elementSet;
		_id = id;
		
		if(_parent != null) {
			_brothers = _parent.getMembers();
		} else {
			_brothers = _elementSet.getExperts();
		}
	}
	
	
	@Override
	public String isValid(String newText) {
		
		newText = newText.trim();
		
		if(newText.equals(_id)) {
			return null;
		} else if(newText.isEmpty()) {
			return "Empty value not allowed";
		} else if(newText.contains(":")) {
			return "Character ':' not allowed";
		} else {
			if(_brothers != null) {
				for(Expert expert: _brothers) {
					if(newText.equals(expert.getId())) {
						return "Duplicate id";
					}
				}
			}
		}
		
		return null;
	}

}
