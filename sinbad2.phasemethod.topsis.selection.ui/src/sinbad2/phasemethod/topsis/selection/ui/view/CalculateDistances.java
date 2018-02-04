package sinbad2.phasemethod.topsis.selection.ui.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import sinbad2.core.workspace.Workspace;
import sinbad2.domain.linguistic.fuzzy.ui.jfreechart.LinguisticDomainChart;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.topsis.selection.SelectionPhase;
import sinbad2.phasemethod.topsis.selection.ui.listener.IChangeWeightListener;
import sinbad2.phasemethod.topsis.selection.ui.nls.Messages;
import sinbad2.phasemethod.topsis.selection.ui.view.provider.ClosenessCoefficientsTableViewerContentProvider;
import sinbad2.phasemethod.topsis.selection.ui.view.provider.ExpertsWeightContentProvider;
import sinbad2.phasemethod.topsis.selection.ui.view.provider.PositiveNegativeTableViewerContentProvider;
import sinbad2.resolutionphase.ResolutionPhasesManager;
import sinbad2.resolutionphase.rating.ui.listener.IStepStateListener;
import sinbad2.resolutionphase.rating.ui.view.RatingView;
import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.twoTuple.TwoTuple;

public class CalculateDistances extends ViewPart implements IStepStateListener, IChangeWeightListener {
	
	public static final String ID = "flintstones.phasemethod.topsis.selection.ui.view.calculatedistances"; //$NON-NLS-1$
	
	private Composite _parent;
	private Composite _informationPanel;
	private Composite _distanceEditorPanel;
	private Composite _chartView;
	
	private TableViewer _tableViewerPositiveNegative;
	private TableViewer _tableViewerIdealSolution;
	
	private PositiveNegativeTableViewerContentProvider _positiveNegativeProvider;
	private ClosenessCoefficientsTableViewerContentProvider closenessCoefficientsProvider;
	
	private LinguisticDomainChart _chart;
	
	private ProblemElementsSet _elementsSet;
	
	private SelectionPhase _selectionPhase;
	private SensitivityAnalysis _sensitivityAnalysis;
	
	private static class DataComparator implements Comparator<Object[]> {
		public int compare(Object[] d1, Object[] d2) {
			Alternative a1 = (Alternative) d1[0];
			Alternative a2 = (Alternative) d2[0];
			return a1.compareTo(a2);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		_selectionPhase = (SelectionPhase) pmm.getPhaseMethod(SelectionPhase.ID).getImplementation();
		_sensitivityAnalysis = (SensitivityAnalysis) ResolutionPhasesManager.getInstance().getResolutionPhase(SensitivityAnalysis.ID).getImplementation();
		
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();
		
		_parent = parent;

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_parent.setLayoutData(gridData);
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 15;
		layout.verticalSpacing = 15;
		_parent.setLayout(layout);
		
		ExpertsWeightContentProvider.registerChangeWeightListener(this);

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
		
		GridLayout informationLayout = new GridLayout(2, true);
		informationLayout.marginWidth = 10;
		informationLayout.marginHeight = 10;
		
		_informationPanel = new Composite(_distanceEditorPanel, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_informationPanel.setLayoutData(gridData);
		_informationPanel.setLayout(informationLayout);
		
		createDistancesTable();
		createClosenessCoefficientsTable();
		createChart();
		setSensitivityAnalysisData();
	}

	private void createDistancesTable() {
		GridLayout positiveNegativeLayout = new GridLayout(1, true);
		positiveNegativeLayout.marginWidth = 10;
		positiveNegativeLayout.marginHeight = 10;
		
		Composite positveNegativePanel = new Composite(_informationPanel, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		positveNegativePanel.setLayoutData(gridData);
		positveNegativePanel.setLayout(positiveNegativeLayout);
		
		Label positiveNegativeLabel = new Label(positveNegativePanel, SWT.NONE);
		positiveNegativeLabel.setText(Messages.CalculateDistances_Ideal_solution);
		FontData fontData = positiveNegativeLabel.getFont().getFontData()[0];
		positiveNegativeLabel.setFont(new Font(positiveNegativeLabel.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD)));
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
		
		TableViewerColumn positiveColumn = new TableViewerColumn(_tableViewerPositiveNegative, SWT.NONE);
		positiveColumn.getColumn().setText(Messages.CalculateDistances_Positive_distance);
		positiveColumn.getColumn().setResizable(false);
		positiveColumn.getColumn().setMoveable(false);
		positiveColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				TwoTuple distance = (TwoTuple) data[1];
		
				String labelName = distance.getLabel().getName();
				String alpha = Double.toString(distance.getAlpha());
				
				if(alpha.equals("-0.0") || alpha.equals("0.0")) { //$NON-NLS-1$ //$NON-NLS-2$
					alpha = "0"; //$NON-NLS-1$
				}

				int size = 4;
				if(alpha.startsWith("-")) { //$NON-NLS-1$
					size = 5;
				}
				
				if(alpha.length() > size) {
					alpha = alpha.substring(0, size);
				}
				
				if(alpha.length() > 1) {
					if(alpha.endsWith("0")) { //$NON-NLS-1$
						alpha = alpha.substring(0, size - 1);
					}
				}
				return "(" + labelName + ", " + alpha + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		});

