package sinbad2.resolutionphase.rating.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import sinbad2.resolutionphase.rating.ui.view.RatingView;

public class RatingPerspective implements IPerspectiveFactory {
	
	public static final String ID = "flintstones.resolutionphase.rating.perspective"; //$NON-NLS-1$
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(new IPerspectiveListener() {

			@Override
			public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
				
				if(ID.equals(perspective.getId())) {
					IViewReference viewReferences[] = page.getViewReferences();
					for(int i = 0; i < viewReferences.length; i++) {
						if(RatingView.ID.equals(viewReferences[i].getId())) {
							IViewPart view = viewReferences[i].getView(false);
							((RatingView) view).resetRating(false);
						}
					}
				}
			}
			
			@Override
			public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {}

		});
	}
}
