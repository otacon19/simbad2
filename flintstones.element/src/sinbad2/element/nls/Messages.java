package sinbad2.element.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.element.nls.messages"; //$NON-NLS-1$
	public static String MoveCriterionHandler_Move_criterion;
	public static String MoveExpertHandler_Move_expert;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
