package sinbad2.resolutionscheme.ui.state;

public class ResolutionSchemeUIStateChangeEvent {
	
	EResolutionSchemeUIStateChanges _change;
	
	private ResolutionSchemeUIStateChangeEvent(){}
	
	public ResolutionSchemeUIStateChangeEvent(EResolutionSchemeUIStateChanges change) {
		this();
		_change = change;
	}
	
	public EResolutionSchemeUIStateChanges getChange() {
		return _change;
	}

}
