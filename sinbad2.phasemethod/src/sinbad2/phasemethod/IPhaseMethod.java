package sinbad2.phasemethod;

import sinbad2.phasemethod.state.IPhaseMethodStateListener;


public interface IPhaseMethod extends IPhaseMethodStateListener {

	public void clear();

	public IPhaseMethod clone();

	public IPhaseMethod copyStructure();

	public void copyData(IPhaseMethod iMethodPhase);

	public void activate();

	public boolean validate();
	
}
