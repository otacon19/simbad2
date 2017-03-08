package sinbad2.valuation.twoTuple;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.linguistic.LinguisticValuation;
import sinbad2.valuation.twoTuple.nls.Messages;

public class TwoTuple extends LinguisticValuation {
	
	private double _alpha;
	
	public TwoTuple(FuzzySet domain) {
		setDomain(domain);
		_alpha = 0d;
	}
	
	public TwoTuple(FuzzySet domain, LabelLinguisticDomain label) {
		setDomain(domain);
		setLabel(label);
	}
	
	public TwoTuple(FuzzySet domain, LabelLinguisticDomain label, double alpha) {
		setDomain(domain);
		setLabel(label);
		setAlpha(alpha);
	}
	
	public void setLabel(String name) {
		super.setLabel(name);
	}
	
	public void setLabel(LabelLinguisticDomain label) {
		super.setLabel(label);
	}
	
	public void setLabel(int pos) {
		super.setLabel(pos);
	}
	
	public void setAlpha(double alpha) {
		Validator.notInvalidSize(alpha, -0.5, 0.5, "alpha"); //$NON-NLS-1$
		
		int pos = ((FuzzySet) _domain).getLabelSet().getPos(_label);
		
		if((pos == 0) && (alpha < 0)) {
			throw new IllegalArgumentException(Messages.TwoTuple_Invalid_alpha_value);
		}
		
		if((pos == ((FuzzySet) _domain).getLabelSet().getCardinality() - 1) && (alpha > 0)) {
			throw new IllegalArgumentException(Messages.TwoTuple_Invalid_alpha_value);
		}
		
		_alpha = alpha;
	}
	
	public double getAlpha() {
		return _alpha;
	}
	
	public void calculateDelta(double beta) {
		int labelIndex = (int) Math.round(beta);
		setLabel(labelIndex);
		
		double alpha = beta - labelIndex;
		alpha = Math.round(alpha * 100d) / 100d;
		
		if(alpha == 0.5) {
			labelIndex++;
			setLabel(labelIndex);
			alpha = -0.5;
		}
		setAlpha(alpha);
	}
	
	public double calculateInverseDelta() {
		return _alpha + ((FuzzySet) _domain).getLabelSet().getPos(_label);
	}
	
	@Override
	public Valuation negateValuation() {
		TwoTuple result = (TwoTuple) clone();
		
		FuzzySet domain = (FuzzySet) _domain;
		if(domain.getLabelSet().getCardinality() > 1) {
			result.calculateDelta((domain.getLabelSet().getCardinality() - 1) - calculateInverseDelta());
		}
		
		return result;
	}
	
	public TwoTuple transform(FuzzySet fuzzySet) {

		TwoTuple result = null;
		
		Validator.notNull(fuzzySet);
		
		if (!fuzzySet.isBLTS()) {
			throw new IllegalArgumentException(Messages.TwoTuple_Not_BLTS_fuzzy_set);
		}
		
		int thisCardinality = ((FuzzySet) _domain).getLabelSet().getCardinality();
		int otherCardinality = fuzzySet.getLabelSet().getCardinality();
		
		double numerator = calculateInverseDelta() * ((double) (otherCardinality - 1));
		double denominator = (double) (thisCardinality - 1);
		
		double beta = numerator / denominator;
		
		result = new TwoTuple(fuzzySet);
		result.calculateDelta(beta);
		
		return result;
	}
	
	@Override
	public String toString() {
		return ("[" + _label + ", " + _alpha + "]" + Messages.TwoTuple_In + _domain); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public String prettyFormat() {
		return ("(" + _label + ", " + _alpha + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(obj == null) {
			return false;
		}
		
		if(this.getClass() != obj.getClass()) {
			return false;
		}
		
		TwoTuple other = (TwoTuple) obj;
		return new EqualsBuilder().append(_label, other._label).append(_domain, other._domain).append(_alpha, other._alpha).isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(_label).append(_domain).append(_alpha).toHashCode();
	}
	
	@Override
	public int compareTo(Valuation other) {
		Validator.notNull(other);
		Validator.notIllegalElementType(other, new String[] { TwoTuple.class.toString()});
		
		if(_domain.equals(other.getDomain())) {
			LabelLinguisticDomain otherLabel = ((TwoTuple) other)._label;
			double otherAlpha = ((TwoTuple) other)._alpha;
			
			int aux;
			if((aux = _label.compareTo(otherLabel)) == 0) {
				return Double.compare(_alpha, otherAlpha);
			} else {
				return aux;
			}	
		} else {
			throw new IllegalArgumentException(Messages.TwoTuple_Different_domains);
		}
	}
	
	@Override
	public Object clone() {
		Object result = null;
		result = super.clone();
		
		return result;
	}
}
