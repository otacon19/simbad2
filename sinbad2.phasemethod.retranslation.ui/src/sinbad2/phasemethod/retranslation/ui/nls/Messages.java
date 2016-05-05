package sinbad2.phasemethod.retranslation.ui.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.phasemethod.retranslation.ui.nls.messages"; //$NON-NLS-1$
	public static String Retranslation_Description;
	public static String Retranslation_Name;
	public static String Retranslation_Retranslation;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
