package sinbad2.valuation.elicit;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.AggregationOperatorsManager;
import sinbad2.aggregationoperator.owa.OWA;
import sinbad2.aggregationoperator.owa.YagerQuantifiers;
import sinbad2.core.validator.Validator;
import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.domain.linguistic.unbalanced.Unbalanced;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.elicit.nls.Messages;
import sinbad2.valuation.hesitant.EUnaryRelationType;
import sinbad2.valuation.twoTuple.TwoTuple;

public class ELICIT extends Valuation {

	public static final String ID = "flintstones.valuation.hesitant.twoTuple"; //$NON-NLS-1$

	private EUnaryRelationType _unaryRelation;

	private TwoTuple _term;
	private TwoTuple _upperTerm;
	private TwoTuple _lowerTerm;
	private TwoTuple _label;

	private double _gamma1;
	private double _gamma2;

	private TrapezoidalFunction _beta;

	private static class MyComparator implements Comparator<Object[]> {
		@Override
		public int compare(Object[] o1, Object[] o2) {
			return Double.compare((Double) o1[1], (Double) o2[1]);
		}
	}

	public ELICIT() {
		super();

		_unaryRelation = null;
		_term = null;
		_upperTerm = null;
		_lowerTerm = null;
		_label = null;
		_beta = null;
	}

	public ELICIT(FuzzySet domain) {
		this();
		setDomain(domain);
	}

	@Override
	public void setDomain(Domain domain) {
		Validator.notNull(domain);
		Validator.notIllegalElementType(domain,
				new String[] { FuzzySet.class.toString(), Unbalanced.class.toString() });
		Validator.notEmpty(((FuzzySet) domain).getLabelSet().getLabels().toArray());

		_domain = (FuzzySet) domain;
	}

	public void setTwoTupleLabel(int pos) {
		LabelLinguisticDomain newLabel = ((FuzzySet) _domain).getLabelSet().getLabel(pos);
		Validator.notNull(newLabel);

		_label = new TwoTuple((FuzzySet) _domain, newLabel, 0);
		disableRelations();
	}

	public void setTwoTupleLabel(String name) {
		LabelLinguisticDomain newLabel = ((FuzzySet) _domain).getLabelSet().getLabel(name);
		Validator.notNull(newLabel);

		_label = new TwoTuple((FuzzySet) _domain, newLabel, 0);
		disableRelations();
	}

	public void setTwoTupleLabel(LabelLinguisticDomain label) {
		Validator.notNull(label);

		if (((FuzzySet) _domain).getLabelSet().containsLabel(label)) {
			_label = new TwoTuple((FuzzySet) _domain, label, 0);
			disableRelations();
		} else {
			throw new IllegalArgumentException(
					Messages.HesitantTwoTupleValuation_Hesitant_valuation_label_not_contained_in_domain);
		}
	}

	public void setTwoTupleLabel(TwoTuple hTwoTuple) {
		Validator.notNull(hTwoTuple);

		if (((FuzzySet) _domain).getLabelSet().containsLabel(hTwoTuple.getLabel())) {
			_label = hTwoTuple;
			disableRelations();
		} else {
			throw new IllegalArgumentException(
					Messages.HesitantTwoTupleValuation_Hesitant_valuation_label_not_contained_in_domain);
		}
	}

	public TwoTuple getTwoTupleLabel() {
		return _label;
	}

	public void setTwoTupleTerm(TwoTuple term) {
		_term = term;
	}

	public TwoTuple getTwoTupleTerm() {
		return _term;
	}

	public TwoTuple getTwoTupleLowerTerm() {
		return _lowerTerm;
	}

	public TwoTuple getTwoTupleUpperTerm() {
		return _upperTerm;
	}

	public void setGamma1(double gamma1) {
		this._gamma1 = gamma1;
	}

	public double getGamma1() {
		return this._gamma1;
	}

	public void setGamma2(double gamma2) {
		this._gamma2 = gamma2;
	}

	public double getGamma2() {
		return this._gamma2;
	}

	public void setBeta(TrapezoidalFunction fuzzyNumber) {
		_beta = fuzzyNumber;
	}

	public TrapezoidalFunction getBeta() {
		return _beta;
	}

