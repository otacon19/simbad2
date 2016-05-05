package sinbad2.phasemethod.aggregation.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.phasemethod.aggregation.nls.messages"; //$NON-NLS-1$
	public static String AggregationPhase_Illegal_element_type;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
