package flintstones.resolutionphase.ui.state;

public class ResolutionPhaseUIStateChangeEvent {

	private EResolutionPhaseUIStateChanges _change;
	
	private ResolutionPhaseUIStateChangeEvent() {}
	
	public ResolutionPhaseUIStateChangeEvent(EResolutionPhaseUIStateChanges change) {
		this();
		_change = change;
	}
	
	public EResolutionPhaseUIStateChanges getChange() {
		return _change;
	}
}
