package sinbad2.domain.type;

import sinbad2.domain.Domain;

public abstract class Linguistic extends Domain {
	
	public Linguistic() {
		super();
	}
	
	@Override
	public Object clone() {
		Linguistic result = null;
		result = (Linguistic) super.clone();
		
		return result;
	}
	

}
