package sinbad2.domain.linguistic.fuzzy.label;


import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.resolutionphase.io.XMLRead;

public class LabelLinguisticDomain implements Cloneable, Comparable<LabelLinguisticDomain> {
	
	private String _name;
	private IMembershipFunction _semantic;
	
	public LabelLinguisticDomain(){}
	
	public LabelLinguisticDomain(String name, IMembershipFunction semantic) {
		Validator.notEmpty(name);
		Validator.notNull(semantic);
		
		_name = name;
		_semantic = semantic;
	}
	
	public String getName() {
		return _name;
	}
	
	public void setName(String name) {
		_name = name;
	}
	
	
	public IMembershipFunction getSemantic() {
		return _semantic;
	}
	
	public void setSemantic(IMembershipFunction semantic) {
		_semantic = semantic;
	}
	
	@Override
	public String toString() {
		return(_name + "::" + _semantic.toString()); //$NON-NLS-1$
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(obj == null || (this.getClass() != obj.getClass())) {
			return false;
		}
		
		final LabelLinguisticDomain other = (LabelLinguisticDomain) obj;
		
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(_name, other._name);
		eb.append(_semantic, other._semantic);
		
		return eb.isEquals();
		
	}
	
	public void save(XMLStreamWriter writer) throws XMLStreamException {		
		writer.writeStartElement("semantic"); //$NON-NLS-1$
		writer.writeAttribute("type", _semantic.getClass().getName()); //$NON-NLS-1$
		_semantic.save(writer);
		writer.writeEndElement();
	}
	
	public void read(XMLRead reader)  throws XMLStreamException {
		reader.goToStartElement("semantic"); //$NON-NLS-1$
		String type = reader.getStartElementAttribute("type"); //$NON-NLS-1$
		Class<?> function = null;
		
		try {
			function = Class.forName(type);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			_semantic = (IMembershipFunction) function.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		_semantic.read(reader);
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(_name);
		hcb.append(_semantic);
		return hcb.hashCode();
	}
	
	@Override
	public Object clone() {
		Object result = null;
		
		try {
			result = super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		((LabelLinguisticDomain) result)._name = new String(_name);
		((LabelLinguisticDomain) result)._semantic = (IMembershipFunction) _semantic.clone();
		
		return result;
		
	}
	
	@Override
	public int compareTo(LabelLinguisticDomain other) {
		Validator.notNull(other);
		
		return _semantic.compareTo(other._semantic);
	}
	
}
