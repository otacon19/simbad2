package sinbad2.phasemethod.topsis.selection.ui.view;


import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.AggregationOperatorsManager;
import sinbad2.aggregationoperator.EAggregationOperatorType;
import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.method.ui.MethodsUIManager;
import sinbad2.resolutionphase.rating.ui.listener.IStepStateListener;
import sinbad2.resolutionphase.rating.ui.view.RatingView;

public class AggregationExperts extends ViewPart implements IStepStateListener {

	public static final String ID = "flintstones.phasemethod.topsis.selection.ui.view.aggregationexperts";

	private Composite _parent;
	private Composite _aggregationEditorPanel;

	private Combo _aggregationOperatorCombo;
	
	private TableViewer _tableViewerIdealSolution;
	private TableViewer _tableViewerNoIdealSolution;
	private TableViewer _tableViewerColectiveValuations;

	@Override
	public void createPartControl(Composite parent) {
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

		TableViewerColumn criterionColumn = new TableViewerColumn(_tableViewerIdealSolution, SWT.NONE);
		criterionColumn.getColumn().setText("Criterion");
		criterionColumn.getColumn().pack();
		criterionColumn.getColumn().setResizable(false);
		criterionColumn.getColumn().setMoveable(false);
		criterionColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return "";
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
		});
		
		TableViewerColumn valuationColumn = new TableViewerColumn(_tableViewerIdealSolution, SWT.NONE);
		valuationColumn.getColumn().setText("Valuation");
		valuationColumn.getColumn().pack();
		valuationColumn.getColumn().setResizable(false);
		valuationColumn.getColumn().setMoveable(false);
		valuationColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return "";
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

		criterionColumn = new TableViewerColumn(_tableViewerNoIdealSolution, SWT.NONE);
		criterionColumn.getColumn().setText("Criterion");
		criterionColumn.getColumn().pack();
		criterionColumn.getColumn().setResizable(false);
		criterionColumn.getColumn().setMoveable(false);
		criterionColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return "";
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
		});
		
		valuationColumn = new TableViewerColumn(_tableViewerNoIdealSolution, SWT.NONE);
		valuationColumn.getColumn().setText("Valuation");
		valuationColumn.getColumn().pack();
		valuationColumn.getColumn().setResizable(false);
		valuationColumn.getColumn().setMoveable(false);
		valuationColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return "";
			}
		});
		
		GridLayout colectiveValuationsLayout = new GridLayout(1, false);
		selectOperatorLayout.marginWidth = 10;
		selectOperatorLayout.marginHeight = 10;
		
		Composite colectiveValuationsPanel = new Composite(_aggregationEditorPanel, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
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

		TableViewerColumn alternativeColumn = new TableViewerColumn(_tableViewerColectiveValuations, SWT.NONE);
		alternativeColumn.getColumn().setText("Alternative");
		alternativeColumn.getColumn().pack();
		alternativeColumn.getColumn().setResizable(false);
		alternativeColumn.getColumn().setMoveable(false);
		alternativeColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return "";
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
				return "";
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
		});
		
		valuationColumn = new TableViewerColumn(_tableViewerColectiveValuations, SWT.NONE);
		valuationColumn.getColumn().setText("Colective valuation");
		valuationColumn.getColumn().pack();
		valuationColumn.getColumn().setResizable(false);
		valuationColumn.getColumn().setMoveable(false);
		valuationColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return "";
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
			if (operator instanceof WeightedAggregationOperator) {
				aggregationOperatorsNames.add("(W) " + aggregationOperatorsManager.getAggregationOperator((String) aggregationOperatorsIds.toArray()[i]).getName());
			} else {
				aggregationOperatorsNames.add(aggregationOperatorsManager.getAggregationOperator((String) aggregationOperatorsIds.toArray()[i]).getName());
			}
		}
		
		_aggregationOperatorCombo.setItems(aggregationOperatorsNames.toArray(new String[0]));
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyRatingView(RatingView rating) {
		// TODO Auto-generated method stub
		
	}
}