	public void setUnaryRelation(EUnaryRelationType unaryRelation, TwoTuple term) {
		Validator.notNull(unaryRelation);
		Validator.notNull(term);

		if (!((FuzzySet) _domain).getLabelSet().containsLabel(term.getLabel())) {
			throw new IllegalArgumentException(
					Messages.HesitantTwoTupleValuation_Hesitant_valuation_label_not_contained_in_domain);
		}

		_unaryRelation = unaryRelation;
		_term = term;

		disablePrimary();
		disableBinary();
	}

	public EUnaryRelationType getUnaryRelation() {
		return _unaryRelation;
	}

	public void setBinaryRelation(TwoTuple lowerTerm, TwoTuple upperTerm) {
		Validator.notNull(lowerTerm);
		Validator.notNull(upperTerm);

		if (!((FuzzySet) _domain).getLabelSet().containsLabel(lowerTerm.getLabel())) {
			throw new IllegalArgumentException(
					Messages.HesitantTwoTupleValuation_Hesitant_valuation_lower_term_not_contained_in_domain);
		}

		if (!((FuzzySet) _domain).getLabelSet().containsLabel(upperTerm.getLabel())) {
			throw new IllegalArgumentException(
					Messages.HesitantTwoTupleValuation_Hesitant_valuation_upper_term_not_contained_in_domain);
		}

		if (upperTerm.compareTo(lowerTerm) < 0) {
			throw new IllegalArgumentException(
					Messages.HesitantTwoTupleValuation_Hesitant_valuation_lower_term_is_bigger_than_upper_term);
		}

		_lowerTerm = lowerTerm;
		_upperTerm = upperTerm;

		disablePrimary();
		disableUnary();
	}

	public void setBinaryRelation(int lowerTermPos, int upperTermPos) {
		LabelLinguisticDomain lowerTerm = ((FuzzySet) _domain).getLabelSet().getLabel(lowerTermPos);
		Validator.notNull(lowerTerm);
		LabelLinguisticDomain upperTerm = ((FuzzySet) _domain).getLabelSet().getLabel(upperTermPos);
		Validator.notNull(upperTerm);

		if (upperTermPos <= lowerTermPos) {
			throw new IllegalArgumentException(
					Messages.HesitantTwoTupleValuation_Hesitant_valuation_lower_term_is_bigger_than_upper_term);
		}

		_lowerTerm = new TwoTuple((FuzzySet) _domain, lowerTerm, 0);
		_upperTerm = new TwoTuple((FuzzySet) _domain, upperTerm, 0);

		disablePrimary();
		disableUnary();
	}

	public void setBinaryRelation(String lowerTermName, String upperTermName) {
		LabelLinguisticDomain lowertTerm = ((FuzzySet) _domain).getLabelSet().getLabel(lowerTermName);
		Validator.notNull(lowertTerm);
		LabelLinguisticDomain upperTerm = ((FuzzySet) _domain).getLabelSet().getLabel(upperTermName);
		Validator.notNull(upperTerm);

		if (upperTermName.compareTo(lowerTermName) <= 0) {
			throw new IllegalArgumentException(
					Messages.HesitantTwoTupleValuation_Hesitant_valuation_lower_term_is_bigger_than_upper_term);
		}

		_lowerTerm = new TwoTuple((FuzzySet) _domain, lowertTerm, 0);
		_upperTerm = new TwoTuple((FuzzySet) _domain, upperTerm, 0);

		disablePrimary();
		disableUnary();
	}

	public boolean isPrimary() {
		return (_label != null);
	}

	public boolean isComposite() {
		return (isUnary() || isBinary());
	}

	public boolean isUnary() {
		return ((_unaryRelation != null) && (_term != null));
	}

	public boolean isBinary() {
		return ((_lowerTerm != null) && (_upperTerm != null));
	}

	private void disablePrimary() {
		_label = null;
	}

	private void disableRelations() {
		disableUnary();
		disableBinary();
	}

	private void disableUnary() {
		_unaryRelation = null;
		_term = null;
	}

	private void disableBinary() {
		_lowerTerm = null;
		_upperTerm = null;
	}

	@Override
	public FuzzySet unification(Domain fuzzySet) {
		return null;
	}

