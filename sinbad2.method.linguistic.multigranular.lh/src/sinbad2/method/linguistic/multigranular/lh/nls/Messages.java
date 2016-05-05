package sinbad2.method.linguistic.multigranular.lh.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.method.linguistic.multigranular.lh.nls.messages"; //$NON-NLS-1$
	public static String LH_Evaluations_in_different_domains_with_the_same_cardinality;
	public static String LH_Evaluations_in_not_BLTS_domains;
	public static String LH_Evaluations_in_not_linguistic_domain;
	public static String LH_Hesitant_evaluations;
	public static String LH_Impossible_to_build_linguistic_hierarchy_taking_the_domains_used;
	public static String LH_Not_set_all_assignments;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
