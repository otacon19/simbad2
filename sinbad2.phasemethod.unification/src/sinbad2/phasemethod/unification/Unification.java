package sinbad2.phasemethod.unification;

import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.phasemethod.IPhaseMethod;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public IPhaseMethod clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

}
