package sinbad2.valuation.hesitant.twoTuple;

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
import sinbad2.valuation.hesitant.EUnaryRelationType;
import sinbad2.valuation.hesitant.twoTuple.nls.Messages;
import sinbad2.valuation.twoTuple.TwoTuple;

public class HesitantTwoTupleValuation extends Valuation {

	public static final String ID = "flintstones.valuation.hesitant.twoTuple"; //$NON-NLS-1$

	private EUnaryRelationType _unaryRelation;
	
	private TwoTuple _term;
	private TwoTuple _upperTerm;
	private TwoTuple _lowerTerm;
	private TwoTuple _label;
	
	private double _gamma1;
	private double _gamma2;

	private TrapezoidalFunction _fuzzyNumber;

	private static class MyComparator implements Comparator<Object[]> {
		@Override
		public int compare(Object[] o1, Object[] o2) {
			return Double.compare((Double) o1[1], (Double) o2[1]);
		}
	}

	public HesitantTwoTupleValuation() {
		super();

		_unaryRelation = null;
		_term = null;
		_upperTerm = null;
		_lowerTerm = null;
		_label = null;
		_fuzzyNumber = null;
	}

	public HesitantTwoTupleValuation(FuzzySet domain) {
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
	
	public void setFuzzyNumber(TrapezoidalFunction fuzzyNumber) {
		_fuzzyNumber = fuzzyNumber;
	}

	public TrapezoidalFunction getFuzzyNumber() {
		return _fuzzyNumber;
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

	public void createRelation(TrapezoidalFunction fuzzyNumber) {
		setFuzzyNumber(fuzzyNumber);

		double a = _fuzzyNumber.getLimits()[0], b = _fuzzyNumber.getLimits()[1], c = _fuzzyNumber.getLimits()[2],
				d = _fuzzyNumber.getLimits()[3], centroid = 0, minDistanceA = Double.POSITIVE_INFINITY,
				minDistanceD = Double.POSITIVE_INFINITY, distance;

		LabelLinguisticDomain labelCloserToA = null, labelCloserToD = null;

		for (LabelLinguisticDomain l : ((FuzzySet) _domain).getLabelSet().getLabels()) {
			centroid = ((TrapezoidalFunction) l.getSemantic()).centroid();
			distance = a - centroid;
			if (Math.abs(distance) < Math.abs(minDistanceA)) {
				minDistanceA = distance;
				labelCloserToA = l;
			}

			distance = d - centroid;
			if (Math.abs(distance) < Math.abs(minDistanceD)) {
				minDistanceD = distance;
				labelCloserToD = l;
			}
		}

		if (c == d && c == 1) {
			computeTwoTupleTranslationAtLeastCase(labelCloserToA, minDistanceA);
		} else if (a == b && a == 0) {
			computeTwoTupleTranslationAtMostCase(labelCloserToD, minDistanceD);
		} else {
			computeTwoTupleTranslationBetweenCase(labelCloserToA, labelCloserToD, minDistanceA, minDistanceD);
		}
	}

	private void computeTwoTupleTranslationAtLeastCase(LabelLinguisticDomain labelCloserToA, double minDistanceA) {
		LabelSetLinguisticDomain labelSet = ((FuzzySet) _domain).getLabelSet();

		LabelLinguisticDomain labelCenter = labelSet.getLabel((labelSet.getCardinality() / 2) + 1);
		double labelCenterCentroid = labelCenter.getSemantic().centroid();

		LabelLinguisticDomain nextLabelCenter = labelSet.getLabel(labelSet.getPos(labelCenter) + 1);
		double nextLabelCenterCentroid = nextLabelCenter.getSemantic().centroid();

		double middlePoint = (labelCenterCentroid + nextLabelCenterCentroid) / 2d;
		double consecutiveLabelsDistance = Math.abs(middlePoint - labelCenterCentroid);

		double alpha = (minDistanceA * 0.5d) / consecutiveLabelsDistance;
		
		setGamma1(((_fuzzyNumber.getB() - _fuzzyNumber.getA()) * 0.5d) / consecutiveLabelsDistance);

		setUnaryRelation(EUnaryRelationType.AtLeast, new TwoTuple((FuzzySet) _domain, labelCloserToA, alpha));
	}

	private void computeTwoTupleTranslationAtMostCase(LabelLinguisticDomain labelCloserToD, double minDistanceD) {
		LabelSetLinguisticDomain labelSet = ((FuzzySet) _domain).getLabelSet();

		LabelLinguisticDomain labelCenter = labelSet.getLabel((labelSet.getCardinality() / 2));
		double labelCenterCentroid = labelCenter.getSemantic().centroid();

		LabelLinguisticDomain nextLabelCenter = labelSet.getLabel(labelSet.getPos(labelCenter) + 1);
		double nextLabelCenterCentroid = nextLabelCenter.getSemantic().centroid();

		double middlePoint = (labelCenterCentroid + nextLabelCenterCentroid) / 2d;
		double consecutiveLabelsDistance = Math.abs(middlePoint - labelCenterCentroid);

		double alpha = (minDistanceD * 0.5d) / consecutiveLabelsDistance;
		
		setGamma1(((_fuzzyNumber.getD() - _fuzzyNumber.getC()) * 0.5d) / consecutiveLabelsDistance);

		setUnaryRelation(EUnaryRelationType.AtMost, new TwoTuple((FuzzySet) _domain, labelCloserToD, alpha));
	}

	private void computeTwoTupleTranslationBetweenCase(LabelLinguisticDomain labelCloserToA, LabelLinguisticDomain labelCloserToD, double minDistanceA, double minDistanceD) {
		LabelSetLinguisticDomain labelSet = ((FuzzySet) _domain).getLabelSet();

		if (labelCloserToA.equals(labelSet.getLabel(0)) && minDistanceA < 0) {
			minDistanceA = 0;
		} else if (labelCloserToD.equals(labelSet.getLabel(labelSet.getCardinality() - 1)) && (minDistanceD > 0)) {
			minDistanceD = 0;
		}

		LabelLinguisticDomain labelCenter = labelSet.getLabel((labelSet.getCardinality() / 2));
		double labelCenterCentroid = labelCenter.getSemantic().centroid();

		LabelLinguisticDomain nextLabelCenter = labelSet.getLabel(labelSet.getPos(labelCenter) + 1);
		double nextLabelCenterCentroid = nextLabelCenter.getSemantic().centroid();

		double middlePoint = (labelCenterCentroid + nextLabelCenterCentroid) / 2d;
		double consecutiveLabelsDistance = Math.abs(middlePoint - labelCenterCentroid);

		double alpha1 = (minDistanceA * 0.5d) / consecutiveLabelsDistance;
		double alpha2 = (minDistanceD * 0.5d) / consecutiveLabelsDistance;
		
		setGamma1(((_fuzzyNumber.getB() - _fuzzyNumber.getA()) * 0.5d) / consecutiveLabelsDistance);
		setGamma2(((_fuzzyNumber.getD() - _fuzzyNumber.getC()) * 0.5d) / consecutiveLabelsDistance);

		setBinaryRelation(new TwoTuple((FuzzySet) _domain, labelCloserToA, alpha1), new TwoTuple((FuzzySet) _domain, labelCloserToD, alpha2));
	}

	public TrapezoidalFunction computeInverse() {
		Double a = null, b = null, c = null, d = null;
		if(this.isPrimary()) {
			TwoTuple primary = getTwoTupleLabel();
			return (TrapezoidalFunction) primary.getLabel().getSemantic();
		} else if(this.isUnary()) {
			TwoTuple unary = getTwoTupleTerm();
			if(_unaryRelation.equals(EUnaryRelationType.AtLeast)) {
				c = d = 1.0d;
				a = computeTrapezoidalValue(unary);
				b = computeUnknownTrapezoidalValueAtLeastCase(a, _gamma1);
			} else if(_unaryRelation.equals(EUnaryRelationType.AtMost)) {
				a = b = 0d;
				d = computeTrapezoidalValue(unary);
				c = computeUnknownTrapezoidalValueAtMostCase(d, _gamma1);
			}
		} else {
			TwoTuple lower = getTwoTupleLowerTerm();
			TwoTuple upper = getTwoTupleUpperTerm();
			a = computeTrapezoidalValue(lower);
			d = computeTrapezoidalValue(upper);
			Double[] trapezoidalValues = computeUnknownTrapezoidalValuesBinaryRelation(a, d);
			b = trapezoidalValues[0];
			c = trapezoidalValues[1];
		}
		_fuzzyNumber = new TrapezoidalFunction(a, b, c, d);
		return _fuzzyNumber;
	}

	private double computeTrapezoidalValue(TwoTuple twoTuple) {
		LabelSetLinguisticDomain labelSet = ((FuzzySet) _domain).getLabelSet();

		LabelLinguisticDomain labelCenter = labelSet.getLabel((labelSet.getCardinality() / 2));
		double labelCenterCentroid = labelCenter.getSemantic().centroid();

		LabelLinguisticDomain nextLabelCenter = labelSet.getLabel(labelSet.getPos(labelCenter) + 1);
		double nextLabelCenterCentroid = nextLabelCenter.getSemantic().centroid();

		double middlePoint = (labelCenterCentroid + nextLabelCenterCentroid) / 2d;
		double consecutiveLabelsDistance = Math.abs(middlePoint - labelCenterCentroid);

		double minDistanceToLabel = (twoTuple.getAlpha() * consecutiveLabelsDistance) / 0.5d;
		return minDistanceToLabel + twoTuple.getLabel().getSemantic().centroid();
	}
	
	private Double computeUnknownTrapezoidalValueAtLeastCase(Double a, Double gamma) {
		LabelSetLinguisticDomain labelSet = ((FuzzySet) _domain).getLabelSet();
		
		LabelLinguisticDomain labelCenter = labelSet.getLabel((labelSet.getCardinality() / 2));
		double labelCenterCentroid = labelCenter.getSemantic().centroid();

		LabelLinguisticDomain nextLabelCenter = labelSet.getLabel(labelSet.getPos(labelCenter) + 1);
		double nextLabelCenterCentroid = nextLabelCenter.getSemantic().centroid();

		double middlePoint = (labelCenterCentroid + nextLabelCenterCentroid) / 2d;
		double consecutiveLabelsDistance = Math.abs(middlePoint - labelCenterCentroid);
		
		double distance = (gamma * consecutiveLabelsDistance) / 0.5d;
		
		return a + distance;
	}
	
	private Double computeUnknownTrapezoidalValueAtMostCase(Double d, Double gamma) {
		LabelSetLinguisticDomain labelSet = ((FuzzySet) _domain).getLabelSet();
		
		LabelLinguisticDomain labelCenter = labelSet.getLabel((labelSet.getCardinality() / 2));
		double labelCenterCentroid = labelCenter.getSemantic().centroid();

		LabelLinguisticDomain nextLabelCenter = labelSet.getLabel(labelSet.getPos(labelCenter) + 1);
		double nextLabelCenterCentroid = nextLabelCenter.getSemantic().centroid();

		double middlePoint = (labelCenterCentroid + nextLabelCenterCentroid) / 2d;
		double consecutiveLabelsDistance = Math.abs(middlePoint - labelCenterCentroid);
		
		double distance = (gamma * consecutiveLabelsDistance) / 0.5d;
		
		return d - distance;
	}
	
	private Double[] computeUnknownTrapezoidalValuesBinaryRelation(Double a, Double d) {
		Double[] result = new Double[2];
		result[0] = computeUnknownTrapezoidalValueAtLeastCase(a, _gamma1);
		result[1] = computeUnknownTrapezoidalValueAtMostCase(d, _gamma2);
		return result;
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

	public TrapezoidalFunction calculateFuzzyEnvelope(FuzzySet domain) {
		double a, b, c, d;
		int g = domain.getLabelSet().getCardinality();
		Boolean lower = null;

		AggregationOperatorsManager aggregationOperatorManager = AggregationOperatorsManager.getInstance();
		AggregationOperator owa = aggregationOperatorManager.getAggregationOperator(OWA.ID);

		if (isPrimary()) {
			IMembershipFunction semantic = getTwoTupleLabel().getLabel().getSemantic();
			a = semantic.getCoverage().getMin();
			b = semantic.getCenter().getMin();
			c = semantic.getCenter().getMax();
			d = semantic.getCoverage().getMax();
		} else {
			int envelope[] = getEnvelopeLinguisticLabelsIndex();
			if (isUnary()) {
				switch (getUnaryRelation()) {
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
				a = ((FuzzySet) getDomain()).getLabelSet().getLabel(envelope[0]).getSemantic().getCoverage().getMin();
				d = ((FuzzySet) getDomain()).getLabelSet().getLabel(envelope[1]).getSemantic().getCoverage().getMax();
				if (envelope[0] + 1 == envelope[1]) {
					b = ((FuzzySet) getDomain()).getLabelSet().getLabel(envelope[0]).getSemantic().getCenter().getMin();
					c = ((FuzzySet) getDomain()).getLabelSet().getLabel(envelope[1]).getSemantic().getCenter().getMax();
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
					d = ((FuzzySet) getDomain()).getLabelSet().getLabel(envelope[1]).getSemantic().getCoverage()
							.getMax();
				} else {
					a = ((FuzzySet) getDomain()).getLabelSet().getLabel(envelope[0]).getSemantic().getCoverage()
							.getMin();
					b = ((TwoTuple) aux).calculateInverseDelta() / ((double) g - 1);
					c = 1.0D;
					d = 1.0D;
				}
			}
		}

		_fuzzyNumber = new TrapezoidalFunction(new double[] { a, b, c, d });

		return _fuzzyNumber;
	}

	public int[] getEnvelopeLinguisticLabelsIndex() {
		int[] result = null;
		LabelLinguisticDomain[] envelope = getEnvelopeLinguisticLabels();

		if (envelope != null) {
			result = new int[2];
			result[0] = ((FuzzySet) _domain).getLabelSet().getPos(envelope[0]);
			result[1] = ((FuzzySet) _domain).getLabelSet().getPos(envelope[1]);
		}

		return result;
	}

	public LabelLinguisticDomain[] getEnvelopeLinguisticLabels() {
		LabelLinguisticDomain[] result = new LabelLinguisticDomain[2];
		int pos, cardinality;

		if (isPrimary()) {
			result[0] = _label.getLabel();
			result[1] = _label.getLabel();
		} else if (isUnary()) {
			switch (_unaryRelation) {
			case LowerThan:
				pos = ((FuzzySet) _domain).getLabelSet().getPos(_term.getLabel()) - 1;
				if (pos == -1) {
					pos = 0;
				}
				result[0] = ((FuzzySet) _domain).getLabelSet().getLabel(0);
				result[1] = ((FuzzySet) _domain).getLabelSet().getLabel(pos);
				break;
			case GreaterThan:
				cardinality = ((FuzzySet) _domain).getLabelSet().getCardinality();
				pos = ((FuzzySet) _domain).getLabelSet().getPos(_term.getLabel()) + 1;
				result[0] = ((FuzzySet) _domain).getLabelSet().getLabel(pos);
				result[1] = ((FuzzySet) _domain).getLabelSet().getLabel(cardinality - 1);
				break;
			case AtLeast:
				cardinality = ((FuzzySet) _domain).getLabelSet().getCardinality();
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

	public TwoTuple[] getEnvelopeTwoTuple() {
		TwoTuple[] result = new TwoTuple[2];
		int pos, cardinality;

		if (isPrimary()) {
			result[0] = _label;
			result[1] = _label;
		} else if (isUnary()) {
			switch (_unaryRelation) {
			case LowerThan:
				pos = ((FuzzySet) _domain).getLabelSet().getPos(_term.getLabel()) - 1;
				if (pos == -1) {
					pos = 0;
				}
				result[0] = new TwoTuple((FuzzySet) _domain, ((FuzzySet) _domain).getLabelSet().getLabel(0));
				result[1] = new TwoTuple((FuzzySet) _domain, ((FuzzySet) _domain).getLabelSet().getLabel(pos));
				break;
			case GreaterThan:
				cardinality = ((FuzzySet) _domain).getLabelSet().getCardinality();
				pos = ((FuzzySet) _domain).getLabelSet().getPos(_term.getLabel()) + 1;
				result[0] = new TwoTuple((FuzzySet) _domain, ((FuzzySet) _domain).getLabelSet().getLabel(pos));
				result[1] = new TwoTuple((FuzzySet) _domain,
						((FuzzySet) _domain).getLabelSet().getLabel(cardinality - 1));
				break;
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
	public Valuation negateValuation() {
		return null;
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
	public String changeFormatValuationToString() {

		if (isPrimary()) {
			return _label.changeFormatValuationToString();
		} else {
			if (isUnary()) {
				String aux = getUnaryRelation().getRelationType();
				aux = aux.toLowerCase();
				aux = aux.substring(0, 1).toUpperCase() + aux.substring(1);
				return aux + " " + getTwoTupleTerm().prettyFormat() + " m\u00b3"; //$NON-NLS-1$
			} else {
				return Messages.HesitantTwoTupleValuation_Between + " " + getTwoTupleLowerTerm().prettyFormat() + " " //$NON-NLS-1$ //$NON-NLS-2$
						+ Messages.HesitantTwoTupleValuation_and + " " + getTwoTupleUpperTerm().prettyFormat();//$NON-NLS-1$ $NON-NLS-2$
																												// $NON-NLS-3$
			}
		}
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

		final HesitantTwoTupleValuation other = (HesitantTwoTupleValuation) obj;

		return new EqualsBuilder().append(_label, other._label).append(_domain, other._domain)
				.append(_unaryRelation, other._unaryRelation).append(_term, other._term)
				.append(_lowerTerm, other._lowerTerm).append(_upperTerm, other._upperTerm).isEquals();
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

	/**
	 * Huimin Zhang method The multiatribute group decision making method based
	 * on aggregation operators with interval-valued 2-tuple linguistic
	 * information.
	 */
	@Override
	public int compareTo(Valuation other) {
		Validator.notNull(other);
		Validator.notIllegalElementType(other, new String[] { HesitantTwoTupleValuation.class.toString() });

		if (_domain.equals(other.getDomain())) {
			TwoTuple[] interval1 = this.getEnvelopeTwoTuple();
			TwoTuple[] interval2 = ((HesitantTwoTupleValuation) other).getEnvelopeTwoTuple();

			int[] labelIndexesInterval1 = this.getEnvelopeLinguisticLabelsIndex();
			int[] labelIndexesInterval2 = ((HesitantTwoTupleValuation) other).getEnvelopeLinguisticLabelsIndex();
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

	/**
	 * H.Liu et al method Application of interval 2-tuple linguistic MULTIMOORA
	 * method for health-care waste treatment technology evaluation and
	 * selection
	 * 
	 * @param valuations
	 * @return
	 */
	public static List<Object[]> rankingMatrix(List<Object[]> valuations) {
		double matrix[][] = new double[valuations.size()][valuations.size()];
		double max, beta1, beta2, delta1, delta2, h1, h2, degree;
		TwoTuple[] interval1, interval2;

		for (int i = 0; i < valuations.size(); ++i) {
			interval1 = ((HesitantTwoTupleValuation) valuations.get(i)[0]).getEnvelopeTwoTuple();
			delta1 = interval1[1].calculateReverseDelta();
			beta1 = interval1[0].calculateReverseDelta();
			for (int j = 0; j < valuations.size(); ++j) {
				max = 0;
				if (i == j) {
					max = 0.5;
				} else {
					interval2 = ((HesitantTwoTupleValuation) valuations.get(j)[0]).getEnvelopeTwoTuple();
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
	 * S. Abbasbandy and T. Hajjari method A new approach for ranking of
	 * trapezoidal fuzzy numbers
	 * 
	 * @param valuations
	 * @return
	 */
	public static List<Object[]> rankingTrapezoidalFuzzyNumbers(List<Object[]> valuations) {
		List<Double> magnitudes = new LinkedList<Double>();
		for (Object[] valuation : valuations) {
			HesitantTwoTupleValuation v = ((HesitantTwoTupleValuation) valuation[0]);
			TrapezoidalFunction vtr = v.getFuzzyNumber();
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
}
