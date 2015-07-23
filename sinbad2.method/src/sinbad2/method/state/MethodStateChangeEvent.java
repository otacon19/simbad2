package sinbad2.method.state;


public class MethodStateChangeEvent {
	
	private EMethodStateChanges _change;
	
	private MethodStateChangeEvent() {}
	
	public MethodStateChangeEvent(EMethodStateChanges change) {
		this();
		_change = change;
	}
	
	public EMethodStateChanges getChange() {
		return _change;
	}
	
}
