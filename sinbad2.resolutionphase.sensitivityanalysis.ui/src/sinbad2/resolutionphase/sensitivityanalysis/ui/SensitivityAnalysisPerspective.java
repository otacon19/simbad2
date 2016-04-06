package sinbad2.resolutionphase.sensitivityanalysis.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import sinbad2.core.workspace.Workspace;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.domain.ui.view.domain.DomainViewManager;
import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;
import sinbad2.resolutionphase.sensitivityanalysis.ui.ranking.RankingViewManager;

public class SensitivityAnalysisPerspective implements IPerspectiveFactory {
	
	public static final String ID = "flintstones.resolutionphase.sensitivityanalysis.perspective"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(new IPerspectiveListener() {

			DomainViewManager domainViewManager = DomainViewManager.getInstance();
			RankingViewManager rankingViewManager = RankingViewManager.getInstance();

			@Override
			public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {

				boolean sensitivityAnalysisActivated = ID.equals(perspective.getId());

				if (sensitivityAnalysisActivated) {
					DomainsManager manager = DomainsManager.getInstance();
					NumericRealDomain domain = (NumericRealDomain) manager.copyDomain(NumericRealDomain.ID);
					domain.setMinMax(0d, 1d);
					SensitivityAnalysis sa = (SensitivityAnalysis) Workspace.getWorkspace().getElement(SensitivityAnalysis.ID);
					Object ranking = new Object[] { sa.getAlternativesIds(), sa.getAlternativesFinalPreferences() };
					domainViewManager.setContent(domain, ranking);
					rankingViewManager.setContent(sa);
					sa.calculateDecisionMatrix();
				}

			}

			@Override
			public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {}
		});
	}

}
