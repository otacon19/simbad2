package sinbad2.element.ui.handler.criterion.add;

import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;

import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;

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
			return "Empty value not allowed";
		} else if(newText.contains(":")) {
			return "Character ':' not allowed";
		} else {
			if(brothers_or_others != null) {
				for(Criterion criterion: brothers_or_others) {
					if(newText.equals(criterion.getId())) {
						return "Duplicated id";
					}
				}
			}
		}
		
		return null;
	}

}
