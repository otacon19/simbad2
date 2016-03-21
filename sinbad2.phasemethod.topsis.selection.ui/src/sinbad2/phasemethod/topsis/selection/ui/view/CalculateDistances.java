package sinbad2.phasemethod.topsis.selection.ui.view;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

import sinbad2.element.ProblemElement;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.topsis.selection.ui.Images;
import sinbad2.phasemethod.topsis.selection.SelectionPhase;
import sinbad2.phasemethod.topsis.selection.ui.view.dialog.WeightsDialog;
import sinbad2.phasemethod.topsis.selection.ui.view.provider.DistanceIdealSolutionTableViewerContentProvider;
import sinbad2.phasemethod.topsis.selection.ui.view.provider.PositiveNegativeTableViewerContentProvider;

public class CalculateDistances extends ViewPart {
	
	public static final String ID = "flintstones.phasemethod.topsis.selection.ui.view.calculatedistances";
	
	private Composite _parent;
	private Composite _distanceEditorPanel;
	
	private Combo _distanceCombo;
	
	private TableViewer _tableViewerPositiveNegative;
	private TableViewer _tableViewerIdealSolution;
	
	private PositiveNegativeTableViewerContentProvider _positiveNegativeProvider;
	private DistanceIdealSolutionTableViewerContentProvider _distanceIdealSolutionProvider;
	
	private SelectionPhase _selectionPhase;
	
	private static ProblemElement[] getLeafElements(ProblemElement root) {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();

		List<ProblemElement> result = new LinkedList<ProblemElement>();
		List<Expert> children = elementsSet.getAllExpertChildren((Expert) root);
		for(Expert child : children) {
			if(!child.hasChildren()) {
				result.add(child);
			} else {
				ProblemElement[] subchildren = getLeafElements(child);
				for(ProblemElement subchild : subchildren) {
					result.add(subchild);
				}
			}
		}
		
		return result.toArray(new ProblemElement[0]);
	}

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
		_distanceEditorPanel = new Composite(_parent, SWT.NONE);

		GridLayout ratingEditorPanelLayout = new GridLayout(1, false);
		ratingEditorPanelLayout.marginRight = 0;
		ratingEditorPanelLayout.verticalSpacing = 0;
		ratingEditorPanelLayout.marginWidth = 10;
		ratingEditorPanelLayout.marginHeight = 10;
		_distanceEditorPanel.setLayout(ratingEditorPanelLayout);
		_distanceEditorPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

		GridLayout selectDistanceLayout = new GridLayout(2, false);
		selectDistanceLayout.marginWidth = 10;
		selectDistanceLayout.marginHeight = 10;
		
