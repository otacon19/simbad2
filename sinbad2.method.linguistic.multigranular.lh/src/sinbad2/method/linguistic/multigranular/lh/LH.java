package sinbad2.method.linguistic.multigranular.lh;

import java.util.List;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.method.MethodImplementation;
import sinbad2.method.state.MethodStateChangeEvent;

public class LH extends MethodImplementation {

	public static final String ID = "flintstones.method.linguistic.multigranular.lh";
	
	private DomainSet _domainsSet;
	
	private static final String EVALUATIONS_IS_NOT_A_BLTS_DOMAIN = "Evaluations is not a BLTS domain";
	private static final String EVALUATIONS_IS_NOT_A_LINGUISTIC_DOMAIN = "Evaluations is not a linguistic domain";
	
	public LH() {
		DomainsManager domainsManager = DomainsManager.getInstance();
		_domainsSet = domainsManager.getActiveDomainSet();
	}
	
	@Override
	public MethodImplementation newInstance() {
		return new LH();
	}

	@Override
	public void notifyMethodStateChange(MethodStateChangeEvent event) {}

	@Override
	public String isAvailable() {
		List<Domain> domains = _domainsSet.getDomains();
		for(Domain d: domains) {
			if(d instanceof FuzzySet) {
				if(!((FuzzySet) d).isBLTS()) {
					return EVALUATIONS_IS_NOT_A_BLTS_DOMAIN;
				}
			} else {
				return EVALUATIONS_IS_NOT_A_LINGUISTIC_DOMAIN;
			}
		}
		return "";
	}

}

