package flintstones.resolutionphase;

import flintstones.resolutionphase.state.IResolutionPhaseStateListener;

public interface IResolutionPhase extends IResolutionPhaseStateListener {
	
	//TODO operaciones xml
	
	public void clear();
	
	public IResolutionPhase clone();
	
	public IResolutionPhase copyStructure();
	
	public void copyData(IResolutionPhase iResolutionPhase);
	
	public void activate();
	
	public boolean validate();
	
}
