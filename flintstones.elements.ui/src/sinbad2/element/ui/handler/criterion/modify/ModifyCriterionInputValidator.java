package sinbad2.element.ui.handler.criterion.modify;

import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;

import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.ui.nls.Messages;

public class ModifyCriterionInputValidator implements IInputValidator {
	
	private Criterion _parent;
	private ProblemElementsSet _elementSet;
	private String _id;
	
	public ModifyCriterionInputValidator() {
		super();
	}
	
	public ModifyCriterionInputValidator(Criterion parent, String id, ProblemElementsSet elementSet) {
		_parent = parent;
		_id = id;
		_elementSet = elementSet;
	}
	
	@Override
	public String isValid(String newText) {
		
		newText = newText.trim();
		
		List<Criterion> brothers_or_others = null;
		
		if(_parent != null) {
			brothers_or_others = _parent.getSubcriteria();
		} else {
			brothers_or_others = _elementSet.getCriteria();
		}
		
		if(newText.equals(_id)) {
			return null;
		} else if(newText.isEmpty()) {
				return Messages.ModifyCriterionInputValidator_Empty_value_not_allowed;
			} else if(newText.contains(":")) { //$NON-NLS-1$
				return Messages.ModifyCriterionInputValidator_Character_colon_not_allowed;
			} else {
				if(brothers_or_others != null) {
					for(Criterion criterion: brothers_or_others) {
						if(newText.equals(criterion.getId())) {
							return Messages.ModifyCriterionInputValidator_Duplicated_id;
						}
					}
				}
			}
		
		return null;
	}


}
