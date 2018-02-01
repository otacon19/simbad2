package sinbad2.phasemethod.topsis.selection.ui.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.phasemethod.topsis.selection.ui.nls.messages"; //$NON-NLS-1$
	public static String selection_Calculate_solutions;
	public static String calculate_solutions_Alternative;
	public static String calculate_solutions_Collective_valuation;
	public static String calculate_solutions_Collective_valuations;
	public static String calculate_solutions_Criterion;
	public static String calculate_solutions_Expert;
	public static String calculate_solutions_Ideal_solution;
	public static String calculate_solutions_No_ideal_solution;
	public static String calculate_solutions_Type;
	public static String calculate_solutions_Valuation;
	public static String CalculateDistances_Alternative;
	public static String CalculateDistances_Calculate_distances;
	public static String CalculateDistances_Closeness_coefficient;
	public static String CalculateDistances_Criterion;
	public static String CalculateDistances_Ideal_solution;
	public static String CalculateDistances_Ideal_solution_distance;
	public static String CalculateDistances_Negative_distance;
	public static String CalculateDistances_Positive_distance;
	public static String CalculateDistances_Ranking;
	public static String selection_Calculate_weights;
	public static String calculate_weights_Weight;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
