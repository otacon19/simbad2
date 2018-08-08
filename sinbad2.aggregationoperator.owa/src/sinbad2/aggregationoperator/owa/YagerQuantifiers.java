package sinbad2.aggregationoperator.owa;

public class YagerQuantifiers {
	
	public enum QuantificationType {
		most("most"), at_least_half("at least half"), as_many_as_possible("as_many_as_possible"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		@SuppressWarnings("unused")
		private String _text;

		private QuantificationType(String text) {
			_text = text;
		}
	}
	
	public enum NumeredQuantificationType {
		FilevYager("Filev-Yager"); //$NON-NLS-1$

		private String _text;

		private NumeredQuantificationType(String text) {
			_text = text;
		}

		public String getText() {
			return _text;
		}
	}
	
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

	public static double[] QWeighted(NumeredQuantificationType type, int g, int[] envelope, Boolean lower, Boolean b) {

		double[] result = null;
		
		if (type == NumeredQuantificationType.FilevYager) {
			int values;
			double alpha;
			if (lower == null) { //Between
				if (envelope[0] == envelope[1]) {
					result = new double[1];
					result[0] = 1;
				} else {
					if(b) {
						return QWeightedBetweenPointB(g, envelope);
					} else {
						return QWeightedBetweenPointC(g, envelope);
					}
				}
			} else {
				int aux;

				if(lower) {//At most and Lower than
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
				} else { //At least and Greater than
					values = (envelope[1] - envelope[0]) + 1;
					aux = values - 1;

					result = new double[values];
					alpha = ((double) envelope[0]) / g;

					for (int i = 0; i <= aux; i++) {
						if (i == 0) {
							result[i] = Math.pow(alpha, g - envelope[0]);
						} else {
							result[i] = (1d - alpha) * Math.pow(alpha, g - envelope[0] - i);
						}
					}
				}
			}
		}
		return result;
	}
	
	public static double[] QWeightedBetweenPointB(int g, int[] envelope) {
		double[] result = null;
		int values;
		double alpha;
		
		if (envelope[0] == envelope[1]) {
			result = new double[1];
			result[0] = 1;
		} else {
			if((envelope[0] + envelope[1]) % 2 == 0) {
				values = (envelope[1] - envelope[0] + 2) / 2;
				result = new double[values];
				alpha = envelope[0] / g;
				for(int i = 0; i < values; ++i) {
					if(i == 0) {
						result[i] = Math.pow(alpha, (envelope[1] - envelope[0] / 2d));
					} else if(i == values - 1) {
						result[i] = 1 - alpha;
					} else {
						result[i] = (1 - alpha) * Math.pow(alpha, (envelope[1] - envelope[0] - (i + 1)) / 2d);
					}
				}	
			} else {
				values = (envelope[1] - envelope[0] + 1) / 2;
				result = new double[values];
				alpha = envelope[0] / g;
				for(int i = 0; i < values; ++i) {
					if(i == 0) {
						result[i] = Math.pow(alpha, (envelope[1] - envelope[0] - 1 / 2d));
					} else if(i == values - 1) {
						result[i] = 1 - alpha;
					} else {
						result[i] = (1 - alpha) * Math.pow(alpha, (envelope[1] - envelope[0] - (i + 2)) / 2d);
					}
				}	
			}
		}
			
		return result;
	}

	public static double[] QWeightedBetweenPointC(int g, int[] envelope) {
		double[] result = null;
		int values;
		double alpha2;
		
		if (envelope[0] == envelope[1]) {
			result = new double[1];
			result[0] = 1;
		} else {
			if((envelope[0] + envelope[1]) % 2 == 0) {
				values = (envelope[1] - envelope[0] + 2) / 2;
				result = new double[values];
				alpha2 = (g - envelope[0]) / g;
				
				for(int i = 0; i < values; ++i) {
					if(i == 0) {
						result[i] = alpha2;
					} else if(i == values - 1) {
						result[i] = Math.pow(1 - alpha2, (envelope[1] - envelope[0]) / 2d);
					} else {
						result[i] = alpha2 * Math.pow(1d - alpha2, (envelope[1] - envelope[0] - (i + 1)) / 2d);
					}
				}	
			} else {
				values = (envelope[1] - envelope[0] + 1) / 2;
				result = new double[values];
				alpha2 = (g - envelope[0]) / g;
				
				for(int i = 0; i < values; ++i) {
					if(i == 0) {
						result[i] = alpha2;
					} else if(i == values - 1) {
						result[i] = Math.pow(1d - alpha2, (envelope[1] - envelope[0] - 1d) / 2d);
					} else {
						result[i] = (alpha2) * Math.pow(1d - alpha2, (envelope[1] - envelope[0] - (i + 2)) / 2d);
					}
				}	
			}
		}
			
		return result;
	}
	
	public static double[] Quantification(QuantificationType type, int numberOfValuations) {

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
