package sinbad2.phasemethod.todim.resolution.ui.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.phasemethod.todim.resolution.ui.nls.messages"; //$NON-NLS-1$
	public static String CalculateRanking_Alternative;
	public static String CalculateRanking_Center_of_Gravity;
	public static String CalculateRanking_Criterion;
	public static String CalculateRanking_Dominance_degree;
	public static String CalculateRanking_Fuzzy_numbers;
	public static String CalculateRanking_Fuzzy_TODIM;
	public static String CalculateRanking_Global_dominance_degree;
	public static String CalculateRanking_Ranking;
	public static String CalculateRanking_Reference_criterion;
	public static String CalculateRanking_Relative_weight;
	public static String CalculateRanking_Weight;
	public static String DMTableContentProvider_Consensus_matrix;
	public static String ProblemInformation_Aggregated_valuation;
	public static String ProblemInformation_Aggregation;
	public static String ProblemInformation_All_experts;
	public static String ProblemInformation_Alternative;
	public static String ProblemInformation_Calculate_distance;
	public static String ProblemInformation_Criterion;
	public static String ProblemInformation_Distance;
	public static String ProblemInformation_Expert;
	public static String ProblemInformation_Expert_valuation;
	public static String ProblemInformation_Threshold;
	public static String DMTableContentProvider_Decision_matrix;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
