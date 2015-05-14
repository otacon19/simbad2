package flintstones.resolutionphase.state;

public class ResolutionPhaseStateChangeEvent {
	
	private EResolutionPhaseStateChange _change;
	
	private ResolutionPhaseStateChangeEvent(){}
	
	public ResolutionPhaseStateChangeEvent(EResolutionPhaseStateChange change) {
		this();
		_change = change;
	}

	public EResolutionPhaseStateChange getChange() {
		return _change;
	}
	
}
