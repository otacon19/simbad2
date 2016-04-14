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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import sinbad2.core.workspace.Workspace;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;
import sinbad2.resolutionphase.sensitivityanalysis.ui.analysis.chart.AlternativesEvolutionWeigthsLineChart;
import sinbad2.resolutionphase.sensitivityanalysis.ui.analysis.chart.MinimunValueBetweenAlternativesBarChart;
import sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis.IChangeSATableValues;
import sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis.SATable;
import sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis.SensitivityAnalysisView;

public class AnalysisView extends ViewPart implements ISelectionChangedListener, IChangeSATableValues {
	public AnalysisView() {
	}

	private Composite _parent;
	private Composite _chartComposite;
	private Composite _spinnerComposite;
	
	private SATable _saTable;
	private Spinner _weightSpinner;
	
	private Object[] _pairAlternatives;
	private Criterion _criterionSelected;
	
	private MinimunValueBetweenAlternativesBarChart _barChart;
	private AlternativesEvolutionWeigthsLineChart _lineChart;
	
	private SensitivityAnalysis _sensitivityAnalysis;
	private SensitivityAnalysisView _sensitivityAnalysisView;
	
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
		
		String id = "flintstones.resolutionphase.sensitivityanalysis.ui.views.sensitivityanalysis";
		IViewReference viewReferences[] = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
		for(int i = 0; i < viewReferences.length; i++) {
			if(id.equals(viewReferences[i].getId())) {
				_sensitivityAnalysisView = (SensitivityAnalysisView) viewReferences[i].getView(false);
			}
		}

		_saTable = _sensitivityAnalysisView.getSATable();
		_saTable.addSelectionChangedListener(this);
		_saTable.getProvider().registerNotifyChangeSATableListener(this);
		
		createChartComposite();
		createSpinnerComposite();
	}

	private void createChartComposite() {
		_chartComposite = new Composite(_parent, SWT.NONE);
		_chartComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_chartComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	
		initializeChart();
	}
	
	private void createSpinnerComposite() {
		_spinnerComposite = new Composite(_parent, SWT.NONE);
		_spinnerComposite.setLayout(new GridLayout());
		GridData layout = new GridData(SWT.RIGHT, SWT.RIGHT, true, false, 1, 1);
		layout.widthHint = 65;
		layout.heightHint = 35;
		_spinnerComposite.setLayoutData(layout);
		_spinnerComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		initializeSpinner();
	}

	private void initializeChart() {
		removeChart();
		
		double[] percents = new double[0];
		
		if(_typeChart == 0) {
			if(_weightSpinner != null) {
				_weightSpinner.setVisible(false);
			}
			_barChart = new MinimunValueBetweenAlternativesBarChart();
			_barChart.initialize(_chartComposite, _chartComposite.getSize().x, _chartComposite.getSize().y, SWT.NONE, percents);
		} else {
			_lineChart = new AlternativesEvolutionWeigthsLineChart();
			_lineChart.initialize(_chartComposite, _chartComposite.getSize().x, _chartComposite.getSize().y, SWT.NONE, _sensitivityAnalysis);
			_lineChart.setCriterionSelected(_criterionSelected);
			_lineChart.setPositionCurrentValueMarker(_sensitivityAnalysis.getWeights()[_elementsSet.getAllCriteria().indexOf(_criterionSelected)]);
			
			_weightSpinner.setVisible(true);
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
	
	private void removeSpinner() {
		if(_weightSpinner != null) {
			if(!_weightSpinner.isDisposed()) {
				_weightSpinner.dispose();
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
				
				if(_typeBarChart.equals("PERCENTS")) {
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
		if(event.getSelection().toString().length() > 4 ) {
			_typeChart = 0;
			
			_pairAlternatives = new Object[2];
			
			ISelection pairAlternatives = event.getSelection();
			String alternative1 = pairAlternatives.toString().substring(1, 2);
			String alternative2 = pairAlternatives.toString().substring(4, pairAlternatives.toString().length() - 1);
			_pairAlternatives[0] = alternative1;
			_pairAlternatives[1] = alternative2;
		} else {
			_typeChart = 1;
		
			ISelection criterion = event.getSelection();
			String criterionNumber = criterion.toString().substring(2, 3);
			int indexCriterion = Integer.parseInt(criterionNumber) - 1;
			_criterionSelected = _elementsSet.getCriteria().get(indexCriterion);
		}
		
		initializeChart();
		refreshChart();
	}

	@Override
	public void notifyChangeSATableValues(String type) {
		_typeBarChart = type;
		refreshChart();
	}
}
