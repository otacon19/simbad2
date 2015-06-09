package sinbad2.domain.linguistic.fuzzy.label;

import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;

public class LabelLinguisticDomain implements Cloneable, Comparable<LabelLinguisticDomain> {
	
	private String _name;
	private IMembershipFunction _semantic;
	
	private LabelLinguisticDomain() {}
	
	public LabelLinguisticDomain(String name, IMembershipFunction semantic) {
		this();
		
		//TODO validator;
		
		_name = name;
		_semantic = semantic;
	}
	
	public String getName() {
		return _name;
	}
	
	public IMembershipFunction getSemantic() {
		return _semantic;
	}
	
	@Override
	public String toString() {
		return(_name + "::" + _semantic.toString());
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(obj == null || (this.getClass() != obj.getClass())) {
			return false;
		}
		
		//TODO equalsBuilder
		
		return false;
		
	}
	
	//TODO hashcode
	
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
		//TODO validator
		
		return _semantic.compareTo(other._semantic);
	}
	
}
