package sinbad2.element.ui.handler.alternative.add;


import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;

import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.ui.nls.Messages;

public class AddAlternativeInputValidator implements IInputValidator {
	
	private ProblemElementsSet _elementSet;
	
	public AddAlternativeInputValidator() {
		super();
	}
	
	public AddAlternativeInputValidator(ProblemElementsSet elementSet) {
		_elementSet = elementSet;
	}
	
	@Override
	public String isValid(String newText) {
		
		newText = newText.trim();
		
		if(newText.isEmpty()) {
			return Messages.AddAlternativeInputValidator_Empty_value_not_allowed;
		} else if(newText.contains(":")) { //$NON-NLS-1$
			return Messages.AddAlternativeInputValidator_Character_colon_not_allowed;
		} else {
			List<Alternative> alternatives = _elementSet.getAlternatives();
			for(Alternative alternative: alternatives) {
				if(newText.equals(alternative.getId())) {
					return Messages.AddAlternativeInputValidator_Duplicated_id;
				}
			}
		}
		
		return null;
	}

}
