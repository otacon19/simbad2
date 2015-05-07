package flintstones.resolutionphase;

import flintstones.resolutionphase.state.IResolutionPhaseStateListener;

/**
 * IResolutionPhase.java
 * 
 * 
 * 
 * @author Labella Romero, Álvaro
 * @version 1.0
 *
 */
public interface IResolutionPhase extends IResolutionPhaseStateListener {
	
	//TODO xml
	
	public void clear();
	
	public IResolutionPhase clone();
	
	public IResolutionPhase copyEstructure();
	
	public void copyData(IResolutionPhase iResolutionPhase);
	
	public void activate();
	
	public boolean validate();
	
}
