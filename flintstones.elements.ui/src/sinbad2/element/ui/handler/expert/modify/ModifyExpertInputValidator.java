package sinbad2.element.ui.handler.expert.modify;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;

import sinbad2.element.ProblemElementsSet;
import sinbad2.element.expert.Expert;
import sinbad2.element.ui.nls.Messages;

public class ModifyExpertInputValidator implements IInputValidator {
	
	private ProblemElementsSet _elementSet;
	private Expert _parentOfModifyExpert;
	private List<Expert> _brothers;
	private String _id;
	
	public ModifyExpertInputValidator() {
		super();
	}
	
	public ModifyExpertInputValidator(Expert parent, ProblemElementsSet elementSet, String id) {
		_parentOfModifyExpert = parent;
		_elementSet = elementSet;
		_id = id;
		
		_brothers = new LinkedList<Expert>();
		
		if(_parentOfModifyExpert != null) {
			_brothers = _parentOfModifyExpert.getChildren();
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
			return Messages.ModifyExpertInputValidator_Empty_value_not_allowed;
		} else if(newText.contains(":")) { //$NON-NLS-1$
			return Messages.ModifyExpertInputValidator_Character_colon_not_allowed;
		} else {
			if(_brothers != null) {
				for(Expert expert: _brothers) {
					if(newText.equals(expert.getId())) {
						return Messages.ModifyExpertInputValidator_Duplicated_id;
					}
				}
			}
		}
		
		return null;
	}

}
