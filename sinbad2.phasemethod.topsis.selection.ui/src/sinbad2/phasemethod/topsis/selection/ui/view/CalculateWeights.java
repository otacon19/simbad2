package sinbad2.phasemethod.topsis.selection.ui.view;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.fuzzy.ui.jfreechart.LinguisticDomainChart;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.topsis.selection.SelectionPhase;
import sinbad2.phasemethod.topsis.selection.ui.nls.Messages;
import sinbad2.phasemethod.topsis.selection.ui.view.table.ExpertsWeightTable;
import sinbad2.resolutionphase.rating.ui.listener.IStepStateListener;
import sinbad2.resolutionphase.rating.ui.view.RatingView;

public class CalculateWeights extends ViewPart implements IStepStateListener {
	
	public static final String ID = "flintstones.phasemethod.topsis.selection.ui.view.calculatedistances"; //$NON-NLS-1$
	
	private static final String[] FILTER_NAMES = { "Text files (*.txt)" }; //$NON-NLS-1$
	private static final String[] FILTER_EXTS = { "*.txt" }; //$NON-NLS-1$
	
	private Composite _parent;
	private Composite _tableComposite;
	
	private ExpertsWeightTable _expertsWeightTable;
	
	private LinguisticDomainChart _chart;
	
	private boolean _completed = true;
	
	private ProblemElementsSet _elementsSet;
	
	private SelectionPhase _selectionPhase;
	
	private RatingView _ratingView;

	@Override
	public void createPartControl(Composite parent) {
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		_selectionPhase = (SelectionPhase) pmm.getPhaseMethod(SelectionPhase.ID).getImplementation();
		_selectionPhase.execute();
		
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();

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
		createTable();
		createButtonsFile();
		createChart();
	}

	private void createTable() {
		_tableComposite = new Composite(_parent, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_tableComposite.setLayoutData(gridData);
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 15;
		layout.verticalSpacing = 15;
		_tableComposite.setLayout(layout);
		
		_expertsWeightTable = new ExpertsWeightTable(_tableComposite);
		_expertsWeightTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_expertsWeightTable.setModel(_selectionPhase);	
	}
	
	private void createButtonsFile() {
		Composite buttonsComposite = new Composite(_tableComposite, SWT.NONE);
		GridData gridData = new GridData(SWT.RIGHT, SWT.RIGHT, true, false, 1, 1);
		buttonsComposite.setLayoutData(gridData);
		GridLayout layout = new GridLayout(2, true);
		layout.horizontalSpacing = 15;
		layout.verticalSpacing = 15;
		buttonsComposite.setLayout(layout);
		
		Button exportWeights = new Button(buttonsComposite, SWT.NONE);
		exportWeights.setText("Export weights");
		exportWeights.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, 1, 1));
		Button importWeights = new Button(buttonsComposite, SWT.NONE);
		importWeights.setText("Import weights");
		importWeights.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, 1, 1));
		
		exportWeights.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				
				FileDialog dlg = new FileDialog(shell, SWT.SAVE);
				dlg.setFilterNames(FILTER_NAMES);
				dlg.setFilterExtensions(FILTER_EXTS);
				String fileName = dlg.open();
				
				if(fileName != null) {
					generateFileContent(fileName);
				}
			}

			private void generateFileContent(String fileName) {
				StringBuilder content = new StringBuilder();
				for(Expert e: _elementsSet.getOnlyExpertChildren()) {
					for(Criterion c: _elementsSet.getAllSubcriteria()) {
						content.append(e.getId() + ":" + c.getId() + ":" + _selectionPhase.getExpertWeight(e, _elementsSet.getAllSubcriteria().indexOf(c)).getName());
						content.append(System.getProperty("line.separator"));
					}
				}
				
				content.deleteCharAt(content.length() - 1);
				content.deleteCharAt(content.length() - 1);
				
				PrintWriter writer;
				try {
					writer = new PrintWriter(fileName, "UTF-8");
					writer.println(content.toString());
					writer.close();
				} catch (FileNotFoundException | UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}	
			}
		});
		
		importWeights.addSelectionListener(new SelectionAdapter() {
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
					while ((sCurrentLine = br.readLine()) != null) {
						String[] info = sCurrentLine.split(":");
						Expert e = _elementsSet.getExpert(info[0]);
						Criterion criterion = _elementsSet.getCriterion(info[1]);
						LabelLinguisticDomain weight = _selectionPhase.getWeightsDomain().getLabelSet().getLabel(info[2]);
						_selectionPhase.setExpertWeight(e, _elementsSet.getAllSubcriteria().indexOf(criterion), weight);
					}
					_expertsWeightTable.redraw();
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
		});
	}
	
	private void createChart() {
		Composite chartViewParent = new Composite(_parent, SWT.BORDER);
		GridLayout layout = new GridLayout(1, true);
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginBottom = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		chartViewParent.setLayout(layout);
		chartViewParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		final Composite chartView = new Composite(chartViewParent, SWT.BORDER);
		chartView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		_chart = new LinguisticDomainChart();
		_chart.initialize(_selectionPhase.getWeightsDomain(), chartView, chartView.getSize().x, chartView.getSize().y, SWT.BORDER);
		
		chartView.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				_chart.updateSize(chartView.getSize().x, _parent.getSize().y - _tableComposite.getSize().y - 35);
			}
		});
	}
	
	@Override
	public String getPartName() {
		return Messages.selection_Calculate_weights;
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
	public void dispose() {
		super.dispose();
		
		_completed = true;
	}
	
	@Override
	public void setRatingView(RatingView rating) {
		_ratingView = rating;
		notifyStepStateChange();
	}

}
