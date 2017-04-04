package sinbad2.phasemethod.emergency.aggregation.ui.view;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.emergency.aggregation.AggregationPhase;
import sinbad2.phasemethod.emergency.aggregation.ui.view.provider.ExpertsTableContentProvider;
import sinbad2.phasemethod.emergency.aggregation.ui.view.provider.GRPColumnLabelProvider;
import sinbad2.phasemethod.emergency.aggregation.ui.view.provider.GRPTableContentProvider;
import sinbad2.phasemethod.emergency.aggregation.ui.nls.Messages;
import sinbad2.phasemethod.emergency.aggregation.ui.view.provider.CriteriaIdColumnLabelProvider;
import sinbad2.phasemethod.emergency.aggregation.ui.view.provider.ExpertIdColumnLabelProvider;
import sinbad2.phasemethod.emergency.aggregation.ui.view.provider.ExpertWeightColumnLabelProvider;
import sinbad2.phasemethod.emergency.aggregation.ui.view.provider.WeightEditingSupport;
import sinbad2.resolutionphase.rating.ui.listener.IStepStateListener;
import sinbad2.resolutionphase.rating.ui.view.RatingView;
import sinbad2.valuation.real.interval.RealIntervalValuation;

public class AggregationProcess extends ViewPart implements IStepStateListener {

	public static final String ID = "flintstones.phasemethod.emergency.aggregation.ui.view.aggregationprocess"; //$NON-NLS-1$
	
	private Composite _parent;
	private TableViewer _weightsTableViewer;
	private TableViewer _grpTableViewer;
	
	private ProblemElementsSet _elementsSet;
	
	private AggregationPhase _aggregationPhase;
	
	public AggregationProcess() {
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();
		
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		_aggregationPhase = (AggregationPhase) pmm.getPhaseMethod(AggregationPhase.ID).getImplementation();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		_parent = parent;
		
		GridLayout layout = new GridLayout(2, true);
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginWidth = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		_parent.setLayout(layout);

		createTableWeights();
		createTableRP();
		
	}
	
	private void createTableWeights() {
		Composite weightsTableComposite = new Composite(_parent, SWT.NONE);
		weightsTableComposite.setLayout(new GridLayout(1, true));
		weightsTableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		_weightsTableViewer = new TableViewer(weightsTableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		_weightsTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_weightsTableViewer.setContentProvider(new ExpertsTableContentProvider());
		_weightsTableViewer.getTable().setHeaderVisible(true);

		TableViewerColumn expertId = new TableViewerColumn(_weightsTableViewer, SWT.NONE);
		expertId.getColumn().setText(Messages.AggregationProcess_Expert);
		expertId.setLabelProvider(new ExpertIdColumnLabelProvider());
		expertId.getColumn().pack();

		TableViewerColumn expertWeight = new TableViewerColumn(_weightsTableViewer, SWT.NONE);
		expertWeight.getColumn().setText(Messages.AggregationProcess_Weight);
		expertWeight.setLabelProvider(new ExpertWeightColumnLabelProvider());
		expertWeight.getColumn().pack();
		expertWeight.setEditingSupport(new WeightEditingSupport(_weightsTableViewer, 1, _aggregationPhase, _elementsSet, this));
		
		setInitialWeights();
	}

	private void setInitialWeights() {
		List<Expert> experts = _elementsSet.getOnlyExpertChildren();
		List<String[]> result = new LinkedList<String[]>();
		Double[] weights = new Double[experts.size() - 1];
		int numExpert = 0;
		
		for (Expert e : experts) {
			if(!e.getId().equals("predefined_effective_control")) { //$NON-NLS-1$
				String[] row = new String[2];
				row[0] = e.getCanonicalId();
				row[1] = Double.toString(1d / (experts.size() - 1));
				result.add(row);
				
				weights[numExpert] = 1d / (experts.size() - 1);
				numExpert++;
			}
		}
		
		_aggregationPhase.setExpertsWeights(weights);
		
		_weightsTableViewer.setInput(result);
		_weightsTableViewer.getTable().getColumn(0).pack();
		_weightsTableViewer.getTable().getColumn(1).pack();
	}
	
	private void createTableRP() {
		Composite rpTableComposite = new Composite(_parent, SWT.NONE);
		rpTableComposite.setLayout(new GridLayout(1, true));
		rpTableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		_grpTableViewer = new TableViewer(rpTableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		_grpTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_grpTableViewer.setContentProvider(new GRPTableContentProvider());
		_grpTableViewer.getTable().setHeaderVisible(true);

		TableViewerColumn criterionId = new TableViewerColumn(_grpTableViewer, SWT.NONE);
		criterionId.getColumn().setText(Messages.AggregationProcess_Criterion);
		criterionId.setLabelProvider(new CriteriaIdColumnLabelProvider());
		criterionId.getColumn().pack();

		TableViewerColumn rpValues = new TableViewerColumn(_grpTableViewer, SWT.NONE);
		rpValues.getColumn().setText(Messages.AggregationProcess_GRP);
		rpValues.setLabelProvider(new GRPColumnLabelProvider());
		rpValues.getColumn().pack();
		
		setGRPValues();
	}

	
	public void setGRPValues() {
		Map<Criterion, RealIntervalValuation> grps = _aggregationPhase.aggregationProcess();
		
		List<Object[]> result = new LinkedList<Object[]>();
		for(Criterion c: grps.keySet()) {
			Object[] data = new Object[2];
			data[0] = c.getCanonicalId();
			data[1] = grps.get(c);
			result.add(data);
		}
		
		_grpTableViewer.setInput(result);
		_grpTableViewer.getTable().getColumn(0).pack();
		_grpTableViewer.getTable().getColumn(1).pack();
	}
	
	@Override
	public String getPartName() {
		return Messages.AggregationProcess_Aggregation_process;
	}

	@Override
	public void setFocus() {}

	@Override
	public void notifyStepStateChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRatingView(RatingView rating) {
		// TODO Auto-generated method stub
		
	}

}
