package sinbad2.resolutionphase.framework.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class FrameworkPerspective implements IPerspectiveFactory {
	
	public static final String ID = "flintstones.resolutionphase.framework.perspective"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
	}

}
