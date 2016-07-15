package sinbad2.rcp.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.rcp.nls.messages"; //$NON-NLS-1$
	public static String ApplicationWorkbenchWindowAdvisor_application_title;
	public static String ApplicationWorkbenchWindowAdvisor_More_than_one_cBanner;
	public static String ApplicationWorkbenchWindowAdvisor_No_CBanner;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
