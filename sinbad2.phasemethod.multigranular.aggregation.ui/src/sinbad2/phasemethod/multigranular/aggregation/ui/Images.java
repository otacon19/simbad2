package sinbad2.phasemethod.multigranular.aggregation.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Images {

	private static final String PLUGIN_ID = "sinbad2.phasemethod.multigranular.aggregation.ui"; //$NON-NLS-1$
	
	public static final Image GroupOfExperts = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/group-of-experts.png").createImage(); //$NON-NLS-1$

	public static final Image Edit = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/edit_22.png").createImage(); //$NON-NLS-1$

	public static final Image AggregationOperator = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/aggregation_operator_22.png").createImage(); //$NON-NLS-1$

	public static final Image Criterion = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/criterion.png").createImage(); //$NON-NLS-1$

	public static final Image ChangeOrder = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/change_order_22.png").createImage(); //$NON-NLS-1$
	
	private Images() {
	}
}