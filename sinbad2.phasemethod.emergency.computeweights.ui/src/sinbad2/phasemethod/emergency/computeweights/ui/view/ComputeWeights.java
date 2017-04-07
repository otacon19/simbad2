package sinbad2.phasemethod.emergency.computeweights.ui.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.emergency.computeweights.ComputeWeightsPhase;
import sinbad2.phasemethod.emergency.computeweights.ui.Images;
import sinbad2.phasemethod.emergency.computeweights.ui.nls.Messages;
import sinbad2.phasemethod.emergency.computeweights.ui.view.provider.AlternativeColumnLabelProvider;
import sinbad2.phasemethod.emergency.computeweights.ui.view.provider.CriteriaContentProvider;
import sinbad2.phasemethod.emergency.computeweights.ui.view.provider.CriteriaWeightsTableContentProvider;
import sinbad2.phasemethod.emergency.computeweights.ui.view.provider.CriterionCostEditingSupport;
import sinbad2.phasemethod.emergency.computeweights.ui.view.provider.CriterionCostLabelProvider;
import sinbad2.phasemethod.emergency.computeweights.ui.view.provider.CriterionIdColumnLabelProvider;
import sinbad2.phasemethod.emergency.computeweights.ui.view.provider.CriterionIdLabelProvider;
import sinbad2.phasemethod.emergency.computeweights.ui.view.provider.OvColumnLabelProvider;
import sinbad2.phasemethod.emergency.computeweights.ui.view.provider.RankingColumnLabelProvider;
import sinbad2.phasemethod.emergency.computeweights.ui.view.provider.RankingTableContentProvider;
import sinbad2.phasemethod.emergency.computeweights.ui.view.provider.WeightColumnLabelProvider;
import sinbad2.phasemethod.emergency.computeweights.ui.view.provider.YMatrixEditableTable;
import sinbad2.resolutionphase.rating.ui.listener.IStepStateListener;
import sinbad2.resolutionphase.rating.ui.view.RatingView;

public class ComputeWeights extends ViewPart implements IStepStateListener {

public static final String ID = "flintstones.phasemethod.emergency.computeweights.ui.view.computeweights"; //$NON-NLS-1$
	
	private Composite _parent;
	private Composite _tableViewersComposite;
	private YMatrixEditableTable _yMatrixTable;
	private YMatrixEditableTable _yNormalizedMatrixTable;
	private TableViewer _weightsTableViewer;
	private TableViewer _resultsTableViewer;
	private TreeViewer _criteriaTypeTreeViewer;
	private CriteriaContentProvider _criteriaProvider;
	
	private ProblemElementsSet _elementsSet;
	
	private ComputeWeightsPhase _computeWeightsPhase;
	
	private static class ElementComparator implements Comparator<Object[]> {

		@Override
		 public int compare(Object[] pe1, Object[] pe2) {
			if(!pe1[0].equals(pe2[0])) {
				return extractInt((String) pe1[0]) - extractInt((String) pe2[0]);
			} else {
				return extractInt((String) pe1[1]) - extractInt((String) pe2[1]);
			}
	    }

	    int extractInt(String s) {
	        String num = s.replaceAll("\\D", ""); //$NON-NLS-1$ //$NON-NLS-2$
	        return num.isEmpty() ? 0 : Integer.parseInt(num);
	    }
	}

	public ComputeWeights() {
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		_computeWeightsPhase = (ComputeWeightsPhase) pmm.getPhaseMethod(ComputeWeightsPhase.ID).getImplementation();
		
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();
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

		 _computeWeightsPhase.computeWeights(_elementsSet.getAllCriteria());
		
		createYMatrixTable();
		createNormalizedYMatrixTable();
		
		_tableViewersComposite = new Composite(_parent, SWT.NONE);
		layout = new GridLayout(3, true);
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginWidth = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		_tableViewersComposite.setLayout(layout);
		_tableViewersComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		createTypeCriteriaTable();
		createTableWeights();
		createTableResults();
	}

