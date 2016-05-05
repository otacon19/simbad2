package sinbad2.method.linguistic.unbalanced.methodology.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.method.linguistic.unbalanced.methodology.nls.messages"; //$NON-NLS-1$
	public static String Unbalanced_Evaluations_in_different_domains;
	public static String Unbalanced_Evaluations_in_not_unbalanced_domain;
	public static String Unbalanced_Hesitant_evaluations;
	public static String Unbalanced_Not_set_all_assignments;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
