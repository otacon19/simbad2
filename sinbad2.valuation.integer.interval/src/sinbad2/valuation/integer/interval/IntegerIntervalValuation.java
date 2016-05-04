package sinbad2.valuation.integer.interval;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.validator.Validator;
import sinbad2.domain.Domain;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.ValuationsManager;
import sinbad2.valuation.integer.interval.nls.Messages;

public class IntegerIntervalValuation extends Valuation {

	public static final String ID = "flintstones.valuation.integer.interval"; //$NON-NLS-1$
	
	public long _min;
	public long _max;
	
	public IntegerIntervalValuation() {
		super();
		_min = 0;
		_max = 0;
	}
	
	public IntegerIntervalValuation(NumericIntegerDomain domain, long min, long max) {
		super();
		_domain = domain;
		_min = min;
		_max = max;
	}
	
	public void setMin(Long min) {
		_min = min;
	}
	
	public long getMin() {
		return _min;
	}
	
	public void setMax(Long max) {
		_max = max;
	}
	
	public long getMax() {
		return _max;
	}
	
	public void setMinMax(Long min, Long max) {
		Validator.notNull(_domain);
		Validator.notDisorder(new double[] {min,  max}, false);
		
		_min = min;
		_max = max;
	}
	
	public Valuation normalized() {
		ValuationsManager valuationsManager = ValuationsManager.getInstance();
		IntegerIntervalValuation result = (IntegerIntervalValuation) valuationsManager.copyValuation(ID);
		
		DomainsManager domainsManager = DomainsManager.getInstance();
		NumericIntegerDomain domain = (NumericIntegerDomain) domainsManager.copyDomain(NumericIntegerDomain.ID);
		
		domain.setMinMax(((NumericIntegerDomain) _domain).getMin(), ((NumericIntegerDomain) _domain).getMax());
		domain.setInRange(((NumericIntegerDomain) _domain).getInRange());
		
		result.setDomain(domain);
		result.setMinMax(_min, _max);
		
		return result.normalizeInterval();
	}
	
	@Override
	public Valuation negateValuation() {
		IntegerIntervalValuation result = (IntegerIntervalValuation) clone();
		
		long aux = Math.round(((NumericIntegerDomain) _domain).getMin()) + Math.round(((NumericIntegerDomain) _domain).getMax());
		result.setMinMax(aux - _max, aux - _min);
		
		return result;
	}
	
	@Override
	public FuzzySet unification(Domain fuzzySet) {

		Validator.notNull(fuzzySet);

		if (!((FuzzySet) fuzzySet).isBLTS()) {
			throw new IllegalArgumentException(Messages.IntegerIntervalValuation_Not_BLTS_fuzzy_set);
		}
		
		int cardinality;
		IntegerIntervalValuation normalized;
		IMembershipFunction function;

		FuzzySet result = (FuzzySet) fuzzySet.clone();
		cardinality = ((FuzzySet) fuzzySet).getLabelSet().getCardinality();
		normalized = (IntegerIntervalValuation) normalized();

		for (int i = 0; i < cardinality; i++) {
			function = result.getLabelSet().getLabel(i).getSemantic();
			result.setValue(i, function.maxMin(normalized.getMax(), normalized.getMin()));
		}

		return result;
	}
	
	@Override
	public String toString() {
		return ("Integer interval[" + _min + "," + _max + "] in " + _domain.toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(obj == null) {
			return false;
		}
		
		if(obj.getClass() != this.getClass()) {
			return false;
		}
		
		final IntegerIntervalValuation other = (IntegerIntervalValuation) obj;
		
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(_max, other._max);
		eb.append(_min, other._min);
		eb.append(_domain, other._domain);
		
		return eb.isEquals();
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(_max);
		hcb.append(_min);
		hcb.append(_domain);
		return hcb.toHashCode();
	}
	
	@Override
	public int compareTo(Valuation other) {
		Validator.notNull(other);
		Validator.notIllegalElementType(other, new String[] {Integer.class.toString()});
		
		if(_domain.equals(other.getDomain())) {
			long middle = (_max + _min) / 2l;
			long otherMidle = (((IntegerIntervalValuation) other)._max + ((IntegerIntervalValuation) other)._min) / 2l;
			return Long.valueOf(middle).compareTo(Long.valueOf(otherMidle));
		} else {
			throw new IllegalArgumentException(Messages.IntegerInterval_Differents_domains);
		}
	}
	
	@Override
	public Object clone() {
		IntegerIntervalValuation result = null;
		result = (IntegerIntervalValuation) super.clone();
		result._min = new Long(_min);
		result._max = new Long(_max);
		
		return result;
	}

	@Override
	public String changeFormatValuationToString() {
		return "[" + Long.toString(_min) + ", " + Long.toString(_max) + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	@Override
	public void save(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeAttribute("min", Long.toString(_min)); //$NON-NLS-1$
		writer.writeAttribute("max", Long.toString(_max));	 //$NON-NLS-1$
	}

	@Override
	public void read(XMLRead reader) throws XMLStreamException {
		_min = Long.parseLong(reader.getStartElementAttribute("min")); //$NON-NLS-1$
		_max = Long.parseLong(reader.getStartElementAttribute("max")); //$NON-NLS-1$
	}
	
	private Valuation normalizeInterval() {
		IntegerIntervalValuation result = (IntegerIntervalValuation) clone();
		
		long min, max, intervalSize;
		
		min = Math.round(((NumericIntegerDomain) _domain).getMin());
		max = Math.round(((NumericIntegerDomain) _domain).getMax());
		intervalSize = max - min;
		
		max = ((long) (_max - min) / intervalSize);
		min = ((long) (_min - min) / intervalSize);
		
		((NumericIntegerDomain) result._domain).setMinMax(0, 1);
		
		result._min = min;
		result._max = max;
		
		return result;
	}
	
}
