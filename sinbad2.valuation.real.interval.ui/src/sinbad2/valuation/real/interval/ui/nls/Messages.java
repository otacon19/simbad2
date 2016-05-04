package sinbad2.valuation.real.interval.ui.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.valuation.real.interval.ui.nls.messages"; //$NON-NLS-1$
	public static String ValuationPanelRealInterval_Interval_evaluation;
	public static String ValuationPanelRealInterval_Lower_limit;
	public static String ValuationPanelRealInterval_Upper_limit;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
