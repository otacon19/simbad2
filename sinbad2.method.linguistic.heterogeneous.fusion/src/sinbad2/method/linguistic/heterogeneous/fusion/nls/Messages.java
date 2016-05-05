package sinbad2.method.linguistic.heterogeneous.fusion.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.method.linguistic.heterogeneous.fusion.nls.messages"; //$NON-NLS-1$
	public static String Fusion_Hesitant_evaluations;
	public static String Fusion_Not_set_all_assignments;
	public static String Fusion_Not_supported_domains;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
