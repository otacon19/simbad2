package sinbad2.valuation.valuationset;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;

public class ValuationKey implements Cloneable, Comparable<ValuationKey> {
	
	private Expert _expert;
	private Alternative _alternative;
	private Criterion _criterion;

	private ValuationKey() {
		_expert = null;
		_alternative = null;
		_criterion = null;
	}

	public ValuationKey(Expert expert, Alternative alternative, Criterion criterion) {
		this();
		_expert = expert;
		_alternative = alternative;
		_criterion = criterion;
	}

	public Expert getExpert() {
		return _expert;
	}

	public Alternative getAlternative() {
		return _alternative;
	}

	public Criterion getCriterion() {
		return _criterion;
	}

	@Override
	public boolean equals(Object object) {

		if (this == object) {
			return true;
		}

		if (object == null) {
			return false;
		}

		if (object.getClass() != this.getClass()) {
			return false;
		}

		final ValuationKey other = (ValuationKey) object;

		EqualsBuilder eb = new EqualsBuilder();
		eb.append(_expert, other._expert);
		eb.append(_alternative, other._alternative);
		eb.append(_criterion, other._criterion);
		return eb.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(_expert);
		hcb.append(_alternative);
		hcb.append(_criterion);
		return hcb.toHashCode();
	}

	public Object clone() {

		ValuationKey result = null;

		try {
			result = (ValuationKey) super.clone();
		} catch (Exception e) {
			e.printStackTrace();
		}

		result._expert = (_expert != null) ? (Expert) _expert.clone() : null;
		result._alternative = (_alternative != null) ? (Alternative) _alternative.clone() : null;
		result._criterion = (_criterion != null) ? (Criterion) _criterion.clone() : null;

		return result;
	}

	@Override
	public String toString() {
		String result = null;

		result += (_expert != null) ? _expert.getCanonicalId() : "null"; //$NON-NLS-1$
		result += ":"; //$NON-NLS-1$
		result += (_alternative != null) ? _alternative.getCanonicalId() : "null"; //$NON-NLS-1$
		result += ":"; //$NON-NLS-1$
		result += (_criterion != null) ? _criterion.getCanonicalId() : "null"; //$NON-NLS-1$

		return result;
	}

	@Override
	public int compareTo(ValuationKey other) {
		return toString().compareTo(other.toString());
	}
}