	public void createRelation(TrapezoidalFunction beta) {
		
		//beta = new TrapezoidalFunction(0, 0, 0.235, 0.475);
		
		setBeta(beta);
		
		double a = _beta.getLimits()[0], b = _beta.getLimits()[1], c = _beta.getLimits()[2], d = _beta.getLimits()[3];

		// Step 1: Identify relation
		if (c == d && c == 1) {
			computeELICITExpressionAtLeastCase();
		} else if (a == b && a == 0) {
			computeELICITExpressionAtMostCase();
		} else {
			computeELICITExpressionBetweenCase();
		}
	}

	private void computeELICITExpressionAtLeastCase() {
		// Step 1: Compute 2-tuple term from point b of beta to obtain the fuzzy envelope
		setUnaryRelation(EUnaryRelationType.AtLeast, compute2TupleTerm(_beta.getB()));
				
		// Step 2: Compute unknown b with alpha equal to 0
		TrapezoidalFunction fuzzyEnvelope = calculateFuzzyEnvelope();
		
		// Step 3: Compute 2-tuple term
		setUnaryRelation(EUnaryRelationType.AtLeast, compute2TupleTerm(fuzzyEnvelope.getB()));
		
		// Step 4: Compute gamma
		setGamma1(_beta.getA() - fuzzyEnvelope.getA());
	}
	
	private TwoTuple compute2TupleTerm(Double point) {
		Double distance, closestDistance = Double.MAX_VALUE;
		LabelLinguisticDomain selectedTerm = null;

		for (LabelLinguisticDomain label : ((FuzzySet) _domain).getLabelSet().getLabels()) {
			distance = point - ((TrapezoidalFunction) label.getSemantic()).getB();
			if (Math.abs(distance) <= Math.abs(closestDistance)) {
				closestDistance = distance;
				selectedTerm = label;
			}
		}

		Double alpha = Math.round(closestDistance * (((FuzzySet) _domain).getLabelSet().getCardinality() - 1) * 100d) / 100d;

		return new TwoTuple((FuzzySet) _domain, selectedTerm, alpha);
	}
	
	private void computeELICITExpressionAtMostCase() {
		// Step 1: Compute 2-tuple term from point b of beta to obtain the fuzzy envelope
		setUnaryRelation(EUnaryRelationType.AtMost, compute2TupleTerm(_beta.getC()));
						
		// Step 2: Compute unknown b with alpha equal to 1
		TrapezoidalFunction fuzzyEnvelope = calculateFuzzyEnvelope();
				
		// Step 3: Compute 2-tuple term
		setUnaryRelation(EUnaryRelationType.AtMost, compute2TupleTerm(fuzzyEnvelope.getC()));

		// Step 4: Compute gamma
		setGamma1(_beta.getD() - fuzzyEnvelope.getD());
	}

	private void computeELICITExpressionBetweenCase() {
		// Step 1: Compute 2-tuple term from point b and c of beta to obtain the fuzzy envelope
		System.out.println(_beta.getB());
		System.out.println(_beta.getC());
		setBinaryRelation(compute2TupleTerm(_beta.getB()), compute2TupleTerm(_beta.getC()));
		
		// Step 2: Compute unknown b and c with alpha equal to 0 and 1 respectively
		TrapezoidalFunction fuzzyEnvelope = calculateFuzzyEnvelope();
		
		// Step 3: Compute 2-tuple terms
		setBinaryRelation(compute2TupleTerm(fuzzyEnvelope.getB()), compute2TupleTerm(fuzzyEnvelope.getC()));

		// Step 4: Compute gammas
		setGamma1(_beta.getA() - fuzzyEnvelope.getA());
		setGamma2(_beta.getD() - fuzzyEnvelope.getD());
	}
	
	private Double computeUnknownBBetweenCase() {
		AggregationOperatorsManager aggregationOperatorManager = AggregationOperatorsManager.getInstance();
		AggregationOperator owa = aggregationOperatorManager.getAggregationOperator(OWA.ID);
		TwoTuple aux = (TwoTuple) ((OWA) owa).aggregate(computeValuationsBetweenCasePointB(getEnvelopeIndex()), computeOWAWeightsBetweenCase(true, 0d));
		
		return aux.getFuzzyNumber().getB();
	}
	
	private List<Valuation> computeValuationsBetweenCasePointB(int[] envelope) {
		List<Valuation> valuations = new LinkedList<Valuation>();
		
		int max;
		if((envelope[0] + envelope[1]) % 2 != 0) {
			max = (envelope[0] + envelope[1] - 1) / 2;
		} else {
			max = (envelope[0] + envelope[1]) / 2;
		}
		
		valuations.add(_lowerTerm);
		
		for(int i = envelope[0] + 1; i <= max ; i++) {
            valuations.add(new TwoTuple((FuzzySet) _domain, ((FuzzySet) _domain).getLabelSet().getLabel(i)));
        }
        return valuations;
	}
		
