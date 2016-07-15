package sinbad2.element.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Images {

	private static final String PLUGIN_ID = "sinbad2.element.ui"; //$NON-NLS-1$

	public static final Image Alternative = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/alternative.png").createImage(); //$NON-NLS-1$

	public static final Image Expert = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/expert.png").createImage(); //$NON-NLS-1$

	public static final Image GroupOfExperts = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/group-of-experts.png").createImage(); //$NON-NLS-1$

	public static final Image Criteria = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/criteria.png").createImage(); //$NON-NLS-1$

	public static final Image Criterion = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/criterion.png").createImage(); //$NON-NLS-1$

	public static final Image TypeOfCriterion = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/type-of-criterion.png").createImage(); //$NON-NLS-1$

	public static final Image Cost = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/cost.png").createImage(); //$NON-NLS-1$

	public static final Image Benefit = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/benefit.png").createImage(); //$NON-NLS-1$

	private Images() {
	}
}


