package sinbad2.phasemethod.todim.resolution.ui.view;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.AggregationOperatorsManager;
import sinbad2.aggregationoperator.EAggregationOperatorType;
import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.element.ProblemElement;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.excel.ExcelUtil;
import sinbad2.method.ui.MethodsUIManager;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.todim.aggregation.AggregationPhase;
import sinbad2.phasemethod.todim.resolution.ResolutionPhase;
import sinbad2.phasemethod.todim.resolution.ui.Images;
import sinbad2.phasemethod.todim.resolution.ui.nls.Messages;
import sinbad2.phasemethod.todim.resolution.ui.view.dialog.WeightsCriteriaDialog;
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

public class AggregationView extends ViewPart implements IStepStateListener{

	private Composite _parent;
	private Combo _aggregationOperatorsCombo;
	private Button _excelButton;
	
	private DecisionMatrixTable _dmTable;
	private TableViewer _distanceTableViewer;
	
	private Map<String, String> _operators;
	private List<String[]> _inputDistanceTable;
	private boolean _completed;
	
	private AggregationPhase _aggregationPhase;
	private ResolutionPhase _resolutionPhase;
	
	private RatingView _ratingView;
	
	private ProblemElementsSet _elementsSet;
	
	public AggregationView() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		_aggregationPhase = (AggregationPhase) pmm.getPhaseMethod(AggregationPhase.ID).getImplementation();
		_resolutionPhase = (ResolutionPhase) pmm.getPhaseMethod(ResolutionPhase.ID).getImplementation();
		
		_operators = new HashMap<String, String>();
		_inputDistanceTable = new LinkedList<String[]>();
		_completed = false;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		_parent = parent;
		_parent.setLayout(new GridLayout(1, true));
		_parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite aggregationOperatorsComposite = new Composite(_parent, SWT.NONE);
		aggregationOperatorsComposite.setLayout(new GridLayout(1, false));
		aggregationOperatorsComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
		_aggregationOperatorsCombo = new Combo(aggregationOperatorsComposite, SWT.BORDER | SWT.READ_ONLY);
		_aggregationOperatorsCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setOperator(_aggregationOperatorsCombo.getItem(_aggregationOperatorsCombo.getSelectionIndex()));
				_excelButton.setEnabled(true);
				
