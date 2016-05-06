package sinbad2.aggregationoperator.maxMin.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.aggregationoperator.maxMin.nls.messages"; //$NON-NLS-1$
	public static String HesitantOperator_Invalid_domain;
	public static String MaxMin_Not_supported_type;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
