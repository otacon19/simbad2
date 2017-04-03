package sinbad2.phasemethod.emergency.aggregation.ui.view;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.emergency.aggregation.AggregationPhase;
import sinbad2.phasemethod.emergency.aggregation.ui.view.provider.CriteriaTableContentProvider;
import sinbad2.phasemethod.emergency.aggregation.ui.view.provider.ExpertIdColumnLabelProvider;
import sinbad2.phasemethod.emergency.aggregation.ui.view.provider.ExpertWeightColumnLabelProvider;
import sinbad2.phasemethod.emergency.aggregation.ui.view.provider.WeightEditingSupport;
import sinbad2.resolutionphase.rating.ui.listener.IStepStateListener;
import sinbad2.resolutionphase.rating.ui.view.RatingView;

public class AggregationProcess extends ViewPart implements IStepStateListener {

	public static final String ID = "flintstones.phasemethod.emergency.aggregation.ui.view.aggregationprocess"; //$NON-NLS-1$
	
	private Composite _parent;
	private TableViewer _weightsTableViewer;
	
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
		
		GridLayout layout = new GridLayout(1, true);
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginWidth = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		_parent.setLayout(layout);

		createTableWeights();
		
	}

	private void createTableWeights() {
		Composite weightsTableComposite = new Composite(_parent, SWT.NONE);
		weightsTableComposite.setLayout(new GridLayout(1, true));
		weightsTableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		_weightsTableViewer = new TableViewer(weightsTableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		_weightsTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_weightsTableViewer.setContentProvider(new CriteriaTableContentProvider());
		_weightsTableViewer.getTable().setHeaderVisible(true);

		TableViewerColumn expertId = new TableViewerColumn(_weightsTableViewer, SWT.NONE);
		expertId.getColumn().setText("Expert");
		expertId.setLabelProvider(new ExpertIdColumnLabelProvider());
		expertId.getColumn().pack();

		TableViewerColumn expertWeight = new TableViewerColumn(_weightsTableViewer, SWT.NONE);
		expertWeight.getColumn().setText("Weight");
		expertWeight.setLabelProvider(new ExpertWeightColumnLabelProvider());
		expertWeight.getColumn().pack();
		expertWeight.setEditingSupport(new WeightEditingSupport(_weightsTableViewer, 1, _aggregationPhase, _elementsSet));
		
		setInitialWeights();
	}

	private void setInitialWeights() {
		List<Expert> experts = _elementsSet.getOnlyExpertChildren();
		List<String[]> result = new LinkedList<String[]>();
		
		for (Expert e : experts) {
			String[] row = new String[2];
			row[0] = e.getCanonicalId();
			row[1] = Double.toString(1d / experts.size());
			result.add(row);
		}

		_weightsTableViewer.setInput(result);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyStepStateChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRatingView(RatingView rating) {
		// TODO Auto-generated method stub
		
	}

}
