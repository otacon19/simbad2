package sinbad2.phasemethod;

import sinbad2.phasemethod.listener.IPhaseMethodStateListener;

public interface IPhaseMethod extends IPhaseMethodStateListener {

	public void clear();

	public IPhaseMethod clone();

	public IPhaseMethod copyStructure();

	public void copyData(IPhaseMethod iPhaseMethod);

	public void activate();

	public boolean validate();

}
