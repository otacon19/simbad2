package sinbad2.valuation.unifiedValuation;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.twoTuple.TwoTuple;

public class UnifiedValuation extends Valuation {

	private UnifiedValuation() {}
	
	public UnifiedValuation(FuzzySet domain) {
		this();
		_domain = domain;
	}
	
	@Override
	public int compareTo(Valuation other) {
		Validator.notNull(other);
		Validator.notIllegalElementType(other, new String[] { UnifiedValuation.class.toString() });

		FuzzySet thisDomain = (FuzzySet) _domain;
		FuzzySet otherDomain = (FuzzySet) other.getDomain();
		for (int i = 0; i < thisDomain.getLabelSet().getCardinality(); i++) {
			if (!thisDomain.getLabelSet().getLabel(i).equals(otherDomain.getLabelSet().getLabel(i))) {
				throw new IllegalArgumentException("Different domains");
			}
		}

		TwoTuple thisDisunification = disunification(thisDomain);
		TwoTuple otherDisunification = disunification(otherDomain);

		return thisDisunification.compareTo(otherDisunification);
	}
	
	public TwoTuple disunification(FuzzySet fuzzySet) {
		Validator.notNull(fuzzySet);

		TwoTuple result = new TwoTuple((FuzzySet) fuzzySet.clone());

		int size = fuzzySet.getLabelSet().getCardinality();
		double numerator = 0, denominator = 0, measure, beta = 0;
		for (int i = 0; i < size; i++) {
			((FuzzySet) result.getDomain()).setValue(i, 0d);
			measure = fuzzySet.getValue(i);
			numerator += measure * i;
			denominator += measure;
		}

		if (denominator != 0) {
			beta = numerator / denominator;
		}
		
		result.calculateDelta(beta);

		return result;
	}

	@Override
	public Valuation negateValutation() {
		return null;
	}

	@Override
	public String changeFormatValuationToString() {
		FuzzySet domain = (FuzzySet) _domain;
		StringBuilder result = new StringBuilder("{");
		List<LabelLinguisticDomain> labels = domain.getLabelSet().getLabels();
		String name, measure;
		
		for (int i = 0; i < labels.size(); i++) {
			name = labels.get(i).getName();
			measure = domain.getValue(i).toString();
			if (measure.length() > 4) {
				measure = measure.substring(0, 4);
			}
			result.append("(" + name + "," + measure + ")");
			if ((i + 1) != labels.size()) {
				result.append(",");
			}
		}
		result.append("}");
		
		return result.toString();
	}

	@Override
	public void save(XMLStreamWriter writer) throws XMLStreamException {}

	@Override
	public void read(XMLRead reader) throws XMLStreamException {}

}
