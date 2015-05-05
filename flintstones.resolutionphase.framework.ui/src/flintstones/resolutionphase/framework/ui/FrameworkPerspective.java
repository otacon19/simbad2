package flintstones.resolutionphase.framework.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import flintstones.element.ui.view.experts.ExpertsView;

public class FrameworkPerspective implements IPerspectiveFactory {
	
	public static final String ID = "flintstones.resolutionphase.framework.ui"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		
		layout.addView(ExpertsView.getId(), IPageLayout.LEFT, 1.0f, layout.getEditorArea());
	}

}
