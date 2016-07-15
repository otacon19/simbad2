package sinbad2.element.ui.handler.criterion.add;

import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;

import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.ui.nls.Messages;

public class AddCriterionInputValidator implements IInputValidator {
	
	private boolean _isMember;
	private Criterion _parent;
	private ProblemElementsSet _elementSet;
	
	public AddCriterionInputValidator() {
		super();
	}
	
	public AddCriterionInputValidator(Criterion parent, ProblemElementsSet elementSet) {
		_parent = parent;
		_isMember = (_parent != null);
		_elementSet = elementSet;
	}
	
	public void setIsMember(Boolean isMember) {
		_isMember = isMember;
	}
	
	@Override
	public String isValid(String newText) {
		
		newText = newText.trim();
		
		List<Criterion> brothers_or_others = null;
		
		if(_isMember) {
			brothers_or_others = _parent.getSubcriteria();
		} else {
			brothers_or_others = _elementSet.getCriteria();
		}
		
		if(newText.isEmpty()) {
			return Messages.AddCriterionInputValidator_Empty_value_not_allowed;
		} else if(newText.contains(":")) { //$NON-NLS-1$
			return Messages.AddCriterionInputValidator_Character_colon_not_allowed;
		} else {
			if(brothers_or_others != null) {
				for(Criterion criterion: brothers_or_others) {
					if(newText.equals(criterion.getId())) {
						return Messages.AddCriterionInputValidator_Duplicated_id;
					}
				}
			}
		}
		
		return null;
	}

}
