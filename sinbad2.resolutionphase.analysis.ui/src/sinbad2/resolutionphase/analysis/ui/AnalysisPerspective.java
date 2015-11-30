package sinbad2.resolutionphase.analysis.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import sinbad2.resolutionphase.analysis.ui.view.MethodSelectorView;

public class AnalysisPerspective implements IPerspectiveFactory {
	
	public static final String ID = "flintstones.resolutionphase.analysis.perspective"; //$NON-NLS-1$

	private MethodSelectorView _methodSelectorView = null;
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(new IPerspectiveListener() {

			@Override
			public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
				if(ID.equals(perspective.getId())) {
					if(_methodSelectorView == null) {
						for(IViewReference viewReference: page.getViewReferences()) {
							if(MethodSelectorView.ID.equals(viewReference.getId())) {
								_methodSelectorView = (MethodSelectorView) viewReference.getView(false);
							}
						}
					}
					_methodSelectorView.setFocus();
				}
			}

			@Override
			public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
			}
		});
		
	}

}
