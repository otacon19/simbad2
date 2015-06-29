package sinbad2.domain.numeric.real.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.domain.numeric.real.nls.messages"; //$NON-NLS-1$
	public static String NumericRealDomain_Without_range;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
