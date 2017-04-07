package sinbad2.phasemethod.emergency.aggregation;

import java.util.HashMap;
import java.util.Map;

import sinbad2.domain.Domain;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.real.interval.RealIntervalValuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class AggregationPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.emergency.aggregation"; //$NON-NLS-1$
	
	private ValuationSet _valuationsSet;
	private ProblemElementsSet _elementsSet;
	private Double[] _expertsWeights;
	private Map<Criterion, RealIntervalValuation> _grps;
	
	public AggregationPhase() {
		_valuationsSet = ValuationSetManager.getInstance().getActiveValuationSet();
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();
		
		_grps = new HashMap<Criterion, RealIntervalValuation>();
	}
	
	@Override
	public void setUnifiedDomain(Domain domain) {}

	@Override
	public Domain getUnifiedDomain() {
		return null;
	}

	@Override
	public Map<ValuationKey, Valuation> getTwoTupleValuations() {
		return null;
	}
	
	public void setExpertsWeights(Double[] expertsWeights) {
		_expertsWeights = expertsWeights;
	}
	
	public Double[] getExpertsWeights() {
		return _expertsWeights;
	}
	
	public void setGRPs(Map<Criterion, RealIntervalValuation> grps) {
		_grps = grps;
	}
	
	public Map<Criterion, RealIntervalValuation> getGRPs() {
		return _grps;
	}
	
	public Map<Criterion, RealIntervalValuation> aggregationProcess() {		
		_grps = new HashMap<Criterion, RealIntervalValuation>();
		double acumLower, acumUpper;
		int numExpert;
		
		Alternative rp = _elementsSet.getAlternative("RP");
		NumericRealDomain domain = null;
		
		for(Criterion c: _elementsSet.getAllCriteria()) {
			acumLower = 0;
			acumUpper = 0;
			numExpert = 0;
			if(!c.hasSubcriteria()) {
				for(Expert e: _elementsSet.getAllExperts()) {
					if(!e.getId().equals("predefined_effective_control")) {
						RealIntervalValuation v = (RealIntervalValuation) _valuationsSet.getValuation(e, rp, c);
						domain = (NumericRealDomain) v.getDomain();
						if(!e.hasChildren()) {
							acumLower += _expertsWeights[numExpert] * v.getMin();
							acumUpper += _expertsWeights[numExpert] * v.getMax();		
							numExpert++;
						}
					}
				}
			}
			
			RealIntervalValuation aggregatedRealIntervalValuation = new RealIntervalValuation(domain, Math.round(acumLower), Math.round(acumUpper));
			_grps.put(c, aggregatedRealIntervalValuation);
		}
		
		return _grps;
	}

	@Override
	public void clear() {
		_grps.clear();
		_expertsWeights = new Double[0];
	}

	@Override
	public IPhaseMethod clone() {
		AggregationPhase result = null;
		
		try {
			result = (AggregationPhase) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public IPhaseMethod copyStructure() {
		return new AggregationPhase();
	}

	@Override
	public void copyData(IPhaseMethod iPhaseMethod) {
		AggregationPhase unification = (AggregationPhase) iPhaseMethod;

		clear();

		_expertsWeights = unification.getExpertsWeights();
	}

	@Override
	public void activate() {}

	@Override
	public boolean validate() {
		
		if (_valuationsSet.getValuations().isEmpty()) {
			return false;
		}
		
		if(_elementsSet.getExperts().isEmpty()) {
			return false;
		}
		
		if(_elementsSet.getCriteria().isEmpty()) {
			return false;
		}
		
		if(_elementsSet.getAlternatives().isEmpty()) {
			return false;
		}


		return true;
	}
	
	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		if (event.getChange().equals(EPhaseMethodStateChange.ACTIVATED)) {
			activate();
		}
	}

}
