package sinbad2.method.linguistic.hflts.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.method.linguistic.hflts.nls.messages"; //$NON-NLS-1$
	public static String HFLTS_Multiple_domains;
	public static String HFLTS_Multiple_experts;
	public static String HFLTS_Not_hesitant_evaluations;
	public static String HFLTS_Not_supported_domains;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
