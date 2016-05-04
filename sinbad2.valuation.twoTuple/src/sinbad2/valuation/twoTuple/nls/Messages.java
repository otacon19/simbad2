package sinbad2.valuation.twoTuple.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.valuation.twoTuple.nls.messages"; //$NON-NLS-1$
	public static String TwoTuple_Different_domains;
	public static String TwoTuple_In;
	public static String TwoTuple_Invalid_alpha_value;
	public static String TwoTuple_Not_BLTS_fuzzy_set;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
