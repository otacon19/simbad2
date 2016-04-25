package sinbad2.resolutionphase.sensitivityanalysis.ui.analysis;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import sinbad2.core.workspace.Workspace;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.resolutionphase.sensitivityanalysis.ISensitivityAnalysisChangeListener;
import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;
import sinbad2.resolutionphase.sensitivityanalysis.ui.analysis.chart.AlternativesEvolutionWeigthsLineChart;
import sinbad2.resolutionphase.sensitivityanalysis.ui.analysis.chart.MinimunValueBetweenAlternativesBarChart;
import sinbad2.resolutionphase.sensitivityanalysis.ui.analysis.chart.SturdinessMeasureStackedChart;
import sinbad2.resolutionphase.sensitivityanalysis.ui.decisionmaking.DecisionMakingView;
import sinbad2.resolutionphase.sensitivityanalysis.ui.ranking.RankingView;
import sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis.IChangeSATableValues;
import sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis.SATable;
import sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis.SensitivityAnalysisView;

public class AnalysisView extends ViewPart implements ISelectionChangedListener, IChangeSATableValues, ISensitivityAnalysisChangeListener {
	
	private Composite _parent;
	private Composite _chartComposite;
	private Composite _componentsComposite;
	private Composite _spinnerComposite;
	private Composite _buttonComposite;
	
	private SATable _saTable;
	private Spinner _weightSpinner;
	private Button _changeChartButton;
	
	private Object[] _pairAlternatives;
	private Criterion _criterionSelected;
	
	private MinimunValueBetweenAlternativesBarChart _barChart;
	private AlternativesEvolutionWeigthsLineChart _lineChart;
	private SturdinessMeasureStackedChart _stackedChart;
	
	private SensitivityAnalysis _sensitivityAnalysis;
	private DecisionMakingView _decisionMakingView;
	private SensitivityAnalysisView _sensitivityAnalysisView;
	private RankingView _rankingView;
	
	private ControlAdapter _controlListener;
	
	private String _typeBarChart = "ABSOLUTE";
	private int _typeChart = 0;
	
	private ProblemElementsSet _elementsSet;

	@Override
	public void createPartControl(Composite parent) {
		_parent = parent;
	
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_parent.setLayoutData(gridData);
		GridLayout layout = new GridLayout(1, true);
		_parent.setLayout(layout);
		_parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		_controlListener = null;
		_pairAlternatives = null;
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		_sensitivityAnalysis = (SensitivityAnalysis) Workspace.getWorkspace().getElement(SensitivityAnalysis.ID);
		_sensitivityAnalysis.registerSensitivityAnalysisChangeListener(this);
		
		_rankingView = (RankingView) getView(RankingView.ID);
		_sensitivityAnalysisView = (SensitivityAnalysisView) getView(SensitivityAnalysisView.ID);
		_decisionMakingView = (DecisionMakingView) getView(DecisionMakingView.ID);

		_saTable = _sensitivityAnalysisView.getSATable();
		_saTable.addSelectionChangedListener(this);
		_saTable.getProvider().registerNotifyChangeSATableListener(this);
		
		createChartComposite();
		
		_componentsComposite = new Composite(_parent, SWT.NONE);
		_componentsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		layout = new GridLayout(2, false);
		_componentsComposite.setLayout(layout);
		_componentsComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		createSpinnerComposite();
		createButtonComposite();
	}

	private void createChartComposite() {
		_chartComposite = new Composite(_parent, SWT.NONE);
		_chartComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_chartComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	
		initializeChart();
	}
	
	private void createSpinnerComposite() {
		_spinnerComposite = new Composite(_componentsComposite, SWT.NONE);
		_spinnerComposite.setLayout(new GridLayout());
		GridData layout = new GridData(SWT.RIGHT, SWT.RIGHT, true, false, 1, 1);
		layout.widthHint = 65;
		layout.heightHint = 35;
		_spinnerComposite.setLayoutData(layout);
		_spinnerComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		initializeSpinner();
	}
	
