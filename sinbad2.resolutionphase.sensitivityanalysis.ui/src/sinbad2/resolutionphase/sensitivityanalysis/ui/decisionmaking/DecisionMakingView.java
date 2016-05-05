package sinbad2.resolutionphase.sensitivityanalysis.ui.decisionmaking;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import sinbad2.core.workspace.Workspace;
import sinbad2.domain.ui.view.domain.DomainViewManager;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.resolutionphase.sensitivityanalysis.ISensitivityAnalysisChangeListener;
import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;
import sinbad2.resolutionphase.sensitivityanalysis.ui.decisionmaking.dialog.WeightsDialog;
import sinbad2.resolutionphase.sensitivityanalysis.ui.nls.Messages;
import sinbad2.resolutionphase.sensitivityanalysis.ui.ranking.RankingView;
import sinbad2.resolutionphase.sensitivityanalysis.ui.ranking.RankingViewManager;

public class DecisionMakingView extends ViewPart implements ISensitivityAnalysisChangeListener {

	public static final String ID = "flintstones.resolutionphase.sensitivityanalysis.ui.views.decisionmaking"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.resolutionphase.sensitivityanalysis.ui.views.decisionmaking.decisionmaking_view"; //$NON-NLS-1$

	private RankingView _rankingView;
	private SensitivityAnalysis _sensitivityAnalysis;

	private DMTable _dmTable = null;
	private Composite _container;
	private Composite _tableComposite;
	private Composite _buttonComposite;
	private Button _changeWeightsButton;

	private ProblemElementsSet _elementsSet;

	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench()
			.getService(IContextService.class);

	@Override
	public void createPartControl(Composite parent) {
		_container = parent;

		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();

		_sensitivityAnalysis = (SensitivityAnalysis) Workspace.getWorkspace().getElement(SensitivityAnalysis.ID);
		_sensitivityAnalysis.registerSensitivityAnalysisChangeListener(this);

		hookFocusListener();

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_container.setLayoutData(gridData);
		GridLayout layout = new GridLayout(1, true);
		_container.setLayout(layout);

		init();
	}

	private void init() {
		_tableComposite = new Composite(_container, SWT.NONE);
		_tableComposite.setLayout(new GridLayout());
		_tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_dmTable = new DMTable(_tableComposite, _sensitivityAnalysis);
		_dmTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		_buttonComposite = new Composite(_container, SWT.NONE);
		_buttonComposite.setLayout(new GridLayout());
		_buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		_changeWeightsButton = new Button(_buttonComposite, SWT.NONE);
		_changeWeightsButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, 1, 1));
		_changeWeightsButton.setText(Messages.DecisionMakingView_Weights);
		_buttonComposite.pack();

		_changeWeightsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				double[] weights = _sensitivityAnalysis.getWeights();
				List<Double> weightsList = new LinkedList<Double>();
				for (int i = 0; i < weights.length; ++i) {
					weightsList.add(weights[i]);
				}
				Map<String, List<Double>> criteriaWeights = new HashMap<String, List<Double>>();
				criteriaWeights.put(null, weightsList);

				WeightsDialog dialog = new WeightsDialog(Display.getCurrent().getActiveShell(), _elementsSet.getAllSubcriteriaElement(), criteriaWeights, 1, "criterion", "all criteria"); //$NON-NLS-1$ //$NON-NLS-2$

				weights = null;
				List<Double> ws;
				if (dialog.open() == WeightsDialog.SAVE) {
					Map<String, List<Double>> mapWeights = dialog.getWeights();
					ws = mapWeights.get(null);
					weights = new double[ws.size()];
					for (int i = 0; i < weights.length; ++i) {
						weights[i] = ws.get(i);
					}

					_sensitivityAnalysis.calculateDecisionMatrix(ws, _rankingView.getModel());
				}
			}
		});

		refreshDMTable();
	}

	private void refreshDMTable() {
		_dmTable.setModel(_sensitivityAnalysis.getAlternativesIds(), _sensitivityAnalysis.getCriteriaIds(), _sensitivityAnalysis.getDecisionMatrix());
	}

	public DMTable getTable() {
		return _dmTable;
	}

	private void disposeDMTable() {
		if (_dmTable != null) {
			if (!_dmTable.isDisposed()) {
				_tableComposite.dispose();
				_dmTable.dispose();
			}
		}
	}

	private void disposeButton() {
		if (_changeWeightsButton != null) {
			if (!_changeWeightsButton.isDisposed()) {
				_buttonComposite.dispose();
				_changeWeightsButton.dispose();
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
		disposeButton();
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

		if (_rankingView == null) {
			_rankingView = (RankingView) getView(RankingView.ID);
		}

		Object[] aggregatedValuationsData = _sensitivityAnalysis.getAggregatedValuationsPosAndAlpha();
		RankingViewManager.getInstance().setContent(_sensitivityAnalysis.getRanking());
	
		if (_rankingView.getModel() != 1) {
			DomainViewManager.getInstance().setContent(_sensitivityAnalysis.getUnifiedDomain(),
					new Object[] { _sensitivityAnalysis.getAlternativesIds(),
							_sensitivityAnalysis.getAlternativesFinalPreferences(), aggregatedValuationsData[0],
							aggregatedValuationsData[1] });
		} else {
			DomainViewManager.getInstance().setContent(_sensitivityAnalysis.getUnifiedDomain(),
					new Object[] { _sensitivityAnalysis.getAlternativesIds(), new double[0],
							aggregatedValuationsData[0], aggregatedValuationsData[1] });
		}
	}

	private IViewPart getView(String id) {
		IViewPart view = null;
		IViewReference viewReferences[] = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
		for (int i = 0; i < viewReferences.length; i++) {
			if (id.equals(viewReferences[i].getId())) {
				view = viewReferences[i].getView(false);
			}
		}
		return view;
	}
}
