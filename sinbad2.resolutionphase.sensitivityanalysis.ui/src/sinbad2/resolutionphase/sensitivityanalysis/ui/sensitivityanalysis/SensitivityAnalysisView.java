package sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import sinbad2.core.workspace.Workspace;
import sinbad2.resolutionphase.sensitivityanalysis.ISensitivityAnalysisChangeListener;
import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;

public class SensitivityAnalysisView extends ViewPart implements ISensitivityAnalysisChangeListener {
	
	public static final String ID = "flintstones.resolutionphase.sensitivityanalysis.ui.views.sensitivityanalysis"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.resolutionphase.sensitivityanalysis.ui.views.sensitivityanalysis.sensitivityanalysis_view"; //$NON-NLS-1$

	private SATable _saTable = null;
	private Composite _container;
	private SensitivityAnalysis _sensitivityAnalysis = null;

	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);

	@Override
	public void createPartControl(Composite parent) {
		_container = parent;
		_sensitivityAnalysis = (SensitivityAnalysis) Workspace.getWorkspace().getElement(SensitivityAnalysis.ID);
		_sensitivityAnalysis.registerSensitivityAnalysisChangeListener(this);
		initSATable();
		hookFocusListener();

		getSite().setSelectionProvider(_saTable);
	}
	
	private void initSATable() {
		disposeSATable();
		_saTable = new SATable(_container);
		_saTable.setModel(_sensitivityAnalysis.getAlternativesIds(), _sensitivityAnalysis.getCriteriaIds(), _sensitivityAnalysis.getMinimumAbsoluteChangeInCriteriaWeights(), _sensitivityAnalysis.getDecisionMatrix(), _sensitivityAnalysis.getWeights());
	}
	
	private void disposeSATable() {
		if (_saTable != null) {
			if (!_saTable.isDisposed()) {
				_saTable.dispose();
			}
		}
	}

	@Override
	public void dispose() {
		_sensitivityAnalysis.unregisterSensitivityAnalysisChangeListener(this);
		disposeSATable();
		super.dispose();
	}
	
	@Override
	public void setFocus() {
		_container.setFocus();
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
		initSATable();
	}


}
