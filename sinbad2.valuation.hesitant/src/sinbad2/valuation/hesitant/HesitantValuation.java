package sinbad2.valuation.hesitant;


import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import sinbad2.core.validator.Validator;
import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.valuation.Valuation;

public class HesitantValuation extends Valuation {
	
	public static final String ID = "flintstones.valuation.hesitant";
	
	private LabelLinguisticDomain _term;
	private LabelLinguisticDomain _lowerTerm;
	private LabelLinguisticDomain _upperTerm;
	
	private LabelLinguisticDomain _label;

	private HesitantValuation() {
		super();
		
		_label = null;
		_term = null;
		_lowerTerm = null;
		_upperTerm = null;	
	}
	
	public HesitantValuation(FuzzySet domain) {
		this();
		
		setDomain(domain);
	}
	
	public LabelLinguisticDomain getLabel() {
		return _label;
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
	}

	public void setLabel(String name) {
		LabelLinguisticDomain newLabel = ((FuzzySet) _domain).getLabelSet().getLabel(name);
		Validator.notNull(newLabel);
		
		_label = newLabel;
		
	}
	
	public void setLabel(LabelLinguisticDomain label) {
		Validator.notNull(label);
		
		if(((FuzzySet) _domain).getLabelSet().containsLabel(label)) {
			_label = (LabelLinguisticDomain) label;
		} else {
			throw new IllegalArgumentException("Label not contains in domain.");
		}
		
	}

	@Override
	public int compareTo(Valuation other) {
		Validator.notNull(other);
		Validator.notIllegalElementType(other, new String[] { HesitantValuation.class.toString() });
		
		if(_domain.equals(other.getDomain())) {
			
		}
		
		return 0;
		
	}
	

	@Override
	public Valuation negateValutation() {
		return null;
	}

	@Override
	public String changeFormatValuationToString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(XMLStreamWriter writer) throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void read(XMLRead reader) throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Object clone() {
		Object result = null;
		
		result = super.clone();
		
		return result;
	}

}
