package sinbad2.phasemethod.unbalanced.methodology.unification.ui.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.phasemethod.unbalanced.methodology.unification.ui.nls.messages"; //$NON-NLS-1$
	public static String CalculateRepresentation_Calculate_representation;
	public static String GenerateLH_Empty_value;
	public static String GenerateLH_Generate_LH;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
