package sinbad2.domain.ui.handler.add;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.domain.ui.nls.messages"; //$NON-NLS-1$
	public static String AddDomainHandler_Add_domain;
	public static String AddDomainHandler_Domain;
	public static String AddDomainHandler_Domains;
	public static String AddDomainHandler_New_domain;
	public static String AddDomainHandler_To_assess_valuations;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
