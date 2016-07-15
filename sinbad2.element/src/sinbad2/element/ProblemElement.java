package sinbad2.element;

import sinbad2.core.validator.Validator;

public abstract class ProblemElement implements Cloneable, Comparable<ProblemElement> {
	
	protected String _id;

	public ProblemElement() {
		_id = null;
	}

	public ProblemElement(String id){
		_id = id;
	}
	
	public String getId() {
		return _id;
	}

	public abstract String getCanonicalId();
	
	@Override
	public String toString() {
		return getCanonicalId();
	}

	public void setId(String id) {
		_id = id;
	}

	@Override
	public int compareTo(ProblemElement other) {
		Validator.notNull(other);
		
		return getCanonicalId().compareTo(other.getCanonicalId());
	}

	@Override
	public Object clone() {
		ProblemElement result = null;
		
		try {
			result = (ProblemElement) super.clone();
		} catch (CloneNotSupportedException e) {
			e.getStackTrace();
		}
		
		result.setId(new String(_id));
		
		return result;
		
	}

}
