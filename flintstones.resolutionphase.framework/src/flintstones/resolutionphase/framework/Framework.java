package flintstones.resolutionphase.framework;

import flintstones.element.ElementSet;
import flintstones.element.ElementsManager;
import flintstones.resolutionphase.IResolutionPhase;
import flintstones.resolutionphase.state.EResolutionPhaseStateChanges;
import flintstones.resolutionphase.state.ResolutionPhaseStateChangeEvent;

public class Framework  implements IResolutionPhase {
	
	public static final String ID = "flintstones.resolutionphase.framework"; //$NON-NLS-1$
	
	private ElementSet _elementSet;
	
	private ElementsManager _elementsManager;
	
	public Framework() {

		_elementsManager = ElementsManager.getInstance();
		
		_elementSet = new ElementSet();
	}
	
	
	public ElementSet getElementSet() {
		return _elementSet;
	}

	@Override
	public void resolutionPhaseStateChange(ResolutionPhaseStateChangeEvent event) {
		if(event.getChange().equals(EResolutionPhaseStateChanges.ACTIVATED)) {
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
			result._elementSet = (ElementSet) _elementSet.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("Error " + e.getMessage());
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
