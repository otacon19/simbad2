package sinbad2.valuation.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Images {
	
	private static final String PLUGIN_ID = "sinbad2.valuation.ui"; //$NON-NLS-1$
	
	public static final Image VALUATION = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "icons/valuation.png").createImage(); //$NON-NLS-1$
	
	private Images() {}
	
}
