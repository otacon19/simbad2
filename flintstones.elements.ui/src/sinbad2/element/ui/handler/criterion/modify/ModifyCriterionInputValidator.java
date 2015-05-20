package sinbad2.element.ui.handler.criterion.modify;

import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;

import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;

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
