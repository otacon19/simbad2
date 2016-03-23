package sinbad2.phasemethod.topsis.selection.ui.view;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.AggregationOperatorsManager;
import sinbad2.aggregationoperator.EAggregationOperatorType;
import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.element.ProblemElement;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.method.ui.MethodsUIManager;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.topsis.selection.SelectionPhase;
import sinbad2.phasemethod.topsis.selection.ui.Images;
import sinbad2.phasemethod.topsis.selection.ui.view.dialog.WeightsDialog;
import sinbad2.phasemethod.topsis.selection.ui.view.provider.ColectiveValuationsTableViewerContentProvider;
import sinbad2.phasemethod.topsis.selection.ui.view.provider.IdealSolutionTableViewerContentProvider;
import sinbad2.phasemethod.topsis.selection.ui.view.provider.NoIdealSolutionTableViewerContentProvider;
import sinbad2.resolutionphase.rating.ui.listener.IStepStateListener;
import sinbad2.resolutionphase.rating.ui.view.RatingView;
import sinbad2.valuation.twoTuple.TwoTuple;

public class AggregationExperts extends ViewPart implements IStepStateListener {

	public static final String ID = "flintstones.phasemethod.topsis.selection.ui.view.aggregationexperts";

	private Composite _parent;
	private Composite _aggregationEditorPanel;

	private Combo _aggregationOperatorCombo;
	
	private TableViewer _tableViewerIdealSolution;
	private TableViewer _tableViewerNoIdealSolution;
	private TableViewer _tableViewerColectiveValuations;
	
	private ColectiveValuationsTableViewerContentProvider _colectiveValuationsProvider;
	private IdealSolutionTableViewerContentProvider _idealSolutionProvider;
	private NoIdealSolutionTableViewerContentProvider _noIdealSolutionProvider;
	
	private boolean _completed;
	
	private Map<String, String> _operators;
	
	private RatingView _ratingView;
	
	private SelectionPhase _selectionPhase;
	
	private static ProblemElement[] getLeafElements(ProblemElement root) {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();

		List<ProblemElement> result = new LinkedList<ProblemElement>();
		List<Criterion> subcriteria = elementsSet.getAllCriterionSubcriteria((Criterion) root);
		for(Criterion subcriterion : subcriteria) {
			if(!subcriterion.hasSubcriteria()) {
				result.add(subcriterion);
			} else {
				ProblemElement[] sub2criteria = getLeafElements(subcriterion);
				for(ProblemElement sub2criterion : sub2criteria) {
					result.add(sub2criterion);
				}
			}
		}
		
		return result.toArray(new ProblemElement[0]);
	}

	@Override
	public void createPartControl(Composite parent) {	
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		_selectionPhase = (SelectionPhase) pmm.getPhaseMethod(SelectionPhase.ID).getImplementation();
		
		_completed = false;
		
		_operators = new HashMap<String, String>();
		
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
		_aggregationEditorPanel = new Composite(_parent, SWT.NONE);

		GridLayout ratingEditorPanelLayout = new GridLayout(1, false);
		ratingEditorPanelLayout.marginRight = 0;
		ratingEditorPanelLayout.verticalSpacing = 0;
		ratingEditorPanelLayout.marginWidth = 10;
		ratingEditorPanelLayout.marginHeight = 10;
		_aggregationEditorPanel.setLayout(ratingEditorPanelLayout);
		_aggregationEditorPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

		GridLayout selectOperatorLayout = new GridLayout(2, false);
		selectOperatorLayout.marginWidth = 10;
		selectOperatorLayout.marginHeight = 10;
		
		Composite selectOperatorPanel = new Composite(_aggregationEditorPanel, SWT.NONE);
		GridData gridData = new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1);
		selectOperatorPanel.setLayoutData(gridData);
		selectOperatorPanel.setLayout(selectOperatorLayout);
		
