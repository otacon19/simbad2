package sinbad2.valuation;

import sinbad2.domain.Domain;

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
		//TODO validator
		
		if(_registry.getElement(EValuationElements.domain).equals(domain.getType())) {
			_domain = domain;
		} else {
			throw new IllegalArgumentException(domain.getType() + " expected"); 
		}
	}
	
	public Domain getDomain() {
		return _domain;
	}
	
	//TODO hashcode
	
	//TODO save and read
	
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
	
	
	public abstract Valuation neg();
	
	public abstract String pretty();
	
	

}