	private void createButtonComposite() {
		_buttonComposite = new Composite(_componentsComposite, SWT.NONE);
		_buttonComposite.setLayout(new GridLayout());
		GridData layout = new GridData(SWT.RIGHT, SWT.RIGHT, false, false, 1, 1);
		_buttonComposite.setLayoutData(layout);
		_buttonComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		initializeButton();
	}

	private void initializeChart() {
		removeChart();
		
		double[] percents = new double[0];
		
		if(_typeChart == 0) {
			if(_weightSpinner != null) {
				_weightSpinner.setVisible(false);
				_changeChartButton.setVisible(false);
			}
			_barChart = new MinimunValueBetweenAlternativesBarChart();
			_barChart.initialize(_chartComposite, _chartComposite.getSize().x, _chartComposite.getSize().y, SWT.NONE, percents);
		} else {
			if(_changeChartButton.getText().equals("Sturdiness")) {
				_lineChart = new AlternativesEvolutionWeigthsLineChart();
				_lineChart.initialize(_chartComposite, _chartComposite.getSize().x, _chartComposite.getSize().y, SWT.NONE, _sensitivityAnalysis);
				_lineChart.setCriterionSelected(_criterionSelected);
				_lineChart.setPositionCurrentValueMarker(_sensitivityAnalysis.getWeights()[_elementsSet.getAllSubcriteria().indexOf(_criterionSelected)]);
				_lineChart.setModel(_rankingView.getModel());
				
				_weightSpinner.setVisible(true);
				_changeChartButton.setVisible(true);
			} else {
				_stackedChart = new SturdinessMeasureStackedChart();
				if(_decisionMakingView.getTable().getModel().equals("MCM")) {
					_stackedChart.initialize(_chartComposite, _chartComposite.getSize().x, _chartComposite.getSize().y, SWT.NONE, _sensitivityAnalysis.getMinimunPercentMCMByCriterion());
				} else {
					_stackedChart.initialize(_chartComposite, _chartComposite.getSize().x, _chartComposite.getSize().y, SWT.NONE, _sensitivityAnalysis.getMinimunPercentMCCByCriterion());
				}
				_weightSpinner.setVisible(false);
			}
		}
			
		if (_controlListener == null) {
			_controlListener = new ControlAdapter() {
				@Override
				public void controlResized(ControlEvent e) {
					initializeChart();
					refreshChart();
				}
			};
			_chartComposite.addControlListener(_controlListener);
		}
	}

