package sinbad2.resolutionphase.frameworkstructuring.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import sinbad2.element.ui.view.elements.ElementView;
import sinbad2.resolutionphase.frameworkstructuring.ui.view.elementassignments.ElementAssignmentsView;

public class FrameworkStructuringPerspective implements IPerspectiveFactory {
	
	public static final String ID = "flintstones.resolutionphase.frameworkstructuring.perspective";
	
	private ElementView _elementView;
	private ElementAssignmentsView _elementAssignmentsView = null;
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(new IPerspectiveListener() {
			
			@Override
			public void perspectiveChanged(IWorkbenchPage page,IPerspectiveDescriptor perspective, String changeId) {}
			
			@Override
			public void perspectiveActivated(IWorkbenchPage page,IPerspectiveDescriptor perspective) {
				
				if(ID.equals(perspective.getId())) {
					if(_elementView == null) {
						for(IViewReference viewReference: page.getViewReferences()) {
							if(ElementView.ID.equals(viewReference.getId())) {
								_elementView = (ElementView) viewReference.getView(false);
							}
						}
					}
					if(_elementAssignmentsView == null) {
						for(IViewReference viewReference: page.getViewReferences()) {
							if(ElementAssignmentsView.ID.equals(viewReference.getId())) {
								_elementAssignmentsView = (ElementAssignmentsView) viewReference.getView(false);
							}
						}
					}

					_elementView.setFocus();
					_elementView.selectFirst();
					_elementAssignmentsView.setFocus();
				}
			}
		});
		
	}

}
