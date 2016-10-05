package sinbad2.resolutionphase.sensitivityanalysis.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import sinbad2.core.workspace.Workspace;
import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;
import sinbad2.resolutionphase.sensitivityanalysis.ui.analysis.AnalysisView;
public class SensitivityAnalysisPerspective implements IPerspectiveFactory {
	
	public static final String ID = "flintstones.resolutionphase.sensitivityanalysis.perspective"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(new IPerspectiveListener() {

			@Override
			public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {		
				
				boolean sensitivityAnalysisActivated = ID.equals(perspective.getId());

				if (sensitivityAnalysisActivated) {
					
					IViewPart view = null;
					IViewReference viewReferences[] = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
					for (int i = 0; i < viewReferences.length; i++) {
						if (AnalysisView.ID.equals(viewReferences[i].getId())) {
							view = viewReferences[i].getView(false);
						}
					}
					
					((AnalysisView) view).clear();
					
					SensitivityAnalysis sa = (SensitivityAnalysis) Workspace.getWorkspace().getElement(SensitivityAnalysis.ID);
					sa.calculateDecisionMatrix(null);
				}
			}

			@Override
			public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
			}
		});
	}
}
