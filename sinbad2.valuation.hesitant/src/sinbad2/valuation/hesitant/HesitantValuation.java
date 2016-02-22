package sinbad2.valuation.hesitant;


import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.validator.Validator;
import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.valuation.Valuation;

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
		Validator.notIllegalElementType(domain, new String[] { FuzzySet.class.toString() });
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
		
		if(((FuzzySet) _domain).getLabelSet().containsLabel(label)) {
			_label = label;
			disableRelations();
		} else {
			throw new IllegalArgumentException("Label not contains in domain.");
		}
	}
	
	public LabelLinguisticDomain getLabel() {
		return _label;
	}
	
	public void setUnaryRelation(EUnaryRelationType unaryRelation, LabelLinguisticDomain term) {
		Validator.notNull(unaryRelation);
		Validator.notNull(term);
		
		if(!((FuzzySet) _domain).getLabelSet().containsLabel(term)) {
			throw new IllegalArgumentException("Label not contains in domain.");
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
		
		if(!((FuzzySet) _domain).getLabelSet().containsLabel(lowerTerm)) {
			throw new IllegalArgumentException("Lower term not contains in domain.");
		}
		
		if(!((FuzzySet) _domain).getLabelSet().containsLabel(upperTerm)) {
			throw new IllegalArgumentException("Upper term not contains in domain.");
		}
		
		if(upperTerm.compareTo(lowerTerm) <= 0) {
			throw new IllegalArgumentException("Upper term is bigger than lower term.");
		}
		
		_lowerTerm = lowerTerm;
		_upperTerm = upperTerm;
		
		disablePrimary();
		disableUnary();
	}
	
	public void setBinaryRelation(int lowerTermPos, int upperTermPos) {
		LabelLinguisticDomain lowertTerm = ((FuzzySet) _domain).getLabelSet().getLabel(lowerTermPos);
		Validator.notNull(lowertTerm);
		LabelLinguisticDomain upperTerm = ((FuzzySet) _domain).getLabelSet().getLabel(upperTermPos);
		Validator.notNull(upperTerm);
		
		if(upperTermPos <= lowerTermPos) {
			throw new IllegalArgumentException("Upper term is bigger than lower term.");
		}
		
		_lowerTerm = lowertTerm;
		_upperTerm = upperTerm;
		
		disablePrimary();
		disableUnary();
	}
	
	public void setBinaryRelation(String lowerTermName, String upperTermName) {
		LabelLinguisticDomain lowertTerm = ((FuzzySet) _domain).getLabelSet().getLabel(lowerTermName);
		Validator.notNull(lowertTerm);
		LabelLinguisticDomain upperTerm = ((FuzzySet) _domain).getLabelSet().getLabel(upperTermName);
		Validator.notNull(upperTerm);
		
		if(upperTermName.compareTo(lowerTermName) <= 0) {
			throw new IllegalArgumentException("Upper term is bigger than lower term.");
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
	
	public LabelLinguisticDomain[] getEnvelope() {
		LabelLinguisticDomain[] result = new LabelLinguisticDomain[2];
		int pos, cardinality;
		
		if(isPrimary()) {
			result[0] = _label;
			result[1] = _label;
		} else if(isUnary()) {
			switch(_unaryRelation) {
				case LowerThan:
					pos = ((FuzzySet) _domain).getLabelSet().getPos(_term) - 1;
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
		} else if(isBinary()) {
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
		
		if(envelope != null) {
			result = new int[2];
			result[0] = ((FuzzySet) _domain).getLabelSet().getPos(envelope[0]);
			result[1] = ((FuzzySet) _domain).getLabelSet().getPos(envelope[1]);
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		if(isPrimary()) {
			return (_label + " in " + _domain.toString());
		} else if(isUnary()) {
			return (_unaryRelation.toString() + " " + _term + " in " + _domain.toString());
		} else if(isBinary()) {
			return ("between " + _lowerTerm + " and " + _upperTerm + " in " + _domain.toString());
		} else {
			return "";
		}
	}
	
	@Override
	public Valuation negateValuation() {
		return null;
	}
	
	@Override
	public int hashCode() {
		
		if(_unaryRelation != null ) {
			return new HashCodeBuilder(17, 31).append(_label).append(_domain).append(_unaryRelation.toString()).append(_term)
					.append(_lowerTerm).append(_upperTerm).toHashCode();
		} else {
			return new HashCodeBuilder(17, 31).append(_label).append(_domain).append("").append(_term).append(_lowerTerm).
					append(_upperTerm).toHashCode();
		}
	}
	
	@Override
	public int compareTo(Valuation other) {
		Validator.notNull(other);
		Validator.notIllegalElementType(other, new String[] { HesitantValuation.class.toString() });
		
		if(_domain.equals(other.getDomain())) {
			int[] tEnvelopeIndex = getEnvelopeIndex();
			int[] oEnvelopeIndex = ((HesitantValuation) other).getEnvelopeIndex();
			double tc = (double) (tEnvelopeIndex[1] + tEnvelopeIndex[0]) / 2d;
			double tw = (double) (tEnvelopeIndex[1] - tEnvelopeIndex[0]) / 2d;
			double oc = (double) (oEnvelopeIndex[1] + oEnvelopeIndex[0]) / 2d;
			double ow = (double) (oEnvelopeIndex[1] - oEnvelopeIndex[0]) / 2d;
			
			double acceptability;
			if((tw + ow) == 0) {
				if(tc == oc) {
					return 0;
				} else if(tc > oc) {
					return 1;
				} else {
					return -1;
				}
			}
			
			acceptability = (tc - oc) / (tw + ow);
			double limit = 0.25;
			if((acceptability <= limit) && (acceptability >= -limit)) {
				return 0;
			} else if(acceptability > limit) {
				return 1;
			} else {
				return -1;
			}
		} else {
			throw new IllegalArgumentException("Different domains");
		}
	}

	@Override
	public String changeFormatValuationToString() {
		
		if(isPrimary()) {
			return _label.getName();
		} else {
			if(isUnary()) {
				String aux = getUnaryRelation().getRelationType();
				aux = aux.toLowerCase();
				aux = aux.substring(0, 1).toUpperCase() + aux.substring(1);
				return aux + " " + getTerm().getName();
			} else {
				return "Between" + " " + getLowerTerm().getName() + " " + "and" + " " + getUpperTerm().getName();
			}
		}
		
	}
	
	@Override
	public boolean equals(Object obj) {

		if(this == obj) {
			return true;
		}
		
		if(obj == null) {
			return false;
		}
		
		if(obj.getClass() != this.getClass()) {
			return false;
		}
		
		final HesitantValuation other = (HesitantValuation) obj;
		
		return new EqualsBuilder().append(_label, other._label).append(_domain, other._domain).append(_unaryRelation, other._unaryRelation)
				.append(_term, other._term).append(_lowerTerm, other._lowerTerm).append(_upperTerm, other._upperTerm).isEquals();
	}

	@Override
	public void save(XMLStreamWriter writer) throws XMLStreamException {
		
		writer.writeStartElement("hesitant"); //$NON-NLS-1$
		
		writer.writeStartElement("label"); //$NON-NLS-1$
		_label.save(writer);
		writer.writeEndElement();
		writer.writeStartElement("term"); //$NON-NLS-1$
		_term.save(writer);
		writer.writeEndElement();
		writer.writeStartElement("upperTerm"); //$NON-NLS-1$
		_upperTerm.save(writer);
		writer.writeEndElement();
		writer.writeStartElement("lowerTerm"); //$NON-NLS-1$
		_lowerTerm.save(writer);
		writer.writeEndElement();
		
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
				if ("label".equals(reader.getStartElementLocalPart())) { //$NON-NLS-1$
					_label = new LabelLinguisticDomain();
					_label.read(reader);
				} else if("term".equals(reader.getStartElementLocalPart())){ //$NON-NLS-1$
					_term = new LabelLinguisticDomain();
					_term.read(reader);
				} else if("upperTerm".equals(reader.getStartElementLocalPart())){ //$NON-NLS-1$
					_upperTerm = new LabelLinguisticDomain();
					_upperTerm.read(reader);
				} else if("lowerTerm".equals(reader.getStartElementLocalPart())) { //$NON-NLS-1$
					_lowerTerm = new LabelLinguisticDomain();
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
	public Domain unification(Domain fuzzySet) {
		return null;
	}
	
}
