package sinbad2.method.multigranular;


import java.util.List;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.method.MethodImplementation;
import sinbad2.method.state.MethodStateChangeEvent;

public class Multigranular extends MethodImplementation {
	
	public static final String ID = "flintstones.method.linguistic.multigranular.fusion";
	
	private static final String EVALUATIONS_IN_NOT_BLTS_DOMAIN = "Evaluations in not BLTS domain";
	private static final String EVALUATIONS_IN_NOT_LINGUISTIC_DOMAIN = "Evaluations in not linguistic domain";
	
	private DomainSet _domainsSet;
	
	public Multigranular() {
		DomainsManager domainsManager = DomainsManager.getInstance();
		_domainsSet = domainsManager.getActiveDomainSet();
	}
	
	@Override
	public MethodImplementation newInstance() {
		return new Multigranular();
	}

	@Override
	public void notifyMethodStateChange(MethodStateChangeEvent event) {}

	@Override
	public String isAvailable() {
		List<Domain> domains = _domainsSet.getDomains();
		for(Domain d: domains) {
			if(d instanceof FuzzySet) {
				if(!((FuzzySet) d).isBLTS()) {
					return EVALUATIONS_IN_NOT_BLTS_DOMAIN;
				}
			} else {
				return EVALUATIONS_IN_NOT_LINGUISTIC_DOMAIN;
			}
		}
		return "";
	}
}
