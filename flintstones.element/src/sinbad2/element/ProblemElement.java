package sinbad2.element;

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

	public abstract String getPathId();
	
	@Override
	public String toString() {
		return getPathId();
	}

	public void setId(String id) {
		_id = id;
	}

	@Override
	public int compareTo(ProblemElement other) {
		// TODO validator
		
		return getPathId().compareTo(other.getPathId());
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
