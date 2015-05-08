package flintstones.element.ui.handler.expert.add;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;

import flintstones.element.ElementSet;
import flintstones.element.expert.Expert;

public class AddExpertInputValidator implements IInputValidator {
	
	private boolean _isMember;
	private Expert _parent;
	private ElementSet _elementSet;
	
	public AddExpertInputValidator() {
		super();
	}
	
	public AddExpertInputValidator(Expert parent, ElementSet elementSet) {
		_parent = parent;;
		_isMember = (_parent != null);
		_elementSet = elementSet;
		
	}
	
	public void setIsMember(boolean isMember) {
		_isMember = isMember;
	}
	
	@Override
	public String isValid(String newText) {
		newText = newText.trim();
		
		List<Expert> brothers = null;
		
		if(_isMember) {
			brothers = _parent.getMembers();
		} else {
			brothers = _elementSet.getExperts();
			brothers = new LinkedList<Expert>();
		}
		
		if(newText.isEmpty()) {
			return "Valor vacío no permitido";
		} else if(newText.contains(":")) {
			return "Carácter : no permitido";
		} else {
			if(brothers != null) {
				for(Expert expert: brothers) {
					if(newText.equals(expert.getId())) {
						return "id's duplicados";
					}
				}
			}
		}
		return null;
	}

}
