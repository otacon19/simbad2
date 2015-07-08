package sinbad2.valuation.valuationset.ui.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.valuation.valuationset.ui.nls.messages"; //$NON-NLS-1$
	public static String ElementValuationsView_Alternative;
	public static String ElementValuationsView_Criterion;
	public static String ElementValuationsView_Expert;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
