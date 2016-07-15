package sinbad2.element.ui.handler.expert.add;

import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;

import sinbad2.element.ProblemElementsSet;
import sinbad2.element.expert.Expert;
import sinbad2.element.ui.nls.Messages;

public class AddExpertInputValidator implements IInputValidator {
	
	private ProblemElementsSet _elementSet;
	private boolean _isMember;
	private Expert _parentOfNewExpert;
		
	public AddExpertInputValidator() {
		super();
	}
	
	public AddExpertInputValidator(Expert parent, ProblemElementsSet elementSet) {
		_parentOfNewExpert = parent;;
		_isMember = (_parentOfNewExpert != null);
		_elementSet = elementSet;
		
	}
	
	public void setIsMember(boolean isMember) {
		_isMember = isMember;
	}
	
	@Override
	public String isValid(String newText) {
		newText = newText.trim();
		
		List<Expert> brothers = null;
		List<Expert> otherExperts = null;
		
		if(_isMember) {
			brothers = _parentOfNewExpert.getChildren();
		} else {
			otherExperts = _elementSet.getExperts();
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
			} else {
				if(otherExperts != null) {
					for(Expert expert: otherExperts) {
						if(newText.equals(expert.getId())) {
							return Messages.AddExpertInputValidator_Duplicated_id;
						}
					}
				}
			}
		}
		return null;
	}

}
