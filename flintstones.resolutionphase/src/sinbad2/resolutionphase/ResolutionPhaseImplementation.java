
package sinbad2.resolutionphase;

import sinbad2.core.workspace.IWorkspaceContent;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.resolutionphase.state.IResolutionPhaseStateListener;

public abstract class ResolutionPhaseImplementation implements IResolutionPhaseStateListener, IWorkspaceContent {
	
	protected ResolutionPhase _resolutionPhase;
	protected ProblemElementsSet _elementSet;
	protected ProblemElementsManager _elementsManager;
	
	public ResolutionPhase getResolutionPhase() {
		return _resolutionPhase;
	}
	
	public void setResolutionPhase(ResolutionPhase resolutionPhase) {
		_resolutionPhase = resolutionPhase;
	}
	
	public ProblemElementsSet getProblemElementsSet() {
		return _elementSet;
	}
	
	public void setElementSet(ProblemElementsSet elementSet) {
		_elementSet = elementSet;
	}
	
	@Override
	public Object getElement(String id) {
		return null;
	}
	
	@Override
	public IWorkspaceContent copyStructure() {
		ResolutionPhaseImplementation result = this.newInstance();
		
		result.setResolutionPhase(_resolutionPhase);
		result.setElementSet(_elementSet);

		return result;
	}
	
	@Override
	public IWorkspaceContent copyData() {
		IWorkspaceContent result = copyStructure();
		result.copyData(this);

		return result;
	}
	
	@Override
	public void copyData(IWorkspaceContent content) {
		clear();
		ResolutionPhaseImplementation rpi = (ResolutionPhaseImplementation) content;
		_elementSet.setExperts(rpi.getProblemElementsSet().getExperts());
	}

	@Override
	public void clear() {
		_elementSet.clear();
	}

	@Override
	public void activate() {
		_elementsManager = ProblemElementsManager.getInstance();
		if(_elementSet == null) {
			_elementSet = new ProblemElementsSet();
		}
		_elementsManager.setActiveElementSet(_elementSet);
	}
	
	public abstract ResolutionPhaseImplementation newInstance();

}
