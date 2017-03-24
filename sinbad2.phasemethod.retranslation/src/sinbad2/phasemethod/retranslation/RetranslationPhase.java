package sinbad2.phasemethod.retranslation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.element.ProblemElement;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.EUnaryRelationType;
import sinbad2.valuation.hesitant.twoTuple.HesitantTwoTupleValuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class RetranslationPhase implements IPhaseMethod {
	
	public static final String ID = "flintstones.phasemethod.retranslation"; //$NON-NLS-1$

	private List<Object[]> _lhDomains;
	private boolean _isActivated;
	
	private ValuationSet _valuationSet;

	public RetranslationPhase() {
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = valuationSetManager.getActiveValuationSet();
		
		_lhDomains = new LinkedList<Object[]>();
		_isActivated = false;
	}
	
	public List<Object[]> getLHDomains() {
		return _lhDomains;
	}
	
	public void setLHDomains(List<Object[]> lhDomains) {
		_lhDomains = lhDomains;
	}
	
	@Override
	public Domain getUnifiedDomain() {
		return null;
	}
	
	@Override
	public void setUnifiedDomain(Domain domain) {
	}
	
	@Override
	public Map<ValuationKey, Valuation> getTwoTupleValuations() {
		return null;
	}
	
	public boolean isActivated() {
		return _isActivated;
	}
	
	public void isActivated(boolean state) {
		_isActivated = state;
	}
	
	public Map<ProblemElement, Valuation> transform(Map<ProblemElement, Valuation> problemResult, FuzzySet resultsDomain) {

		Map<ProblemElement, Valuation> results = null;

		if(resultsDomain != null) {
			results = new HashMap<ProblemElement, Valuation>();

			Valuation valuation, auxValuation = null;
			for(ProblemElement alternative : problemResult.keySet()) {
				valuation = problemResult.get(alternative);
				if(valuation instanceof UnifiedValuation) {
					valuation = ((UnifiedValuation) valuation).disunification((FuzzySet) ((UnifiedValuation) valuation).getDomain());
				} else if(valuation instanceof HesitantTwoTupleValuation) {
					if(((HesitantTwoTupleValuation) valuation).isPrimary()) {
						TwoTuple label = ((HesitantTwoTupleValuation) valuation).getTwoTupleLabel();
						TwoTuple transformedLabel = label.transform(resultsDomain);
						auxValuation = new HesitantTwoTupleValuation(resultsDomain);
						((HesitantTwoTupleValuation) auxValuation).setTwoTupleLabel(transformedLabel);
						((HesitantTwoTupleValuation) auxValuation).setFuzzyNumber(((HesitantTwoTupleValuation) valuation).getFuzzyNumber());
					} else if(((HesitantTwoTupleValuation) valuation).isUnary()) {
						EUnaryRelationType relation = ((HesitantTwoTupleValuation) valuation).getUnaryRelation();
						TwoTuple label = ((HesitantTwoTupleValuation) valuation).getTwoTupleTerm();
						TwoTuple transformedLabel = label.transform(resultsDomain);
						auxValuation = new HesitantTwoTupleValuation(resultsDomain);
						((HesitantTwoTupleValuation) auxValuation).setUnaryRelation(relation, transformedLabel);
						((HesitantTwoTupleValuation) auxValuation).setFuzzyNumber(((HesitantTwoTupleValuation) valuation).getFuzzyNumber());
					} else {
						TwoTuple labelLower = ((HesitantTwoTupleValuation) valuation).getTwoTupleLowerTerm();
						TwoTuple labelUpper = ((HesitantTwoTupleValuation) valuation).getTwoTupleUpperTerm();
						TwoTuple transformedLabelLower = labelLower.transform(resultsDomain);
						TwoTuple transformedLabelUpper = labelUpper.transform(resultsDomain);
						auxValuation = new HesitantTwoTupleValuation(resultsDomain);
						((HesitantTwoTupleValuation) auxValuation).setBinaryRelation(transformedLabelLower, transformedLabelUpper);
						((HesitantTwoTupleValuation) auxValuation).setFuzzyNumber(((HesitantTwoTupleValuation) valuation).getFuzzyNumber());
					}
				} else {
					if(valuation != null) {
						auxValuation = ((TwoTuple) valuation).transform(resultsDomain);
					}
				}
				results.put(alternative, auxValuation);
			}
		}

		return results;
	}
	
	@Override
	public void copyData(IPhaseMethod iPhaseMethod) {
		RetranslationPhase retranslationPhase = (RetranslationPhase) iPhaseMethod;
		
		clear();
		
		_lhDomains = retranslationPhase.getLHDomains();
		_isActivated = retranslationPhase.isActivated();
	}
	
	@Override
	public void clear() {
		_lhDomains.clear();
		_isActivated = false;
	}

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
	public void activate() {
		_isActivated = true;
	}	

	@Override
	public boolean validate() {
		
		if(_valuationSet.getValuations().isEmpty()) {
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
