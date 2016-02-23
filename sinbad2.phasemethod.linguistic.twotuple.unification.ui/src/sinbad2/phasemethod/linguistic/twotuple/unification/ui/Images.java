package sinbad2.phasemethod.linguistic.twotuple.unification.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Images {

	private static final String PLUGIN_ID = "sinbad2.phasemethod.linguistic.twotuple.unification.ui"; //$NON-NLS-1$

	public static final Image Alternative = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/alternative.png").createImage(); //$NON-NLS-1$

	public static final Image GroupOfExperts = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/group-of-experts.png").createImage(); //$NON-NLS-1$

	public static final Image Criteria = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/criteria.png").createImage(); //$NON-NLS-1$
	
	public static final Image Valuation = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/valuation.png").createImage(); //$NON-NLS-1$
	
	public static final Image Domain = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/domain.png").createImage(); //$NON-NLS-1$

	private Images() {
	}
}
