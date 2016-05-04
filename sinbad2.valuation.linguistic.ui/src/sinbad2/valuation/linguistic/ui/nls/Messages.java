package sinbad2.valuation.linguistic.ui.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.valuation.linguistic.ui.nls.messages"; //$NON-NLS-1$
	public static String ValuationPanelLinguistic_Linguistic_evaluation;
	public static String ValuationPanelLinguistic_Value;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
