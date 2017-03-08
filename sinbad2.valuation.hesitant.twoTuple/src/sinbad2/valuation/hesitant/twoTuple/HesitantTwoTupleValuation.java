package sinbad2.valuation.hesitant.twoTuple;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.validator.Validator;
import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.unbalanced.Unbalanced;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.EUnaryRelationType;
import sinbad2.valuation.hesitant.twoTuple.nls.Messages;
import sinbad2.valuation.twoTuple.TwoTuple;

public class HesitantTwoTupleValuation extends Valuation {

	public static final String ID = "flintstones.valuation.hesitant.twoTuple"; //$NON-NLS-1$

	private EUnaryRelationType _unaryRelation;
	private TwoTuple _term;
	private TwoTuple _upperTerm;
	private TwoTuple _lowerTerm;
	private TwoTuple _label;

	public HesitantTwoTupleValuation() {
		super();
		_unaryRelation = null;
		_term = null;
		_upperTerm = null;
		_lowerTerm = null;
		_label = null;
	}

	public HesitantTwoTupleValuation(FuzzySet domain) {
		this();
		setDomain(domain);
	}

	@Override
	public void setDomain(Domain domain) {
		Validator.notNull(domain);
		Validator.notIllegalElementType(domain, new String[] { FuzzySet.class.toString(), Unbalanced.class.toString() });
		Validator.notEmpty(((FuzzySet) domain).getLabelSet().getLabels().toArray());

		_domain = (FuzzySet) domain;
	}

	public void setTwoTupleLabel(int pos) {
		LabelLinguisticDomain newLabel = ((FuzzySet) _domain).getLabelSet().getLabel(pos);
		Validator.notNull(newLabel);

		_label = new TwoTuple((FuzzySet) _domain, newLabel, 0);
		disableRelations();
	}

	public void setTwoTupleLabel(String name) {
		LabelLinguisticDomain newLabel = ((FuzzySet) _domain).getLabelSet().getLabel(name);
		Validator.notNull(newLabel);

		_label = new TwoTuple((FuzzySet) _domain, newLabel, 0);
		disableRelations();
	}

	public void setTwoTupleLabel(LabelLinguisticDomain label) {
		Validator.notNull(label);

		if (((FuzzySet) _domain).getLabelSet().containsLabel(label)) {
			_label =  new TwoTuple((FuzzySet) _domain, label, 0);
			disableRelations();
		} else {
			throw new IllegalArgumentException(Messages.HesitantTwoTupleValuation_Hesitant_valuation_label_not_contained_in_domain);
		}
	}
	
	public void setTwoTupleLabel(TwoTuple hTwoTuple) {
		Validator.notNull(hTwoTuple);
		
		if (((FuzzySet) _domain).getLabelSet().containsLabel(hTwoTuple.getLabel())) {
			_label =  hTwoTuple;
			disableRelations();
		} else {
			throw new IllegalArgumentException(Messages.HesitantTwoTupleValuation_Hesitant_valuation_label_not_contained_in_domain);
		}
	}

	public TwoTuple getTwoTupleLabel() {
		return _label;
	}

	public void setUnaryRelation(EUnaryRelationType unaryRelation, TwoTuple term) {
		Validator.notNull(unaryRelation);
		Validator.notNull(term);

		if (!((FuzzySet) _domain).getLabelSet().containsLabel(term.getLabel())) {
			throw new IllegalArgumentException(Messages.HesitantTwoTupleValuation_Hesitant_valuation_label_not_contained_in_domain);
		}

		_unaryRelation = unaryRelation;
		_term = term;

		disablePrimary();
		disableBinary();
	}

	public EUnaryRelationType getUnaryRelation() {
		return _unaryRelation;
	}

	public TwoTuple getTwoTupleTerm() {
		return _term;
	}

	public void setBinaryRelation(TwoTuple lowerTerm, TwoTuple upperTerm) {
		Validator.notNull(lowerTerm);
		Validator.notNull(upperTerm);

		if (!((FuzzySet) _domain).getLabelSet().containsLabel(lowerTerm.getLabel())) {
			throw new IllegalArgumentException(Messages.HesitantTwoTupleValuation_Hesitant_valuation_lower_term_not_contained_in_domain);
		}

		if (!((FuzzySet) _domain).getLabelSet().containsLabel(upperTerm.getLabel())) {
			throw new IllegalArgumentException(Messages.HesitantTwoTupleValuation_Hesitant_valuation_upper_term_not_contained_in_domain);
		}

		if (upperTerm.compareTo(lowerTerm) <= 0) {
			throw new IllegalArgumentException(Messages.HesitantTwoTupleValuation_Hesitant_valuation_lower_term_is_bigger_than_upper_term);
		}

		_lowerTerm = lowerTerm;
		_upperTerm = upperTerm;

		disablePrimary();
		disableUnary();
	}