		Composite selectDistancePanel = new Composite(_distanceEditorPanel, SWT.NONE);
		GridData gridData = new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1);
		selectDistancePanel.setLayoutData(gridData);
		selectDistancePanel.setLayout(selectDistanceLayout);
		
		Label selectDistanceLabel = new Label(selectDistancePanel, SWT.NONE);
		selectDistanceLabel.setText("Select distance operator");
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1);
		selectDistanceLabel.setLayoutData(gridData);
		
		_distanceCombo = new Combo(selectDistancePanel, SWT.READ_ONLY);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1);
		_distanceCombo.setLayoutData(gridData);
		_distanceCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDistance(_distanceCombo.getItem(_distanceCombo.getSelectionIndex()));
			}
		});
		
		fillCombo();
		
		GridLayout positiveNegativeLayout = new GridLayout(1, true);
		positiveNegativeLayout.marginWidth = 10;
		positiveNegativeLayout.marginHeight = 10;
		
		Composite positveNegativePanel = new Composite(_distanceEditorPanel, SWT.BORDER);
		gridData = new GridData(SWT.CENTER, SWT.FILL, true, true, 1, 1);
		positveNegativePanel.setLayoutData(gridData);
		positveNegativePanel.setLayout(positiveNegativeLayout);
		
		Label positiveNegativeLabel = new Label(positveNegativePanel, SWT.NONE);
		positiveNegativeLabel.setText("Ideal solution");
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		positiveNegativeLabel.setLayoutData(gridData);
		
		_tableViewerPositiveNegative = new TableViewer(positveNegativePanel, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_tableViewerPositiveNegative.getTable().setLayoutData(gridData);
		_tableViewerPositiveNegative.getTable().setHeaderVisible(true);
		
		_positiveNegativeProvider = new PositiveNegativeTableViewerContentProvider();
		_tableViewerPositiveNegative.setContentProvider(_positiveNegativeProvider);

		TableViewerColumn alternativeColumn = new TableViewerColumn(_tableViewerPositiveNegative, SWT.NONE);
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
		
		TableViewerColumn criterionColumn = new TableViewerColumn(_tableViewerPositiveNegative, SWT.NONE);
		criterionColumn.getColumn().setText("Criterion");
		criterionColumn.getColumn().pack();
		criterionColumn.getColumn().setResizable(false);
		criterionColumn.getColumn().setMoveable(false);
		criterionColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				return ((Criterion) data[1]).getId();
			}
		});
		
		TableViewerColumn typeColumn = new TableViewerColumn(_tableViewerPositiveNegative, SWT.NONE);
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
		
		TableViewerColumn positiveColumn = new TableViewerColumn(_tableViewerPositiveNegative, SWT.NONE);
		positiveColumn.getColumn().setText("Positive distance");
		positiveColumn.getColumn().pack();
		positiveColumn.getColumn().setResizable(false);
		positiveColumn.getColumn().setMoveable(false);
		positiveColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				double distance = (double) data[2];
				double rounded = Math.floor(100 * distance + 0.5) / 100;
				
				return Double.toString(rounded);
			}
		});
		

		TableViewerColumn negativeColumn = new TableViewerColumn(_tableViewerPositiveNegative, SWT.NONE);
		negativeColumn.getColumn().setText("Negative distance");
		negativeColumn.getColumn().pack();
		negativeColumn.getColumn().setResizable(false);
		negativeColumn.getColumn().setMoveable(false);
		negativeColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				double distance = (double) data[3];
				double rounded = Math.floor(100 * distance + 0.5) / 100;
				
				return Double.toString(rounded);
			}
		});
	
		
		GridLayout distanceIdealSolutionLayout = new GridLayout(1, false);
		distanceIdealSolutionLayout.marginWidth = 10;
		distanceIdealSolutionLayout.marginHeight = 10;
		
		Composite distanceIdealSolutionPanel = new Composite(_distanceEditorPanel, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		distanceIdealSolutionPanel.setLayoutData(gridData);
		distanceIdealSolutionPanel.setLayout(distanceIdealSolutionLayout);
		
		Label colectiveValuationsLabel = new Label(distanceIdealSolutionPanel, SWT.NONE);
		colectiveValuationsLabel.setText("Ideal solution distance");
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		colectiveValuationsLabel.setLayoutData(gridData);
		
		_tableViewerIdealSolution = new TableViewer(distanceIdealSolutionPanel, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_tableViewerIdealSolution.getTable().setLayoutData(gridData);
		_tableViewerIdealSolution.getTable().setHeaderVisible(true);
		
		_distanceIdealSolutionProvider = new DistanceIdealSolutionTableViewerContentProvider();
		_tableViewerIdealSolution.setContentProvider(_distanceIdealSolutionProvider);

		TableViewerColumn ranking = new TableViewerColumn(_tableViewerIdealSolution, SWT.NONE);
		ranking.getColumn().setText("Ranking");
		ranking.getColumn().pack();
		ranking.getColumn().setResizable(false);
		ranking.getColumn().setMoveable(false);
		ranking.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				return ((String) data[0]);
			}
		});
		
		alternativeColumn = new TableViewerColumn(_tableViewerIdealSolution, SWT.NONE);
		alternativeColumn.getColumn().setText("Alternative");
		alternativeColumn.getColumn().pack();
		alternativeColumn.getColumn().setResizable(false);
		alternativeColumn.getColumn().setMoveable(false);
		alternativeColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				return ((Alternative) data[1]).getId();
			}
		});
		
		TableViewerColumn positiveDistanceColumn = new TableViewerColumn(_tableViewerIdealSolution, SWT.NONE);
		positiveDistanceColumn.getColumn().setText("Positive distance");
		positiveDistanceColumn.getColumn().pack();
		positiveDistanceColumn.getColumn().setResizable(false);
		positiveDistanceColumn.getColumn().setMoveable(false);
		positiveDistanceColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				double distance = (double) data[2];
				double rounded = Math.floor(100 * distance + 0.5) / 100;
				
				return Double.toString(rounded);
			}
		});
		
		TableViewerColumn negativeDistanceColumn = new TableViewerColumn(_tableViewerIdealSolution, SWT.NONE);
		negativeDistanceColumn.getColumn().setText("Negative distance");
		negativeDistanceColumn.getColumn().pack();
		negativeDistanceColumn.getColumn().setResizable(false);
		negativeDistanceColumn.getColumn().setMoveable(false);
		negativeDistanceColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				double distance = (double) data[3];
				double rounded = Math.floor(100 * distance + 0.5) / 100;
				
				return Double.toString(rounded);
			}
		});
	
		TableViewerColumn closenessCoefficient = new TableViewerColumn(_tableViewerIdealSolution, SWT.NONE);
		closenessCoefficient.getColumn().setText("Closeness coefficient");
		closenessCoefficient.getColumn().pack();
		closenessCoefficient.getColumn().setResizable(false);
		closenessCoefficient.getColumn().setMoveable(false);
		closenessCoefficient.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				double coefficient = (double) data[4];
				double rounded = Math.floor(100 * coefficient + 0.5) / 100;
				
				return Double.toString(rounded);
			}
		});
		
	}

	private void fillCombo() {
		_distanceCombo.add("Euclidean distance");
		_distanceCombo.add("Weighted distance");
	}
	
	private void setDistance(String distance) {
		Map<String, List<Double>> mapWeights = new HashMap<String, List<Double>>();
		
		if(distance.contains("Weighted")) {
			ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
			ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
			
			ProblemElement nullElement = null;
			ProblemElement[] secondary = getLeafElements(nullElement);
			WeightsDialog dialog = new WeightsDialog(Display.getCurrent().getActiveShell(), elementsSet.getAllElementCriterionSubcriteria(null), secondary, null, 1, "criterion", "all criteria");
			
			int exitValue = dialog.open();
			if(exitValue == WeightsDialog.SAVE) {
				mapWeights = dialog.getWeights();
			} else { 
				mapWeights = null;
			}
		}
		
		List<Object[]> idealDistances = _selectionPhase.calculateIdealEuclideanDistance(mapWeights.get(null));
		List<Object[]> noIdealDistances = _selectionPhase.calculateNoIdealEuclideanDistance(mapWeights.get(null));
		List<Object[]> distances = new LinkedList<Object[]>();
		
		for(int i = 0; i < idealDistances.size(); ++i) {
			Object[] idealData = idealDistances.get(i);
			Object[] noIdealData = noIdealDistances.get(i);
			Object[] distanceData = new Object[4];
			distanceData[0] = idealData[0];
			distanceData[1] = idealData[1];
			distanceData[2] = idealData[2];
			distanceData[3] = noIdealData[2];
			distances.add(distanceData);
		}

		_positiveNegativeProvider.setInput(distances);
		_tableViewerPositiveNegative.setInput(_positiveNegativeProvider.getInput());
		
		List<Object[]> coefficients = _selectionPhase.calculateClosenessCoefficient();
		_distanceIdealSolutionProvider.setInput(coefficients);
		_tableViewerIdealSolution.setInput(_distanceIdealSolutionProvider.getInput());
	}
	
	@Override
	public String getPartName() {
		return "Calculate distances";
	}

	@Override
	public void setFocus() {
		_distanceCombo.setFocus();
	}

}
