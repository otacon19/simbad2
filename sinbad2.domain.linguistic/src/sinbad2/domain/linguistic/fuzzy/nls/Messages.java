package sinbad2.domain.linguistic.fuzzy.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.domain.linguistic.fuzzy.nls.messages"; //$NON-NLS-1$
	public static String FuzzySet_Inexistent_element;
	public static String LabelSetLinguisticDomain_Duplicated_label_name;
	public static String TrapezoidalFunction_Invalid_element_type;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
