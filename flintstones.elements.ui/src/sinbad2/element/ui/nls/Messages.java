package sinbad2.element.ui.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.element.ui.nls.messages"; //$NON-NLS-1$
	public static String AddExpertHandler_Add_expert;
	public static String AddExpertHandler_Insert_id_expert;
	public static String AddExpertInputDialog_Is_member_of;
	public static String AddExpertInputValidator_Character_colon_not_allowed;
	public static String AddExpertInputValidator_Duplicated_id;
	public static String AddExpertInputValidator_Empty_value_not_allowed;
	public static String ModifyExpertHandler_Insert_expert_id;
	public static String ModifyExpertHandler_Insert_id_expert;
	public static String ModifyExpertHandler_Modify_expert;
	public static String ModifyExpertInputValidator_Character_colon_not_allowed;
	public static String ModifyExpertInputValidator_Duplicated_id;
	public static String ModifyExpertInputValidator_Empty_value_not_allowed;
	public static String RemoveExpertHandler_Remove_expert;
	public static String RemoveExpertsHandler_Remove_multiple_experts;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
