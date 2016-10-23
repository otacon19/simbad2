package sinbad2.resolutionphase.sensitivityanalysis.ui.analysis;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
import sinbad2.method.MethodsManager;
import sinbad2.resolutionphase.sensitivityanalysis.EProblem;
import sinbad2.resolutionphase.sensitivityanalysis.ISensitivityAnalysisChangeListener;
import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;
import sinbad2.resolutionphase.sensitivityanalysis.ui.analysis.chart.AlternativesEvolutionWeigthsLineChart;
import sinbad2.resolutionphase.sensitivityanalysis.ui.analysis.chart.MinimunValueBetweenAlternativesBarChart;
import sinbad2.resolutionphase.sensitivityanalysis.ui.analysis.chart.SturdinessMeasureStackedChart;
import sinbad2.resolutionphase.sensitivityanalysis.ui.nls.Messages;
import sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis.IChangeSATableValues;
import sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis.SATable;
import sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis.SensitivityAnalysisView;

public class AnalysisView extends ViewPart implements ISelectionChangedListener, IChangeSATableValues, ISensitivityAnalysisChangeListener {
	
	public static final String ID = "flintstones.resolutionphase.sensitivityanalysis.ui.views.analysis"; //$NON-NLS-1$
	
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
	private SensitivityAnalysisView _sensitivityAnalysisView;

	private ControlAdapter _controlListener;
	
	private String _typeBarChart = "ABSOLUTE"; //$NON-NLS-1$
	private int _typeChart = 0;
	
	private boolean _isTODIM = false;
	private boolean _isTOPSIS = false;
	private int _typeTODIMChart;
	
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
		
		loadChangeListeners();
			
