package sinbad2.domain.linguistic.unbalanced.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.domain.linguistic.unbalanced.nls.messages"; //$NON-NLS-1$
	public static String Unbalanced_Dirty_label;
	public static String Unbalanced_Invalid_cardinality;
	public static String Unbalanced_Invalid_initial_domain;
	public static String Unbalanced_Invalid_sl;
	public static String Unbalanced_Invalid_sl_density;
	public static String Unbalanced_Invalid_sr;
	public static String Unbalanced_Invalid_sr_density;
	public static String Unbalanced_Pair_cardinality;
	public static String Unbalanced_Pair_initial_domain;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
