package sinbad2.phasemethod.unbalanced.methodology.unification;

import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.linguistic.unbalanced.Unbalanced;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;

public class UnificationPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.unbalanced.methodology.unification";
	
	private DomainsManager _domainsManager;
	private DomainSet _domainSet;
	
	private static UnificationPhase _instance;
	
	public UnificationPhase() {
		_domainsManager = DomainsManager.getInstance();
		_domainSet = _domainsManager.getActiveDomainSet();
	}
	
	public static UnificationPhase getInstance() {
		if(_instance == null) {
			_instance = new UnificationPhase();
		}
		
		return _instance;
	}
	
	public DomainSet getDomainSet() {
		return _domainSet;
	}
	
	public Unbalanced getDomainLH() {
		return (Unbalanced) _domainSet.getDomains().get(0);
	}
	
	@Override
	public IPhaseMethod copyStructure() {
		return new UnificationPhase();
	}

	@Override
	public void copyData(IPhaseMethod iPhaseMethod) {
		UnificationPhase unification = (UnificationPhase) iPhaseMethod;
		
		clear();
		
		_domainSet.setDomains(unification.getDomainSet().getDomains());
		
	}
	
	@Override
	public void clear() {
		_domainSet.clear();
	}

	@Override
	public void activate() {
		_domainsManager.setActiveDomainSet(_domainSet);
	}

	@Override
	public boolean validate() {
		if(_domainSet.getDomains().isEmpty()) {
			return false;
		}
		
		return true;
	}

	@Override
	public IPhaseMethod clone() {
		UnificationPhase result = null;

		try {
			result = (UnificationPhase) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		if (event.getChange().equals(EPhaseMethodStateChange.ACTIVATED)) {
			activate();
		}
	}

}
