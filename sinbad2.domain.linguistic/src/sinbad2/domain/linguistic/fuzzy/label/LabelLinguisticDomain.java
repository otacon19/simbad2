package sinbad2.domain.linguistic.fuzzy.label;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;

public class LabelLinguisticDomain implements Cloneable, Comparable<LabelLinguisticDomain> {
	
	private String _name;
	private IMembershipFunction _semantic;
	
	public LabelLinguisticDomain(){}
	
	public LabelLinguisticDomain(String name, IMembershipFunction semantic) {
		
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
		
		final LabelLinguisticDomain other = (LabelLinguisticDomain) obj;
		
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(_name, other._name);
		eb.append(_semantic, other._semantic);
		
		return eb.isEquals();
		
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
		//TODO validator
		
		return _semantic.compareTo(other._semantic);
	}
	
}
