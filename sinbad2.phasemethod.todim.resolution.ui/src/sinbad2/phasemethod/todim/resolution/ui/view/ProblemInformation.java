package sinbad2.phasemethod.todim.resolution.ui.view;

import java.util.Map;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import sinbad2.core.utils.Pair;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.excel.ExcelUtil;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.todim.resolution.ResolutionPhase;
import sinbad2.phasemethod.todim.resolution.ui.Images;
import sinbad2.phasemethod.todim.resolution.ui.nls.Messages;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.AggregatedValuationColumnLabelProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.AlternativeColumnLabelProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.CriterionColumnLabelProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.DistanceColumnLabelProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.DistanceTableContentProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.ExpertColumnLabelProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.ExpertValuationColumnLabelProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.ThresholdColumnLabelProvider;
import sinbad2.resolutionphase.rating.ui.listener.IStepStateListener;
import sinbad2.resolutionphase.rating.ui.view.RatingView;
import sinbad2.valuation.Valuation;

public class ProblemInformation extends ViewPart implements IStepStateListener {

	public static final String ID = "flintstones.phasemethod.todim.resolution.ui.view.probleminformation"; //$NON-NLS-1$
	
	private Composite _parent;
	private Button _excelButton;
	
	private DecisionMatrixTable _dmTable;
	private TableViewer _distanceTableViewer;
	
	private boolean _completed;
	
	private ResolutionPhase _resolutionPhase;
	
	private RatingView _ratingView;
	
	private ProblemElementsSet _elementsSet;
	
	public ProblemInformation() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		_resolutionPhase = (ResolutionPhase) pmm.getPhaseMethod(ResolutionPhase.ID).getImplementation();
		
		_completed = true;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		_parent = parent;
		_parent.setLayout(new GridLayout(1, true));
		_parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite decisionMatrixComposite = new Composite(_parent, SWT.NONE);
		decisionMatrixComposite.setLayout(new GridLayout(1, true));
		decisionMatrixComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		_dmTable = new DecisionMatrixTable(decisionMatrixComposite);
		_dmTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));	
		
		Composite distanceComposite = new Composite(_parent, SWT.NONE);
		distanceComposite.setLayout(new GridLayout(1, true));
		distanceComposite.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true, 1, 1));
		((GridData) distanceComposite.getLayoutData()).minimumWidth = 650;
		
		_distanceTableViewer = new TableViewer(distanceComposite);
		_distanceTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_distanceTableViewer.setContentProvider(new DistanceTableContentProvider());
		_distanceTableViewer.getTable().setHeaderVisible(true);
		
		TableViewerColumn expert = new TableViewerColumn(_distanceTableViewer, SWT.NONE);
		expert.setLabelProvider(new ExpertColumnLabelProvider());
		expert.getColumn().setText(Messages.ProblemInformation_Expert);
		expert.getColumn().pack();
		
		TableViewerColumn alternative = new TableViewerColumn(_distanceTableViewer, SWT.NONE);
		alternative.setLabelProvider(new AlternativeColumnLabelProvider());
		alternative.getColumn().setText(Messages.ProblemInformation_Alternative);
		alternative.getColumn().pack();
		
		TableViewerColumn criterion = new TableViewerColumn(_distanceTableViewer, SWT.NONE);
		criterion.setLabelProvider(new CriterionColumnLabelProvider());
		criterion.getColumn().setText(Messages.ProblemInformation_Criterion);
		criterion.getColumn().pack();
		
		TableViewerColumn expertOpinion = new TableViewerColumn(_distanceTableViewer, SWT.NONE);
		expertOpinion.setLabelProvider(new ExpertValuationColumnLabelProvider());
		expertOpinion.getColumn().setText(Messages.ProblemInformation_Expert_valuation);
		expertOpinion.getColumn().pack();
		
		TableViewerColumn aggregatedValuation = new TableViewerColumn(_distanceTableViewer, SWT.NONE);
		aggregatedValuation.setLabelProvider(new AggregatedValuationColumnLabelProvider());
		aggregatedValuation.getColumn().setText(Messages.ProblemInformation_Aggregated_valuation);
		aggregatedValuation.getColumn().pack();
		
		TableViewerColumn distance = new TableViewerColumn(_distanceTableViewer, SWT.NONE);
		distance.setLabelProvider(new DistanceColumnLabelProvider());
		distance.getColumn().setText(Messages.ProblemInformation_Distance);
		distance.getColumn().pack();
		
		TableViewerColumn threshold = new TableViewerColumn(_distanceTableViewer, SWT.NONE);
		threshold.setLabelProvider(new ThresholdColumnLabelProvider());
		threshold.getColumn().setText(Messages.ProblemInformation_Threshold);
		threshold.getColumn().pack();
	
		Composite buttonsComposite = new Composite(distanceComposite, SWT.NONE);
		buttonsComposite.setLayout(new GridLayout(1, false));
		buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
	
		_excelButton = new Button(buttonsComposite, SWT.NONE);
		_excelButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
		_excelButton.setImage(Images.Excel);
		_excelButton.setEnabled(true);
		_excelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ExcelUtil excelUtil = new ExcelUtil();
				excelUtil.createExcelFileEmergencyProblemStructure(_resolutionPhase.getValuationsTwoTuple(), _resolutionPhase.getExpertsWeights(), _resolutionPhase.getDecisionMatrix());
			}
		});
		
		distanceComposite.layout();
		
		refreshTables();
	}
	
	private void refreshTables() {		
		String[] alternatives = new String[_elementsSet.getAlternatives().size()];
		for(int a = 0; a < alternatives.length; ++a) {
			alternatives[a] = _elementsSet.getAlternatives().get(a).getId();
		}
		
		String[] criteria = new String[_elementsSet.getCriteria().size()];
		for(int c = 0; c < criteria.length; ++c) {
			criteria[c] = _elementsSet.getCriteria().get(c).getId();
		}

		Map<Pair<Alternative, Criterion>, Valuation> decisionMatrix = _resolutionPhase.calculateDecisionMatrix();
		_dmTable.setModel(alternatives, criteria, decisionMatrix);

		_distanceTableViewer.setInput(_resolutionPhase.calculateDistance());
		_distanceTableViewer.refresh();
		
		pack();
	}
	
	private void pack() {
		for(TableColumn tc: _distanceTableViewer.getTable().getColumns()) {
			tc.pack();
		}
	}
	
	@Override
	public String getPartName() {
		return Messages.ProblemInformation_Aggregation;
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
	public void setRatingView(RatingView rating) {
		_ratingView = rating;
		
		notifyStepStateChange();
	}

}
