package sinbad2.phasemethod.topsis.selection.ui.view;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.topsis.selection.SelectionPhase;
import sinbad2.phasemethod.topsis.selection.ui.Images;
import sinbad2.phasemethod.topsis.selection.ui.listener.IChangeWeightListener;
import sinbad2.phasemethod.topsis.selection.ui.nls.Messages;
import sinbad2.phasemethod.topsis.selection.ui.view.provider.ExpertsWeightContentProvider;
import sinbad2.phasemethod.topsis.selection.ui.view.provider.IdealSolutionTableViewerContentProvider;
import sinbad2.phasemethod.topsis.selection.ui.view.provider.NoIdealSolutionTableViewerContentProvider;
import sinbad2.phasemethod.topsis.selection.ui.view.table.DecisionMatrixTable;
import sinbad2.phasemethod.topsis.unification.UnificationPhase;
import sinbad2.resolutionphase.rating.ui.listener.IStepStateListener;
import sinbad2.resolutionphase.rating.ui.view.RatingView;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.valuationset.ValuationKey;

public class CalculateSolutions extends ViewPart implements IStepStateListener, IChangeWeightListener {
	
	public static final String ID = "flintstones.phasemethod.topsis.selection.ui.view.aggregationexperts"; //$NON-NLS-1$
	
	private static final String[] FILTER_NAMES = { "Text files (*.txt)" }; //$NON-NLS-1$
	private static final String[] FILTER_EXTS = { "*.txt" }; //$NON-NLS-1$

	private Composite _parent;
	private Composite _solutionsComposite;
	
	private TableViewer _tableViewerIdealSolution;
	private TableViewer _tableViewerNoIdealSolution;
	private DecisionMatrixTable _decisionMatrixTable;
	
	private IdealSolutionTableViewerContentProvider _idealSolutionProvider;
	private NoIdealSolutionTableViewerContentProvider _noIdealSolutionProvider;
	
	private boolean _completed;
	
	private RatingView _ratingView;
	
	private ProblemElementsSet _elementsSet;
	
	private SelectionPhase _selectionPhase;
	
	private UnificationPhase _unificationPhase;

	@Override
	public void createPartControl(Composite parent) {	
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		_selectionPhase = (SelectionPhase) pmm.getPhaseMethod(SelectionPhase.ID).getImplementation();
		
		 _unificationPhase = (UnificationPhase) pmm.getPhaseMethod(UnificationPhase.ID).getImplementation();
		
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();
		
		_completed = true;

		_parent = parent;
		
		ExpertsWeightContentProvider.registerChangeWeightListener(this);
		
		createContent();
	}

	private void createContent() {
		_parent.setLayout(new GridLayout(1, false));
		_parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		createCollectiveValuationsTable();	

		_solutionsComposite = new Composite(_parent , SWT.NONE);
		_solutionsComposite.setLayout(new GridLayout(2, true));
		_solutionsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		createIdealSolutionTable();
		createNoIdealSolutionTable();
	}
	
	private void createCollectiveValuationsTable() {
		Composite decisionMatrixComposite = new Composite(_parent, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		decisionMatrixComposite.setLayoutData(gridData);
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 15;
		layout.verticalSpacing = 15;
		decisionMatrixComposite.setLayout(layout);
		
		_decisionMatrixTable = new DecisionMatrixTable(decisionMatrixComposite);
		_decisionMatrixTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_decisionMatrixTable.setModel(_selectionPhase);	
		
		createImportPreferencesButton();
	}
	
	private void createImportPreferencesButton() {
		Composite importPreferencesButtonComposite = new Composite(_parent, SWT.NONE);
		GridData gridData = new GridData(SWT.RIGHT, SWT.RIGHT, true, false, 1, 1);
		importPreferencesButtonComposite.setLayoutData(gridData);
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 15;
		layout.verticalSpacing = 15;
		importPreferencesButtonComposite.setLayout(layout);
		
		Button importPreferenceButton = new Button(importPreferencesButtonComposite, SWT.NONE);
		importPreferenceButton.setText("Import new preferences");
		importPreferenceButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, 1, 1));
		
		importPreferenceButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				
				FileDialog dlg = new FileDialog(shell, SWT.OPEN);
				dlg.setFilterNames(FILTER_NAMES);
				dlg.setFilterExtensions(FILTER_EXTS);
				String fileName = dlg.open();
				
				if(fileName != null) {
					readFileContent(fileName);
				}
			}

			private void readFileContent(String fileName) {
				FileReader fr = null;
				BufferedReader br = null;
				try {
					fr = new FileReader(fileName);
					br = new BufferedReader(fr);
					
					String sCurrentLine;
					Map<ValuationKey, Valuation> newValuations = new HashMap<>();
					while ((sCurrentLine = br.readLine()) != null) {
						if(!sCurrentLine.isEmpty()) {
							String[] info = sCurrentLine.split(":");
							Expert expert = _elementsSet.getExpert(info[0]);
							Criterion criterion = _elementsSet.getCriterion(info[1]);
							Alternative alternative = _elementsSet.getAlternative(info[2]);
							String twoTuple = info[3];
							ValuationKey vk = new ValuationKey(expert, alternative, criterion);
							newValuations.put(vk, createNewValuations(vk, twoTuple));
						}
					}
					_unificationPhase.setTwoTupleValuations(newValuations);
					_selectionPhase.execute();
					refreshTables();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (br != null)
							br.close();

						if (fr != null)
							fr.close();

					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}

			private Valuation createNewValuations(ValuationKey vk, String twoTuple) {
				Map<ValuationKey, Valuation> oldValuations = _unificationPhase.getTwoTupleValuations();
				TwoTuple oldValuation = (TwoTuple) oldValuations.get(vk);
				FuzzySet valuationDomain = (FuzzySet) oldValuation.getDomain();
				
				twoTuple = twoTuple.trim();
				twoTuple = twoTuple.replace("(", "");
				twoTuple = twoTuple.replace(")", "");
				String[] twoTupleElements = twoTuple.split(",");
				
				LabelLinguisticDomain newLabel = valuationDomain.getLabelSet().getLabel(twoTupleElements[0]);
				double newAlpha = Double.parseDouble(twoTupleElements[1]);
				
				oldValuation.setLabel(newLabel);
				oldValuation.setAlpha(newAlpha);
				return oldValuation;
			}
		});
	}

	private void setInputTableCollective() {
		_decisionMatrixTable.redraw();
	}

	private void createIdealSolutionTable() {
		GridLayout idealSolutionValuationsLayout = new GridLayout(1, true);
		Composite idealSolutionComposite = new Composite(_solutionsComposite, SWT.NONE);
		idealSolutionComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		idealSolutionComposite.setLayout(idealSolutionValuationsLayout);
		
		Label idealSolutionLabel = new Label(idealSolutionComposite, SWT.NONE);
		idealSolutionLabel.setText(Messages.calculate_solutions_Ideal_solution);
		FontData fontData = idealSolutionLabel.getFont().getFontData()[0];
		idealSolutionLabel.setFont(new Font(idealSolutionLabel.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD)));
		idealSolutionLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		
		_tableViewerIdealSolution = new TableViewer(idealSolutionComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		_tableViewerIdealSolution.getTable().setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_tableViewerIdealSolution.getTable().setHeaderVisible(true);
		
		_idealSolutionProvider = new IdealSolutionTableViewerContentProvider();
		_tableViewerIdealSolution.setContentProvider(_idealSolutionProvider);

		TableViewerColumn criterionColumn = new TableViewerColumn(_tableViewerIdealSolution, SWT.NONE);
		criterionColumn.getColumn().setText(Messages.calculate_solutions_Criterion);
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
		typeColumn.getColumn().setText(Messages.calculate_solutions_Type);
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
				if(((Criterion) data[0]).isCost()) {
					return Images.Cost;
				} else {
					return Images.Benefit;
				}
			}
		});
		
		TableViewerColumn valuationColumn = new TableViewerColumn(_tableViewerIdealSolution, SWT.NONE);
		valuationColumn.getColumn().setText(Messages.calculate_solutions_Valuation);
		valuationColumn.getColumn().pack();
		valuationColumn.getColumn().setResizable(true);
		valuationColumn.getColumn().setMoveable(true);
		valuationColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				TwoTuple valuation = (TwoTuple) data[1];

				String valuationString = valuation.prettyFormat();
				valuationString = valuationString.replace("(", "");
				valuationString = valuationString.replace(")", "");
				String[] elements = valuationString.split(",");

				return "(" + elements[0] + ", " + elements[1] + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		});
		
		setInputIdealSolutionTable();
	}
	
	private void setInputIdealSolutionTable() {
		_idealSolutionProvider.setInput(_selectionPhase.getIdealSolution());
		_tableViewerIdealSolution.setInput(_idealSolutionProvider.getInput());	
		
		for(TableColumn tc: _tableViewerIdealSolution.getTable().getColumns()) {
			tc.pack();
		}
	}

	private void createNoIdealSolutionTable() {
		GridLayout noIdealSolutionLayout = new GridLayout(1, true);
		Composite noIdealSolutionComposite = new Composite(_solutionsComposite, SWT.NONE);
		noIdealSolutionComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		noIdealSolutionComposite.setLayout(noIdealSolutionLayout);
		
		Label noIdealSolutionLabel = new Label(noIdealSolutionComposite, SWT.NONE);
		FontData fontData = noIdealSolutionLabel.getFont().getFontData()[0];
		noIdealSolutionLabel.setFont(new Font(noIdealSolutionLabel.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD)));
		noIdealSolutionLabel.setText(Messages.calculate_solutions_No_ideal_solution);
		noIdealSolutionLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		
		_tableViewerNoIdealSolution = new TableViewer(noIdealSolutionComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		_tableViewerNoIdealSolution.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_tableViewerNoIdealSolution.getTable().setHeaderVisible(true);
		
		_noIdealSolutionProvider = new NoIdealSolutionTableViewerContentProvider();
		_tableViewerNoIdealSolution.setContentProvider(_noIdealSolutionProvider);

		TableViewerColumn criterionColumn = new TableViewerColumn(_tableViewerNoIdealSolution, SWT.NONE);
		criterionColumn.getColumn().setText(Messages.calculate_solutions_Criterion);
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
		
		TableViewerColumn typeColumn = new TableViewerColumn(_tableViewerNoIdealSolution, SWT.NONE);
		typeColumn.getColumn().setText(Messages.calculate_solutions_Type);
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
				if(((Criterion) data[0]).isCost()) {
					return Images.Cost;
				} else {
					return Images.Benefit;
				}
			}
		});
		
		TableViewerColumn valuationColumn = new TableViewerColumn(_tableViewerNoIdealSolution, SWT.NONE);
		valuationColumn.getColumn().setText(Messages.calculate_solutions_Valuation);
		valuationColumn.getColumn().pack();
		valuationColumn.getColumn().setResizable(true);
		valuationColumn.getColumn().setMoveable(true);
		valuationColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object[] data = (Object[]) element;
				TwoTuple valuation = (TwoTuple) data[1];
				
				String valuationString = valuation.prettyFormat();
				valuationString = valuationString.replace("(", "");
				valuationString = valuationString.replace(")", "");
				String[] elements = valuationString.split(",");

				return "(" + elements[0] + ", " + elements[1] + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		});
		
		setInputNoIdealSolution();
	}

	private void setInputNoIdealSolution() {
		_noIdealSolutionProvider.setInput(_selectionPhase.getNoIdealSolution());
		_tableViewerNoIdealSolution.setInput(_noIdealSolutionProvider.getInput());
		
		for(TableColumn tc: _tableViewerNoIdealSolution.getTable().getColumns()) {
			tc.pack();
		}
	}

	@Override
	public String getPartName() {
		return Messages.selection_Calculate_solutions;
	}

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
	
	@Override
	public void dispose() {
		super.dispose();
		
		ExpertsWeightContentProvider.removeChangeWeightListener(this);
		_completed = true;
	}

	@Override
	public void setFocus() {}

	@Override
	public void notifyWeigthChanged() {
		refreshTables();
	}

	private void refreshTables() {
		setInputTableCollective();
		setInputIdealSolutionTable();
		setInputNoIdealSolution();
	}
}