	private List<Double> computeOWAWeightsBetweenCase(Boolean pointB, Double alpha) {
		 YagerQuantifiers.NumeredQuantificationType nqt = YagerQuantifiers.NumeredQuantificationType.FilevYager;
		 double[] auxWeights = YagerQuantifiers.QWeigthedBinaryRelation(nqt, ((FuzzySet) _domain).getLabelSet().getCardinality() - 1, getEnvelopeIndex(), pointB, alpha);
		 
		 List<Double> weights = new LinkedList<Double>();
		 weights.add(new Double(-1));
		 for(Double w: auxWeights) {
			 weights.add(w);
		 }
		 
		 return weights;
	}
	
	private Double computeUnknownCBetweenCase() {
		AggregationOperatorsManager aggregationOperatorManager = AggregationOperatorsManager.getInstance();
		AggregationOperator owa = aggregationOperatorManager.getAggregationOperator(OWA.ID);
		TwoTuple aux = (TwoTuple) ((OWA) owa).aggregate(computeValuationsBetweenCasePointC(getEnvelopeIndex()), computeOWAWeightsBetweenCase(false, 1d));
		
		return aux.getFuzzyNumber().getB();
	}
	
	private List<Valuation> computeValuationsBetweenCasePointC(int[] envelope) {
		List<Valuation> valuations = new LinkedList<Valuation>();
		
		int max;
		if((envelope[0] + envelope[1]) % 2 != 0) {
			max = (envelope[0] + envelope[1] + 1) / 2;
		} else {
			max = (envelope[0] + envelope[1]) / 2;
		}
		
		valuations.add(_upperTerm);
		
		for(int i = envelope[1] - 1; i >= max ; i--) {
            valuations.add(new TwoTuple((FuzzySet) _domain, ((FuzzySet) _domain).getLabelSet().getLabel(i)));
        }
		
        return valuations;
	}
	
	public TrapezoidalFunction computeInverse() {
		Double a = null, b = null, c = null, d = null;

		if (this.isPrimary()) {
			TwoTuple primary = getTwoTupleLabel();
			return (TrapezoidalFunction) primary.getLabel().getSemantic();
		} else if (this.isUnary()) {
			if (_unaryRelation.equals(EUnaryRelationType.AtLeast)) {
				c = d = 1.0d;
				a = computeAPointAtLeastCase();
				b = computeBPoint(_term);
			} else if (_unaryRelation.equals(EUnaryRelationType.AtMost)) {
				a = b = 0d;
				d = computeDPointAtMostCase();
				c = computeCPoint(_term);
			}
		} else {
			Double[] trapezoidalValues = computeSupportBetweenCase();
			a = trapezoidalValues[0];
			b = trapezoidalValues[1];
			c = computeBPoint(_lowerTerm);
			d = computeCPoint(_upperTerm);

		}
		_beta = new TrapezoidalFunction(a, b, c, d);
		return _beta;
	}

	private Double computeAPointAtLeastCase() {
		return _term.getFuzzyNumber().getA() - _gamma1;
	}

	private double computeBPoint(TwoTuple term) {
		return term.getFuzzyNumber().getB();
	}

	private Double computeDPointAtMostCase() {
		return _term.getFuzzyNumber().getD() - _gamma1;
	}

	private double computeCPoint(TwoTuple term) {
		return term.getFuzzyNumber().getC();
	}

	private Double[] computeSupportBetweenCase() {
		Double[] result = new Double[2];
		result[0] = _lowerTerm.getFuzzyNumber().getA() - _gamma1;
		result[1] = _upperTerm.getFuzzyNumber().getA() - _gamma2;
		return result;
	}

