package sinbad2.phasemethod.topsis.selection.ui.view;

import java.util.Collections;
import java.util.Comparator;
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

import sinbad2.core.workspace.Workspace;
import sinbad2.element.ProblemElement;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.topsis.selection.ui.Images;
import sinbad2.phasemethod.topsis.selection.ui.nls.Messages;
import sinbad2.phasemethod.topsis.selection.SelectionPhase;
import sinbad2.phasemethod.topsis.selection.ui.view.dialog.WeightsDialog;
import sinbad2.phasemethod.topsis.selection.ui.view.provider.DistanceIdealSolutionTableViewerContentProvider;
import sinbad2.phasemethod.topsis.selection.ui.view.provider.PositiveNegativeTableViewerContentProvider;
import sinbad2.resolutionphase.rating.ui.listener.IStepStateListener;
import sinbad2.resolutionphase.rating.ui.view.RatingView;

public class CalculateDistances extends ViewPart implements IStepStateListener {
	
	public static final String ID = "flintstones.phasemethod.topsis.selection.ui.view.calculatedistances"; //$NON-NLS-1$
	
	private Composite _parent;
	private Composite _distanceEditorPanel;
	
	private Combo _distanceCombo;
	
	private TableViewer _tableViewerPositiveNegative;
	private TableViewer _tableViewerIdealSolution;
	
	private PositiveNegativeTableViewerContentProvider _positiveNegativeProvider;
	private DistanceIdealSolutionTableViewerContentProvider _distanceIdealSolutionProvider;
	
	private SelectionPhase _selectionPhase;
	
