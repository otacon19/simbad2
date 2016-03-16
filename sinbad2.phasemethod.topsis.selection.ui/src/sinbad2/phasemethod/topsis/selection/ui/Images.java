package sinbad2.phasemethod.topsis.selection.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Images {

	private static final String PLUGIN_ID = "sinbad2.phasemethod.topsis.selection.ui"; //$NON-NLS-1$

	public static final Image Cost = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/cost.png").createImage(); //$NON-NLS-1$

	public static final Image Benefit = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/benefit.png").createImage(); //$NON-NLS-1$

	private Images() {
	}
}


