package sinbad2.valuation.linguistic;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.linguistic.nls.Messages;

public class LinguisticValuation extends Valuation {
	
	public static final String ID = "flintstones.valuation.linguistic"; //$NON-NLS-1$
	
	public LabelLinguisticDomain _label;
	
	public LinguisticValuation() {
		super();
		
		_label = new LabelLinguisticDomain();
	}
	
	public void setLabel(int pos) {
		LabelLinguisticDomain label = ((FuzzySet) _domain).getLabelSet().getLabel(pos);
		Validator.notNull(label);
		
		_label = label;
	}
	
	public void setLabel(String name) {
		LabelLinguisticDomain label = ((FuzzySet) _domain).getLabelSet().getLabel(name);
		Validator.notNull(label);
		
		_label = (LabelLinguisticDomain) label;
	}
	
	public void setLabel(LabelLinguisticDomain label) {
		Validator.notNull(label);
		
		if(((FuzzySet) _domain).getLabelSet().containsLabel(label)) {
			_label = (LabelLinguisticDomain) label;
		} else {
			throw new IllegalArgumentException(Messages.LinguisticValuation_Not_contains_in_domain);
		}
	}
	
	public LabelLinguisticDomain getLabel() {
		return _label;
	}

	@Override
	public Valuation negateValutation() {
		LinguisticValuation result = (LinguisticValuation) clone();
		
		FuzzySet domain = (FuzzySet) _domain;
		if(domain.getLabelSet().getCardinality() > 1) {
			int negPos = (domain.getLabelSet().getCardinality() - 1) -  domain.getLabelSet().getPos(_label);
			result.setLabel(negPos);
		}
		
		return result;
	}
	
	@Override
	public String changeFormatValuationToString() {
		return _label.getName();
	}
	
	//TODO unification
	
	@Override
	public String toString() {
		return (_label + " in " + _domain.toString()); //$NON-NLS-1$
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(obj ==  null) {
			return false;
		}
		
		if(this.getClass() != obj.getClass()) {
			return false;
		}
		
		LinguisticValuation other = (LinguisticValuation) obj;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(_domain, other._domain);
		eb.append(_label, other._label);
		
		return eb.isEquals();
	}
	
	@Override
	public int compareTo(Valuation other) {
		Validator.notNull(other);
		Validator.notIllegalElementType(other, new String[] { LinguisticValuation.class.toString() });
		
		if(_domain.equals(other.getDomain())) {
			return _label.compareTo(((LinguisticValuation) other)._label);
		} else {
			throw new IllegalArgumentException(Messages.LinguisticValuation_Different_domains);
		}
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(_domain);
		hcb.append(_label);
		return hcb.toHashCode();
	}
	
	@Override
	public Object clone() {
		Object result = null;
		
		result = super.clone();
		
		return result;
	}

	@Override
	public void save(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("label"); //$NON-NLS-1$
		_label.save(writer);
		writer.writeEndElement();
	}

	@Override
	public void read(XMLRead reader) throws XMLStreamException {
		_label = new LabelLinguisticDomain();
		_label.read(reader);
	}
}
