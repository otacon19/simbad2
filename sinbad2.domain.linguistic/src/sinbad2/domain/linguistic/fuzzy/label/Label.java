package sinbad2.domain.linguistic.fuzzy.label;

import sinbad2.domain.linguistic.fuzzy.semantic.IMembership;

public class Label implements Cloneable, Comparable<Label> {
	
	private String _name;
	private IMembership _semantic;
	
	private Label() {}
	
	public Label(String name, IMembership semantic) {
		this();
		
		//TODO validator;
		
		_name = name;
		_semantic = semantic;
	}
	
	public String getName() {
		return _name;
	}
	
	public IMembership getSemantic() {
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
		
		((Label) result)._name = new String(_name);
		((Label) result)._semantic = (IMembership) _semantic.clone();
		
		return result;
		
	}
	
	@Override
	public int compareTo(Label other) {
		//TODO validator
		
		return _semantic.compareTo(other._semantic);
	}
	
}
