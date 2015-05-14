package flintstones.resolutionphase.framework;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import flintstones.resolutionphase.IResolutionPhase;
import flintstones.resolutionphase.state.EResolutionPhaseStateChange;
import flintstones.resolutionphase.state.ResolutionPhaseStateChangeEvent;

public class Framework  implements IResolutionPhase {
	
	public static final String ID = "flintstones.resolutionphase.framework"; //$NON-NLS-1$
	
	private ProblemElementsSet _elementSet;
	
	private ProblemElementsManager _elementsManager;
	
	public Framework() {

		_elementsManager = ProblemElementsManager.getInstance();
		
		_elementSet = new ProblemElementsSet();
	}
	
	
	public ProblemElementsSet getElementSet() {
		return _elementSet;
	}

	@Override
	public void notifyResolutionPhaseStateChange(ResolutionPhaseStateChangeEvent event) {
		if(event.getChange().equals(EResolutionPhaseStateChange.ACTIVATED)) {
			activate();
		}
		
	}

	@Override
	public void clear() {
		_elementSet.clear();
		
	}

	@Override
	public IResolutionPhase clone() {
		Framework result = null;
		
		try {
			result = (Framework) super.clone();
			result._elementSet = (ProblemElementsSet) _elementSet.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("Error " + e.getMessage()); //$NON-NLS-1$
		}
		
		return result;
	}

	@Override
	public IResolutionPhase copyStructure() {
		return new Framework();
	}

	@Override
	public void copyData(IResolutionPhase iResolutionPhase) {
		Framework framework = (Framework) iResolutionPhase;
		
		clear();
		_elementSet.setExperts(framework.getElementSet().getExperts());

	}

	@Override
	public void activate() {
		_elementsManager.setActiveElementSet(_elementSet);
		
	}

	@Override
	public boolean validate() {
		
		if(_elementSet.getExperts().isEmpty()) {
			return false;
		}
		
		return true;
	}

}
