package flintstones.resolutionscheme.state;

public class ResolutionSchemeStateChangeEvent {
	
	private EResolutionSchemeStateChanges _change;
	
	private ResolutionSchemeStateChangeEvent() {
	}
	
	public ResolutionSchemeStateChangeEvent(EResolutionSchemeStateChanges change) {
		this();
		_change = change;
	}
	
	public EResolutionSchemeStateChanges getChange() {
		return _change;
	}

}
