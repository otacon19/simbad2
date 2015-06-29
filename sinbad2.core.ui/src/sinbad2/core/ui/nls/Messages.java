package sinbad2.core.ui.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.core.ui.nls.messages"; //$NON-NLS-1$
	public static String ReadHandler_File_name_not_valid;
	public static String ReadHandler_File_open_fail;
	public static String ReadHandler_File_open_fail_permissions;
	public static String ReadHandler_Invalid_file_name;
	public static String ReadHandler_Invalid_flintstones_file;
	public static String SaveAsHandler_File_name_not_valid;
	public static String SaveAsHandler_File_not_saving;
	public static String SaveAsHandler_File_not_saving_permissions;
	public static String SaveAsHandler_File_not_saving_wrong;
	public static String SaveAsHandler_Invalid_file_name;
	public static String SaveHandler_File_name_not_valid;
	public static String SaveHandler_File_not_saving;
	public static String SaveHandler_File_not_saving_permissions;
	public static String SaveHandler_File_not_saving_wrong;
	public static String SaveHandler_Invalid_file_name;
	public static String TitleUpdater_File_removed;
	public static String WorkspaceSourceProvider_Save_current_changes;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
