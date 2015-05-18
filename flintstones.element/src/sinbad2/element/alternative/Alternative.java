package sinbad2.element.alternative;

import sinbad2.element.ProblemElement;

public class Alternative extends ProblemElement{
	
	public Alternative() {
		super();
	}
	
	public Alternative(String id) {
		super(id);
	}
	
	@Override
	public String getFormatId() {
		return _id;
	}
	
	@Override
	public boolean equals(Object object) {
		if(this == object) {
			return true;
		}
		
		if(object == null || object.getClass() != this.getClass()) {
			return false;
		}
		
		//TODO builder
		
		return false;
	}
	
	@Override
	public Object clone() {
		Alternative result = null;
		result = (Alternative) super.clone();
		
		return result;
		
	}
}
