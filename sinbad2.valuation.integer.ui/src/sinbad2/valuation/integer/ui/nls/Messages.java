package sinbad2.valuation.integer.ui.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.valuation.integer.ui.nls.messages"; //$NON-NLS-1$
	public static String ValuationPanelInteger_Integer_evaluation;
	public static String ValuationPanelInteger_Value;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
