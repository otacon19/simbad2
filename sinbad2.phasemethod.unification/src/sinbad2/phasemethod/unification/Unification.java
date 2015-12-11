package sinbad2.phasemethod.unification;

import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;

public class Unification implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.unification";
	
	private DomainsManager _domainsManager;
	private DomainSet _domainSet;
	
	public Unification() {
		_domainsManager = DomainsManager.getInstance();
		_domainSet = _domainsManager.getActiveDomainSet();
	}
	
	public DomainSet getDomainSet() {
		return _domainSet;
	}
	
	@Override
	public IPhaseMethod copyStructure() {
		return new Unification();
	}
	
	@Override
	public void copyData(IPhaseMethod iMethodPhase) {
		Unification unification = (Unification) iMethodPhase;
		
		clear();
		
		_domainSet.setDomains(unification.getDomainSet().getDomains());
	}
	
	@Override
	public void clear() {
		_domainSet.clear();	
	}
	
	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		if (event.getChange().equals(EPhaseMethodStateChange.ACTIVATED)) {
			activate();
		}
	}

	@Override
	public IPhaseMethod clone() {
		Unification result = null;
		
		try {
			result = (Unification) super.clone();
			result._domainSet = (DomainSet) _domainSet.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public void activate() {
		_domainsManager.setActiveDomainSet(_domainSet);
	}

	@Override
	public boolean validate() {
		
		if (_domainSet.getDomains().isEmpty()) {
			return false;
		}

		return true;
	}

}
