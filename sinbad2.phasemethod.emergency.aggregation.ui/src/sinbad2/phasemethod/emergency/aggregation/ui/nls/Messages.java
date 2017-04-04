package sinbad2.phasemethod.emergency.aggregation.ui.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.phasemethod.emergency.aggregation.ui.nls.messages"; //$NON-NLS-1$
	public static String AggregationProcess_Aggregation_process;
	public static String AggregationProcess_Criterion;
	public static String AggregationProcess_Expert;
	public static String AggregationProcess_GRP;
	public static String AggregationProcess_Weight;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
