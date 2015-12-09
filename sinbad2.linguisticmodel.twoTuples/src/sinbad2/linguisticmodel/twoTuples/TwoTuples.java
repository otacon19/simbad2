package sinbad2.linguisticmodel.twoTuples;

import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.fuzzy.label.LabelSetLinguisticDomain;

public class TwoTuples {
	
	LabelLinguisticDomain _s;
	double _alpha;

	public TwoTuples(LabelLinguisticDomain s, double alpha ) {
		_s = s;
		_alpha = alpha;
	}
	
	public LabelLinguisticDomain getS() {
		return _s;
	}
	
	public double getAlpha() {
		return _alpha;
	}
	
	public void calculate(FuzzySet domain, double beta) {
		LabelSetLinguisticDomain labels = domain.getLabelSet();
		int i = (int) Math.round(beta);
		
		_s = labels.getLabel(i);
		_alpha = beta - i;
	}
}
