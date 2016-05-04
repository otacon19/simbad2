package sinbad2.valuation.real.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.valuation.real.nls.messages"; //$NON-NLS-1$
	public static String RealValuation_Not_BLTS_fuzzy_set;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
