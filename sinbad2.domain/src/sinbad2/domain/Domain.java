package sinbad2.domain;

public abstract class Domain implements Cloneable, Comparable<Domain> {
	
	private String _id;
	private String _name;
	private String _type;
	private DomainRegistryExtension _registry;
	
	public Domain() {
		_id = null;
		_name = null;
		_type = null;
		_registry = null;
	}
	
	public String getId() {
		return _id;
	}
	
	public void setId(String id) {
		_id = id;
	}
	
	public String getName() {
		return _name;
	}
	
	public void setName(String name) {
		_name = name;
	}
	
	public String getType() {
		return _type;
	}
	
	public void setType(String type) {
		_type = type;
	}
	
	public DomainRegistryExtension getRegistry() {
		return _registry;
	}
	
	public void setRegistry(DomainRegistryExtension registry) {
		_registry = registry;
	}
	
	//TODO save, read
	
	public abstract double midpoint();
	
	public abstract String formatDescriptionDomain();
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(obj == null || (obj.getClass() != this.getClass()) || !super.equals(obj)) {
			return false;
		}
		
		return false;
		
		//TODO builder
	}
	
	@Override
	public Object clone() {
		Domain result = null;
		
		try {
			result = (Domain) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		result._registry = (DomainRegistryExtension) _registry.clone();
		
		return result;
	}
	
	@Override
	public int compareTo(Domain domain) {
		
		if(_id == null) {
			return -1;
		}
		
		return _id.compareTo(domain.getId());
	}

}
