package sinbad2.phasemethod.aggregation;

import java.util.HashMap;
import java.util.Map;

import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.unbalanced.Unbalanced;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.element.ProblemElement;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.twoTuple.TwoTuple;

public class UnbalancedUtils {
	
	private static Unbalanced _unbalancedDomain;
	
	private static final int UNBALANCED_LEFT = 0;
	private static final int UNBALANCED_RIGHT = 1;
	
	public static Map<ProblemElement, Valuation> transformUnbalanced(Map<ProblemElement, Valuation> problemResult, Unbalanced resultsDomain) {

		_unbalancedDomain = resultsDomain;
		Map<ProblemElement, Valuation> results = null;

		if(resultsDomain != null) {

			results = new HashMap<ProblemElement, Valuation>();

			Valuation valuation;
			Unbalanced hgls = null;
			LabelLinguisticDomain label, labelTest = null;
			double alpha;
			Map<Integer, Integer> domains = null;
			int testPos;
			boolean find;
			Integer labelPos;
			Unbalanced domainTest;
			int domainPos;
			int domainSize;

			int size;
			Unbalanced[] auxDomains = null;
			LabelLinguisticDomain[] labels = null;
			double[] alphas = null;
			int[] sizes = null;

			int[] lh = resultsDomain.getLh();
			Map<Integer, Unbalanced> lhDomains = new HashMap<Integer, Unbalanced>();

			for(int i = 0; i < lh.length; i++) {
				lhDomains.put(lh[i], createDomain(lh[i]));
			}

			for(ProblemElement alternative : problemResult.keySet()) {
				valuation = problemResult.get(alternative);
				if(valuation != null) {
					if(hgls == null) {
						hgls = (Unbalanced) valuation.getDomain();
					}
					label = ((TwoTuple) valuation).getLabel();
					alpha = ((TwoTuple) valuation).getAlpha();

					find = false;

					domainPos = lh.length - 1;
					do {
						domainSize = lh[domainPos];
						domainTest = lhDomains.get(domainSize);
						testPos = domainTest.getLabelSet().getPos(label);
						labelPos = resultsDomain.labelPos(domainSize, testPos);
						if(labelPos != null) {
							find = true;
							labelTest = ((Unbalanced) _unbalancedDomain).getLabelSet().getLabel(labelPos);
							domains = resultsDomain.getLabel(labelPos);
							size = domains.size();
							auxDomains = new Unbalanced[size];
							labels = new LabelLinguisticDomain[size];
							sizes = new int[size];
							alphas = new double[size];
							int i = 0;
							for(Integer auxSize : domains.keySet()) {
								auxDomains[i] = lhDomains.get(auxSize);
								labels[i] = auxDomains[i].getLabelSet().getLabel(domains.get(auxSize));
								sizes[i] = auxSize;
								if (labels[i] == label) {
									alphas[i] = alpha;
								} else {
									alphas[i] = ((TwoTuple) valuation).transform(auxDomains[i]).getAlpha();
								}
								i++;
							}
						} else {
							domainPos--;
							valuation = ((TwoTuple) valuation).transform(lhDomains.get(lh[domainPos]));
							label = ((TwoTuple) valuation).getLabel();
							alpha = ((TwoTuple) valuation).getAlpha();
						}
					} while (!find);

					if((domains.size() == 1) || (alpha == 0)) {
						valuation = transformToResultsDomain(labelTest, alphas[0]);
					} else {
						if(alpha > 0) {
							if(smallSide(labelTest) == UNBALANCED_RIGHT) {
								if (sizes[0] > sizes[1]) {
									valuation = transformToResultsDomain(labelTest, alphas[0]);
								} else {
									valuation = transformToResultsDomain(labelTest, alphas[1]);
								}
							} else {
								if(sizes[0] > sizes[1]) {
									valuation = transformToResultsDomain(labelTest, alphas[1]);
								} else {
									valuation = transformToResultsDomain(labelTest, alphas[0]);
								}
							}
						} else {
							if (smallSide(label) == UNBALANCED_RIGHT) {
								if (sizes[0] > sizes[1]) {
									valuation = transformToResultsDomain(labelTest, alphas[1]);
								} else {
									valuation = transformToResultsDomain(labelTest, alphas[0]);
								}
							} else {
								if (sizes[0] > sizes[1]) {
									valuation = transformToResultsDomain(labelTest, alphas[0]);
								} else {
									valuation = transformToResultsDomain(labelTest, alphas[1]);
								}
							}
						}
					}
				}

				results.put(alternative, valuation);
			}
		}

		return results;
	}
	
	private static Unbalanced createDomain(int cardinality) {
		String[] labels = new String[cardinality];
		for(int i = 0; i < cardinality; i++) {
			labels[i] = Integer.toString(i);
		}
		
		Unbalanced domain = new Unbalanced();
		domain.createTrapezoidalFunction(labels);

		return domain;
	}
	
	private static int smallSide(LabelLinguisticDomain l) {
		NumericRealDomain center = l.getSemantic().getCenter();
		NumericRealDomain coverage = l.getSemantic().getCoverage();

		double left = center.getMin() - coverage.getMin();
		double right = coverage.getMax() - center.getMax();

		if(left > right) {
			return UNBALANCED_RIGHT;
		} else {
			return UNBALANCED_LEFT;
		}
	}
	
	private static Valuation transformToResultsDomain(LabelLinguisticDomain label, double alpha) {
		TwoTuple result = new TwoTuple((FuzzySet) _unbalancedDomain, label, alpha);
		return result;
	}
}