				_completed = true;
				notifyStepStateChange();
			}
		});
		
		fillCombo();
		
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
		expert.getColumn().setText(Messages.AggregationView_Expert);
		expert.getColumn().pack();
		
		TableViewerColumn alternative = new TableViewerColumn(_distanceTableViewer, SWT.NONE);
		alternative.setLabelProvider(new AlternativeColumnLabelProvider());
		alternative.getColumn().setText(Messages.AggregationView_Alternative);
		alternative.getColumn().pack();
		
		TableViewerColumn criterion = new TableViewerColumn(_distanceTableViewer, SWT.NONE);
		criterion.setLabelProvider(new CriterionColumnLabelProvider());
		criterion.getColumn().setText(Messages.AggregationView_Criterion);
		criterion.getColumn().pack();
		
		TableViewerColumn expertOpinion = new TableViewerColumn(_distanceTableViewer, SWT.NONE);
		expertOpinion.setLabelProvider(new ExpertValuationColumnLabelProvider());
		expertOpinion.getColumn().setText(Messages.AggregationView_Expert_valuation);
		expertOpinion.getColumn().pack();
		
		TableViewerColumn aggregatedValuation = new TableViewerColumn(_distanceTableViewer, SWT.NONE);
		aggregatedValuation.setLabelProvider(new AggregatedValuationColumnLabelProvider());
		aggregatedValuation.getColumn().setText(Messages.AggregationView_Aggregated_valuation);
		aggregatedValuation.getColumn().pack();
		
		TableViewerColumn distance = new TableViewerColumn(_distanceTableViewer, SWT.NONE);
		distance.setLabelProvider(new DistanceColumnLabelProvider());
		distance.getColumn().setText(Messages.AggregationView_Distance);
		distance.getColumn().pack();
		
		TableViewerColumn threshold = new TableViewerColumn(_distanceTableViewer, SWT.NONE);
		threshold.setLabelProvider(new ThresholdColumnLabelProvider());
		threshold.getColumn().setText(Messages.AggregationView_Threshold);
		threshold.getColumn().pack();
	
		Composite buttonsComposite = new Composite(distanceComposite, SWT.NONE);
		buttonsComposite.setLayout(new GridLayout(1, false));
		buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
	
		_excelButton = new Button(buttonsComposite, SWT.NONE);
		_excelButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
		_excelButton.setImage(Images.Excel);
		_excelButton.setEnabled(false);
		_excelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ExcelUtil excelUtil = new ExcelUtil();
				excelUtil.createExcelFileEmergencyProblemStructure(_aggregationPhase.getValuationsTwoTuple(), _aggregationPhase.getExpertsWeights(), _aggregationPhase.getDecisionMatrix());
			}
		});
		
		distanceComposite.layout();
		
		refreshTables();
	}

	private void setOperator(String operator) {
		AggregationOperatorsManager aggregationOperatorsManager = AggregationOperatorsManager.getInstance();
		AggregationOperator aggregationOperator = aggregationOperatorsManager.getAggregationOperator(_operators.get(operator));
		
		Map<String, List<Double>> mapWeights = new HashMap<String, List<Double>>();
		if(aggregationOperator instanceof WeightedAggregationOperator) { 
			ProblemElement[] secondary = _elementsSet.getAllCriteria().toArray(new Criterion[0]);
			WeightsCriteriaDialog dialog = new WeightsCriteriaDialog(Display.getCurrent().getActiveShell(), _elementsSet.getAllElementExpertChildren(null), secondary, null, 1, Messages.AggregationView_Expert, Messages.AggregationView_All_experts);
			
			int exitValue = dialog.open();
			if(exitValue == WeightsCriteriaDialog.SAVE) {
				mapWeights = dialog.getWeights();
			} else { 
				mapWeights = new HashMap<String, List<Double>>();
			}
		}
		
		_aggregationPhase.calculateDecisionMatrix(aggregationOperator, mapWeights);
		_inputDistanceTable = _aggregationPhase.calculateDistance();
		_distanceTableViewer.setInput(_inputDistanceTable);
		
		refreshTables();
	}
	
	private void fillCombo() {
		AggregationOperatorsManager aggregationOperatorsManager = AggregationOperatorsManager.getInstance();
		TreeSet<String> aggregationOperatorsIds = new TreeSet<String>();
		MethodsUIManager methodsUIManager = MethodsUIManager.getInstance();	
		Set<EAggregationOperatorType> operatorsTypes = methodsUIManager.getActivateMethodUI().getMethod().getAggregationTypesSupported();

		String[] operatorsIds;
		for(EAggregationOperatorType operatorType: operatorsTypes) {
			if(!operatorType.toString().equals("Hesitant")) { //El MaxMin solo se puede usar si todas las valoraciones son hesitant //$NON-NLS-1$
				operatorsIds = aggregationOperatorsManager.getAggregationOperatorsIdByType(operatorType);
				for(String operator: operatorsIds) {
					aggregationOperatorsIds.add(operator);
				}
			}
		}
		
		List<String> aggregationOperatorsNames = new LinkedList<String>();
		AggregationOperator operator;
		for(int i = 0; i < aggregationOperatorsIds.size(); i++) {
			operator = aggregationOperatorsManager.getAggregationOperator((String) aggregationOperatorsIds.toArray()[i]);
			String name = ""; //$NON-NLS-1$
			if (operator instanceof WeightedAggregationOperator) {
				name = "(W) " + aggregationOperatorsManager.getAggregationOperator((String) aggregationOperatorsIds.toArray()[i]).getName(); //$NON-NLS-1$
				aggregationOperatorsNames.add(name);
			} else {
				name = aggregationOperatorsManager.getAggregationOperator((String) aggregationOperatorsIds.toArray()[i]).getName();
				aggregationOperatorsNames.add(name);
			}
			
			_operators.put(name, aggregationOperatorsIds.toArray(new String[0])[i]);
		}
		
		_aggregationOperatorsCombo.setItems(aggregationOperatorsNames.toArray(new String[0]));
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

		_dmTable.setModel(alternatives, criteria, _aggregationPhase.getDecisionMatrix());
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
		return Messages.AggregationView_Aggregation;
	}
	
	@Override
	public void setFocus() {
		_aggregationOperatorsCombo.setFocus();
	}

	@Override
	public void notifyStepStateChange() {
		
		_resolutionPhase.clear();
		
		if(_completed) {
			_ratingView.loadNextStep();
			_completed = false;
		}
	}

	@Override
	public void setRatingView(RatingView rating) {
		_ratingView = rating;
	}

}
