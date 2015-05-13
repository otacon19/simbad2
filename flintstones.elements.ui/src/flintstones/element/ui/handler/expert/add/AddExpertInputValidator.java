package flintstones.element.ui.handler.expert.add;

import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;

import flintstones.element.ElementSet;
import flintstones.element.expert.Expert;
import flintstones.element.ui.nls.Messages;

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
		}
		
		if(newText.isEmpty()) {
			return Messages.AddExpertInputValidator_Empty_value_not_allowed;
		} else if(newText.contains(":")) { //$NON-NLS-1$
			return Messages.AddExpertInputValidator_Character_colon_not_allowed;
		} else {
			if(brothers != null) {
				for(Expert expert: brothers) {
					if(newText.equals(expert.getId())) {
						return Messages.AddExpertInputValidator_Duplicated_id;
					}
				}
			}
		}
		return null;
	}

}
