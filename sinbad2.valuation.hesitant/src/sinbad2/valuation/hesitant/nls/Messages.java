package sinbad2.valuation.hesitant.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.valuation.hesitant.nls.messages"; //$NON-NLS-1$
	public static String HesitantValuation_and;
	public static String HesitantValuation_And;
	public static String HesitantValuation_between;
	public static String HesitantValuation_Between;
	public static String HesitantValuation_Different_domains;
	public static String HesitantValuation_In;
	public static String HesitantValuation_Label_not_contains_in_domain;
	public static String HesitantValuation_Lower_term_not_contains_in_domain;
	public static String HesitantValuation_Upper_term_is_bigger_than_lower_term;
	public static String HesitantValuation_Upper_term_not_contains_in_domain;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
