package sinbad2.phasemethod.retranslation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.element.ProblemElement;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;

public class RetranslationPhase implements IPhaseMethod {
	
	public static final String ID = "flintstones.phasemethod.retranslation";

	private List<Object[]> _lhDomains;
	
	private static RetranslationPhase _instance = null;
	
	public static RetranslationPhase getInstance() {
		if(_instance == null) {
			_instance = new RetranslationPhase();
		}
		return _instance;
	}

	public List<Object[]> getLHDomains() {
		return _lhDomains;
	}
	
	public void setLHDomains(List<Object[]> lhDomains) {
		_lhDomains = lhDomains;
	}
	
	public Map<ProblemElement, Valuation> transform(Map<ProblemElement, Valuation> problemResult, FuzzySet resultsDomain) {

		Map<ProblemElement, Valuation> results = null;

		if(resultsDomain != null) {
			results = new HashMap<ProblemElement, Valuation>();

			Valuation valuation;
			for(ProblemElement alternative : problemResult.keySet()) {
				valuation = problemResult.get(alternative);
				if(valuation instanceof UnifiedValuation) {
					valuation = ((UnifiedValuation) valuation).disunification((FuzzySet) ((UnifiedValuation) valuation).getDomain());
				} else {
					if(valuation != null) {
						valuation = ((TwoTuple) valuation).transform(resultsDomain);
					}
				}
				results.put(alternative, valuation);
			}
		}

		return results;
	}
	
	
	@Override
	public void clear() {}

	@Override
	public IPhaseMethod clone() {
		RetranslationPhase result = null;

		try {
			result = (RetranslationPhase) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return result;
	}
	@Override
	public IPhaseMethod copyStructure() {
		return new RetranslationPhase();
	}

	@Override
	public void copyData(IPhaseMethod iPhaseMethod) {}

	@Override
	public void activate() {}

	@Override
	public boolean validate() {
		return true;
	}
	
	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		if (event.getChange().equals(EPhaseMethodStateChange.ACTIVATED)) {
			activate();
		}
	}
}
