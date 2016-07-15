package sinbad2.element.alternative;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.element.ProblemElement;

public class Alternative extends ProblemElement {
	
	public Alternative() {
		super();
	}
	
	public Alternative(String id) {
		super(id);
	}
	
	@Override
	public String getCanonicalId() {
		return _id;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(obj == null ) {
			return false;
		}
		
		if(obj.getClass() != this.getClass()) {
			return false;
		}
		
		final Alternative other = (Alternative) obj;
		return new EqualsBuilder().append(_id, other._id).isEquals();

	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(_id);
		return hcb.toHashCode();
	}
	
	@Override
	public Object clone() {
		Alternative result = null;
		result = (Alternative) super.clone();
		
		return result;
		
	}
}
