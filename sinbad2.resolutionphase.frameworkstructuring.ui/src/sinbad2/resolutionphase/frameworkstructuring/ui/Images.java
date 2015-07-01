package sinbad2.resolutionphase.frameworkstructuring.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Images {
	
	private static final String PLUGIN_ID = "sinbad2.resolutionphase.frameworkstructuring.ui";
											 
	public static final Image Apply = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "icons/execute.png").createImage();
	
	private Images() {}
}
