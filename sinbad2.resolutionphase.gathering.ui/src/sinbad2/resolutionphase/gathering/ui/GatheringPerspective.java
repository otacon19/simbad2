package sinbad2.resolutionphase.gathering.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import sinbad2.element.ui.view.elements.ElementView;
import sinbad2.valuation.valuationset.ui.view.ElementValuationsView;

public class GatheringPerspective implements IPerspectiveFactory {
	
	public static final String ID = "flintstones.resolutionphase.gathering.perspective"; //$NON-NLS-1$

	private ElementView _elementView = null;
	private ElementValuationsView _elementValuationsView = null;
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(new IPerspectiveListener() {

					@Override
					public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {

						if (ID.equals(perspective
								.getId())) {
							if (_elementView == null) {
								for (IViewReference viewReference : page.getViewReferences()) {
									if (ElementView.ID.equals(viewReference
											.getId())) {
										_elementView = (ElementView) viewReference
												.getView(false);
									}
								}
							}
							
							if (_elementValuationsView == null) {
								for (IViewReference viewReference : page
										.getViewReferences()) {
									if (ElementValuationsView.ID.equals(viewReference
											.getId())) {
										_elementValuationsView = (ElementValuationsView) viewReference
												.getView(false);
									}
								}
							}

							_elementView.setFocus();
							_elementView.selectFirst();
							_elementValuationsView.setFocus();
						}
					}

					@Override
					public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {}

				});
	}

}
