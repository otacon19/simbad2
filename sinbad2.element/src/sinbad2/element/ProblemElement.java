package sinbad2.element;

public abstract class ProblemElement implements Cloneable, Comparable<ProblemElement> {

	protected String _id;

	public ProblemElement() {
		_id = null;
	}

	public ProblemElement(String id) {
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
		return extractInt(this.getId()) - extractInt(other.getId());
	}

	private int extractInt(String s) {
		String num = s.replaceAll("\\D", "");
		return num.isEmpty() ? 0 : Integer.parseInt(num);
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