	public TrapezoidalFunction calculateFuzzyEnvelope() {
		int cardinality = ((FuzzySet) _domain).getLabelSet().getCardinality();

		Boolean atMostCase = null;
		if (isPrimary()) {
			return computeFuzzyEnvelopePrimaryRelation();
		} else {

			if (isUnary()) {
				atMostCase = (getUnaryRelation() == EUnaryRelationType.AtMost) ? true : false;
			}

			int envelope[] = getEnvelopeIndex();

			if (atMostCase == null) { //Binary relation
				return computeFuzzyEnvelopeBinaryRelation(cardinality, envelope);
			} else { //Unary relation
				return computeFuzzyEnvelopeUnaryRelation(cardinality, envelope, atMostCase);
			}
		}
	}

	private TrapezoidalFunction computeFuzzyEnvelopePrimaryRelation() {
		IMembershipFunction semantic = _label.getLabel().getSemantic();
		return new TrapezoidalFunction(semantic.getCoverage().getMin(), semantic.getCenter().getMin(),
				semantic.getCenter().getMax(), semantic.getCoverage().getMax());
	}

	public int[] getEnvelopeIndex() {
		int[] result = null;
		LabelLinguisticDomain[] envelope = getEnvelope();

		if (envelope != null) {
			result = new int[2];
			result[0] = ((FuzzySet) _domain).getLabelSet().getPos(envelope[0]);
			result[1] = ((FuzzySet) _domain).getLabelSet().getPos(envelope[1]);
		}

		return result;
	}

	public LabelLinguisticDomain[] getEnvelope() {
		LabelLinguisticDomain[] result = new LabelLinguisticDomain[2];

		if (isPrimary()) {
			result[0] = _label.getLabel();
			result[1] = _label.getLabel();
		} else if (isUnary()) {
			switch (_unaryRelation) {
			case AtLeast:
				int cardinality = ((FuzzySet) _domain).getLabelSet().getCardinality();
				result[0] = _term.getLabel();
				result[1] = ((FuzzySet) _domain).getLabelSet().getLabel(cardinality - 1);
				break;
			case AtMost:
				result[0] = ((FuzzySet) _domain).getLabelSet().getLabel(0);
				result[1] = _term.getLabel();
				break;
			default:
				result = null;
			}
		} else if (isBinary()) {
			result[0] = _lowerTerm.getLabel();
			result[1] = _upperTerm.getLabel();
		} else {
			result = null;
		}

		return result;
	}
	
	private TrapezoidalFunction computeFuzzyEnvelopeBinaryRelation(int cardinality, int[] envelope) {
		double a, b, c, d;

		a = _lowerTerm.getLabel().getSemantic().getCoverage().getMin();
		d = _upperTerm.getLabel().getSemantic().getCoverage().getMax();
		
		if (envelope[0] + 1 == envelope[1]) {
			b = ((FuzzySet) getDomain()).getLabelSet().getLabel(envelope[0]).getSemantic().getCenter().getMin();
			c = ((FuzzySet) getDomain()).getLabelSet().getLabel(envelope[1]).getSemantic().getCenter().getMax();
		} else {
			b = computeUnknownBBetweenCase();
			c = computeUnknownCBetweenCase();
		}
		
		return new TrapezoidalFunction(a, b, c, d);
	}

	private TrapezoidalFunction computeFuzzyEnvelopeUnaryRelation(int cardinality, int[] envelope, Boolean atMostCase) {
		double a, b, c, d;

		if (atMostCase.booleanValue()) {
			a = 0.0D;
			b = 0.0D;
			c = computeUnknownC();
			d = ((FuzzySet) _domain).getLabelSet().getLabel(envelope[1]).getSemantic().getCoverage().getMax();
		} else {
			a = ((FuzzySet) _domain).getLabelSet().getLabel(envelope[0]).getSemantic().getCoverage().getMin(); 
			b = computeUnknownB();
			c = 1.0D;
			d = 1.0D;
		}
		
		return new TrapezoidalFunction(a, b, c, d);
	}
	
	private Double computeUnknownC() {
		AggregationOperatorsManager aggregationOperatorManager = AggregationOperatorsManager.getInstance();
		AggregationOperator owa = aggregationOperatorManager.getAggregationOperator(OWA.ID);
		
		TwoTuple aux = (TwoTuple) ((OWA) owa).aggregate(computeValuationsAtMostCase(getEnvelopeIndex()), computeOWAWeights(1d, true));
		
		return aux.getFuzzyNumber().getB();
	}
		
