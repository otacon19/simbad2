package sinbad2.valuation.ui.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.valuation.ui.nls.messages"; //$NON-NLS-1$
	public static String ValuationPanel_Evaluate;
	public static String ValuationPanel_Remove;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