		Label selectOperatorLabel = new Label(selectOperatorPanel, SWT.NONE);
		selectOperatorLabel.setText("Select operator for experts");
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1);
		selectOperatorLabel.setLayoutData(gridData);
		
		_aggregationOperatorCombo = new Combo(selectOperatorPanel, SWT.READ_ONLY);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1);
		_aggregationOperatorCombo.setLayoutData(gridData);
		_aggregationOperatorCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setOperator(_aggregationOperatorCombo.getItem(_aggregationOperatorCombo.getSelectionIndex()));
				
				_completed = true;
				
				notifyStepStateChange();
			}
		});
		
		fillCombo();
		
		GridLayout solutionsLayout = new GridLayout(2, true);
		solutionsLayout.marginWidth = 10;
		solutionsLayout.marginHeight = 10;
		
		Composite solutionsPanel = new Composite(_aggregationEditorPanel, SWT.BORDER);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		solutionsPanel.setLayoutData(gridData);
		solutionsPanel.setLayout(solutionsLayout);
		
		GridLayout idealSolutionLayout = new GridLayout(1, true);
		idealSolutionLayout.marginWidth = 10;
		idealSolutionLayout.marginHeight = 10;
		
		Composite idealSolutionPanel = new Composite(solutionsPanel, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		idealSolutionPanel.setLayoutData(gridData);
		idealSolutionPanel.setLayout(idealSolutionLayout);
		
		Label idealSolutionLabel = new Label(idealSolutionPanel, SWT.NONE);
		idealSolutionLabel.setText("Ideal solution");
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		idealSolutionLabel.setLayoutData(gridData);
		
		_tableViewerIdealSolution = new TableViewer(idealSolutionPanel, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_tableViewerIdealSolution.getTable().setLayoutData(gridData);
		_tableViewerIdealSolution.getTable().setHeaderVisible(true);
		
		_idealSolutionProvider = new IdealSolutionTableViewerContentProvider();
		_tableViewerIdealSolution.setContentProvider(_idealSolutionProvider);

		TableViewerColumn criterionColumn = new TableViewerColumn(_tableViewerIdealSolution, SWT.NONE);
		criterionColumn.getColumn().setText("Criterion");
		criterionColumn.getColumn().pack();
		criterionColumn.getColumn().setResizable(false);
		criterionColumn.getColumn().setMoveable(false);
		criterionColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				return ((Criterion) data[0]).getCanonicalId();
			}
		});
		
		TableViewerColumn typeColumn = new TableViewerColumn(_tableViewerIdealSolution, SWT.NONE);
		typeColumn.getColumn().setText("Type");
		typeColumn.getColumn().pack();
		typeColumn.getColumn().setResizable(false);
		typeColumn.getColumn().setMoveable(false);
		typeColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return "";
			}
			
			@Override
			public Image getImage(Object element) {
				Object[] data = (Object[]) element;
				if(((Criterion) data[0]).getCost()) {
					return Images.Cost;
				} else {
					return Images.Benefit;
				}
			}
		});
		
		TableViewerColumn valuationColumn = new TableViewerColumn(_tableViewerIdealSolution, SWT.NONE);
		valuationColumn.getColumn().setText("Valuation");
		valuationColumn.getColumn().pack();
		valuationColumn.getColumn().setResizable(false);
		valuationColumn.getColumn().setMoveable(false);
		valuationColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				TwoTuple valuation = (TwoTuple) data[1];

				String labelName = valuation.getLabel().getName();
				String alpha = Double.toString(valuation.getAlpha());
				
				if(alpha.equals("-0.0") || alpha.equals("0.0")) { //$NON-NLS-1$
					alpha = "0"; //$NON-NLS-1$
				}

				int size = 4;
				if(alpha.startsWith("-")) {
					size = 5;
				}
				
				if(alpha.length() > size) {
					alpha = alpha.substring(0, size);
				}
				
				if(alpha.length() > 1) {
					if(alpha.endsWith("0")) {
						alpha = alpha.substring(0, size - 1);
					}
				}
				return "(" + labelName + ", " + alpha + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		});
		
		GridLayout noIdealSolutionLayout = new GridLayout(1, true);
		noIdealSolutionLayout.marginWidth = 10;
		noIdealSolutionLayout.marginHeight = 10;
		
		Composite noIdealSolutionPanel = new Composite(solutionsPanel, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		noIdealSolutionPanel.setLayoutData(gridData);
		noIdealSolutionPanel.setLayout(noIdealSolutionLayout);
		
		Label noIdealSolutionLabel = new Label(noIdealSolutionPanel, SWT.NONE);
		noIdealSolutionLabel.setText("No ideal solution");
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		noIdealSolutionLabel.setLayoutData(gridData);
		
		_tableViewerNoIdealSolution = new TableViewer(noIdealSolutionPanel, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_tableViewerNoIdealSolution.getTable().setLayoutData(gridData);
		_tableViewerNoIdealSolution.getTable().setHeaderVisible(true);
		
		_noIdealSolutionProvider = new NoIdealSolutionTableViewerContentProvider();
		_tableViewerNoIdealSolution.setContentProvider(_noIdealSolutionProvider);

		criterionColumn = new TableViewerColumn(_tableViewerNoIdealSolution, SWT.NONE);
		criterionColumn.getColumn().setText("Criterion");
		criterionColumn.getColumn().pack();
		criterionColumn.getColumn().setResizable(false);
		criterionColumn.getColumn().setMoveable(false);
		criterionColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				return ((Criterion) data[0]).getCanonicalId();
			}
		});
		
		typeColumn = new TableViewerColumn(_tableViewerNoIdealSolution, SWT.NONE);
		typeColumn.getColumn().setText("Type");
		typeColumn.getColumn().pack();
		typeColumn.getColumn().setResizable(false);
		typeColumn.getColumn().setMoveable(false);
		typeColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return "";
			}
			
			@Override
			public Image getImage(Object element) {
				Object[] data = (Object[]) element;
				if(((Criterion) data[0]).getCost()) {
					return Images.Cost;
				} else {
					return Images.Benefit;
				}
			}
		});
		
		valuationColumn = new TableViewerColumn(_tableViewerNoIdealSolution, SWT.NONE);
		valuationColumn.getColumn().setText("Valuation");
		valuationColumn.getColumn().pack();
		valuationColumn.getColumn().setResizable(false);
		valuationColumn.getColumn().setMoveable(false);
		valuationColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				TwoTuple valuation = (TwoTuple) data[1];

				String labelName = valuation.getLabel().getName();
				String alpha = Double.toString(valuation.getAlpha());
				
				if(alpha.equals("-0.0") || alpha.equals("0.0")) { //$NON-NLS-1$
					alpha = "0"; //$NON-NLS-1$
				}

				int size = 4;
				if(alpha.startsWith("-")) {
					size = 5;
				}
				
				if(alpha.length() > size) {
					alpha = alpha.substring(0, size);
				}
				
				if(alpha.length() > 1) {
					if(alpha.endsWith("0")) {
						alpha = alpha.substring(0, size - 1);
					}
				}
				return "(" + labelName + ", " + alpha + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		});
		
		GridLayout colectiveValuationsLayout = new GridLayout(1, false);
		colectiveValuationsLayout.marginWidth = 10;
		colectiveValuationsLayout.marginHeight = 10;
		
		Composite colectiveValuationsPanel = new Composite(_aggregationEditorPanel, SWT.NONE);
		gridData = new GridData(SWT.CENTER, SWT.FILL, true, true, 1, 1);
		colectiveValuationsPanel.setLayoutData(gridData);
		colectiveValuationsPanel.setLayout(colectiveValuationsLayout);
		
		Label colectiveValuationsLabel = new Label(colectiveValuationsPanel, SWT.NONE);
		colectiveValuationsLabel.setText("Colective valuations");
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		colectiveValuationsLabel.setLayoutData(gridData);
		
		_tableViewerColectiveValuations = new TableViewer(colectiveValuationsPanel, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_tableViewerColectiveValuations.getTable().setLayoutData(gridData);
		_tableViewerColectiveValuations.getTable().setHeaderVisible(true);
		
		_colectiveValuationsProvider = new ColectiveValuationsTableViewerContentProvider();
		_tableViewerColectiveValuations.setContentProvider(_colectiveValuationsProvider);

		TableViewerColumn alternativeColumn = new TableViewerColumn(_tableViewerColectiveValuations, SWT.NONE);
		alternativeColumn.getColumn().setText("Alternative");
		alternativeColumn.getColumn().pack();
		alternativeColumn.getColumn().setResizable(false);
		alternativeColumn.getColumn().setMoveable(false);
		alternativeColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				return ((Alternative) data[0]).getId();
			}
		});
		
		criterionColumn = new TableViewerColumn(_tableViewerColectiveValuations, SWT.NONE);
		criterionColumn.getColumn().setText("Criterion");
		criterionColumn.getColumn().pack();
		criterionColumn.getColumn().setResizable(false);
		criterionColumn.getColumn().setMoveable(false);
		criterionColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				return ((Criterion) data[1]).getCanonicalId();
			}
		});
		
		typeColumn = new TableViewerColumn(_tableViewerColectiveValuations, SWT.NONE);
		typeColumn.getColumn().setText("Type");
		typeColumn.getColumn().pack();
		typeColumn.getColumn().setResizable(false);
		typeColumn.getColumn().setMoveable(false);
		typeColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return "";
			}
			
			@Override
			public Image getImage(Object element) {
				Object[] data = (Object[]) element;
				if(((Criterion) data[1]).getCost()) {
					return Images.Cost;
				} else {
					return Images.Benefit;
				}
			}
		});
		
		valuationColumn = new TableViewerColumn(_tableViewerColectiveValuations, SWT.NONE);
		valuationColumn.getColumn().setText("Colective valuation");
		valuationColumn.getColumn().pack();
		valuationColumn.getColumn().setResizable(false);
		valuationColumn.getColumn().setMoveable(false);
		valuationColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				TwoTuple valuation = (TwoTuple) data[2];

				String labelName = valuation.getLabel().getName();
				String alpha = Double.toString(valuation.getAlpha());
				
				if(alpha.equals("-0.0") || alpha.equals("0.0")) { //$NON-NLS-1$
					alpha = "0"; //$NON-NLS-1$
				}

				int size = 4;
				if(alpha.startsWith("-")) {
					size = 5;
				}
				
				if(alpha.length() > size) {
					alpha = alpha.substring(0, size);
				}
				
				if(alpha.length() > 1) {
					if(alpha.endsWith("0")) {
						alpha = alpha.substring(0, size - 1);
					}
				}
				return "(" + labelName + ", " + alpha + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		});
		
	}

	private void fillCombo() {
		AggregationOperatorsManager aggregationOperatorsManager = AggregationOperatorsManager.getInstance();
		TreeSet<String> aggregationOperatorsIds = new TreeSet<String>();
		MethodsUIManager methodsUIManager = MethodsUIManager.getInstance();	
		Set<EAggregationOperatorType> operatorsTypes = methodsUIManager.getActivateMethodUI().getMethod().getAggregationTypesSupported();

		String[] operatorsIds;
		for(EAggregationOperatorType operatorType: operatorsTypes) {
			operatorsIds = aggregationOperatorsManager.getAggregationOperatorsIdByType(operatorType);
			for(String operator: operatorsIds) {
				aggregationOperatorsIds.add(operator);
			}
		}
		
		List<String> aggregationOperatorsNames = new LinkedList<String>();
		AggregationOperator operator;
		for(int i = 0; i < aggregationOperatorsIds.size(); i++) {
			operator = aggregationOperatorsManager.getAggregationOperator((String) aggregationOperatorsIds.toArray()[i]);
			String name = "";
			if (operator instanceof WeightedAggregationOperator) {
				name = "(W) " + aggregationOperatorsManager.getAggregationOperator((String) aggregationOperatorsIds.toArray()[i]).getName();
				aggregationOperatorsNames.add(name);
			} else {
				name = aggregationOperatorsManager.getAggregationOperator((String) aggregationOperatorsIds.toArray()[i]).getName();
				aggregationOperatorsNames.add(name);
			}
			_operators.put(name, aggregationOperatorsIds.toArray(new String[0])[i]);
		}
		
		_aggregationOperatorCombo.setItems(aggregationOperatorsNames.toArray(new String[0]));
	}

	private void setOperator(String operator) {
		AggregationOperatorsManager aggregationOperatorsManager = AggregationOperatorsManager.getInstance();
		AggregationOperator aggregationOperator = aggregationOperatorsManager.getAggregationOperator(_operators.get(operator));
		
		Map<String, List<Double>> mapWeights = new HashMap<String, List<Double>>();
		if(aggregationOperator instanceof WeightedAggregationOperator) { 
			ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
			ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
			
			ProblemElement nullElement = null;
			ProblemElement[] secondary = getLeafElements(nullElement);
			WeightsDialog dialog = new WeightsDialog(Display.getCurrent().getActiveShell(), elementsSet.getAllElementExpertChildren(null), secondary, null, 1, "expert", "all experts");
			
			int exitValue = dialog.open();
			if(exitValue == WeightsDialog.SAVE) {
				mapWeights = dialog.getWeights();
			} else { 
				mapWeights = null;
			}
		}
		
		List<Object[]> decisionMatrix = _selectionPhase.calculateDecisionMatrix(aggregationOperator, mapWeights);
		_colectiveValuationsProvider.setInput(decisionMatrix);
		_tableViewerColectiveValuations.setInput(_colectiveValuationsProvider.getInput());
		
		List<Object[]> idealSolution = _selectionPhase.calculateIdealSolution();
		_idealSolutionProvider.setInput(idealSolution);
		_tableViewerIdealSolution.setInput(_idealSolutionProvider.getInput());
		
		List<Object[]> noIdealSolution = _selectionPhase.calculateNoIdealSolution();
		_noIdealSolutionProvider.setInput(noIdealSolution);
		_tableViewerNoIdealSolution.setInput(_noIdealSolutionProvider.getInput());
		
		_tableViewerColectiveValuations.getTable().getColumn(3).pack();
		_tableViewerIdealSolution.getTable().getColumn(0).pack();
		_tableViewerNoIdealSolution.getTable().getColumn(0).pack();
		_tableViewerIdealSolution.getTable().getColumn(2).pack();
		_tableViewerNoIdealSolution.getTable().getColumn(2).pack();
	}
	
	@Override
	public String getPartName() {
		return "Aggregation experts";
	}
	
	@Override
	public void setFocus() {
		_aggregationOperatorCombo.setFocus();	
	}

	@Override
	public void notifyStepStateChange() {
		if(_completed) {
			_ratingView.loadNextStep();
			_completed = false;
		}	
	}

	@Override
	public void notifyRatingView(RatingView rating) {
		_ratingView = rating;
	}
}
