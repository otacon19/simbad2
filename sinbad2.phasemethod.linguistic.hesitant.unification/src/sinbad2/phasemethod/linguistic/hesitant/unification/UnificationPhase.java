package sinbad2.phasemethod.linguistic.hesitant.unification;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.AggregationOperatorsManager;
import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.aggregationoperator.owa.OWA;
import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.HesitantValuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;


public class UnificationPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.linguistic.hesitant.unification";
	
	private Map<ValuationKey, Valuation> _unifiedValuationsResult;
	private Map<ValuationKey, Valuation> _twoTupleValuationsResult;
	private Map<ValuationKey, TrapezoidalFunction> _envelopeValuations;
	
	private ValuationSet _valutationSet;
	private DomainSet _domainSet;
	
	public UnificationPhase() {
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valutationSet = valuationSetManager.getActiveValuationSet();
		
		DomainsManager domainsManager = DomainsManager.getInstance();
		_domainSet = domainsManager.getActiveDomainSet();
		
		_unifiedValuationsResult = new HashMap<ValuationKey, Valuation>();
		_twoTupleValuationsResult = new LinkedHashMap<ValuationKey, Valuation>();
		_envelopeValuations = new LinkedHashMap<ValuationKey, TrapezoidalFunction>();
	}
	
	public Map<ValuationKey, Valuation> getUnifiedValuationsResult() {
		return _unifiedValuationsResult;
	}
	
	public void setUnifiedValuationsResult(Map<ValuationKey, Valuation> unifiedValuationsResult) {
		_unifiedValuationsResult = unifiedValuationsResult;
	}

	
	public Map<ValuationKey, Valuation> getTwoTupleValuationsResult() {
		return _twoTupleValuationsResult;
	}
	
	public void setTwoTupleValuationsResult(Map<ValuationKey, Valuation> twoTupleValuationsResult) {
		_twoTupleValuationsResult = twoTupleValuationsResult;
	}
	
	public Map<ValuationKey, TrapezoidalFunction> getEnvelopeValuations() {
		return _envelopeValuations;
	}
	
	public void setEnvelopeValuations(Map<ValuationKey, TrapezoidalFunction> envelopeValuations) {
		_envelopeValuations = envelopeValuations;
	}

	@Override
	public IPhaseMethod copyStructure() {
		return new UnificationPhase();
	}

	@Override
	public void copyData(IPhaseMethod iPhaseMethod) {
		UnificationPhase unification = (UnificationPhase) iPhaseMethod;

		clear();

		_unifiedValuationsResult = unification.getUnifiedValuationsResult();
		_twoTupleValuationsResult = unification.getTwoTupleValuationsResult();
		_envelopeValuations = unification.getEnvelopeValuations();
	}

	@Override
	public void clear() {
		_unifiedValuationsResult.clear();
		_twoTupleValuationsResult.clear();
		_envelopeValuations.clear();
	}

	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		if (event.getChange().equals(EPhaseMethodStateChange.ACTIVATED)) {
			activate();
		}
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
	public void activate() {}

	@Override
	public boolean validate() {

		if(_domainSet.getDomains().isEmpty()) {
			return false;
		}
		
		if (_valutationSet.getValuations().isEmpty()) {
			return false;
		}

		return true;
	}

	public Map<ValuationKey, Valuation> unification(FuzzySet unifiedDomain) {
		_unifiedValuationsResult = new HashMap<ValuationKey, Valuation>();
		
		if(unifiedDomain != null) {
			Valuation valuation;

			Map<ValuationKey, Valuation> valuations = _valutationSet.getValuations();
			for(ValuationKey vk : valuations.keySet()) {
				
				valuation = valuations.get(vk);
				
				if(valuation instanceof HesitantValuation) {
					unifiedToTwoTupleHesitant(vk, (HesitantValuation) valuation, unifiedDomain);
				}
			}
		}
		
		return _unifiedValuationsResult;
	}
	
	private void unifiedToTwoTupleHesitant(ValuationKey vk, HesitantValuation valuation, FuzzySet domain) {	
		double a, b, c, d;
		int g = domain.getLabelSet().getCardinality();
		Boolean lower = null;
		
		AggregationOperatorsManager aggregationOperatorManager = AggregationOperatorsManager.getInstance();
		AggregationOperator owa = aggregationOperatorManager.getAggregationOperator(OWA.ID);
		
        if(valuation.isPrimary()) {
            IMembershipFunction semantic = valuation.getLabel().getSemantic();
            a = semantic.getCoverage().getMin();
            b = semantic.getCenter().getMin();
            c = semantic.getCenter().getMax();
            d = semantic.getCoverage().getMax();
        } else {
            int envelope[] = valuation.getEnvelopeIndex();
            if(valuation.isUnary()) {
                switch(valuation.getUnaryRelation()) {
                case LowerThan:
                	lower = Boolean.valueOf(true);
                	break;
                case AtMost:
                	lower = Boolean.valueOf(true);
                	break;
                default:
                	lower = Boolean.valueOf(false);
                	break;
                }
            } else {
                lower = null;
            }

            WeightedAggregationOperator.NumeredQuantificationType nqt = WeightedAggregationOperator.NumeredQuantificationType.FilevYager;
            List<Double> weights = new LinkedList<Double>();
			double[] auxWeights = WeightedAggregationOperator.QWeighted(nqt, g - 1, envelope, lower);
			
			weights.add(new Double(-1));
			for(Double weight : auxWeights) {
				weights.add(weight);
			}
            
            if(lower == null) {
                a = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[0]).getSemantic().getCoverage().getMin();
                d = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[1]).getSemantic().getCoverage().getMax();
                if(envelope[0] + 1 == envelope[1]) {
                    b = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[0]).getSemantic().getCenter().getMin();
                    c = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[1]).getSemantic().getCenter().getMax();
                } else {
                    int sum = envelope[1] + envelope[0];
                    int top;
                    if(sum % 2 == 0) {
                        top = sum / 2;
                    } else {
                        top = (sum - 1) / 2;
                    }
                    List<Valuation> valuations = new LinkedList<Valuation>();
                    for(int i = envelope[0]; i <= top; i++) {
                        valuations.add(new TwoTuple(domain, domain.getLabelSet().getLabel(i)));
                    }

                    Valuation aux = ((OWA) owa).aggregate(valuations, weights);
                    b = ((TwoTuple) aux).calculateInverseDelta() / ((double) g - 1);
                    c = 2D * domain.getLabelSet().getLabel(top).getSemantic().getCenter().getMin() - b;
                }
            } else {
                List<Valuation> valuations = new LinkedList<Valuation>();
                for(int i = envelope[0]; i <= envelope[1]; i++) {
                    valuations.add(new TwoTuple(domain, domain.getLabelSet().getLabel(i)));
                }

                Valuation aux = ((OWA) owa).aggregate(valuations, weights);
                if(lower.booleanValue()) {
                    a = 0.0D;
                    b = 0.0D;
                    c = ((TwoTuple) aux).calculateInverseDelta() / ((double) g - 1);
                    d = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[1]).getSemantic().getCoverage().getMax();
                } else {
                    a = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[0]).getSemantic().getCoverage().getMin();
                    b = ((TwoTuple) aux).calculateInverseDelta() / ((double) g - 1);
                    c = 1.0D;
                    d = 1.0D;
                }
            }
        }
        
        System.out.println(valuation.changeFormatValuationToString());
        
        TrapezoidalFunction tmf = new TrapezoidalFunction(new double[] {a, b, c, d});
        _envelopeValuations.put(vk, tmf);
        
        transformToTwoTuple(vk, tmf, domain);
	}
	
	private void transformToTwoTuple(ValuationKey vk, TrapezoidalFunction tmf, Domain domain) {
		IMembershipFunction function;
		FuzzySet result;

		result = (FuzzySet) ((FuzzySet) domain).clone();
		int g = result.getLabelSet().getCardinality();

		for(int i = 0; i < g; i++) {
			System.out.println(result.getLabelSet().getLabel(i).getName());
			function = result.getLabelSet().getLabel(i).getSemantic();
			result.setValue(i, function.maxMin(tmf));
		}
		
		Valuation unifiedValuation = new UnifiedValuation(result);
		TwoTuple twoTuple = ((UnifiedValuation) unifiedValuation).disunification(result);
		
		_unifiedValuationsResult.put(vk, twoTuple);
	}

	public Domain getDomain() {
		return (FuzzySet) _domainSet.getDomains().get(0);
	}
}