	private List<Valuation> computeValuationsAtMostCase(int[] envelope) {
		List<Valuation> valuations = new LinkedList<Valuation>();
		
        for(int i = envelope[0]; i <= envelope[1] - 1; i++) {
            valuations.add(new TwoTuple((FuzzySet) _domain, ((FuzzySet) _domain).getLabelSet().getLabel(i)));
        }
        
        valuations.add(_term);
        
        return valuations;
	}
	
	private List<Double> computeOWAWeights(Double alpha, Boolean lower) {
		 YagerQuantifiers.NumeredQuantificationType nqt = YagerQuantifiers.NumeredQuantificationType.FilevYager;
		 double[] auxWeights = YagerQuantifiers.QWeigthedUnaryRelation(nqt, ((FuzzySet) _domain).getLabelSet().getCardinality() - 1, getEnvelopeIndex(), lower, alpha);
		 
		 List<Double> weights = new LinkedList<Double>();
		 weights.add(new Double(-1));
		 for(Double w: auxWeights) {
			 weights.add(w);
		 }
		 
		 return weights;
	}
	
	private Double computeUnknownB() {
		AggregationOperatorsManager aggregationOperatorManager = AggregationOperatorsManager.getInstance();
		AggregationOperator owa = aggregationOperatorManager.getAggregationOperator(OWA.ID);
		
		TwoTuple aux = (TwoTuple) ((OWA) owa).aggregate(computeValuationsAtLeastCase(getEnvelopeIndex()), computeOWAWeights(0d, false));
		
		return aux.getFuzzyNumber().getB();
	}
	
	private List<Valuation> computeValuationsAtLeastCase(int[] envelope) {
		List<Valuation> valuations = new LinkedList<Valuation>();
		
		valuations.add(_term);
        for(int i = envelope[0] + 1; i <= envelope[1]; i++) {
            valuations.add(new TwoTuple((FuzzySet) _domain, ((FuzzySet) _domain).getLabelSet().getLabel(i)));
        }
        
        return valuations;
	}
	
	/**
	 * Huimin Zhang method The multiatribute group decision making method based on
	 * aggregation operators with interval-valued 2-tuple linguistic information.
	 */
	@Override
	public int compareTo(Valuation other) {
		Validator.notNull(other);
		Validator.notIllegalElementType(other, new String[] { ELICIT.class.toString() });

		if (_domain.equals(other.getDomain())) {
			TwoTuple[] interval1 = this.getEnvelopeELICITExpression();
			TwoTuple[] interval2 = ((ELICIT) other).getEnvelopeELICITExpression();

			int[] labelIndexesInterval1 = this.getEnvelopeLinguisticLabelsIndex();
			int[] labelIndexesInterval2 = ((ELICIT) other).getEnvelopeLinguisticLabelsIndex();
			int g = ((FuzzySet) _domain).getLabelSet().getCardinality() - 1;

			double Sinterval1 = ((labelIndexesInterval1[0] + labelIndexesInterval1[1]) / (2d * g))
					+ ((interval1[0].getAlpha() + interval1[1].getAlpha()) / 2d);
			double Sinterval2 = ((labelIndexesInterval2[0] + labelIndexesInterval2[1]) / (2d * g))
					+ ((interval2[0].getAlpha() + interval2[1].getAlpha()) / 2d);

			if (Sinterval1 > Sinterval2) {
				return 1;
			} else if (Sinterval1 < Sinterval2) {
				return -1;
			} else {
				double Hinterval1 = ((labelIndexesInterval1[1] - labelIndexesInterval1[0]) / g)
						+ interval1[1].getAlpha() - interval1[0].getAlpha();
				double Hinterval2 = ((labelIndexesInterval2[1] - labelIndexesInterval2[0]) / g)
						+ interval2[1].getAlpha() - interval2[0].getAlpha();
				if (Hinterval1 > Hinterval2) {
					return -1;
				} else if (Hinterval1 < Hinterval2) {
					return 1;
				} else {
					return 0;
				}
			}
		} else {
			throw new IllegalArgumentException(Messages.HesitantTwoTupleValuation_Different_domains);
		}
	}

