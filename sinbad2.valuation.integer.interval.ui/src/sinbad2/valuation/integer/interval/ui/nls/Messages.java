package sinbad2.valuation.integer.interval.ui.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.valuation.integer.interval.ui.nls.messages"; //$NON-NLS-1$
	public static String ValuationPanelIntegerInterval_Interval_evaluation;
	public static String ValuationPanelIntegerInterval_Lower_limit;
	public static String ValuationPanelIntegerInterval_Upper_limit;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