	private static class DataComparator implements Comparator<Object[]> {
		public int compare(Object[] d1, Object[] d2) {
			Alternative a1 = (Alternative) d1[0];
			Alternative a2 = (Alternative) d2[0];
			int compare = a1.compareTo(a2);
			
			if(compare != 0) {
				return compare;
			} else {
				Criterion c1 = (Criterion) d1[1];
				Criterion c2 = (Criterion) d2[1];
				return c1.compareTo(c2);
			}
		}
	}
	
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
		selectDistanceLabel.setText(Messages.CalculateDistances_Select_distance_operator);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1);
		selectDistanceLabel.setLayoutData(gridData);
		
		_distanceCombo = new Combo(selectDistancePanel, SWT.READ_ONLY);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1);
		_distanceCombo.setLayoutData(gridData);
		_distanceCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDistance(_distanceCombo.getItem(_distanceCombo.getSelectionIndex()));
				
				notifyStepStateChange();
			}
		});
		
		fillCombo();
		
		GridLayout informationLayout = new GridLayout(2, true);
		informationLayout.marginWidth = 10;
		informationLayout.marginHeight = 10;
		
		Composite informationPanel = new Composite(_distanceEditorPanel, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		informationPanel.setLayoutData(gridData);
		informationPanel.setLayout(informationLayout);
		
		GridLayout positiveNegativeLayout = new GridLayout(1, true);
		positiveNegativeLayout.marginWidth = 10;
		positiveNegativeLayout.marginHeight = 10;
		
		Composite positveNegativePanel = new Composite(informationPanel, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		positveNegativePanel.setLayoutData(gridData);
		positveNegativePanel.setLayout(positiveNegativeLayout);
		
		Label positiveNegativeLabel = new Label(positveNegativePanel, SWT.NONE);
		positiveNegativeLabel.setText(Messages.CalculateDistances_Ideal_solution);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		positiveNegativeLabel.setLayoutData(gridData);
		
		_tableViewerPositiveNegative = new TableViewer(positveNegativePanel, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_tableViewerPositiveNegative.getTable().setLayoutData(gridData);
		_tableViewerPositiveNegative.getTable().setHeaderVisible(true);
		
		_positiveNegativeProvider = new PositiveNegativeTableViewerContentProvider();
		_tableViewerPositiveNegative.setContentProvider(_positiveNegativeProvider);

		TableViewerColumn alternativeColumn = new TableViewerColumn(_tableViewerPositiveNegative, SWT.NONE);
		alternativeColumn.getColumn().setText(Messages.CalculateDistances_Alternative);
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
		criterionColumn.getColumn().setText(Messages.CalculateDistances_Criterion);
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
		
		TableViewerColumn typeColumn = new TableViewerColumn(_tableViewerPositiveNegative, SWT.NONE);
		typeColumn.getColumn().setText(Messages.CalculateDistances_Type);
		typeColumn.getColumn().pack();
		typeColumn.getColumn().setResizable(false);
		typeColumn.getColumn().setMoveable(false);
		typeColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ""; //$NON-NLS-1$
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
		positiveColumn.getColumn().setText(Messages.CalculateDistances_Positive_distance);
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
		negativeColumn.getColumn().setText(Messages.CalculateDistances_Negative_distance);
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
		
		Composite distanceIdealSolutionPanel = new Composite(informationPanel, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		distanceIdealSolutionPanel.setLayoutData(gridData);
		distanceIdealSolutionPanel.setLayout(distanceIdealSolutionLayout);
		
		Label colectiveValuationsLabel = new Label(distanceIdealSolutionPanel, SWT.NONE);
		colectiveValuationsLabel.setText(Messages.CalculateDistances_Ideal_solution_distance);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		colectiveValuationsLabel.setLayoutData(gridData);
		
		_tableViewerIdealSolution = new TableViewer(distanceIdealSolutionPanel, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_tableViewerIdealSolution.getTable().setLayoutData(gridData);
		_tableViewerIdealSolution.getTable().setHeaderVisible(true);
		
		_distanceIdealSolutionProvider = new DistanceIdealSolutionTableViewerContentProvider();
		_tableViewerIdealSolution.setContentProvider(_distanceIdealSolutionProvider);

		TableViewerColumn ranking = new TableViewerColumn(_tableViewerIdealSolution, SWT.NONE);
		ranking.getColumn().setText(Messages.CalculateDistances_Ranking);
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
		alternativeColumn.getColumn().setText(Messages.CalculateDistances_Alternative);
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
		positiveDistanceColumn.getColumn().setText(Messages.CalculateDistances_Positive_distance);
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
		negativeDistanceColumn.getColumn().setText(Messages.CalculateDistances_Negative_distance);
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
		closenessCoefficient.getColumn().setText(Messages.CalculateDistances_Closeness_coefficient);
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
		_distanceCombo.add(Messages.CalculateDistances_Eucliden_distance);
		_distanceCombo.add(Messages.CalculateDistances_Weighted_distance);
	}
	
	private void setDistance(String distance) {
		Map<String, List<Double>> mapWeights;
		List<Double> weights = null;
		
		if(distance.contains(Messages.CalculateDistances_Weighted)) {
			ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
			ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
			
			ProblemElement nullElement = null;
			ProblemElement[] secondary = getLeafElements(nullElement);
			WeightsDialog dialog = new WeightsDialog(Display.getCurrent().getActiveShell(), elementsSet.getAllElementCriterionSubcriteria(null), secondary, null, 1, Messages.CalculateDistances_criterion, Messages.CalculateDistances_all_criteria);
			
			int exitValue = dialog.open();
			if(exitValue == WeightsDialog.SAVE) {
				mapWeights = dialog.getWeights();
				if(mapWeights.size() == 1) {
					weights = mapWeights.get(null);
				} else {
					weights = new LinkedList<Double>();
					List<List<Double>> weightsByCriterion = new LinkedList<List<Double>>();
					double weightByCriterion;
					int i = 0;
					while(i < mapWeights.size()) {
						List<Double> weightsSameCriterion = new LinkedList<Double>();
						for(Expert expert: elementsSet.getAllExperts()) {
							List<Double> expertWeights= mapWeights.get(expert.getCanonicalId());
							weightByCriterion = expertWeights.get(i);
							weightsSameCriterion.add(weightByCriterion);
						}
						weightsByCriterion.add(weightsSameCriterion);
						++i;
					}
					
					for(List<Double> weightCriterion: weightsByCriterion) {
						weights.add(calculateArithmeticMean(weightCriterion));
					}
				}
			}
		}
		
		List<Object[]> idealDistances = _selectionPhase.calculateIdealEuclideanDistanceByCriterion(weights);
		List<Object[]> noIdealDistances = _selectionPhase.calculateNoIdealEuclideanDistanceByCriterion(weights);
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

		Collections.sort(distances, new DataComparator());
		
		_positiveNegativeProvider.setInput(distances);
		_tableViewerPositiveNegative.setInput(_positiveNegativeProvider.getInput());
		
		List<Object[]> coefficients = _selectionPhase.calculateClosenessCoefficient();
		_distanceIdealSolutionProvider.setInput(coefficients);
		_tableViewerIdealSolution.setInput(_distanceIdealSolutionProvider.getInput());
	}
	
	private Double calculateArithmeticMean(List<Double> weightCriterion) {
		double result = 0;
		for(double weight: weightCriterion) {
			result += weight;
		}
		
		return result / weightCriterion.size();
	}

	@Override
	public String getPartName() {
		return Messages.CalculateDistances_Calculate_distances;
	}

	@Override
	public void setFocus() {
		_distanceCombo.setFocus();
	}

	@Override
	public void notifyStepStateChange() {
		Workspace.getWorkspace().updatePhases();	
	}

	@Override
	public void setRatingView(RatingView rating) {}

}