	private void createYMatrixTable() {
		Composite YMatrixComposite = new Composite(_parent, SWT.NONE);
		YMatrixComposite.setLayout(new GridLayout(1, true));
		YMatrixComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		_yMatrixTable = new YMatrixEditableTable(YMatrixComposite);
		_yMatrixTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		setYMatrixData();
	}

	private void setYMatrixData() {
		String[] experts = new String[_computeWeightsPhase.getExpertsWithoutPredefined().size()];
		for (int e = 0; e < experts.length; ++e) {
			experts[e] = _computeWeightsPhase.getExpertsWithoutPredefined().get(e).getId();
		}

		String[] criteria = new String[_elementsSet.getCriteria().size()];
		for (int c = 0; c < criteria.length; ++c) {
			criteria[c] = _elementsSet.getCriteria().get(c).getId();
		}

		_yMatrixTable.setModel(experts, criteria, _computeWeightsPhase.getY(), "Y"); //$NON-NLS-1$
	}
	
	private void createNormalizedYMatrixTable() {
		Composite normalizedYMatrixComposite = new Composite(_parent, SWT.NONE);
		normalizedYMatrixComposite.setLayout(new GridLayout(1, true));
		normalizedYMatrixComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		_yNormalizedMatrixTable = new YMatrixEditableTable(normalizedYMatrixComposite);
		_yNormalizedMatrixTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		setNormalizedYMatrixData();
	}

	private void setNormalizedYMatrixData() {
		String[] experts = new String[_computeWeightsPhase.getExpertsWithoutPredefined().size()];
		for (int e = 0; e < experts.length; ++e) {
			experts[e] = _computeWeightsPhase.getExpertsWithoutPredefined().get(e).getId();
		}

		String[] criteria = new String[_elementsSet.getCriteria().size()];
		for (int c = 0; c < criteria.length; ++c) {
			criteria[c] = _elementsSet.getCriteria().get(c).getId();
		}

		_yNormalizedMatrixTable.setModel(experts, criteria, _computeWeightsPhase.getYN(), "YN"); //$NON-NLS-1$
	}
	
	private void createTypeCriteriaTable() {		
		Composite criteriaComposite = new Composite(_tableViewersComposite, SWT.NONE);
		criteriaComposite.setLayout(new GridLayout(1, true));
		criteriaComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		_criteriaTypeTreeViewer = new TreeViewer(criteriaComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		_criteriaTypeTreeViewer.getTree().setHeaderVisible(true);
		_criteriaTypeTreeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		_criteriaTypeTreeViewer.getTree().addListener(SWT.MeasureItem, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				event.height = 25;
				
			}
		});
		_criteriaProvider = new CriteriaContentProvider(_criteriaTypeTreeViewer);
		_criteriaTypeTreeViewer.setContentProvider(_criteriaProvider);

		TreeViewerColumn tvc = new TreeViewerColumn(_criteriaTypeTreeViewer, SWT.NONE);
		tvc.setLabelProvider(new CriterionIdLabelProvider());
		TreeColumn tc = tvc.getColumn();
		tc.setMoveable(true);
		tc.setResizable(false);
		tc.setText(Messages.ComputeWeights_Criterion);
		tc.setImage(Images.Criterion);
		tc.pack();	
		
		tvc = new TreeViewerColumn(_criteriaTypeTreeViewer, SWT.NONE);
		tvc.setLabelProvider(new CriterionCostLabelProvider());
		tvc.setEditingSupport(new CriterionCostEditingSupport(_criteriaTypeTreeViewer));
		tc = tvc.getColumn();
		tc.setToolTipText(Messages.ComputeWeights_Cost_Benefit);
		tc.setMoveable(true);
		tc.setResizable(false);
		tc.setImage(Images.TypeOfCriterion);
		tc.pack();
		
		_criteriaTypeTreeViewer.setInput(_criteriaProvider.getInput());
		
