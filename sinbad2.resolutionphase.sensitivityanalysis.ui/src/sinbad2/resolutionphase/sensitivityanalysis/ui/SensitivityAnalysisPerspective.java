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
import sinbad2.domain.valuations.DomainsValuationsManager;
import sinbad2.resolutionphase.sensitivityanalysis.MockModel;
import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;
import sinbad2.resolutionphase.sensitivityanalysis.ui.ranking.RankingViewManager;
import sinbad2.valuation.real.RealValuation;

public class SensitivityAnalysisPerspective implements IPerspectiveFactory {
	
	public static final String ID = "flintstones.resolutionphase.sensitivityanalysis.perspective"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(new IPerspectiveListener() {

			DomainViewManager domainViewManager = DomainViewManager.getInstance();
			RankingViewManager rankingViewManager = RankingViewManager.getInstance();
			DomainsValuationsManager dvm = DomainsValuationsManager.getInstance();

			@Override
			public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {

				boolean sensitivityAnalysisActivated = ID.equals(perspective.getId());

				if (sensitivityAnalysisActivated) {
					DomainsManager manager = DomainsManager.getInstance();
					NumericRealDomain domain = (NumericRealDomain) manager.copyDomain(NumericRealDomain.ID);
					domain.setMinMax(0d, 1d);
					dvm.addSupportedValuationForDomain(RealValuation.ID, domain.getId());
					MockModel model = new MockModel();
					SensitivityAnalysis sa = (SensitivityAnalysis) Workspace.getWorkspace().getElement(SensitivityAnalysis.ID);
					sa.setModel(model);
					Object ranking = new Object[] { model._alternatives, model._alternativesFinalPreferences };
					domainViewManager.setContent(domain, ranking);
					rankingViewManager.setContent(model);
				}

			}

			@Override
			public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {}
		});
	}

}
