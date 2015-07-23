package sinbad2.phasemethod.ui.state;


public class PhaseMethodUIStateChangeEvent {
	
	private EPhaseMethodUIStateChange _change;
	
	private PhaseMethodUIStateChangeEvent() {}
	
	public PhaseMethodUIStateChangeEvent(EPhaseMethodUIStateChange change) {
		this();
		_change = change;
	}
	
	public EPhaseMethodUIStateChange getChange() {
		return _change;
	}
}
