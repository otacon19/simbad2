package sinbad2.valuation.unifiedValuation.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.valuation.unifiedValuation.nls.messages"; //$NON-NLS-1$
	public static String UnifiedValuation_Different_domains;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
