package sinbad2.element.ui.handler.alternative.modify;

import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;

import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.ui.nls.Messages;

public class ModifyAlternativeInputValidator implements IInputValidator {
	
	private ProblemElementsSet _elementSet;
	private String _oldId;
	
	public ModifyAlternativeInputValidator() {
		super();
	}
	
	public ModifyAlternativeInputValidator(ProblemElementsSet elementSet, String id) {
		_elementSet = elementSet;
		_oldId = id;
	}
	
	@Override
	public String isValid(String newText) {
		
		newText = newText.trim();
		
		if(newText.equals(_oldId)) {
			return null;
		} else if(newText.isEmpty()) {
			return Messages.ModifyAlternativeInputValidator_Empty_value_not_allowed;
		} else if(newText.contains(":")) { //$NON-NLS-1$
			return Messages.ModifyAlternativeInputValidator_Character_colon_not_allowed;
		} else {
			List<Alternative> alternatives = _elementSet.getAlternatives();
			for(Alternative alternative: alternatives) {
				if(newText.equals(alternative.getId())) {
					return Messages.ModifyAlternativeInputValidator_Duplicated_id;
				}
			}
		}
		
		return null;
	}

}