		TableViewerColumn negativeColumn = new TableViewerColumn(_tableViewerPositiveNegative, SWT.NONE);
		negativeColumn.getColumn().setText(Messages.CalculateDistances_Negative_distance);
		negativeColumn.getColumn().setResizable(false);
		negativeColumn.getColumn().setMoveable(false);
		negativeColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				TwoTuple distance = (TwoTuple) data[2];
				
				String labelName = distance.getLabel().getName();
				String alpha = Double.toString(distance.getAlpha());
				
				if(alpha.equals("-0.0") || alpha.equals("0.0")) { //$NON-NLS-1$ //$NON-NLS-2$
					alpha = "0"; //$NON-NLS-1$
				}

				int size = 4;
				if(alpha.startsWith("-")) { //$NON-NLS-1$
					size = 5;
				}
				
				if(alpha.length() > size) {
					alpha = alpha.substring(0, size);
				}
				
				if(alpha.length() > 1) {
					if(alpha.endsWith("0")) { //$NON-NLS-1$
						alpha = alpha.substring(0, size - 1);
					}
				}
				return "(" + labelName + ", " + alpha + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				
			}
		});
		
		setInputDistancesTable();
		
		alternativeColumn.getColumn().pack();
		positiveColumn.getColumn().pack();
		negativeColumn.getColumn().pack();
	}
	
	private void setInputDistancesTable() {
		List<Object[]> distances = new LinkedList<Object[]>();
		List<TwoTuple> idealDistances = _selectionPhase.getIdealDistances();
		List<TwoTuple> noIdealDistances = _selectionPhase.getNoIdealDistances();
		for(int i = 0; i < _selectionPhase.getIdealDistances().size(); ++i) {
			Object[] distanceData = new Object[3];
			distanceData[0] = _elementsSet.getAlternatives().get(i);
			distanceData[1] = idealDistances.get(i);
			distanceData[2] = noIdealDistances.get(i);
			distances.add(distanceData);
		}

		Collections.sort(distances, new DataComparator());
		
		_positiveNegativeProvider.setInput(distances);
		_tableViewerPositiveNegative.setInput(_positiveNegativeProvider.getInput());
		
	}

	private void createClosenessCoefficientsTable() {
		GridLayout distanceIdealSolutionLayout = new GridLayout(1, false);
		distanceIdealSolutionLayout.marginWidth = 10;
		distanceIdealSolutionLayout.marginHeight = 10;
		
		Composite distanceIdealSolutionPanel = new Composite(_informationPanel, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		distanceIdealSolutionPanel.setLayoutData(gridData);
		distanceIdealSolutionPanel.setLayout(distanceIdealSolutionLayout);
		
		Label colLectiveValuationsLabel = new Label(distanceIdealSolutionPanel, SWT.NONE);
		colLectiveValuationsLabel.setText(Messages.CalculateDistances_Ideal_solution_distance);
		FontData fontData = colLectiveValuationsLabel.getFont().getFontData()[0];
		colLectiveValuationsLabel.setFont(new Font(colLectiveValuationsLabel.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD)));
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		colLectiveValuationsLabel.setLayoutData(gridData);
		
		_tableViewerIdealSolution = new TableViewer(distanceIdealSolutionPanel, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_tableViewerIdealSolution.getTable().setLayoutData(gridData);
		_tableViewerIdealSolution.getTable().setHeaderVisible(true);
		
		closenessCoefficientsProvider = new ClosenessCoefficientsTableViewerContentProvider();
		_tableViewerIdealSolution.setContentProvider(closenessCoefficientsProvider);

		TableViewerColumn alternativeColumn = new TableViewerColumn(_tableViewerIdealSolution, SWT.NONE);
		alternativeColumn.getColumn().setText(Messages.CalculateDistances_Alternative);
		alternativeColumn.getColumn().setResizable(false);
		alternativeColumn.getColumn().setMoveable(false);
		alternativeColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				return ((Alternative) data[0]).getId();
			}
		});
		
		TableViewerColumn closenessCoefficient = new TableViewerColumn(_tableViewerIdealSolution, SWT.NONE);
		closenessCoefficient.getColumn().setText(Messages.CalculateDistances_Closeness_coefficient);
		closenessCoefficient.getColumn().setResizable(false);
		closenessCoefficient.getColumn().setMoveable(false);
		closenessCoefficient.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				TwoTuple distance = (TwoTuple) data[1];
				
				String labelName = distance.getLabel().getName();
				String alpha = Double.toString(distance.getAlpha());
				
				if(alpha.equals("-0.0") || alpha.equals("0.0")) { //$NON-NLS-1$ //$NON-NLS-2$
					alpha = "0"; //$NON-NLS-1$
				}

				int size = 4;
				if(alpha.startsWith("-")) { //$NON-NLS-1$
					size = 5;
				}
				
				if(alpha.length() > size) {
					alpha = alpha.substring(0, size);
				}
				
				if(alpha.length() > 1) {
					if(alpha.endsWith("0")) { //$NON-NLS-1$
						alpha = alpha.substring(0, size - 1);
					}
				}
				return "(" + labelName + ", " + alpha + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		});
		
		TableViewerColumn ranking = new TableViewerColumn(_tableViewerIdealSolution, SWT.NONE);
		ranking.getColumn().setText(Messages.CalculateDistances_Ranking);
		ranking.getColumn().setResizable(false);
		ranking.getColumn().setMoveable(false);
		ranking.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				return (Integer.toString((int) data[2]));
			}
		});
		
		setInputCoefficientsTable();
		
		alternativeColumn.getColumn().pack();
		closenessCoefficient.getColumn().pack();
		ranking.getColumn().pack();
	}

	private void setInputCoefficientsTable() {
		closenessCoefficientsProvider.setInput(_selectionPhase.getClosenessCoeficient());
		_tableViewerIdealSolution.setInput(closenessCoefficientsProvider.getInput());
	}

	private void createChart() {
		Composite chartViewParent = new Composite(_distanceEditorPanel, SWT.BORDER);
		GridLayout layout = new GridLayout(1, false);
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

		_chartView = new Composite(chartViewParent, SWT.BORDER);
		_chartView.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		_chart = new LinguisticDomainChart();
		_chart.initialize(_selectionPhase.getDistanceDomain(), _chartView, _chartView.getSize().x, _chartView.getSize().y, SWT.BORDER);
		
		_chartView.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				_chart.updateSize(_chartView.getSize().x, _chartView.getSize().y);
			}
		});
		
		displayAlternatives();
	}
	
	private void displayAlternatives() {
		List<TwoTuple> closenessCoefficients = _selectionPhase.getClosenessCoeficient();
		
		String[] alternatives = new String[closenessCoefficients.size()];
		int[] pos = new int[alternatives.length];
		double[] alpha = new double[alternatives.length];
		for(int i = 0; i < alternatives.length; ++i) {
			alternatives[i] = Integer.toString(i + 1);
			pos[i] = _selectionPhase.getDistanceDomain().getLabelSet().getPos(closenessCoefficients.get(i).getLabel());
			alpha[i] = closenessCoefficients.get(i).getAlpha();
		}
		_chart.displayAlternatives(alternatives, pos, alpha);
	}

	private void setSensitivityAnalysisData() {
		_sensitivityAnalysis.setDomain(_selectionPhase.getUnifiedDomain());
		_sensitivityAnalysis.setAlternatives(_elementsSet.getAlternatives());
		
		setDecisionMatrix();
		setWeights();
		setFinalPreferences();
		
		
		notifyStepStateChange();
	}
	
	private void setDecisionMatrix() {
		Valuation[][] dm = _selectionPhase.getDecisionMatrix();
		Double[][] dmNumbers = new Double[dm.length][dm[0].length];
		for(int i = 0; i < dm.length; ++i) {
			for(int j = 0; j < dm[i].length; ++j) {
				dmNumbers[i][j] = (double) (Math.round(((TwoTuple) dm[i][j]).calculateInverseDelta() * 100d) / 100d);
			}
		}
		
		_sensitivityAnalysis.setDecisionMatrix(dmNumbers);
	}

	private void setWeights() {
		TwoTuple[] weights = _selectionPhase.getCriteriaWeights();
		Double[] weightsNumber = new Double[weights.length];
		int wcont = 0;
		double acum = 0;
		for(TwoTuple weight: weights) {
			weightsNumber[wcont] = weight.calculateInverseDelta();
			acum += weightsNumber[wcont];
			wcont++;
		}
		
		for(int i = 0; i < weightsNumber.length; ++i) {
			weightsNumber[i] /= acum;
		}
		
		_sensitivityAnalysis.setWeights(weightsNumber);
	}

	private void setFinalPreferences() {
		List<TwoTuple> coefficients = _selectionPhase.getClosenessCoeficient();
		Double[] preferences = new Double[coefficients.size()];
		int cont = 0;
		for(TwoTuple c: coefficients) {
			preferences[cont] = c.calculateInverseDelta();
			cont++;
		}
		_sensitivityAnalysis.setAlternativesFinalPreferences(preferences);
	}

	@Override
	public String getPartName() {
		return Messages.CalculateDistances_Calculate_distances;
	}

	@Override
	public void setFocus() {}

	@Override
	public void notifyStepStateChange() {
		Workspace.getWorkspace().updatePhases();	
	}

	@Override
	public void setRatingView(RatingView rating) {}

	@Override
	public void notifyWeigthChanged() {
		refresh();
	}

	private void refresh() {
		setInputDistancesTable();
		setInputCoefficientsTable();
		refreshChart();
	}

	private void refreshChart() {
		disposeControlsChartComposite();

		_chart = new LinguisticDomainChart();
		_chart.initialize(_selectionPhase.getDistanceDomain(), _chartView, _chartView.getSize().x, _chartView.getSize().y, SWT.BORDER);
		
		displayAlternatives();
	}

	private void disposeControlsChartComposite() {
		for(Control c: _chartView.getChildren()) {
			c.dispose();
		}
	}

}
