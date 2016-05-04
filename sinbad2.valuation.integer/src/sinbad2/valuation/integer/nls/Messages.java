package sinbad2.valuation.integer.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.valuation.integer.nls.messages"; //$NON-NLS-1$
	public static String IntegerValuation_Not_BLTS_fuzzy_set;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
