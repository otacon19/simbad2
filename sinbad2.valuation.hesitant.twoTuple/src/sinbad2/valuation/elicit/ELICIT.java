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
import sinbad2.domain.linguistic.fuzzy.label.LabelSetLinguisticDomain;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.domain.linguistic.unbalanced.Unbalanced;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.elicit.nls.Messages;
import sinbad2.valuation.hesitant.EUnaryRelationType;
import sinbad2.valuation.hesitant.HesitantValuation;
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

		if (upperTerm.compareTo(lowerTerm) <= 0) {
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
		// Step 2: compute centroids
		List<Double> centroids = computeCentroids();

		// Step 3: compute distance between the centroids and beta
		Double a = _beta.getLimits()[0];
		List<Double> distances = computeMinimumDistanceToX(centroids, a);

		// Step 4: select the linguistic term
		LabelLinguisticDomain s_i = selectLinguisticTerm(distances);

		// Step 5: obtain the symbolic translation alpha
		LabelSetLinguisticDomain labelSet = ((FuzzySet) _domain).getLabelSet();
		int g = labelSet.getCardinality() - 1;
		double distanceBetweenTerms = 1d / (2 * g);
		double alpha = (0.5 * (a - centroids.get(labelSet.getPos(s_i)))) / distanceBetweenTerms;

		// Build partially ELICIT expression
		setUnaryRelation(EUnaryRelationType.AtLeast, new TwoTuple((FuzzySet) _domain, s_i, alpha));

		// Step 6: gamma computation
		TrapezoidalFunction fuzzyEnvelopeELICIT = calculateFuzzyEnvelope();
		setGamma1(fuzzyEnvelopeELICIT.getB() - _beta.getB());
	}

	private List<Double> computeMinimumDistanceToX(List<Double> centroids, Double point) {
		List<Double> distances = new LinkedList<Double>();

		for (Double centroid : centroids) {
			distances.add(Math.abs(point - centroid));
		}
		return distances;
	}

	private List<Double> computeCentroids() {
		List<Double> centroids = new LinkedList<Double>();

		for (LabelLinguisticDomain l : ((FuzzySet) _domain).getLabelSet().getLabels()) {
			centroids.add(((TrapezoidalFunction) l.getSemantic()).centroid());
		}
		return centroids;
	}

	private LabelLinguisticDomain selectLinguisticTerm(List<Double> distances) {
		Double min = Double.POSITIVE_INFINITY;

		int index = 0, selectedIndex = 0;
		for (Double distance : distances) {
			if (distance < min) {
				min = distance;
				selectedIndex = index;
			}
			index++;
		}
		return ((FuzzySet) _domain).getLabelSet().getLabel(selectedIndex);
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
			List<Double> weights = computeWeightsYager(cardinality, envelope, atMostCase);

			if (atMostCase == null) {
				return computeFuzzyEnvelopeBinaryRelation(cardinality, envelope, weights);
			} else {
				return computeFuzzyEnvelopeUnaryRelation(cardinality, envelope, weights, atMostCase);
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

	private List<Double> computeWeightsYager(int cardinality, int[] envelope, Boolean atMostCase) {
		YagerQuantifiers.NumeredQuantificationType nqt = YagerQuantifiers.NumeredQuantificationType.FilevYager;
		double[] auxWeights = YagerQuantifiers.QWeighted(nqt, cardinality - 1, envelope, atMostCase);

		List<Double> weights = new LinkedList<Double>();
		weights.add(new Double(-1));
		for (Double weight : auxWeights) {
			weights.add(weight);
		}

		return weights;
	}

	private TrapezoidalFunction computeFuzzyEnvelopeBinaryRelation(int cardinality, int[] envelope, List<Double> weights) {
		double a, b, c, d;

		a = _beta.getA();
		d = _beta.getD();

		if (envelope[0] + 1 == envelope[1]) {
			b = ((FuzzySet) getDomain()).getLabelSet().getLabel(envelope[0]).getSemantic().getCenter().getMin();
			c = ((FuzzySet) getDomain()).getLabelSet().getLabel(envelope[1]).getSemantic().getCenter().getMax();
		} else {
			int sum = envelope[1] + envelope[0], top;
			if (sum % 2 == 0) {
				top = sum / 2;
			} else {
				top = (sum - 1) / 2;
			}

			List<Valuation> valuations = new LinkedList<Valuation>();
			for (int i = envelope[0]; i <= top; i++) {
				if(i == envelope[0]) {
					valuations.add(_lowerTerm);
				} else if(i == top) {
					valuations.add(_upperTerm);
				} else {
					valuations.add(new TwoTuple((FuzzySet) _domain, ((FuzzySet) _domain).getLabelSet().getLabel(i)));
				}
			}

			AggregationOperatorsManager aggregationOperatorManager = AggregationOperatorsManager.getInstance();
			AggregationOperator owa = aggregationOperatorManager.getAggregationOperator(OWA.ID);
			Valuation aux = ((OWA) owa).aggregate(valuations, weights);

			b = ((TwoTuple) aux).calculateInverseDelta() / ((double) cardinality - 1);
			c = 2D * ((FuzzySet) _domain).getLabelSet().getLabel(top).getSemantic().getCenter().getMin() - b;
		}
		return new TrapezoidalFunction(a, b, c, d);
	}

	private TrapezoidalFunction computeFuzzyEnvelopeUnaryRelation(int cardinality, int[] envelope, List<Double> weights, Boolean atMostCase) {
		double a, b, c, d;

		List<Valuation> valuations = new LinkedList<Valuation>();
		for (int i = envelope[0]; i <= envelope[1]; i++) {
			if(atMostCase) {
				if(i == envelope[1]) {
					valuations.add(_term);
				} else {
					valuations.add(new TwoTuple((FuzzySet) _domain, ((FuzzySet) _domain).getLabelSet().getLabel(i)));
				}
			} else {
				if(i == envelope[0]) {
					valuations.add(_term);
				} else {
					valuations.add(new TwoTuple((FuzzySet) _domain, ((FuzzySet) _domain).getLabelSet().getLabel(i)));
				}
			}
		}

		AggregationOperatorsManager aggregationOperatorManager = AggregationOperatorsManager.getInstance();
		AggregationOperator owa = aggregationOperatorManager.getAggregationOperator(OWA.ID);
		Valuation aux = ((OWA) owa).aggregate(valuations, weights);
		
		if (atMostCase.booleanValue()) {
			a = 0.0D;
			b = 0.0D;
			c = ((TwoTuple) aux).calculateInverseDelta() / ((double) cardinality - 1);
			d = _beta.getD();
		} else {
			a = _beta.getA();
			b = ((TwoTuple) aux).calculateInverseDelta() / ((double) cardinality - 1);
			c = 1.0D;
			d = 1.0D;
		}
		return new TrapezoidalFunction(a, b, c, d);
	}

	private void computeELICITExpressionAtMostCase() {
		// Step 2: compute centroids
		List<Double> centroids = computeCentroids();

		// Step 3: compute distance between the centroids and beta
		Double d = _beta.getLimits()[3];
		List<Double> distances = computeMinimumDistanceToX(centroids, d);

		// Step 4: select the linguistic term
		LabelLinguisticDomain s_i = selectLinguisticTerm(distances);

		// Step 5: obtain the symbolic translation alpha
		LabelSetLinguisticDomain labelSet = ((FuzzySet) _domain).getLabelSet();
		int g = labelSet.getCardinality() - 1;
		double distanceBetweenTerms = 1d / (2 * g);
		double alpha = (0.5 * (d - centroids.get(labelSet.getPos(s_i)))) / distanceBetweenTerms;

		// Build partially ELICIT expression
		setUnaryRelation(EUnaryRelationType.AtMost, new TwoTuple((FuzzySet) _domain, s_i, alpha));

		// Step 6: gamma computation
		TrapezoidalFunction fuzzyEnvelopeELICIT = calculateFuzzyEnvelope();
		setGamma1(fuzzyEnvelopeELICIT.getC() - _beta.getC());
	}

	private void computeELICITExpressionBetweenCase() {
		// Step 2: compute centroids
		List<Double> centroids = computeCentroids();

		// Step 3: compute distance between the centroids and beta
		Double a = _beta.getLimits()[0];
		Double d = _beta.getLimits()[3];
		List<Double> distancesA = computeMinimumDistanceToX(centroids, a);
		List<Double> distancesD = computeMinimumDistanceToX(centroids, d);

		// Step 4: select the linguistic term
		LabelLinguisticDomain s_i = selectLinguisticTerm(distancesA);
		LabelLinguisticDomain s_j = selectLinguisticTerm(distancesD);

		// Step 5: obtain the symbolic translation alpha
		LabelSetLinguisticDomain labelSet = ((FuzzySet) _domain).getLabelSet();
		int g = labelSet.getCardinality() - 1;
		double distanceBetweenTerms = 1d / (2 * g);
		double alpha_1 = (0.5 * (a - centroids.get(labelSet.getPos(s_i)))) / distanceBetweenTerms;
		double alpha_2 = (0.5 * (d - centroids.get(labelSet.getPos(s_j)))) / distanceBetweenTerms;

		// Build partially ELICIT expression
		setBinaryRelation(new TwoTuple((FuzzySet) _domain, s_i, alpha_1), new TwoTuple((FuzzySet) _domain, s_j, alpha_2));

		// Step 6: gamma computation
		TrapezoidalFunction fuzzyEnvelopeELICIT = calculateFuzzyEnvelope();

		setGamma1(fuzzyEnvelopeELICIT.getB() - _beta.getB());
		setGamma2(fuzzyEnvelopeELICIT.getC() - _beta.getC());
	}

	public TrapezoidalFunction computeInverse() {
		Double a = null, b = null, c = null, d = null;
		
		if (this.isPrimary()) {
			TwoTuple primary = getTwoTupleLabel();
			return (TrapezoidalFunction) primary.getLabel().getSemantic();
		} else if (this.isUnary()) {
			if (_unaryRelation.equals(EUnaryRelationType.AtLeast)) {
				c = d = 1.0d;
				a = computeTrapezoidalValue(_term);
				b = computeUnknownTrapezoidalValueAtLeastCase();
			} else if (_unaryRelation.equals(EUnaryRelationType.AtMost)) {
				a = b = 0d;
				d = computeTrapezoidalValue(_term);
				c = computeUnknownTrapezoidalValueAtMostCase();
			}
		} else {
			a = computeTrapezoidalValue(_lowerTerm);
			d = computeTrapezoidalValue(_upperTerm);
			Double[] trapezoidalValues = computeUnknownTrapezoidalValuesBinaryRelation();
			b = trapezoidalValues[0];
			c = trapezoidalValues[1];
		}
		_beta = new TrapezoidalFunction(a, b, c, d);
		return _beta;
	}

	private double computeTrapezoidalValue(TwoTuple twoTuple) {
		int g =  ((FuzzySet) _domain).getLabelSet().getCardinality() - 1;
		double distanceBetweenTerms = 1d / (2d * g); 
		double distanceTranslation = (twoTuple.getAlpha() * distanceBetweenTerms) / 0.5d;
		return distanceTranslation + twoTuple.getLabel().getSemantic().centroid();
	}

	private Double computeUnknownTrapezoidalValueAtLeastCase() {
		return calculateFuzzyEnvelope().getB() - _gamma1;
	}

	private Double computeUnknownTrapezoidalValueAtMostCase() {
		return calculateFuzzyEnvelope().getC() - _gamma1;
	}

	private Double[] computeUnknownTrapezoidalValuesBinaryRelation() {
		TrapezoidalFunction fuzzyEnvelopeELICT = calculateFuzzyEnvelope();
		Double[] result = new Double[2];
		result[0] = fuzzyEnvelopeELICT.getB() - _gamma1;
		result[1] = fuzzyEnvelopeELICT.getC() - _gamma2;
		return result;
	}

	public TrapezoidalFunction calculateFuzzyEnvelopeEquivalentCLE(FuzzySet domain) {
		HesitantValuation hv = new HesitantValuation(domain);

		if (isPrimary()) {
			hv.setLabel(_label.getLabel());
		} else if (isUnary()) {
			hv.setUnaryRelation(_unaryRelation, _term.getLabel());
		} else {
			hv.setBinaryRelation(_lowerTerm.getLabel(), _upperTerm.getLabel());
		}

		_beta = hv.calculateFuzzyEnvelope(domain);

		return _beta;
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
				result[1] = new TwoTuple((FuzzySet) _domain, ((FuzzySet) _domain).getLabelSet().getLabel(cardinality - 1));
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

		if (isPrimary()) {
			return _label.changeFormatValuationToString();
		} else {
			if (isUnary()) {
				String aux = getUnaryRelation().getRelationType();
				aux = aux.toLowerCase();
				aux = aux.substring(0, 1).toUpperCase() + aux.substring(1);
				return aux + " " + getTwoTupleTerm().prettyFormat(); //$NON-NLS-1$
			} else {
				return Messages.HesitantTwoTupleValuation_Between + " " + getTwoTupleLowerTerm().prettyFormat() + " " //$NON-NLS-1$ //$NON-NLS-2$
						+ Messages.HesitantTwoTupleValuation_and + " " + getTwoTupleUpperTerm().prettyFormat();//$NON-NLS-1$ $NON-NLS-2$
																												// $NON-NLS-3$
			}
		}
	}
	
	@Override
	public void save(XMLStreamWriter writer) throws XMLStreamException {}

	@Override
	public void read(XMLRead reader) throws XMLStreamException {}

	@Override
	public Object clone() {
		Object result = null;

		result = super.clone();

		return result;
	}
}
