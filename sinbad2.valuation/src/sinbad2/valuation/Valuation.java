package sinbad2.valuation;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.validator.Validator;
import sinbad2.domain.Domain;
import sinbad2.resolutionphase.io.XMLRead;

public abstract class Valuation implements Cloneable, Comparable<Valuation> {

	protected Domain _domain;
	private String _domainExtensionId;
	private String _id;
	private String _name;
	private ValuationRegistryExtension _registry;
	
	public Valuation() {
		_domain = null;
		_id = null;
		_name = null;
		_registry = null;
	}
	
	public void setDomainExtensionId(String domainExtensionId) {
		_domainExtensionId = domainExtensionId;
	}
	
	public String getDomainExtensionId() {
		return _domainExtensionId;
	}
	
	public void setId(String id) {
		_id = id;
	}
	
	public String getId() {
		return _id;
	}
	
	public void setName(String name) {
		_name = name;
	}
	
	public String getName() {
		return _name;
	}
	
	public void setRegistry(ValuationRegistryExtension registry) {
		_registry = registry;
	}
	
	public ValuationRegistryExtension getRegistry() {
		return _registry;
	}
	
	public void setDomain(Domain domain) {
		Validator.notNull(domain);
		
		if(_registry.getElement(EValuationElements.domain).equals(domain.getType())) {
			_domain = domain;
		} else {
			throw new IllegalArgumentException(domain.getType() + " expected");  //$NON-NLS-1$
		}
	}
	
	public Domain getDomain() {
		return _domain;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(_id);
		hcb.append(_name);
		hcb.append(_registry);
		hcb.append(_domain);
		return hcb.toHashCode();
	}
	
	public Object clone() {
		Valuation result = null;
		
		try {
			result = (Valuation) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		
		result._registry = (ValuationRegistryExtension) _registry.clone();
		
		if(_domain != null) {
			result._domain = (Domain) _domain.clone();
		}
		
		return result;
		
	}
	
	public abstract Valuation negateValutation();
	
	public abstract String changeFormatValuationToString();
	
	public abstract void save(XMLStreamWriter writer) throws XMLStreamException;
	
	public abstract void read(XMLRead reader) throws XMLStreamException;
	
}
