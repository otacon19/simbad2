package sinbad2.element.ui.draganddrop;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.expert.Expert;
import sinbad2.element.expert.handler.move.MoveExpertHandler;

public class ExpertsDropListener extends ViewerDropAdapter {
	
	private Expert _expert;
	private Expert _newParent;
	private Expert _oldParent;
	
	public ExpertsDropListener(Viewer viewer) {
		super(viewer);
	}

	@Override
	public boolean performDrop(Object data) {
		MoveExpertHandler handler = new MoveExpertHandler(_expert, _newParent);
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

		if(selectedObject instanceof Expert) {
			_expert = (Expert) selectedObject;
			_oldParent = _expert.getParent();
			if(target != null) {
				_newParent = (Expert) target;
			}
			
			result = checkDropExpert();
			
		}
		
		return result;
	}
	
	private boolean checkDropExpert() {
		ProblemElementsManager elementManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementSet = elementManager.getActiveElementSet();
		
		List<Expert> experts = elementSet.getExperts();
		
		if(_expert == _newParent || (_oldParent == null) && (_newParent == null)){
			 return false;
		} else if(_newParent == null) {
			return !(duplicateID(experts, _expert.getId()));
		} else {
			if((!duplicateID(_newParent.getChildren(), _expert.getId()))) {
				return !isMember(_expert, _newParent);
			} else {
				return false;
			}
		}
		
	}
	
	private boolean isMember(Expert expert, Expert maybeMember) {
		
		if(expert.hasChildren()) {
			for(Expert member: expert.getChildren()) {
				if(member == maybeMember) {
					return true;
				} else {
					if(isMember(member, maybeMember)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}

	private boolean duplicateID(List<Expert> experts, String id) {

		if(experts != null) {
			for(Expert e: experts) {
				if(e.getId().equals(id)) {
					return true;
				}
			}
		}
		
		return false;
	}
	

}
