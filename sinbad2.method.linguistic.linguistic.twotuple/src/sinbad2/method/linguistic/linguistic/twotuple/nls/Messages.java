package sinbad2.method.linguistic.linguistic.twotuple.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.method.linguistic.linguistic.twotuple.nls.messages"; //$NON-NLS-1$
	public static String TwoTuple_Evaluations_in_different_domains;
	public static String TwoTuple_Evaluations_in_not_linguistic_domain;
	public static String TwoTuple_Evaluations_in_unbalanced_domain;
	public static String TwoTuple_Hesitant_evaluations;
	public static String TwoTuple_Not_set_all_assignments;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
