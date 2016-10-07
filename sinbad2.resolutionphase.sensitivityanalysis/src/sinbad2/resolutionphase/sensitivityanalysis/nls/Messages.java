package sinbad2.resolutionphase.sensitivityanalysis.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.resolutionphase.sensitivityanalysis.nls.messages"; //$NON-NLS-1$
	public static String SensitivityAnalysis_Multi_criteria_decision_analysis;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
