package sinbad2.resolutionphase.sensitivityanalysis.ui.decisionmaking;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import sinbad2.core.workspace.Workspace;
import sinbad2.domain.ui.view.domain.DomainViewManager;
import sinbad2.resolutionphase.sensitivityanalysis.ISensitivityAnalysisChangeListener;
import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;
import sinbad2.resolutionphase.sensitivityanalysis.ui.ranking.RankingViewManager;

public class DecisionMakingView extends ViewPart implements ISensitivityAnalysisChangeListener {
	
	public static final String ID = "flintstones.resolutionphase.sensitivityanalysis.ui.views.decisionmaking"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.resolutionphase.sensitivityanalysis.ui.views.decisionmaking.decisionmaking_view"; //$NON-NLS-1$
	
	private SensitivityAnalysis _sensitivityAnalysis;
	
	private DMTable _dmTable = null;
	private Composite _container;

	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);
	
	@Override
	public void createPartControl(Composite parent) {	
		_container = parent;
		
		_sensitivityAnalysis = (SensitivityAnalysis) Workspace.getWorkspace().getElement(SensitivityAnalysis.ID);
		_sensitivityAnalysis.registerSensitivityAnalysisChangeListener(this);
		
		hookFocusListener();
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_container.setLayoutData(gridData);
		GridLayout layout = new GridLayout(1, true);
		_container.setLayout(layout);
		
		initDMTable();
	}

	private void initDMTable() {
		_dmTable = new DMTable(_container, _sensitivityAnalysis);
		_dmTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		refreshDMTable();
	}
	
	private void refreshDMTable() {
		_dmTable.setModel(_sensitivityAnalysis.getAlternativesIds(), _sensitivityAnalysis.getCriteriaIds(), _sensitivityAnalysis.getDecisionMatrix());
	}

	private void disposeDMTable() {
		if (_dmTable != null) {
			if (!_dmTable.isDisposed()) {
				_dmTable.dispose();
			}
		}
	}

	@Override
	public void setFocus() {
		_container.setFocus();
	}
	
	@Override
	public void dispose() {
		_sensitivityAnalysis.unregisterSensitivityAnalysisChangeListener(this);
		disposeDMTable();
		super.dispose();
	}

	private void hookFocusListener() {
		_container.addFocusListener(new FocusListener() {

			private IContextActivation activation = null;

			@Override
			public void focusLost(FocusEvent e) {
				_contextService.deactivateContext(activation);
			}

			@Override
			public void focusGained(FocusEvent e) {
				activation = _contextService.activateContext(CONTEXT_ID);
			}
		});
	}

	@Override
	public void notifySensitivityAnalysisChange() {
		refreshDMTable();
		RankingViewManager.getInstance().setContent(_sensitivityAnalysis.getRanking());
		DomainViewManager.getInstance().setRanking(new Object[] { _sensitivityAnalysis.getAlternativesIds(), _sensitivityAnalysis.getAlternativesFinalPreferences() });
	}
}