		hookDoubleClickListener();
	}
	
	private void hookDoubleClickListener() {
		
		_criteriaTypeTreeViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Criterion criterion = (Criterion) selection.getFirstElement();
				
				if(criterion.isCost()) {
					criterion.setCost(false);
				} else {
					criterion.setCost(true);
				}
				
				refreshTables();	
			}

			@SuppressWarnings("unchecked")
			private void refreshTables() {
				_computeWeightsPhase.computeWeights((List<Criterion>) _criteriaProvider.getInput());
				
				_criteriaTypeTreeViewer.refresh();
				setWeights();
				setRankingValues();
			}
		});
	}

	private void createTableWeights() {
		_weightsTableViewer = new TableViewer(_tableViewersComposite, SWT.BORDER | SWT.FULL_SELECTION);
		_weightsTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_weightsTableViewer.setContentProvider(new CriteriaWeightsTableContentProvider());
		_weightsTableViewer.getTable().setHeaderVisible(true);
		
		TableViewerColumn criterionId = new TableViewerColumn(_weightsTableViewer, SWT.NONE);
		criterionId.getColumn().setText(Messages.ComputeWeights_Criterion);
		criterionId.setLabelProvider(new CriterionIdColumnLabelProvider());
		criterionId.getColumn().pack();

		TableViewerColumn criterionWeight = new TableViewerColumn(_weightsTableViewer, SWT.NONE);
		criterionWeight.getColumn().setText(Messages.ComputeWeights_Weight);
		criterionWeight.setLabelProvider(new WeightColumnLabelProvider());
		criterionWeight.getColumn().pack();
		
		setWeights();
	}

	private void setWeights() {
		Map<Criterion, Double> weights = _computeWeightsPhase.getWeights();
		
		List<Object[]> result = new LinkedList<Object[]>();
		for (Criterion c : weights.keySet()) {
			Object[] data = new Object[2];
			data[0] = c.getId();
			data[1] = weights.get(c);
			result.add(data);
		}
		
		Collections.sort(result, new ElementComparator());
		
		_weightsTableViewer.setInput(result);
		_weightsTableViewer.getTable().getColumn(0).pack();
		_weightsTableViewer.getTable().getColumn(1).pack();
	}
	
	private void createTableResults() {
		_resultsTableViewer = new TableViewer(_tableViewersComposite, SWT.BORDER | SWT.FULL_SELECTION);
		_resultsTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_resultsTableViewer.setContentProvider(new RankingTableContentProvider());
		_resultsTableViewer.getTable().setHeaderVisible(true);

		TableViewerColumn ranking = new TableViewerColumn(_resultsTableViewer, SWT.NONE);
		ranking.getColumn().setText("Ranking"); //$NON-NLS-1$
		ranking.setLabelProvider(new RankingColumnLabelProvider());
		ranking.getColumn().pack();

		TableViewerColumn ov = new TableViewerColumn(_resultsTableViewer, SWT.NONE);
		ov.getColumn().setText("Ov"); //$NON-NLS-1$
		ov.setLabelProvider(new OvColumnLabelProvider());
		ov.getColumn().pack();
		
		TableViewerColumn alternatives = new TableViewerColumn(_resultsTableViewer, SWT.NONE);
		alternatives.getColumn().setText(Messages.ComputeWeights_Alternative);
		alternatives.setLabelProvider(new AlternativeColumnLabelProvider());
		alternatives.getColumn().pack();
		
		setRankingValues();
	}

	
	public void setRankingValues() {
		Map<Alternative, Double> ovs = _computeWeightsPhase.computeOv();
		
		int pos = 1;
		List<Object[]> result = new LinkedList<Object[]>();
		for(Alternative a: ovs.keySet()) {
			Object[] data = new Object[3];
			data[0] = pos;
			data[1] = ovs.get(a);
			data[2] = a.getId();
			result.add(data);
			pos++;
		}
		
		_resultsTableViewer.setInput(result);
		_resultsTableViewer.getTable().getColumn(0).pack();
		_resultsTableViewer.getTable().getColumn(1).pack();
	}
	
	@Override
	public String getPartName() {
		return Messages.ComputeWeights_Compute_weights;
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void notifyStepStateChange() {
		if(_resultsTableViewer != null) {
			setRankingValues();
		}
	}

	@Override
	public void setRatingView(RatingView rating) {}

}