	public TwoTuple[] getEnvelopeELICITExpression() {
		TwoTuple[] result = new TwoTuple[2];
		int cardinality;

		if (isPrimary()) {
			result[0] = _label;
			result[1] = _label;
		} else if (isUnary()) {
			switch (_unaryRelation) {
			case AtLeast:
				cardinality = ((FuzzySet) _domain).getLabelSet().getCardinality();
				result[0] = _term;
				result[1] = new TwoTuple((FuzzySet) _domain,
						((FuzzySet) _domain).getLabelSet().getLabel(cardinality - 1));
				break;
			case AtMost:
				result[0] = new TwoTuple((FuzzySet) _domain, ((FuzzySet) _domain).getLabelSet().getLabel(0));
				result[1] = _term;
				break;
			default:
				result = null;
			}
		} else if (isBinary()) {
			result[0] = _lowerTerm;
			result[1] = _upperTerm;
		} else {
			result = null;
		}

		return result;
	}

	public int[] getEnvelopeLinguisticLabelsIndex() {
		int[] result = null;
		LabelLinguisticDomain[] envelope = getEnvelope();

		if (envelope != null) {
			result = new int[2];
			result[0] = ((FuzzySet) _domain).getLabelSet().getPos(envelope[0]);
			result[1] = ((FuzzySet) _domain).getLabelSet().getPos(envelope[1]);
		}

		return result;
	}

	/**
	 * H.Liu et al method Application of interval 2-tuple linguistic MULTIMOORA
	 * method for health-care waste treatment technology evaluation and selection
	 * 
	 * @param valuations
	 * @return
	 */
	public static List<Object[]> rankingMatrix(List<Object[]> valuations) {
		double matrix[][] = new double[valuations.size()][valuations.size()];
		double max, beta1, beta2, delta1, delta2, h1, h2, degree;
		TwoTuple[] interval1, interval2;

		for (int i = 0; i < valuations.size(); ++i) {
			interval1 = ((ELICIT) valuations.get(i)[0]).getEnvelopeELICITExpression();
			delta1 = interval1[1].calculateReverseDelta();
			beta1 = interval1[0].calculateReverseDelta();
			for (int j = 0; j < valuations.size(); ++j) {
				max = 0;
				if (i == j) {
					max = 0.5;
				} else {
					interval2 = ((ELICIT) valuations.get(j)[0]).getEnvelopeELICITExpression();
					delta2 = interval2[1].calculateReverseDelta();
					beta2 = interval2[0].calculateReverseDelta();

					h1 = delta1 - beta1;
					h2 = delta2 - beta2;
					degree = (delta2 - beta1) / (h1 + h2);
					if (degree > 0) {
						max = degree;
					}

					if ((1d - degree) > 0) {
						max = 1d - degree;
					} else {
						max = 0;
					}
				}
				matrix[i][j] = max;
			}
		}

		List<Object[]> ranking = computeRanking(matrix);
		List<Object[]> orderedValuations = new LinkedList<Object[]>();

		for (int r = 0; r < ranking.size(); ++r) {
			orderedValuations.add(valuations.get((int) ranking.get(r)[0]));
		}

		return orderedValuations;
	}

	private static List<Object[]> computeRanking(double[][] matrix) {
		List<Object[]> sums = new LinkedList<Object[]>();
		double sum;
		Object[] data;

		for (int i = 0; i < matrix.length; ++i) {
			sum = 0;
			data = new Object[2];
			for (int j = 0; j < matrix[i].length; ++j) {
				sum += matrix[i][j];
			}
			data[0] = i;
			data[1] = sum;
			sums.add(data);
		}

		Collections.sort(sums, new MyComparator());
		Collections.reverse(sums);

		return sums;
	}

	/**
	 * S. Abbasbandy and T. Hajjari method A new approach for ranking of trapezoidal
	 * fuzzy numbers
	 * 
	 * @param valuations
	 * @return
	 */
	public static List<Object[]> rankingTrapezoidalFuzzyNumbers(List<Object[]> valuations) {
		List<Double> magnitudes = new LinkedList<Double>();
		for (Object[] valuation : valuations) {
			ELICIT v = ((ELICIT) valuation[0]);
			TrapezoidalFunction vtr = v.getBeta();
			magnitudes.add(computeMagnitude(vtr));
		}

		List<Double> magnitudesAux = new LinkedList<Double>();
		magnitudesAux.addAll(magnitudes);

		Collections.sort(magnitudesAux);
		Collections.reverse(magnitudesAux);

		return orderRanking(magnitudes, magnitudesAux, valuations);
	}

	private static Double computeMagnitude(TrapezoidalFunction vtr) {
		double alpha = distance(vtr.getA(), vtr.getB());
		double beta = distance(vtr.getC(), vtr.getD());
		return (1d / 2d) * (vtr.getB() + vtr.getC() - (alpha / 6) + (beta / 6));
	}