		createChartComposite();
		createComponents();
	}

	private void loadChangeListeners() {
		_sensitivityAnalysis = (SensitivityAnalysis) Workspace.getWorkspace().getElement(SensitivityAnalysis.ID);
		_sensitivityAnalysis.registerSensitivityAnalysisChangeListener(this);
		
		_sensitivityAnalysisView = (SensitivityAnalysisView) getView(SensitivityAnalysisView.ID);

		_saTable = _sensitivityAnalysisView.getSATable();
		_saTable.addSelectionChangedListener(this);
		_sensitivityAnalysisView.registerNotifyChangeSATableListener(this);	
	}

	private void createChartComposite() {
		_chartComposite = new Composite(_parent, SWT.NONE);
		_chartComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_chartComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	
		initializeChart();
	}
	
	private void initializeChart() {//Hay que ver como asignar el texto a los botones
		_isTODIM = MethodsManager.getInstance().getActiveMethod().getId().contains("todim");
		_isTOPSIS = MethodsManager.getInstance().getActiveMethod().getId().contains("topsis");
		
		removeChart();
		
		if(!_isTODIM && !_isTOPSIS) {
			initializeBarChart();
		} else {
			if(_isTODIM) {
				_typeTODIMChart = 0;
				initializeLineTODIMChart();
			} else {
				initializeLineChart();
			}
		}
			
		addControlListener();
	}

	private void initializeBarChart() {
		_barChart = new MinimunValueBetweenAlternativesBarChart();
		_barChart.initialize(_chartComposite, _chartComposite.getSize().x, _chartComposite.getSize().y, SWT.NONE, new double[0]);
		
		if(_weightSpinner != null) {
			_weightSpinner.setVisible(false);
			_changeChartButton.setVisible(false);
		}
	}
	
	private void initializeLineTODIMChart() {
		_lineChart = new AlternativesEvolutionWeigthsLineChart();
		_lineChart.initialize(_chartComposite, _chartComposite.getSize().x, _chartComposite.getSize().y, SWT.NONE, _sensitivityAnalysis, _typeTODIMChart);
		
		assignModelLineChart();
		
		if(_weightSpinner != null) {
			assignSpinnerInitialValues();
		}
	}
	
	private void assignModelLineChart() {
		
		if(_criterionSelected != null) {
			_lineChart.setCriterionSelected(_criterionSelected);
			_lineChart.setPositionCurrentValueMarker(_sensitivityAnalysis.getWeights()[_elementsSet.getAllSubcriteria().indexOf(_criterionSelected)]);
			_lineChart.setModel(_sensitivityAnalysis.getModel());
			_lineChart.setPositionVariableValueMarker(_weightSpinner.getSelection() / 100d);
			_lineChart.refreshChart();
		}
	}

	private List<Double> transformWeightsToList() {
		List<Double> weights = new LinkedList<Double>();
		Double[] ws = _sensitivityAnalysis.getWeights();
		for(int w = 0; w < ws.length; ++w) {
			weights.add(ws[w]);
		}
		
		Collections.sort(weights);
		Collections.reverse(weights);
		
		return weights;
	}

	private void initializeLineChart() {
		_lineChart = new AlternativesEvolutionWeigthsLineChart();
		_lineChart.initialize(_chartComposite, _chartComposite.getSize().x, _chartComposite.getSize().y, SWT.NONE, _sensitivityAnalysis);
			
		assignModelLineChart();
		
		if(_weightSpinner != null) {
			assignSpinnerInitialValues();
		}
	}
	
	private void addControlListener() {
		
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

	
	private void createComponents() {
		_componentsComposite = new Composite(_parent, SWT.NONE);
		_componentsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		GridLayout layout = new GridLayout(2, false);
		_componentsComposite.setLayout(layout);
		_componentsComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		createSpinnerComposite();
		createButtonComposite();
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
		
		assignSpinnerInitialValues();
		
		_spinnerComposite.pack();
	}
	
	private void assignSpinnerInitialValues() {
		if(!_isTODIM) {
			_weightSpinner.setValues(0, 0, 100, 2, 1, 1);
			_weightSpinner.setVisible(true);
		} else {
			if(_typeTODIMChart == 0) {
				List<Double> weights = transformWeightsToList();
				Double secondMaxWeight = Math.round(weights.get(1) * 1000d) / 1000d + 0.01;
				_weightSpinner.setValues((int) (secondMaxWeight * 100), 0, 100, 2, 1, 1);
				_weightSpinner.setVisible(false);
			} else {
				_weightSpinner.setValues(100, 100, 1500, 2, 10, 10);
				_weightSpinner.setVisible(true);
			}
		}
	}

	private void removeSpinner() {
		if(_weightSpinner != null) {
			if(!_weightSpinner.isDisposed()) {
				_weightSpinner.dispose();
			}
		}
	}
	
	private void createButtonComposite() {
		_buttonComposite = new Composite(_componentsComposite, SWT.NONE);
		_buttonComposite.setLayout(new GridLayout());
		GridData layout = new GridData(SWT.RIGHT, SWT.RIGHT, false, false, 1, 1);
		_buttonComposite.setLayoutData(layout);
		_buttonComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		initializeButton();
	}
	
	private void initializeButton() {
		
		removeButton();
		
		_changeChartButton = new Button(_buttonComposite, SWT.BORDER);
		_changeChartButton.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, true, true, 1, 1));
		
		initializeTextButton();
		
		_changeChartButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeChart();
				
				if(_changeChartButton.getText().equals(Messages.AnalysisView_Sturdiness)) {
					initializeStackedChart();
					_changeChartButton.setText(Messages.AnalysisView_Evolution);
				} else if(_changeChartButton.getText().equals(Messages.AnalysisView_Attenuation_factor)) {
					_typeTODIMChart = 1;
					initializeLineTODIMChart();
					_changeChartButton.setText(Messages.AnalysisView_Evolution);	
				} else if(_changeChartButton.getText().equals(Messages.AnalysisView_Evolution)){
					if(_isTODIM) {
						_typeTODIMChart = 0;
						initializeLineTODIMChart();
						_changeChartButton.setText(Messages.AnalysisView_Attenuation_factor);
					} else {
						initializeLineChart();
						_changeChartButton.setText(Messages.AnalysisView_Sturdiness);
					}
				}
				
				refreshChart();
			}
		});
		
		_changeChartButton.pack();
	}
	
	private void removeButton() {
		if(_changeChartButton != null) {
			if(!_changeChartButton.isDisposed()) {
				_changeChartButton.dispose();
			}
		}
	}
	
	private void initializeTextButton() {
		
		if(_changeChartButton != null) {
			String activatedMethod = MethodsManager.getInstance().getActiveMethod().getId();
			if(activatedMethod.contains("todim")) { //$NON-NLS-1$
				_changeChartButton.setText(Messages.AnalysisView_Attenuation_factor);
			} else if(activatedMethod.contains("topsis")) { //$NON-NLS-1$
				_changeChartButton.setVisible(false);
			} else {
				_changeChartButton.setText(Messages.AnalysisView_Sturdiness);
			}
		}
	}

	private void initializeStackedChart() {
		_stackedChart = new SturdinessMeasureStackedChart();
		if(_sensitivityAnalysis.getProblem() == EProblem.MOST_CRITICAL_MEASURE) { //$NON-NLS-1$
			_stackedChart.initialize(_chartComposite, _chartComposite.getSize().x, _chartComposite.getSize().y, SWT.NONE, _sensitivityAnalysis.getMinimunPercentMCMByCriterion());
		} else {
			_stackedChart.initialize(_chartComposite, _chartComposite.getSize().x, _chartComposite.getSize().y, SWT.NONE, _sensitivityAnalysis.getMinimunPercentMCCByCriterion());
		}
		
		_weightSpinner.setVisible(false);
		_changeChartButton.setVisible(true);
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
			if(_pairAlternatives != null && !_isTODIM && !_isTOPSIS) {
				setTypeDataBarChart();		
				_barChart.refreshChart();
			}
		} else {
			assignModelLineChart();
		}
	}
	
	private void setTypeDataBarChart() {
		int a1Index = Integer.parseInt((String) _pairAlternatives[0]);
		int a2Index = Integer.parseInt((String) _pairAlternatives[1]);
		int[] indexes = new int[2];
		indexes[0] = a1Index;
		indexes[1] = a2Index;
		_barChart.setCurrentAlternativesPair(indexes);
		
		if(_typeBarChart.equals(Messages.AnalysisView_RELATIVE)) {
			double[] percents = _sensitivityAnalysis.getMinimumPercentPairAlternatives(a1Index, a2Index);
			_barChart.setValues(percents);
			_barChart.setTypeData(_typeBarChart);
		} else {
			double[] absolute = _sensitivityAnalysis.getMinimumAbsolutePairAlternatives(a1Index, a2Index);
			_barChart.setValues(absolute);
			_barChart.setTypeData(_typeBarChart);
		}
	}

	@Override
	public void setFocus() {}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		
		if(event.getSelection().toString().contains(",")) { //$NON-NLS-1$
			_typeChart = 0;
			setPairAlternatives(event);	
		} else {
			_typeChart = 1;
			setCriterionSelected(event);
		}
		
		refreshChart();
	}
	
	private void setPairAlternatives(SelectionChangedEvent event) {
		_pairAlternatives = new Object[2];
		
		ISelection pairAlternatives = event.getSelection();
		String alternative1 = pairAlternatives.toString().substring(1, 2);
		String alternative2 = pairAlternatives.toString().substring(4, pairAlternatives.toString().length() - 1);
		_pairAlternatives[0] = alternative1;
		_pairAlternatives[1] = alternative2;
	}
	
	private void setCriterionSelected(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		String stringSelection = selection.toString();
		if(!stringSelection.contains(">")) { //$NON-NLS-1$
			stringSelection = stringSelection.substring(1, stringSelection.length() - 1);
			_criterionSelected = _elementsSet.getCriterion(stringSelection);
		} else {
			stringSelection = stringSelection.substring(stringSelection.lastIndexOf('>') + 1, stringSelection.length() - 1);
			_criterionSelected = _elementsSet.getCriterion(stringSelection);
		}
	}


	@Override
	public void notifyChangeSATableValues(String type) {
		_typeBarChart = type;
		refreshChart();
	}

	@Override
	public void notifySensitivityAnalysisChange() {
		refreshChart();
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

	@Override
	public void dispose() {
		super.dispose();
	}
	
	public void clear() {
		_typeBarChart = "ABSOLUTE"; //$NON-NLS-1$
		_typeChart = 0;
		_isTODIM = false;
		_criterionSelected = null;
	}
}
