package sinbad2.element.ui.handler.alternative.modify;

import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;

import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;

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
