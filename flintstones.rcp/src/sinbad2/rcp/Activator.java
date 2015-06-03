package sinbad2.rcp;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import sinbad2.core.workspace.Workspace;
import sinbad2.resolutionscheme.ui.ResolutionSchemeUI;
import sinbad2.resolutionscheme.ui.ResolutionSchemesUIManager;
import sinbad2.valuation.ui.ValuationUIsManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	
	private static ResolutionSchemesUIManager _resolutionSchemesUIManager = ResolutionSchemesUIManager.getInstance();
	
	// The plug-in ID
	public static final String PLUGIN_ID = "flintstones.rcp"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
		ResolutionSchemeUI resolutionSchemeUI = null;
		
		_resolutionSchemesUIManager = ResolutionSchemesUIManager.getInstance();
		String[] resolutionSchemesUI = _resolutionSchemesUIManager.getIdsRegisters();
		
		resolutionSchemeUI = _resolutionSchemesUIManager.getUI(resolutionSchemesUI[0]);
		Workspace workspace = Workspace.getWorkspace();
		workspace.setContent(resolutionSchemeUI.getResolutionScheme().getImplementation());
		
	    _resolutionSchemesUIManager.activate(resolutionSchemeUI.getId());
	    
	    //TODO temporal, para cargar las valoraciones
	    ValuationUIsManager.getInstance();
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
