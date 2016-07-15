package sinbad2.resolutionphase.framework.ui.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.resolutionphase.framework.ui.nls.messages"; //$NON-NLS-1$
	public static String FrameworkPreferencesPage_description;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
