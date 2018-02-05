package sinbad2.phasemethod.topsis.selection.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import sinbad2.domain.linguistic.fuzzy.ui.jfreechart.LinguisticDomainChart;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.topsis.selection.SelectionPhase;
import sinbad2.phasemethod.topsis.selection.ui.nls.Messages;
import sinbad2.phasemethod.topsis.selection.ui.view.table.ExpertsWeightTable;
import sinbad2.resolutionphase.rating.ui.listener.IStepStateListener;
import sinbad2.resolutionphase.rating.ui.view.RatingView;

public class CalculateWeights extends ViewPart implements IStepStateListener {
	
	public static final String ID = "flintstones.phasemethod.topsis.selection.ui.view.calculatedistances"; //$NON-NLS-1$
	
	private Composite _parent;
	
	private LinguisticDomainChart _chart;
	
	private boolean _completed = true;
	
	private SelectionPhase _selectionPhase;
	
	private RatingView _ratingView;

	@Override
	public void createPartControl(Composite parent) {
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		_selectionPhase = (SelectionPhase) pmm.getPhaseMethod(SelectionPhase.ID).getImplementation();

		_parent = parent;

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_parent.setLayoutData(gridData);
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 15;
		layout.verticalSpacing = 15;
		_parent.setLayout(layout);

		createContent();
	}

	private void createContent() {
		createTable();
		createChart();
	}

	private void createTable() {
		Composite tableComposite = new Composite(_parent, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		tableComposite.setLayoutData(gridData);
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 15;
		layout.verticalSpacing = 15;
		tableComposite.setLayout(layout);
		
		ExpertsWeightTable expertsWeightTable = new ExpertsWeightTable(tableComposite);
		expertsWeightTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		expertsWeightTable.setModel(_selectionPhase);
	}
	
	private void createChart() {
		Composite chartViewParent = new Composite(_parent, SWT.BORDER);
		GridLayout layout = new GridLayout(1, true);
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginBottom = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		chartViewParent.setLayout(layout);
		chartViewParent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		final Composite chartView = new Composite(chartViewParent, SWT.BORDER);
		chartView.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		_chart = new LinguisticDomainChart();
		_chart.initialize(_selectionPhase.getWeightsDomain(), chartView, chartView.getSize().x, chartView.getSize().y, SWT.BORDER);
		
		chartView.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				_chart.updateSize(chartView.getSize().x, chartView.getSize().y);
			}
		});
	}

	@Override
	public String getPartName() {
		return Messages.selection_Calculate_weights;
	}

	@Override
	public void setFocus() {}

	@Override
	public void notifyStepStateChange() {
		if(_completed) {
			_ratingView.loadNextStep();
			_completed = false;
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		
		_completed = true;
	}
	
	@Override
	public void setRatingView(RatingView rating) {
		_ratingView = rating;
		notifyStepStateChange();
	}

}
