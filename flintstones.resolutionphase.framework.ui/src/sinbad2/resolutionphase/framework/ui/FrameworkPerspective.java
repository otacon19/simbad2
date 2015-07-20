package sinbad2.resolutionphase.framework.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;

import sinbad2.domain.Domain;
import sinbad2.domain.ui.view.domain.DomainView;
import sinbad2.domain.ui.view.domain.DomainViewManager;
import sinbad2.domain.ui.view.domains.DomainsView;
import sinbad2.element.ui.sourceprovider.ElementSourceProvider;

public class FrameworkPerspective implements IPerspectiveFactory {

	public static final String ID = "flintstones.resolutionphase.framework.perspective"; //$NON-NLS-1$

	private DomainViewManager _domainViewManager = null;
	private DomainView _domainView = null;
	private IWorkbenchPage _page = null;

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);

		if(_domainViewManager == null) {
			_domainViewManager = DomainViewManager.getInstance();
		}

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(new IPerspectiveListener() {

			ISourceProviderService sourceProviderService = null;
			ElementSourceProvider elementSourceProvider = null;
			DomainsView domainsView = null;

			@Override
			public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
				
				if(_page == null) {
					_page = page;
				}

				if(elementSourceProvider == null) {
					if(sourceProviderService == null) {
						sourceProviderService = (ISourceProviderService) page.getWorkbenchWindow().getService(ISourceProviderService.class);
					}
					elementSourceProvider = (ElementSourceProvider) sourceProviderService.getSourceProvider(ElementSourceProvider.CAN_BE_MODIFIED_STATE);
				}

				boolean frameworkActivated = ID.equals(perspective.getId());
				elementSourceProvider.changeCanBeModifiedState(frameworkActivated);

				if(frameworkActivated) {
					Domain selectedDomain;
					if (domainsView == null) {
						for(IViewReference viewReference : page.getViewReferences()) {
							if(DomainsView.ID.equals(viewReference.getId())) {
								domainsView = (DomainsView) viewReference.getView(false);
							}
						}
					}
					selectedDomain = (domainsView != null) ? domainsView.getSelectedDomain() : null;

					_domainViewManager.setContent(selectedDomain, null);

					if(_domainView == null) {
						for(IViewReference viewReference : page.getViewReferences()) {
							if(DomainView.ID.equals(viewReference.getId())) {
								_domainView = (DomainView) viewReference.getView(false);
							}
						}
					}

					if (selectedDomain == null) {
						if (_domainView != null) {
							page.hideView(_domainView);
							_domainView = null;
						}
					} else {
						try {
							_domainView = (DomainView) page.showView(DomainView.ID);
							_domainViewManager.setContent(selectedDomain, null);
						} catch (PartInitException e) {
							e.printStackTrace();
						}
					}
				}

			}

			@Override
			public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {}
		});

	}
}

