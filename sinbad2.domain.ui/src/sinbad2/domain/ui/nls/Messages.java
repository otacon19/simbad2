package sinbad2.domain.ui.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.domain.ui.nls.messages"; //$NON-NLS-1$
	public static String AddDomainHandler_Add_domain;
	public static String AddDomainHandler_Domain;
	public static String AddDomainHandler_Domains;
	public static String AddDomainHandler_New_domain;
	public static String AddDomainHandler_To_assess_valuations;
	public static String DomainIndexView_Index;
	public static String DomainsView_Description;
	public static String DomainsView_Valuation;
	public static String ModifyDomainHandler_Modify_domain;
	public static String RemoveDomainHandler_Remove_domain;
	public static String RemoveMultipleDomainsHandler_Remove_multiple_domains;
	public static String SelectModifyDomainDialog_Build_mode;
	public static String SelectNewDomainDialog_Build_mode;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
