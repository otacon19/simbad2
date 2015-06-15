package sinbad2.core.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "sinbad2.core.nls.messages"; //$NON-NLS-1$
	public static String ParameterValidator_Cannot_pass_disorder_values;
	public static String ParameterValidator_Cannot_pass_empty_array;
	public static String ParameterValidator_Cannot_pass_empty_string;
	public static String ParameterValidator_Cannot_pass_illegal_objects;
	public static String ParameterValidator_Cannot_pass_negative_value;
	public static String ParameterValidator_Cannot_pass_null_value;
	public static String ParameterValidator_Cannot_pass_same_element;
	public static String ParameterValidator_Invalid_range;
	public static String ParameterValidator_Invalid_size;
	public static String ParameterValidator_Value_must_be_in_range_1;
	public static String ParameterValidator_Value_must_be_in_range_2;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
