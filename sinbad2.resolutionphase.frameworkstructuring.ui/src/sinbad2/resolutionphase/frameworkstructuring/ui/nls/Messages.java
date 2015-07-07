package sinbad2.resolutionphase.frameworkstructuring.ui.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.resolutionphase.frameworkstructuring.ui.nls.messages"; //$NON-NLS-1$
	public static String AssignmentsProviderView_All;
	public static String AssignmentsProviderView_Alternative;
	public static String AssignmentsProviderView_Apply;
	public static String AssignmentsProviderView_Criterion;
	public static String AssignmentsProviderView_Domain;
	public static String AssignmentsProviderView_Expert;
	public static String ElementAssignmentsView_Alternative;
	public static String ElementAssignmentsView_Criterion;
	public static String ElementAssignmentsView_Expert;
	public static String FrameworkStructuringPreferencesPage_Framework_structuring_preferences;
	public static String FrameworkStructuringPreferencesPage_In_alternative_domain_assignments_table_show_experts_in_row;
	public static String FrameworkStructuringPreferencesPage_In_criterion_domain_assignments_table_show_alternatives_in_rows;
	public static String FrameworkStructuringPreferencesPage_In_expert_domain_assignments_table_in_rows;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
