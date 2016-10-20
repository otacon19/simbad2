package sinbad2.phasemethod.todim.unification;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.AggregationOperatorsManager;
import sinbad2.aggregationoperator.owa.OWA;
import sinbad2.aggregationoperator.owa.YagerQuantifiers;
import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.element.alternative.Alternative;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.HesitantValuation;
import sinbad2.valuation.integer.IntegerValuation;
import sinbad2.valuation.integer.interval.IntegerIntervalValuation;
import sinbad2.valuation.linguistic.LinguisticValuation;
import sinbad2.valuation.real.RealValuation;
import sinbad2.valuation.real.interval.RealIntervalValuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class UnificationPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.todim.unification"; //$NON-NLS-1$

	private Domain _unifiedDomain;
	
	private Map<ValuationKey, TrapezoidalFunction> _fuzzyValuations;

	private ValuationSet _valutationSet;
	private DomainSet _domainSet;
	
	public UnificationPhase() {
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valutationSet = valuationSetManager.getActiveValuationSet();
		_domainSet = DomainsManager.getInstance().getActiveDomainSet();

		_fuzzyValuations = new HashMap<ValuationKey, TrapezoidalFunction>();
	}

	@Override
	public Map<ValuationKey, Valuation> getTwoTupleValuations() {
		return null;
	}
	
	public Map<ValuationKey, TrapezoidalFunction> getEnvelopeValuations() {
		return _fuzzyValuations;
	}

	public void setEnvelopeValuations(Map<ValuationKey, TrapezoidalFunction> envelopeValuations) {
		_fuzzyValuations = envelopeValuations;
	}
	
	public Map<ValuationKey, TrapezoidalFunction> getFuzzyValuations() {
		return _fuzzyValuations;
	}
	
	@Override
	public Domain getUnifiedDomain() {
		return _unifiedDomain;
	}

	public void setUnifiedDomain(Domain domain) {
		_unifiedDomain = domain;
	}
	
	@Override
	public IPhaseMethod copyStructure() {
		return new UnificationPhase();
	}

	@Override
	public void copyData(IPhaseMethod iPhaseMethod) {
		UnificationPhase unification = (UnificationPhase) iPhaseMethod;

		clear();

		_fuzzyValuations = unification.getEnvelopeValuations();
	}

	@Override
	public void clear() {
		_fuzzyValuations.clear();
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
	public void activate() {
	}

	@Override
	public boolean validate() {

		if (_valutationSet.getValuations().isEmpty()) {
			return false;
		}

		return true;
	}

	public Map<ValuationKey, TrapezoidalFunction> unification() {
		Map<ValuationKey, Valuation> numericalIntegerValuesToNormalized = new HashMap<ValuationKey, Valuation>();
		Map<ValuationKey, Valuation> numericalRealValuesToNormalized = new HashMap<ValuationKey, Valuation>();
		Map<ValuationKey, Valuation> intervalIntegerValuesToNormalized = new HashMap<ValuationKey, Valuation>();
		Map<ValuationKey, Valuation> intervalRealValuesToNormalized = new HashMap<ValuationKey, Valuation>();

		if (_unifiedDomain != null) {
			Alternative alternative;
			Valuation valuation;
			
			Map<ValuationKey, Valuation> valuations = _valutationSet.getValuations();
			for (ValuationKey vk : valuations.keySet()) {
				alternative = vk.getAlternative();			
				if(!alternative.getId().contains("null_")) {
					valuation = valuations.get(vk);
	
					if (valuation instanceof IntegerValuation) {
						if (((NumericIntegerDomain) valuation.getDomain()).getInRange()) {
							numericalIntegerValuesToNormalized.put(vk, valuation);
						}
					} else if (valuation instanceof RealValuation) {
						if (((NumericRealDomain) valuation.getDomain()).getInRange()) {
							numericalRealValuesToNormalized.put(vk, valuation);
						}
					} else if (valuation instanceof IntegerIntervalValuation) {
						if (((NumericIntegerDomain) valuation.getDomain()).getInRange()) {
							intervalIntegerValuesToNormalized.put(vk, valuation);
						}
					} else if (valuation instanceof RealIntervalValuation) {
						if (((NumericRealDomain) valuation.getDomain()).getInRange()) {
							intervalRealValuesToNormalized.put(vk, valuation);
						}
					} else if (valuation instanceof LinguisticValuation) {
						_fuzzyValuations.put(vk, (TrapezoidalFunction) ((LinguisticValuation) valuation).getLabel().getSemantic());
					} else if (valuation instanceof HesitantValuation) {
						calculateFuzzyEnvelope(vk, (HesitantValuation) valuation, (FuzzySet) _unifiedDomain);
					}
				}
			}
		}

		if (numericalIntegerValuesToNormalized.size() > 0) {
			createIntegerNumericalValuesFuzzyNumber(numericalIntegerValuesToNormalized);
		}
		
		if (numericalRealValuesToNormalized.size() > 0) {
			createRealNumericalValuesFuzzyNumber(numericalRealValuesToNormalized);
		}
		
		if(intervalIntegerValuesToNormalized.size() > 0) {
			createIntervalIntegerValuesFuzzyNumber(intervalIntegerValuesToNormalized);
		}
		
		if(intervalRealValuesToNormalized.size() > 0) {
			createIntervalRealValuesFuzzyNumber(intervalRealValuesToNormalized);
		}
		
		return _fuzzyValuations;
	}

	private void calculateFuzzyEnvelope(ValuationKey vk, HesitantValuation valuation, FuzzySet domain) {
		double a, b, c, d;
		int g = domain.getLabelSet().getCardinality();
		Boolean lower = null;

		AggregationOperatorsManager aggregationOperatorManager = AggregationOperatorsManager.getInstance();
		AggregationOperator owa = aggregationOperatorManager.getAggregationOperator(OWA.ID);

		if (valuation.isPrimary()) {
			IMembershipFunction semantic = valuation.getLabel().getSemantic();
			a = semantic.getCoverage().getMin();
			b = semantic.getCenter().getMin();
			c = semantic.getCenter().getMax();
			d = semantic.getCoverage().getMax();
		} else {
			int envelope[] = valuation.getEnvelopeIndex();
			if (valuation.isUnary()) {
				switch (valuation.getUnaryRelation()) {
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

			YagerQuantifiers.NumeredQuantificationType nqt = YagerQuantifiers.NumeredQuantificationType.FilevYager;
			List<Double> weights = new LinkedList<Double>();
			double[] auxWeights = YagerQuantifiers.QWeighted(nqt, g - 1, envelope, lower);

			weights.add(new Double(-1));
			for (Double weight : auxWeights) {
				weights.add(weight);
			}

			if (lower == null) {
				a = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[0]).getSemantic().getCoverage()
						.getMin();
				d = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[1]).getSemantic().getCoverage()
						.getMax();
				if (envelope[0] + 1 == envelope[1]) {
					b = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[0]).getSemantic().getCenter()
							.getMin();
					c = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[1]).getSemantic().getCenter()
							.getMax();
				} else {
					int sum = envelope[1] + envelope[0];
					int top;
					if (sum % 2 == 0) {
						top = sum / 2;
					} else {
						top = (sum - 1) / 2;
					}
					List<Valuation> valuations = new LinkedList<Valuation>();
					for (int i = envelope[0]; i <= top; i++) {
						valuations.add(new TwoTuple(domain, domain.getLabelSet().getLabel(i)));
					}

					Valuation aux = ((OWA) owa).aggregate(valuations, weights);
					b = ((TwoTuple) aux).calculateInverseDelta() / ((double) g - 1);
					c = 2D * domain.getLabelSet().getLabel(top).getSemantic().getCenter().getMin() - b;
				}
			} else {
				List<Valuation> valuations = new LinkedList<Valuation>();
				for (int i = envelope[0]; i <= envelope[1]; i++) {
					valuations.add(new TwoTuple(domain, domain.getLabelSet().getLabel(i)));
				}

				Valuation aux = ((OWA) owa).aggregate(valuations, weights);
				if (lower.booleanValue()) {
					a = 0.0D;
					b = 0.0D;
					c = ((TwoTuple) aux).calculateInverseDelta() / ((double) g - 1);
					d = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[1]).getSemantic()
							.getCoverage().getMax();
				} else {
					a = ((FuzzySet) valuation.getDomain()).getLabelSet().getLabel(envelope[0]).getSemantic()
							.getCoverage().getMin();
					b = ((TwoTuple) aux).calculateInverseDelta() / ((double) g - 1);
					c = 1.0D;
					d = 1.0D;
				}
			}
		}

		TrapezoidalFunction tmf = new TrapezoidalFunction(new double[] { a, b, c, d });
		_fuzzyValuations.put(vk, tmf);
	}
	
	private void createIntegerNumericalValuesFuzzyNumber(Map<ValuationKey, Valuation> numericalIntegerValuesToNormalized) {
		double normalizedValue = 0;
		
		Map<String, Double> maxValueForEachDomain = getIntegerValuationsMaxValue(numericalIntegerValuesToNormalized);
		
		for(Domain domain: _domainSet.getDomains()) {
			for(ValuationKey vk: numericalIntegerValuesToNormalized.keySet()) {
				IntegerValuation v = (IntegerValuation) numericalIntegerValuesToNormalized.get(vk);
				if(v.getDomain().getId().equals(domain.getId())) {
					normalizedValue = v.getValue() / maxValueForEachDomain.get(domain.getId());
					createNumericFuzzyNumber(vk, normalizedValue);
				}
			}
		}
	}
	
	private Map<String, Double> getIntegerValuationsMaxValue(Map<ValuationKey, Valuation> numericalIntegerValuesToNormalized) {
		Map<String, Double> maxValuesForEachDomains = new HashMap<String, Double>();
		double max = Double.MIN_VALUE;
		
		for(Domain domain: _domainSet.getDomains()) {
			for(ValuationKey vk: numericalIntegerValuesToNormalized.keySet()) {
				IntegerValuation v = (IntegerValuation) numericalIntegerValuesToNormalized.get(vk);
				if(v.getDomain().getId().equals(domain.getId())) {
					if(v.getValue() > max) {
						max = v.getValue();
					}
				}
			}
			maxValuesForEachDomains.put(domain.getId(), max);
		}
		return maxValuesForEachDomains;
	}
	
	private void createNumericFuzzyNumber(ValuationKey vk, double normalizedValue) {
		TrapezoidalFunction tpf = new TrapezoidalFunction(new double[]{normalizedValue, normalizedValue, normalizedValue, normalizedValue});
		_fuzzyValuations.put(vk, tpf);
	}
	
	private void createRealNumericalValuesFuzzyNumber(Map<ValuationKey, Valuation> numericalRealValuesToNormalized) {
		double normalizedValue = 0;
		
		Map<String, Double> maxValueForEachDomain = getRealValuationsMaxValue(numericalRealValuesToNormalized);
		
		for(Domain domain: _domainSet.getDomains()) {
			for(ValuationKey vk: numericalRealValuesToNormalized.keySet()) {
				RealValuation v = (RealValuation) numericalRealValuesToNormalized.get(vk);
				if(v.getDomain().getId().equals(domain.getId())) {
					normalizedValue = v.getValue() / maxValueForEachDomain.get(domain.getId());
					createNumericFuzzyNumber(vk, normalizedValue);
				}
			}
		}
	}
	
	private Map<String, Double> getRealValuationsMaxValue(Map<ValuationKey, Valuation> numericalRealValuesToNormalized) {
		Map<String, Double> maxValuesForEachDomains = new HashMap<String, Double>();
		double max = Double.MIN_VALUE;
		
		for(Domain domain: _domainSet.getDomains()) {
			for(ValuationKey vk: numericalRealValuesToNormalized.keySet()) {
				RealValuation v = (RealValuation) numericalRealValuesToNormalized.get(vk);
				if(v.getDomain().getId().equals(domain.getId())) {
					if(v.getValue() > max) {
						max = v.getValue();
					}
				}
			}
			maxValuesForEachDomains.put(domain.getId(), max);
		}
		return maxValuesForEachDomains;
	}
	
	private void createIntervalIntegerValuesFuzzyNumber(Map<ValuationKey, Valuation> numericalRealValuesToNormalized) {
	
		for(Domain domain: _domainSet.getDomains()) {
			for(ValuationKey vk: numericalRealValuesToNormalized.keySet()) {
				IntegerIntervalValuation v = (IntegerIntervalValuation) numericalRealValuesToNormalized.get(vk);
				if(v.getDomain().getId().equals(domain.getId())) {
					createIntervalIntegerFuzzyNumber(domain, vk, v);
				}
			}
		}
	}

	private void createIntervalIntegerFuzzyNumber(Domain domain, ValuationKey vk, IntegerIntervalValuation v) {	
		double lower = (v.getMin() - ((NumericIntegerDomain) domain).getMin()) / (((NumericIntegerDomain) domain).getMax() - ((NumericIntegerDomain) domain).getMin());
		double upper = (v.getMax() - ((NumericIntegerDomain) domain).getMin()) / (((NumericIntegerDomain) domain).getMax() - ((NumericIntegerDomain) domain).getMin());

		_fuzzyValuations.put(vk, new TrapezoidalFunction(new double[]{lower, lower, upper, upper}));
	}
	
	private void createIntervalRealValuesFuzzyNumber(Map<ValuationKey, Valuation> numericalRealValuesToNormalized) {
		
		for(Domain domain: _domainSet.getDomains()) {
			for(ValuationKey vk: numericalRealValuesToNormalized.keySet()) {
				RealIntervalValuation v = (RealIntervalValuation) numericalRealValuesToNormalized.get(vk);
				if(v.getDomain().getId().equals(domain.getId())) {
					createIntervalRealFuzzyNumber(domain, vk, v);
				}
			}
		}
	}

	private void createIntervalRealFuzzyNumber(Domain domain, ValuationKey vk, RealIntervalValuation v) {		
		double lower = (v.getMin() - ((NumericRealDomain) domain).getMin()) / (((NumericRealDomain) domain).getMax() - ((NumericRealDomain) domain).getMin());
		double upper = (v.getMax() - ((NumericRealDomain) domain).getMin()) / (((NumericRealDomain) domain).getMax() - ((NumericRealDomain) domain).getMin());
		
		_fuzzyValuations.put(vk, new TrapezoidalFunction(new double[]{lower, lower, upper, upper}));
	}
}
