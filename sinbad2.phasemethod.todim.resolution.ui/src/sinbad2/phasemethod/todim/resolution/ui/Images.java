package sinbad2.phasemethod.todim.resolution.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Images {

	private static final String PLUGIN_ID = "sinbad2.phasemethod.todim.resolution.ui"; //$NON-NLS-1$
	 
	public static final Image Excel = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "icon/excel_23x23.png").createImage(); //$NON-NLS-1$
	public static final Image File = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "icon/load_file_23x23.png").createImage(); //$NON-NLS-1$
	
	private Images() {}
	
}