	private void initializeSpinner() {
		removeSpinner();
		
		_weightSpinner = new Spinner(_spinnerComposite, SWT.BORDER);
		_weightSpinner.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, true, true, 1, 1));
		_weightSpinner.setMaximum(100);
		_weightSpinner.setMinimum(0);
		_weightSpinner.setDigits(2);
		_weightSpinner.setIncrement(1);
		
		_weightSpinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				double value = ((Spinner) e.widget).getSelection() / 100d;
				_lineChart.setPositionVariableValueMarker(value);
				_lineChart.refreshChart();
			}
		});
		
		_spinnerComposite.pack();
	}
	
	private void initializeButton() {
		removeButton();
		
		_changeChartButton = new Button(_buttonComposite, SWT.BORDER);
		_changeChartButton.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, true, true, 1, 1));
		_changeChartButton.setText("Sturdiness");
		
		_changeChartButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeChart();
				
				if(_changeChartButton.getText().equals("Sturdiness")) {
					_stackedChart = new SturdinessMeasureStackedChart();
					if(_decisionMakingView.getTable().getModel().equals("MCM")) {
						_stackedChart.initialize(_chartComposite, _chartComposite.getSize().x, _chartComposite.getSize().y, SWT.NONE, _sensitivityAnalysis.getMinimunPercentMCMByCriterion());
					} else {
						_stackedChart.initialize(_chartComposite, _chartComposite.getSize().x, _chartComposite.getSize().y, SWT.NONE, _sensitivityAnalysis.getMinimunPercentMCCByCriterion());
					}
					_weightSpinner.setVisible(false);
					_changeChartButton.setText("Evolution");
				} else {
					_lineChart = new AlternativesEvolutionWeigthsLineChart();
					_lineChart.initialize(_chartComposite, _chartComposite.getSize().x, _chartComposite.getSize().y, SWT.NONE, _sensitivityAnalysis);
					_lineChart.setCriterionSelected(_criterionSelected);
					_lineChart.setPositionCurrentValueMarker(_sensitivityAnalysis.getWeights()[_elementsSet.getAllSubcriteria().indexOf(_criterionSelected)]);
					_lineChart.setModel(_rankingView.getModel());
					
					_weightSpinner.setVisible(true);
					_changeChartButton.setText("Sturdiness");
				}
				
				refreshChart();
			}
		});
		
		_changeChartButton.pack();
	}
	
	
	private void removeSpinner() {
		if(_weightSpinner != null) {
			if(!_weightSpinner.isDisposed()) {
				_weightSpinner.dispose();
			}
		}
	}
	
	private void removeButton() {
		if(_changeChartButton != null) {
			if(!_changeChartButton.isDisposed()) {
				_changeChartButton.dispose();
			}
		}
	}
	
	private void removeChart() {
		if (_barChart != null) {
			_barChart.getChartComposite().dispose();
		}
		
		if(_lineChart != null) {
			_lineChart.getChartComposite().dispose();
		}
		
		if(_stackedChart != null) {
			_stackedChart.getChartComposite().dispose();
		}
	}
	
	private void refreshChart() {
		if(_typeChart == 0) {
			if(_pairAlternatives != null) {
				int a1Index = Integer.parseInt((String) _pairAlternatives[0]);
				int a2Index = Integer.parseInt((String) _pairAlternatives[1]);
				int[] indexes = new int[2];
				indexes[0] = a1Index;
				indexes[1] = a2Index;
				_barChart.setCurrentAlternativesPair(indexes);
				
				if(_typeBarChart.equals("RELATIVE")) {
					double[] percents = _sensitivityAnalysis.getMinimumPercentPairAlternatives(a1Index, a2Index);
					_barChart.setValues(percents);
					_barChart.setTypeData(_typeBarChart);
				} else {
					double[] absolute = _sensitivityAnalysis.getMinimumAbsolutePairAlternatives(a1Index, a2Index);
					_barChart.setValues(absolute);
					_barChart.setTypeData(_typeBarChart);
				}
				
				_barChart.refreshChart();
			}
		} else {
			_lineChart.refreshChart();
		}
	}
	
	@Override
	public void setFocus() {}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if(event.getSelection().toString().contains(",")) {
			_typeChart = 0;
			
			_pairAlternatives = new Object[2];
			
			ISelection pairAlternatives = event.getSelection();
			String alternative1 = pairAlternatives.toString().substring(1, 2);
			String alternative2 = pairAlternatives.toString().substring(4, pairAlternatives.toString().length() - 1);
			_pairAlternatives[0] = alternative1;
			_pairAlternatives[1] = alternative2;
		} else {
			_typeChart = 1;
		
			ISelection selection = event.getSelection();
			String stringSelection = selection.toString();
			if(!stringSelection.contains(">")) {
				stringSelection = stringSelection.substring(1, stringSelection.length() - 1);
				_criterionSelected = _elementsSet.getCriterion(stringSelection);
			} else {
				stringSelection = stringSelection.substring(stringSelection.lastIndexOf('>') + 1, stringSelection.length() - 1);
				_criterionSelected = _elementsSet.getCriterion(stringSelection);
			}
		}
		
		initializeChart();
		refreshChart();
	}

	@Override
	public void notifyChangeSATableValues(String type) {
		_typeBarChart = type;
		refreshChart();
	}

	@Override
	public void notifySensitivityAnalysisChange() {
		initializeChart();
		refreshChart();
	}
	
	private IViewPart getView(String id) {
		IViewPart view = null;
		IViewReference viewReferences[] = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getViewReferences();
		for (int i = 0; i < viewReferences.length; i++) {
			if (id.equals(viewReferences[i].getId())) {
				view = viewReferences[i].getView(false);
			}
		}
		return view;
	}
}
