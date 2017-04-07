package sinbad2.phasemethod.emergency.computeweights.ui.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.phasemethod.emergency.computeweights.ui.nls.messages"; //$NON-NLS-1$
	public static String ComputeWeights_Alternative;
	public static String ComputeWeights_Compute_weights;
	public static String ComputeWeights_Cost_Benefit;
	public static String ComputeWeights_Criterion;
	public static String ComputeWeights_Weight;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
