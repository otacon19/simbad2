package sinbad2.phasemethod.emergency.computinggainslosses.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Images {

	private static final String PLUGIN_ID = "sinbad2.phasemethod.emergency.computinggainslosses.ui"; //$NON-NLS-1$

	public static final Image Criteria = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/criteria.png").createImage(); //$NON-NLS-1$
	
	public static final Image Criterion = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/criterion.png").createImage(); //$NON-NLS-1$

	public static final Image TypeOfCriterion = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/type-of-criterion.png").createImage(); //$NON-NLS-1$
	
	public static final Image Cost = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "icons/cost.png") //$NON-NLS-1$
			.createImage();

	public static final Image Benefit = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "icons/benefit.png") //$NON-NLS-1$
			.createImage();

	private Images() {
	}

}
