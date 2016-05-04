package sinbad2.valutation.hesitant.ui.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.valutation.hesitant.ui.nls.messages"; //$NON-NLS-1$
	public static String ValuationPanelHesitant_And;
	public static String ValuationPanelHesitant_Between;
	public static String ValuationPanelHesitant_Binary;
	public static String ValuationPanelHesitant_Composite;
	public static String ValuationPanelHesitant_Hesitant_evaluation;
	public static String ValuationPanelHesitant_Primary;
	public static String ValuationPanelHesitant_Unary;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
