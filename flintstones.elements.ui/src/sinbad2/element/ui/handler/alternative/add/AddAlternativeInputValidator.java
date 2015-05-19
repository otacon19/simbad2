package sinbad2.element.ui.handler.alternative.add;


import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;

import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;

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
			return "Empty value not allowed";
		} else if(newText.contains(":")) {
			return "Character ':' not allowed";
		} else {
			List<Alternative> alternatives = _elementSet.getAlternatives();
			for(Alternative alternative: alternatives) {
				if(newText.equals(alternative.getId())) {
					return "Duplicated id";
				}
			}
		}
		
		return null;
	}

}
