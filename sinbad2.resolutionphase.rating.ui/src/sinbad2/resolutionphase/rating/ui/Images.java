package sinbad2.resolutionphase.rating.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Images {
	
	private static final String PLUGIN_ID = "sinbad2.resolutionphase.rating.ui"; //$NON-NLS-1$
	 
	public static final Image category = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "icons/category_obj.gif").createImage(); //$NON-NLS-1$
	
	public static final Image signed_yes = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "icons/signed_yes.gif").createImage(); //$NON-NLS-1$
	
	public static final Image signed_no = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "icons/signed_no.gif").createImage(); //$NON-NLS-1$
	
	private Images() {}
	
}
