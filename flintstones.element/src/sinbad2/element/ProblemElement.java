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

	public abstract String getFormatId();
	
	@Override
	public String toString() {
		return getFormatId();
	}

	public void setId(String id) {
		_id = id;
	}

	@Override
	public int compareTo(ProblemElement other) {
		// TODO validator
		return getFormatId().compareTo(other.getFormatId());
	}

	@Override
	public Object clone() {
		ProblemElement result = null;
		
		try {
			result = (ProblemElement) super.clone();
		} catch (CloneNotSupportedException e) {
			//No ocurre nunca
		}
		
		result.setId(new String(_id));
		
		return result;
		
	}

	

}
