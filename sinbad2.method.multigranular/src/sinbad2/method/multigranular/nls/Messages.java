package sinbad2.method.multigranular.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.method.multigranular.nls.messages"; //$NON-NLS-1$
	public static String Fusion_Evaluations_in_not_BLTS_domain;
	public static String Fusion_Evaluations_in_not_linguistic_domain;
	public static String Fusion_Hesitant_evaluations;
	public static String Fusion_Not_set_all_assignments;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
