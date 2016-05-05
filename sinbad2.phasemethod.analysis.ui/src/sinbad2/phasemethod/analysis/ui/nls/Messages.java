package sinbad2.phasemethod.analysis.ui.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.phasemethod.analysis.ui.nls.messages"; //$NON-NLS-1$
	public static String Analysis_Alternative;
	public static String Analysis_Alternatives;
	public static String Analysis_Analysis;
	public static String Analysis_Criteria;
	public static String Analysis_Evaluation;
	public static String Analysis_Experts;
	public static String EvaluationColumnLabelProvider_Not_evaluate;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
