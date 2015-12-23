package sinbad2.method.ui.state;

public class MethodUIStateChangeEvent {
	
	EMethodUIStateChanges _change;
	
	private MethodUIStateChangeEvent(){}
	
	public MethodUIStateChangeEvent(EMethodUIStateChanges change) {
		this();
		_change = change;
	}
	
	public EMethodUIStateChanges getChange() {
		return _change;
	}
	
}
