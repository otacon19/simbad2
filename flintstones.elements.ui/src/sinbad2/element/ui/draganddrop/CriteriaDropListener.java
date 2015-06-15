package sinbad2.element.ui.draganddrop;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.criterion.handler.move.MoveCriterionHandler;

public class CriteriaDropListener extends ViewerDropAdapter {
	
	private Criterion _criterion;
	private Criterion _newParent;
	private Criterion _oldParent;
	
	public CriteriaDropListener(Viewer viewer) {
		super(viewer);
	}

	@Override
	public boolean performDrop(Object data) {
		MoveCriterionHandler handler = new MoveCriterionHandler(_criterion, _newParent);
		try {
			handler.execute(null);
		} catch (ExecutionException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		boolean result = false;
		
		Object selectedObject = getSelectedObject();
		if(selectedObject instanceof Criterion) {
			_criterion = (Criterion) selectedObject;
			_oldParent = _criterion.getParent();
			if(target != null) {
				_newParent = (Criterion) target;
			}
			result = checkDropCriterion();	
		}
		
		return result;
	}
	
	private boolean checkDropCriterion() {	
		ProblemElementsManager elementManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementSet = elementManager.getActiveElementSet();
		
		List<Criterion> criteria = elementSet.getCriteria();
		
		if(_criterion == _newParent || ((_oldParent == null) && (_newParent == null))){
			return false;
		} else if(_newParent == null) {
			return !(duplicateID(criteria, _criterion.getId()));
		} else {
			if((!duplicateID(_newParent.getSubcriteria(), _criterion.getId()))) {
				return !isMember(_criterion, _newParent);
			} else {
				return false;
			}
		}
	}

	private boolean isMember(Criterion criterion, Criterion maybeMember) {
		
		if(criterion.hasSubcriteria()) {
			for(Criterion subcriterion: criterion.getSubcriteria()) {
				if(subcriterion == maybeMember) {
					return true;
				} else {
					if(isMember(subcriterion, maybeMember)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}

	private boolean duplicateID(List<Criterion> criteria, String id) {

		if(criteria != null) {
			for(Criterion c: criteria) {
				if(c.getId().equals(id)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
}
