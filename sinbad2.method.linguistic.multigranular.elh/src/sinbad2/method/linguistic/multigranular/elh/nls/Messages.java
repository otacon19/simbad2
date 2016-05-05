package sinbad2.method.linguistic.multigranular.elh.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.method.linguistic.multigranular.elh.nls.messages"; //$NON-NLS-1$
	public static String ELH_Evaluations_in_different_domains_with_the_same_cardinality;
	public static String ELH_Evaluations_in_not_BLTS_domains;
	public static String ELH_Evaluations_in_not_linguistic_domain;
	public static String ELH_Hesitant_evaluations;
	public static String ELH_Not_set_all_assignments;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
