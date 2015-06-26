package sinbad2.valuation.linguistic.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.valuation.linguistic.nls.messages"; //$NON-NLS-1$
	public static String LinguisticValuation_Different_domains;
	public static String LinguisticValuation_Not_contains_in_domain;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
