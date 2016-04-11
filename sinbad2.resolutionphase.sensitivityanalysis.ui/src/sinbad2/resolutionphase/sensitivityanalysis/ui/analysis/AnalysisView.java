package sinbad2.resolutionphase.sensitivityanalysis.ui.analysis;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import sinbad2.core.workspace.Workspace;
import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;
import sinbad2.resolutionphase.sensitivityanalysis.ui.analysis.chart.MinimunValueBetweenAlternativesBarChart;
import sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis.SATable;
import sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis.SensitivityAnalysisView;

public class AnalysisView extends ViewPart implements ISelectionChangedListener {
	
	private Composite _parent;
	private Composite _chartComposite;
	
	private SATable _saTable;
	private Object[] _pairAlternatives;
	
	private MinimunValueBetweenAlternativesBarChart _chart;
	
	private SensitivityAnalysis _sensitivityAnalysis;
	private SensitivityAnalysisView _sensitivityAnalysisView;
	
	private ControlAdapter _controlListener;

	@Override
	public void createPartControl(Composite parent) {
		_parent = parent;
	
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_parent.setLayoutData(gridData);
		GridLayout layout = new GridLayout(1, true);
		_parent.setLayout(layout);
		
		_controlListener = null;
		_pairAlternatives = null;
		
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
		
		createChartComposite();
	}
	
	private void createChartComposite() {
		_chartComposite = new Composite(_parent, SWT.NONE);
		_chartComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_chartComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	
		initializeChart();
	}

	private void initializeChart() {
		removeChart();
		
		double[] percents = new double[0];
		
		_chart = new MinimunValueBetweenAlternativesBarChart();
		_chart.initialize(_chartComposite, _chartComposite.getSize().x, _chartComposite.getSize().y, SWT.BORDER, percents);
		
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

	private void removeChart() {
		if (_chart != null) {
			_chart.getChartComposite().dispose();
		}
	}
	
	private void refreshChart() {
		if(_pairAlternatives != null) {
			int a1Index = Integer.parseInt((String) _pairAlternatives[0]);
			int a2Index = Integer.parseInt((String) _pairAlternatives[1]);
			int[] indexes = new int[2];
			indexes[0] = a1Index;
			indexes[1] = a2Index;
			_chart.setCurrentAlternativesPair(indexes);
			
			double[] percents = _sensitivityAnalysis.getMinimumPercentPairAlternatives(a1Index, a2Index);
			_chart.setValues(percents);
			
			_chart.refreshChart();
		}
	}
	
	@Override
	public void setFocus() {}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		_pairAlternatives = new Object[2];
		
		ISelection pairAlternatives = event.getSelection();
		String alternative1 = pairAlternatives.toString().substring(1, 2);
		String alternative2 = pairAlternatives.toString().substring(4, pairAlternatives.toString().length() - 1);
		_pairAlternatives[0] = alternative1;
		_pairAlternatives[1] = alternative2;
		
		refreshChart();
	}
}
