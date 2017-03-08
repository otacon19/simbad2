package sinbad2.valuation.hesitant;

import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

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
import sinbad2.valuation.hesitant.nls.Messages;
import sinbad2.valuation.twoTuple.TwoTuple;

public class HesitantValuation extends Valuation {

	public static final String ID = "flintstones.valuation.hesitant"; //$NON-NLS-1$

	private EUnaryRelationType _unaryRelation;
	private LabelLinguisticDomain _term;
	private LabelLinguisticDomain _upperTerm;
	private LabelLinguisticDomain _lowerTerm;
	private LabelLinguisticDomain _label;

	public HesitantValuation() {
		super();
		_unaryRelation = null;
		_term = null;
		_upperTerm = null;
		_lowerTerm = null;
		_label = null;
	}

	public HesitantValuation(FuzzySet domain) {
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

	public void setLabel(int pos) {
		LabelLinguisticDomain newLabel = ((FuzzySet) _domain).getLabelSet().getLabel(pos);
		Validator.notNull(newLabel);

		_label = newLabel;
		disableRelations();
	}

	public void setLabel(String name) {
		LabelLinguisticDomain newLabel = ((FuzzySet) _domain).getLabelSet().getLabel(name);
		Validator.notNull(newLabel);

		_label = newLabel;
		disableRelations();
	}

	public void setLabel(LabelLinguisticDomain label) {
		Validator.notNull(label);

		if (((FuzzySet) _domain).getLabelSet().containsLabel(label)) {
			_label = label;
			disableRelations();
		} else {
			throw new IllegalArgumentException(Messages.HesitantValuation_Label_not_contains_in_domain);
		}
	}

	public LabelLinguisticDomain getLabel() {
		return _label;
	}

	public void setUnaryRelation(EUnaryRelationType unaryRelation, LabelLinguisticDomain term) {
		Validator.notNull(unaryRelation);
		Validator.notNull(term);

		if (!((FuzzySet) _domain).getLabelSet().containsLabel(term)) {
			throw new IllegalArgumentException(Messages.HesitantValuation_Label_not_contains_in_domain);
		}

		_unaryRelation = unaryRelation;
		_term = term;

		disablePrimary();
		disableBinary();
	}

	public EUnaryRelationType getUnaryRelation() {
		return _unaryRelation;
	}

	public LabelLinguisticDomain getTerm() {
		return _term;
	}

	public void setBinaryRelation(LabelLinguisticDomain lowerTerm, LabelLinguisticDomain upperTerm) {
		Validator.notNull(lowerTerm);
		Validator.notNull(upperTerm);

		if (!((FuzzySet) _domain).getLabelSet().containsLabel(lowerTerm)) {
			throw new IllegalArgumentException(Messages.HesitantValuation_Lower_term_not_contains_in_domain);
		}

		if (!((FuzzySet) _domain).getLabelSet().containsLabel(upperTerm)) {
			throw new IllegalArgumentException(Messages.HesitantValuation_Upper_term_not_contains_in_domain);
		}

		if (upperTerm.compareTo(lowerTerm) <= 0) {
			throw new IllegalArgumentException(Messages.HesitantValuation_Upper_term_is_bigger_than_lower_term);
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
			throw new IllegalArgumentException(Messages.HesitantValuation_Upper_term_is_bigger_than_lower_term);
		}

		_lowerTerm = lowerTerm;
		_upperTerm = upperTerm;

		disablePrimary();
		disableUnary();
	}

	public void setBinaryRelation(String lowerTermName, String upperTermName) {
		LabelLinguisticDomain lowertTerm = ((FuzzySet) _domain).getLabelSet().getLabel(lowerTermName);
		Validator.notNull(lowertTerm);
		LabelLinguisticDomain upperTerm = ((FuzzySet) _domain).getLabelSet().getLabel(upperTermName);
		Validator.notNull(upperTerm);

		if (upperTermName.compareTo(lowerTermName) <= 0) {
			throw new IllegalArgumentException(Messages.HesitantValuation_Upper_term_is_bigger_than_lower_term);
		}

		_lowerTerm = lowertTerm;
		_upperTerm = upperTerm;

		disablePrimary();
		disableUnary();
	}

	public LabelLinguisticDomain getLowerTerm() {
		return _lowerTerm;
	}

	public LabelLinguisticDomain getUpperTerm() {
		return _upperTerm;
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
            IMembershipFunction semantic = getLabel().getSemantic();
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
            
            if(lower == null) { //Relación binaria
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
   
        return new TrapezoidalFunction(new double[] {a, b, c, d});
	}
	
	public LabelLinguisticDomain[] getEnvelope() {
		LabelLinguisticDomain[] result = new LabelLinguisticDomain[2];
		int pos, cardinality;

		if (isPrimary()) {
			result[0] = _label;
			result[1] = _label;
		} else if (isUnary()) {
			switch (_unaryRelation) {
			//TODO fallo si ponemos LowerThan y la primera etiqueta (se sale del rango)
			case LowerThan:
				pos = ((FuzzySet) _domain).getLabelSet().getPos(_term) - 1;
				if(pos == -1) {
					pos = 0;
				}
				result[0] = ((FuzzySet) _domain).getLabelSet().getLabel(0);
				result[1] = ((FuzzySet) _domain).getLabelSet().getLabel(pos);
				break;
			case GreaterThan:
				cardinality = ((FuzzySet) _domain).getLabelSet().getCardinality();
				pos = ((FuzzySet) _domain).getLabelSet().getPos(_term) + 1;
				result[0] = ((FuzzySet) _domain).getLabelSet().getLabel(pos);
				result[1] = ((FuzzySet) _domain).getLabelSet().getLabel(cardinality - 1);
				break;
			case AtLeast:
				cardinality = ((FuzzySet) _domain).getLabelSet().getCardinality();
				result[0] = _term;
				result[1] = ((FuzzySet) _domain).getLabelSet().getLabel(cardinality - 1);
				break;
			case AtMost:
				result[0] = ((FuzzySet) _domain).getLabelSet().getLabel(0);
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

	@Override
	public String toString() {
		if (isPrimary()) {
			return (_label + Messages.HesitantValuation_In + _domain.toString());
		} else if (isUnary()) {
			return (_unaryRelation.toString() + " " + _term + Messages.HesitantValuation_In + _domain.toString()); //$NON-NLS-1$
		} else if (isBinary()) {
			return (Messages.HesitantValuation_between + _lowerTerm + Messages.HesitantValuation_And + _upperTerm + Messages.HesitantValuation_In + _domain.toString());
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
	public int compareTo(Valuation other) {
		Validator.notNull(other);
		Validator.notIllegalElementType(other, new String[] { HesitantValuation.class.toString() });

		if (_domain.equals(other.getDomain())) {
			int[] tEnvelopeIndex = getEnvelopeIndex();
			int[] oEnvelopeIndex = ((HesitantValuation) other).getEnvelopeIndex();
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
			throw new IllegalArgumentException(Messages.HesitantValuation_Different_domains);
		}
	}

	@Override
	public String changeFormatValuationToString() {

		if (isPrimary()) {
			return _label.getName();
		} else {
			if (isUnary()) {
				String aux = getUnaryRelation().getRelationType();
				aux = aux.toLowerCase();
				aux = aux.substring(0, 1).toUpperCase() + aux.substring(1);
				return aux + " " + getTerm().getName(); //$NON-NLS-1$
			} else {
				return Messages.HesitantValuation_Between + " " + getLowerTerm().getName() + " " + Messages.HesitantValuation_and + " " + getUpperTerm().getName();  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-5$
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

		final HesitantValuation other = (HesitantValuation) obj;

		return new EqualsBuilder().append(_label, other._label).append(_domain, other._domain)
				.append(_unaryRelation, other._unaryRelation).append(_term, other._term)
				.append(_lowerTerm, other._lowerTerm).append(_upperTerm, other._upperTerm).isEquals();
	}

	@Override
	public void save(XMLStreamWriter writer) throws XMLStreamException {

		writer.writeStartElement("hesitant"); //$NON-NLS-1$

		if (_label != null) {
			writer.writeStartElement("labelv"); //$NON-NLS-1$
			writer.writeAttribute("label", _label.getName()); //$NON-NLS-1$
			_label.save(writer);
			writer.writeEndElement();
		}
		if (_term != null) {
			writer.writeStartElement("termv"); //$NON-NLS-1$
			writer.writeAttribute("term", _term.getName()); //$NON-NLS-1$
			_term.save(writer);
			writer.writeEndElement();

			writer.writeStartElement("relation"); //$NON-NLS-1$
			writer.writeAttribute("unaryRelation", _unaryRelation.getRelationType()); //$NON-NLS-1$
			writer.writeEndElement();
		}
		if (_upperTerm != null) {
			writer.writeStartElement("upperTermv"); //$NON-NLS-1$
			writer.writeAttribute("upperTerm", _upperTerm.getName()); //$NON-NLS-1$
			_upperTerm.save(writer);
			writer.writeEndElement();
		}
		if (_lowerTerm != null) {
			writer.writeStartElement("lowerTermv"); //$NON-NLS-1$
			writer.writeAttribute("lowerTerm", _lowerTerm.getName()); //$NON-NLS-1$
			_lowerTerm.save(writer);
			writer.writeEndElement();
		}

		writer.writeEndElement();

	}

	@Override
	public void read(XMLRead reader) throws XMLStreamException {
		XMLEvent event;
		String endtag = null;
		boolean end = false;

		reader.goToStartElement("hesitant"); //$NON-NLS-1$

		while (reader.hasNext() && !end) {
			event = reader.next();
			if (event.isStartElement()) {
				if ("labelv".equals(reader.getStartElementLocalPart())) { //$NON-NLS-1$
					String name = reader.getStartElementAttribute("label"); //$NON-NLS-1$
					_label = new LabelLinguisticDomain();
					_label.setName(name);
					_label.read(reader);
				} else if ("termv".equals(reader.getStartElementLocalPart())) { //$NON-NLS-1$
					String name = reader.getStartElementAttribute("term"); //$NON-NLS-1$
					_term = new LabelLinguisticDomain();
					_term.setName(name);
					_term.read(reader);
				} else if ("relation".equals(reader.getStartElementLocalPart())) { //$NON-NLS-1$
					String unaryRelation = reader.getStartElementAttribute("unaryRelation"); //$NON-NLS-1$
					if (unaryRelation.contains("greater")) { //$NON-NLS-1$
						_unaryRelation = EUnaryRelationType.GreaterThan;
					} else if (unaryRelation.contains("lower")) { //$NON-NLS-1$
						_unaryRelation = EUnaryRelationType.LowerThan;
					} else if (unaryRelation.contains("least")) { //$NON-NLS-1$
						_unaryRelation = EUnaryRelationType.AtLeast;
					} else if (unaryRelation.contains("most")) { //$NON-NLS-1$
						_unaryRelation = EUnaryRelationType.AtMost;
					}
				} else if ("upperTermv".equals(reader.getStartElementLocalPart())) { //$NON-NLS-1$
					String name = reader.getStartElementAttribute("upperTerm"); //$NON-NLS-1$
					_upperTerm = new LabelLinguisticDomain();
					_upperTerm.setName(name);
					_upperTerm.read(reader);
				} else if ("lowerTermv".equals(reader.getStartElementLocalPart())) { //$NON-NLS-1$
					String name = reader.getStartElementAttribute("lowerTerm"); //$NON-NLS-1$
					_lowerTerm = new LabelLinguisticDomain();
					_lowerTerm.setName(name);
					_lowerTerm.read(reader);
				}
			} else if (event.isEndElement()) {
				endtag = reader.getEndElementLocalPart();
				if (endtag.equals("hesitant")) { //$NON-NLS-1$
					end = true;
				}
			}
		}
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
}
