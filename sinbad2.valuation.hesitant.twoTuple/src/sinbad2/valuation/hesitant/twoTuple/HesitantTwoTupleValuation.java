package sinbad2.valuation.hesitant.twoTuple;

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
	
	private TrapezoidalFunction _fuzzyNumber;
	
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
		Validator.notIllegalElementType(domain, new String[] { FuzzySet.class.toString(), Unbalanced.class.toString() });
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
			_label =  new TwoTuple((FuzzySet) _domain, label, 0);
			disableRelations();
		} else {
			throw new IllegalArgumentException(Messages.HesitantTwoTupleValuation_Hesitant_valuation_label_not_contained_in_domain);
		}
	}
	
	public void setTwoTupleLabel(TwoTuple hTwoTuple) {
		Validator.notNull(hTwoTuple);
		
		if (((FuzzySet) _domain).getLabelSet().containsLabel(hTwoTuple.getLabel())) {
			_label =  hTwoTuple;
			disableRelations();
		} else {
			throw new IllegalArgumentException(Messages.HesitantTwoTupleValuation_Hesitant_valuation_label_not_contained_in_domain);
		}
	}
	
	public TwoTuple getTwoTupleLabel() {
		return _label;
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
			throw new IllegalArgumentException(Messages.HesitantTwoTupleValuation_Hesitant_valuation_label_not_contained_in_domain);
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
			throw new IllegalArgumentException(Messages.HesitantTwoTupleValuation_Hesitant_valuation_lower_term_not_contained_in_domain);
		}

		if (!((FuzzySet) _domain).getLabelSet().containsLabel(upperTerm.getLabel())) {
			throw new IllegalArgumentException(Messages.HesitantTwoTupleValuation_Hesitant_valuation_upper_term_not_contained_in_domain);
		}

		if (upperTerm.compareTo(lowerTerm) <= 0) {
			throw new IllegalArgumentException(Messages.HesitantTwoTupleValuation_Hesitant_valuation_lower_term_is_bigger_than_upper_term);
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
			throw new IllegalArgumentException(Messages.HesitantTwoTupleValuation_Hesitant_valuation_lower_term_is_bigger_than_upper_term);
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
			throw new IllegalArgumentException(Messages.HesitantTwoTupleValuation_Hesitant_valuation_lower_term_is_bigger_than_upper_term);
		}

		_lowerTerm = new TwoTuple((FuzzySet) _domain, lowertTerm, 0);
		_upperTerm = new TwoTuple((FuzzySet) _domain, upperTerm, 0);

		disablePrimary();
		disableUnary();
	}
	
	public void createRelation(TrapezoidalFunction fuzzyNumber) {
		
		setFuzzyNumber(fuzzyNumber);
		
		double a = _fuzzyNumber.getLimits()[0], b = _fuzzyNumber.getLimits()[1], c = _fuzzyNumber.getLimits()[2], d = _fuzzyNumber.getLimits()[3], centroid, 
				minDistanceB = Double.POSITIVE_INFINITY, minDistanceC = Double.POSITIVE_INFINITY, distance;
		
		LabelLinguisticDomain labelCloserToB = null, labelCloserToC = null;
		
		for(LabelLinguisticDomain l: ((FuzzySet) _domain).getLabelSet().getLabels()) {
			centroid = ((TrapezoidalFunction) l.getSemantic()).centroid();
			distance = centroid - b;
			if(Math.abs(distance) < Math.abs(minDistanceB)) {
				minDistanceB = distance;
				labelCloserToB = l;
			}
			distance = centroid - c;
			if(Math.abs(distance) < Math.abs(minDistanceC)) {
				minDistanceC = distance;
				labelCloserToC = l;
			}
		}
		
		if(c == d && c == 1) {
			setUnaryRelation(EUnaryRelationType.AtLeast, new TwoTuple((FuzzySet) _domain, labelCloserToB, Math.round(minDistanceB * 100d) / 100d)); 
		} else if(a == b && a == 0) {
			setUnaryRelation(EUnaryRelationType.AtMost, new TwoTuple((FuzzySet) _domain, labelCloserToC, Math.round(minDistanceC * 100d) / 100d));
		} else if(b == c) {
			disableUnary();
			disableBinary();
			_label = new TwoTuple((FuzzySet) _domain, labelCloserToB, Math.round(minDistanceB * 100d) / 100d);
		} else {
			setBinaryRelation(new TwoTuple((FuzzySet) _domain, labelCloserToB, Math.round(minDistanceB * 100d) / 100d), 
				new TwoTuple((FuzzySet) _domain, labelCloserToC, Math.round(minDistanceC * 100d) / 100d));
		}
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

        if(isPrimary()) {
            IMembershipFunction semantic = getTwoTupleLabel().getLabel().getSemantic();
            a = semantic.getCoverage().getMin();
            b = semantic.getCenter().getMin();
            c = semantic.getCenter().getMax();
            d = semantic.getCoverage().getMax();
        } else {
            int envelope[] = getEnvelopeIndex();
            if(isUnary()) {
                switch(getUnaryRelation()) {
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
			for(Double weight : auxWeights) {
				weights.add(weight);
			}
            
            if(lower == null) {
                a = ((FuzzySet) getDomain()).getLabelSet().getLabel(envelope[0]).getSemantic().getCoverage().getMin();
                d = ((FuzzySet) getDomain()).getLabelSet().getLabel(envelope[1]).getSemantic().getCoverage().getMax();
                if(envelope[0] + 1 == envelope[1]) {
                    b = ((FuzzySet) getDomain()).getLabelSet().getLabel(envelope[0]).getSemantic().getCenter().getMin();
                    c = ((FuzzySet) getDomain()).getLabelSet().getLabel(envelope[1]).getSemantic().getCenter().getMax();
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
                    d = ((FuzzySet) getDomain()).getLabelSet().getLabel(envelope[1]).getSemantic().getCoverage().getMax();
                } else {
                    a = ((FuzzySet) getDomain()).getLabelSet().getLabel(envelope[0]).getSemantic().getCoverage().getMin();
                    b = ((TwoTuple) aux).calculateInverseDelta() / ((double) g - 1);
                    c = 1.0D;
                    d = 1.0D;
                }
            }
        }
   
        _fuzzyNumber = new TrapezoidalFunction(new double[] {a, b, c, d});
        
        return _fuzzyNumber;
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
		int pos, cardinality;

		if (isPrimary()) {
			result[0] = _label.getLabel();
			result[1] = _label.getLabel();
		} else if (isUnary()) {
			switch (_unaryRelation) {
			case LowerThan:
				pos = ((FuzzySet) _domain).getLabelSet().getPos(_term.getLabel()) - 1;
				if(pos == -1) {
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
	
	@Override
	public String toString() {
		if (isPrimary()) {
			return (_label + Messages.HesitantTwoTupleValuation_in + _domain.toString());
		} else if (isUnary()) {
			return (_unaryRelation.toString() + " " + _term + Messages.HesitantTwoTupleValuation_in + _domain.toString()); //$NON-NLS-1$
		} else if (isBinary()) {
			return (Messages.HesitantTwoTupleValuation_Between + _lowerTerm + Messages.HesitantTwoTupleValuation_and + _upperTerm + Messages.HesitantTwoTupleValuation_in + _domain.toString());
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
				return aux + " " + getTwoTupleTerm().prettyFormat(); //$NON-NLS-1$
			} else {
				return "Between" + " " + getTwoTupleLowerTerm().prettyFormat() + " " + Messages.HesitantTwoTupleValuation_and + " " + getTwoTupleUpperTerm().prettyFormat();  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-5$
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
	public void save(XMLStreamWriter writer) throws XMLStreamException {}

	@Override
	public void read(XMLRead reader) throws XMLStreamException {}

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

	@Override
	public int compareTo(Valuation other) {
		Validator.notNull(other);
		Validator.notIllegalElementType(other, new String[] { HesitantTwoTupleValuation.class.toString() });

		if (_domain.equals(other.getDomain())) {
			int[] tEnvelopeIndex = getEnvelopeIndex();
			int[] oEnvelopeIndex = ((HesitantTwoTupleValuation) other).getEnvelopeIndex();
			double tc = (double) (tEnvelopeIndex[1] + tEnvelopeIndex[0]) / 2d;
			double tw = (double) (tEnvelopeIndex[1] - tEnvelopeIndex[0]) / 2d;
			double oc = (double) (oEnvelopeIndex[1] + oEnvelopeIndex[0]) / 2d;
			double ow = (double) (oEnvelopeIndex[1] - oEnvelopeIndex[0]) / 2d;

			double acceptability;
			if ((tw + ow) == 0) {
				if (tc == oc) {
					return 0;
				} else if (tc > oc) {
					return 1;
				} else {
					return -1;
				}
			}

			acceptability = (tc - oc) / (tw + ow);
			double limit = 0.25;
			if ((acceptability <= limit) && (acceptability >= -limit)) {
				return 0;
			} else if (acceptability > limit) {
				return 1;
			} else {
				return -1;
			}
		} else {
			throw new IllegalArgumentException("Different domains");
		}
	}
}
