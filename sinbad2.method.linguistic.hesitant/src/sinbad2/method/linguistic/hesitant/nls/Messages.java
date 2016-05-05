package sinbad2.method.linguistic.hesitant.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.method.linguistic.hesitant.nls.messages"; //$NON-NLS-1$
	public static String Hesitant_Multiple_domains;
	public static String Hesitant_Not_hesitant_evaluations;
	public static String Hesitant_Not_supported_domains;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
