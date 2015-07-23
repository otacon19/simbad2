package sinbad2.phasemethod.state;


public class PhaseMethodStateChangeEvent {
	
	private EPhaseMethodStateChange _change;
	
	private PhaseMethodStateChangeEvent(){}
	
	public PhaseMethodStateChangeEvent(EPhaseMethodStateChange change) {
		this();
		_change = change;
	}

	public EPhaseMethodStateChange getChange() {
		return _change;
	}
	
}