	private static double distance(double pointA, double pointB) {
		return Math.abs(pointA - pointB);
	}

	private static List<Object[]> orderRanking(List<Double> magnitudes, List<Double> magnitudesAux,
			List<Object[]> valuations) {
		List<Integer> rankingPos = computeRankingPos(magnitudes, magnitudesAux);
		List<Object[]> ranking = new LinkedList<>();
		for (int r = 0; r < rankingPos.size(); ++r) {
			ranking.add(valuations.get(rankingPos.get(r)));
		}
		return ranking;
	}

	private static List<Integer> computeRankingPos(List<Double> magnitudes, List<Double> magnitudesAux) {
		List<Integer> ranking = new LinkedList<>();
		for (int i = 0; i < magnitudes.size(); ++i) {
			ranking.add(magnitudes.indexOf(magnitudesAux.get(i)));
		}
		return ranking;
	}

	@Override
	public Valuation negateValuation() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (obj.getClass() != this.getClass()) {
			return false;
		}

		final ELICIT other = (ELICIT) obj;

		return new EqualsBuilder().append(_label, other._label).append(_domain, other._domain)
				.append(_unaryRelation, other._unaryRelation).append(_term, other._term)
				.append(_lowerTerm, other._lowerTerm).append(_upperTerm, other._upperTerm).isEquals();
	}

	@Override
	public int hashCode() {
		if (_unaryRelation != null) {
			return new HashCodeBuilder(17, 31).append(_label).append(_domain).append(_unaryRelation.toString())
					.append(_term).append(_lowerTerm).append(_upperTerm).toHashCode();
		} else {
			return new HashCodeBuilder(17, 31).append(_label).append(_domain).append("").append(_term) //$NON-NLS-1$
					.append(_lowerTerm).append(_upperTerm).toHashCode();
		}
	}

	@Override
	public String toString() {
		if (isPrimary()) {
			return (_label + Messages.HesitantTwoTupleValuation_in + _domain.toString());
		} else if (isUnary()) {
			return (_unaryRelation.toString() + " " + _term + Messages.HesitantTwoTupleValuation_in //$NON-NLS-1$
					+ _domain.toString());
		} else if (isBinary()) {
			return (Messages.HesitantTwoTupleValuation_Between + _lowerTerm + Messages.HesitantTwoTupleValuation_and
					+ _upperTerm + Messages.HesitantTwoTupleValuation_in + _domain.toString());
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	@Override
	public String changeFormatValuationToString() {
		StringBuilder sb = new StringBuilder();

		if (isPrimary()) {
			return sb.append(_label.changeFormatValuationToString()).toString();
		} else {
			if (isUnary()) {
				String aux = getUnaryRelation().getRelationType();
				aux = aux.toLowerCase();
				aux = aux.substring(0, 1).toUpperCase() + aux.substring(1);
				return sb.append(aux).append(' ') // $NON-NLS-1$
						.append(getTwoTupleTerm().prettyFormat()).append('^') // $NON-NLS-1$
						.append('(') // $NON-NLS-1$
						.append(Math.round(_gamma1 * 1000d) / 1000d).append(')').toString(); // $NON-NLS-1$

			} else {
				return sb.append(Messages.HesitantTwoTupleValuation_Between).append(' ') // $NON-NLS-1$
						.append(getTwoTupleLowerTerm().prettyFormat()).append('^') // $NON-NLS-1$
						.append('(') // $NON-NLS-1$
						.append(Math.round(_gamma1 * 1000d) / 1000d).append(')') // $NON-NLS-1$
						.append(' ').append(Messages.HesitantTwoTupleValuation_and).append(' ')
						.append(getTwoTupleUpperTerm().prettyFormat()).append('^') // $NON-NLS-1$
						.append('(') // $NON-NLS-1$
						.append(Math.round(_gamma2 * 1000d) / 1000d).append(')').toString(); // $NON-NLS-1$
			}
		}
	}

	@Override
	public void save(XMLStreamWriter writer) throws XMLStreamException {
	}

	@Override
	public void read(XMLRead reader) throws XMLStreamException {
	}

	@Override
	public Object clone() {
		Object result = null;

		result = super.clone();

		return result;
	}
}
