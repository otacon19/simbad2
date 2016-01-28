package sinbad2.aggregationoperator;

import java.util.List;

import sinbad2.valuation.Valuation;

public abstract class WeightedAggregationOperator extends AggregationOperator {
	
	public enum QuantificationType {
		most("most"), at_least_half("at least half"), as_many_as_possible("as_many_as_possible");

		@SuppressWarnings("unused")
		private String _text;

		private QuantificationType(String text) {
			_text = text;
		}
	}
	
	public enum NumeredQuantificationType {
		FilevYager("Filev-Yager");

		private String _text;

		private NumeredQuantificationType(String text) {
			_text = text;
		}

		public String getText() {
			return _text;
		}
	}

	public abstract Valuation aggregate(List<Valuation> valuations, List<Double> weights);

	private static double RIM(double r, double alpha, double beta) {

		if (r <= alpha) {
			return 0;
		} else if (r > beta) {
			return 1;
		} else {
			return ((r - alpha) / (beta - alpha));
		}
	}

	public static double[] Q(int numberOfValuations, double alpha, double beta) {

		double[] result = new double[numberOfValuations];

		double value1 = -1;
		double value2 = -1;
		for (int i = 0; i < numberOfValuations; i++) {
			if (value1 == -1) {
				value2 = (((double) i) / ((double) numberOfValuations));
			} else {
				value2 = value1;
			}
			value1 = (((double) (i + 1)) / ((double) numberOfValuations));

			result[i] = RIM(value1, alpha, beta) - RIM(value2, alpha, beta);
		}

		return result;
	}

	public static double[] QWeighted(NumeredQuantificationType type, int g, int[] envelope, Boolean lower) {

		double[] result = null;
		
		if (type == NumeredQuantificationType.FilevYager) {
			if (lower == null) {
				if (envelope[0] == envelope[1]) {
					result = new double[1];
					result[0] = 1;
				} else {
					result = new double[2];
					result[0] = 0.5;
					result[1] = 0.5;
				}
			} else {
				int values;
				int aux;
				double alpha;

				if (lower) {

					values = envelope[1] + 1;
					aux = values - 1;

					result = new double[values];
					alpha = (((double) values) - 1) / g;

					for (int i = 0; i <= aux; i++) {
						if (i == aux) {
							result[i] = Math.pow(1d - alpha, i);
						} else {
							result[i] = alpha * Math.pow(1d - alpha, i);
						}
					}
				} else {
					values = (envelope[1] - envelope[0]) + 1;
					aux = values - 1;

					result = new double[values];
					alpha = ((double) envelope[0]) / g;

					for (int i = 0; i <= aux; i++) {
						if (i == 0) {
							result[i] = Math.pow(alpha, g - envelope[0]);
						} else {
							result[i] = (1d - alpha)
									* Math.pow(alpha, g - envelope[0] - i);
						}
					}
				}
			}
		}
		return result;

	}

	public static double[] Quantification(QuantificationType type,
			int numberOfValuations) {

		switch (type) {
		case most:
			return Q(numberOfValuations, 0.3, 0.8);

		case at_least_half:
			return Q(numberOfValuations, 0, 0.5);

		case as_many_as_possible:
			return Q(numberOfValuations, 0.5, 1);

		default:
			return null;
		}

	}

	public static double[] getQuantificationParams(QuantificationType type) {

		switch (type) {
		case most:
			return new double[] { 0.3, 0.8 };

		case at_least_half:
			return new double[] { 0, 0.5 };

		case as_many_as_possible:
			return new double[] { 0.5, 1 };

		default:
			return null;
		}

	}
	
}