	public void setBinaryRelation(int lowerTermPos, int upperTermPos) {
		LabelLinguisticDomain lowerTerm = ((FuzzySet) _domain).getLabelSet().getLabel(lowerTermPos);
		Validator.notNull(lowerTerm);
		LabelLinguisticDomain upperTerm = ((FuzzySet) _domain).getLabelSet().getLabel(upperTermPos);
		Validator.notNull(upperTerm);

		if (upperTermPos <= lowerTermPos) {
			throw new IllegalArgumentException(Messages.HesitantTwoTupleValuation_Hesitant_valuation_lower_term_is_bigger_than_upper_term);
		}

		_lowerTerm = new TwoTuple((FuzzySet) _domain, lowerTerm, 0);
		_upperTerm = new TwoTuple((FuzzySet) _domain, upperTerm, 0);

		disablePrimary();
		disableUnary();
	}

	public void setBinaryRelation(String lowerTermName, String upperTermName) {
		LabelLinguisticDomain lowertTerm = ((FuzzySet) _domain).getLabelSet().getLabel(lowerTermName);
		Validator.notNull(lowertTerm);
		LabelLinguisticDomain upperTerm = ((FuzzySet) _domain).getLabelSet().getLabel(upperTermName);
		Validator.notNull(upperTerm);

		if (upperTermName.compareTo(lowerTermName) <= 0) {
			throw new IllegalArgumentException(Messages.HesitantTwoTupleValuation_Hesitant_valuation_lower_term_is_bigger_than_upper_term);
		}

		_lowerTerm = new TwoTuple((FuzzySet) _domain, lowertTerm, 0);
		_upperTerm = new TwoTuple((FuzzySet) _domain, upperTerm, 0);

		disablePrimary();
		disableUnary();
	}

	public TwoTuple getTwoTupleLowerTerm() {
		return _lowerTerm;
	}

	public TwoTuple getTwoTupleUpperTerm() {
		return _upperTerm;
	}

	public boolean isPrimary() {
		return (_label != null);
	}

	public boolean isComposite() {
		return (isUnary() || isBinary());
	}

	public boolean isUnary() {
		return ((_unaryRelation != null) && (_term != null));
	}

	public boolean isBinary() {
		return ((_lowerTerm != null) && (_upperTerm != null));
	}

	@Override
	public String toString() {
		if (isPrimary()) {
			return (_label + Messages.HesitantTwoTupleValuation_in + _domain.toString());
		} else if (isUnary()) {
			return (_unaryRelation.toString() + " " + _term + Messages.HesitantTwoTupleValuation_in + _domain.toString()); //$NON-NLS-1$
		} else if (isBinary()) {
			return (Messages.HesitantTwoTupleValuation_Between + _lowerTerm + Messages.HesitantTwoTupleValuation_and + _upperTerm + Messages.HesitantTwoTupleValuation_in + _domain.toString());
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	@Override
	public Valuation negateValuation() {
		return null;
	}

	@Override
	public int hashCode() {

		if (_unaryRelation != null) {
			return new HashCodeBuilder(17, 31).append(_label).append(_domain).append(_unaryRelation.toString())
					.append(_term).append(_lowerTerm).append(_upperTerm).toHashCode();
		} else {
			return new HashCodeBuilder(17, 31).append(_label).append(_domain).append("").append(_term) //$NON-NLS-1$
					.append(_lowerTerm).append(_upperTerm).toHashCode();
		}
	}

	@Override
	public String changeFormatValuationToString() {

		if (isPrimary()) {
			return _label.changeFormatValuationToString();
		} else {
			if (isUnary()) {
				String aux = getUnaryRelation().getRelationType();
				aux = aux.toLowerCase();
				aux = aux.substring(0, 1).toUpperCase() + aux.substring(1);
				return aux + " " + getTwoTupleTerm().prettyFormat(); //$NON-NLS-1$
			} else {
				return "Between" + " " + getTwoTupleLowerTerm().prettyFormat() + " " + Messages.HesitantTwoTupleValuation_and + " " + getTwoTupleUpperTerm().prettyFormat();  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-5$
			}
		}
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (obj.getClass() != this.getClass()) {
			return false;
		}

		final HesitantTwoTupleValuation other = (HesitantTwoTupleValuation) obj;

		return new EqualsBuilder().append(_label, other._label).append(_domain, other._domain)
				.append(_unaryRelation, other._unaryRelation).append(_term, other._term)
				.append(_lowerTerm, other._lowerTerm).append(_upperTerm, other._upperTerm).isEquals();
	}

	@Override
	public void save(XMLStreamWriter writer) throws XMLStreamException {}

	@Override
	public void read(XMLRead reader) throws XMLStreamException {}

	@Override
	public Object clone() {
		Object result = null;

		result = super.clone();

		return result;
	}

	private void disablePrimary() {
		_label = null;
	}

	private void disableRelations() {
		disableUnary();
		disableBinary();
	}

	private void disableUnary() {
		_unaryRelation = null;
		_term = null;
	}

	private void disableBinary() {
		_lowerTerm = null;
		_upperTerm = null;
	}

	@Override
	public FuzzySet unification(Domain fuzzySet) {
		return null;
	}

	@Override
	public int compareTo(Valuation o) {
		return 0;
	}
}
