package sinbad2.phasemethod.analysis.ui.view;

import java.awt.Color;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.ui.jfreechart.LinguisticDomainChart;
import sinbad2.domain.linguistic.unbalanced.Unbalanced;
import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.domain.numeric.integer.ui.jfreechart.NumericIntegerDomainChart;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.domain.numeric.real.ui.jfreechart.NumericRealDomainChart;
import sinbad2.domain.ui.jfreechart.DomainChart;
import sinbad2.element.ProblemElement;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.method.Method;
import sinbad2.method.MethodsManager;
import sinbad2.phasemethod.PhaseMethod;
import sinbad2.phasemethod.aggregation.AggregationPhase;
import sinbad2.phasemethod.analysis.AnalysisPhase;
import sinbad2.phasemethod.analysis.ui.view.listener.CheckStateListener;
import sinbad2.phasemethod.analysis.ui.view.provider.AlternativeColumnLabelProvider;
import sinbad2.phasemethod.analysis.ui.view.provider.EvaluationColumnLabelProvider;
import sinbad2.phasemethod.analysis.ui.view.provider.FilterContentProvider;
import sinbad2.phasemethod.analysis.ui.view.provider.FilterLabelProvider;
import sinbad2.phasemethod.analysis.ui.view.provider.RankingColumnLabelProvider;
import sinbad2.phasemethod.analysis.ui.view.provider.RankingViewerProvider;
import sinbad2.phasemethod.retranslation.RetranslationPhase;
import sinbad2.resolutionphase.rating.ui.listener.IStepStateListener;
import sinbad2.resolutionphase.rating.ui.view.RatingView;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.integer.IntegerValuation;
import sinbad2.valuation.real.RealValuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;

public class Analysis extends ViewPart implements IStepStateListener {
	
	public static final String ID = "flintstones.phasemethod.heteorgeneous.fusion.analysis.ui.view.analysis";

	private Composite _parent;
	private Composite _expertsComposite;
	private Composite _alternativesComposite;
	private Composite _criteriaComposite;
	private Composite _resultsPanel;
	private Composite _filtersPanel;
	private Composite _chartView;
	
	private CheckboxTreeViewer _expertsCheckboxTreeViewer;
	private CheckboxTreeViewer _alternativesCheckboxTreeViewer;
	private CheckboxTreeViewer _criteriaCheckboxTreeViewer;
	
	private TableViewer _rankingViewer;
	private TableViewerColumn _rankingViewerRankingColumn;
	private TableColumn _rankingRankingColumn;
	private TableViewerColumn _rankingViewerAlternativeColumn;
	private TableColumn _rankingAlternativeColumn;
	private TableViewerColumn _rankingViewerEvaluationColumn;
	private TableColumn _rankingEvaluationColumn;
	
	private CheckStateListener _expertsCheckStateListener;
	private CheckStateListener _alternativesCheckStateListener;
	private CheckStateListener _criteriaCheckStateListener;
	
	private ControlAdapter _controlListener;
	
	private SashForm _filtersForm;

	private DomainChart _chart;
	
	private AggregationPhase _aggregationPhase;
	private AnalysisPhase _analysisPhase;
	private RetranslationPhase _retranslationPhase;
	
	private Map<ProblemElement, Valuation> _aggregationResult;
	
	private ProblemElementsSet _elementsSet;
	
	@Override
	public void createPartControl(Composite parent) {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		_aggregationPhase = AggregationPhase.getInstance();
		_analysisPhase = AnalysisPhase.getInstance();
		_retranslationPhase = RetranslationPhase.getInstance();
		
		_aggregationResult = null;
		
		_controlListener = null;
		
		_parent = parent;
		
		GridLayout layout = new GridLayout(14, true);
		layout.verticalSpacing = 20;
		layout.horizontalSpacing = 15;
		layout.marginLeft = 20;
		layout.marginRight = 15;
		layout.marginWidth = 0;
		layout.marginTop = 20;
		layout.marginBottom = 15;
		_parent.setLayout(layout);

		createFiltersPart();
		createResultsPart();
		hookFilters();	
		activate();
	}

	private void createFiltersPart() {
		_filtersPanel = new Composite(_parent, SWT.NONE);
		_filtersPanel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 6, 1));
		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		_filtersPanel.setLayout(layout);

		_filtersForm = new SashForm(_filtersPanel, SWT.BORDER | SWT.VERTICAL);
		_filtersForm.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

		_expertsComposite = new Composite(_filtersForm, SWT.BORDER);
		GridLayout filterComposite = new GridLayout(1, false);
		filterComposite.verticalSpacing = 0;
		filterComposite.marginWidth = 0;
		filterComposite.marginHeight = 0;
		filterComposite.horizontalSpacing = 0;
		_expertsComposite.setLayout(filterComposite);

		Label label = new Label(_expertsComposite, SWT.CENTER | SWT.BORDER);
		label.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD)); //$NON-NLS-1$
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		label.setText("Experts");

		_expertsCheckboxTreeViewer = new CheckboxTreeViewer(_expertsComposite, SWT.NONE);
		_expertsCheckboxTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		_expertsCheckboxTreeViewer.setContentProvider(new FilterContentProvider(_elementsSet));
		_expertsCheckboxTreeViewer.setLabelProvider(new FilterLabelProvider());

		_alternativesComposite = new Composite(_filtersForm, SWT.BORDER);
		filterComposite = new GridLayout(1, false);
		filterComposite.verticalSpacing = 0;
		filterComposite.marginWidth = 0;
		filterComposite.marginHeight = 0;
		filterComposite.horizontalSpacing = 0;
		_alternativesComposite.setLayout(filterComposite);

		label = new Label(_alternativesComposite, SWT.CENTER | SWT.BORDER);
		label.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD)); //$NON-NLS-1$
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		label.setText("Alternatives");

		_alternativesCheckboxTreeViewer = new CheckboxTreeViewer(_alternativesComposite, SWT.NONE);
		_alternativesCheckboxTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		_alternativesCheckboxTreeViewer.setContentProvider(new FilterContentProvider(_elementsSet));
		_alternativesCheckboxTreeViewer.setLabelProvider(new FilterLabelProvider());

		_criteriaComposite = new Composite(_filtersForm, SWT.BORDER);
		filterComposite = new GridLayout(1, false);
		filterComposite.verticalSpacing = 0;
		filterComposite.marginWidth = 0;
		filterComposite.marginHeight = 0;
		filterComposite.horizontalSpacing = 0;
		_criteriaComposite.setLayout(filterComposite);
		label = new Label(_criteriaComposite, SWT.BORDER | SWT.CENTER);
		label.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD)); //$NON-NLS-1$
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		label.setText("Criteria");

		_criteriaCheckboxTreeViewer = new CheckboxTreeViewer(_criteriaComposite, SWT.NONE);
		_criteriaCheckboxTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		_criteriaCheckboxTreeViewer.setContentProvider(new FilterContentProvider(_elementsSet));
		_criteriaCheckboxTreeViewer.setLabelProvider(new FilterLabelProvider());
	}
	
	private void createResultsPart() {

		_resultsPanel = new Composite(_parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true, 8, 1);
		_resultsPanel.setLayoutData(gridData);
		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		_resultsPanel.setLayout(layout);

		createRankingView();
		createChartView();
	}
	
	private void createRankingView() {

		Composite rankingView = new Composite(_resultsPanel, SWT.NONE);

		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gridData.heightHint = 180;
		rankingView.setLayoutData(gridData);
		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginBottom = 10;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		rankingView.setLayout(layout);

		_rankingViewer = new TableViewer(rankingView, SWT.BORDER);
		_rankingViewer.setContentProvider(new RankingViewerProvider());
		Table rankingViewerTable = _rankingViewer.getTable();
		rankingViewerTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		rankingViewerTable.setHeaderVisible(true);

		_rankingViewerRankingColumn = new TableViewerColumn(_rankingViewer, SWT.CENTER);
		_rankingRankingColumn = _rankingViewerRankingColumn.getColumn();
		_rankingRankingColumn.setWidth(75);
		_rankingRankingColumn.setText("Ranking"); //$NON-NLS-1$
		_rankingViewerRankingColumn.setLabelProvider(new RankingColumnLabelProvider());

		_rankingViewerAlternativeColumn = new TableViewerColumn(_rankingViewer, SWT.NONE);
		_rankingAlternativeColumn = _rankingViewerAlternativeColumn.getColumn();
		_rankingAlternativeColumn.setWidth(130);
		_rankingAlternativeColumn.setText("Alternative");
		_rankingViewerAlternativeColumn.setLabelProvider(new AlternativeColumnLabelProvider());

		_rankingViewerEvaluationColumn = new TableViewerColumn(_rankingViewer, SWT.NONE);
		_rankingEvaluationColumn = _rankingViewerEvaluationColumn.getColumn();
		_rankingEvaluationColumn.setWidth(130);
		_rankingEvaluationColumn.setText("Evaluation");
		_rankingViewerEvaluationColumn.setLabelProvider(new EvaluationColumnLabelProvider());
	}

	private void createChartView() {
		Composite chartViewParent = new Composite(_resultsPanel, SWT.NONE);
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

		_chartView = new Composite(chartViewParent, SWT.NONE);
		_chartView.setLayoutData(new GridData(GridData.FILL_BOTH));

		setChart(null);
	}
	
	private void setChart(Domain domain) {
		removeOldChart();
		
		boolean exit = false;
	
		if(domain instanceof FuzzySet) {
			_chart = new LinguisticDomainChart();
			_chart.initialize(domain, _chartView, _chartView.getSize().x, _chartView.getSize().y, SWT.BORDER);
		} else if(domain instanceof NumericRealDomain) {
			_chart = new NumericRealDomainChart();
			_chart.initialize(domain, _chartView, _chartView.getSize().x, _chartView.getSize().y, SWT.BORDER);
		} else if(domain instanceof NumericIntegerDomain){
			_chart = new NumericIntegerDomainChart();
			_chart.initialize(domain, _chartView, _chartView.getSize().x, _chartView.getSize().y, SWT.BORDER);
		} else {
			exit = true;
		}

		if (!exit) {

			if (_controlListener == null) {
				_controlListener = new ControlAdapter() {
					@Override
					public void controlResized(ControlEvent e) {
						setChart(getDomain());
					}
				};
				_chartView.addControlListener(_controlListener);
			}
			if(_aggregationResult != null) {
				int size = _aggregationResult.size();
				if (size > 0) {
					String[] alternatives = new String[size];
					int[] pos = new int[size];
					double[] measuresReal = new double[size];
					int[] measuresInteger = new int[size];
					double[] alpha = new double[size];
					Color[] colors = new Color[size];
					Valuation valuation = null, aux;
					int i = 0;
	
					for (ProblemElement alternative : _aggregationResult.keySet()) {
						alternatives[i] = alternative.getId();
						aux = _aggregationResult.get(alternative);
						if (aux instanceof UnifiedValuation) {
							valuation = ((UnifiedValuation) aux).disunification((FuzzySet) aux.getDomain());
						} else {
							valuation = aux;
						}
	
						if (valuation instanceof TwoTuple) {
							pos[i] = ((FuzzySet) domain).getLabelSet().getPos(((TwoTuple) valuation).getLabel());
							alpha[i] = ((TwoTuple) valuation).getAlpha();
							i++;
						} else if(valuation instanceof IntegerValuation) {
							measuresInteger[i] = (int) ((IntegerValuation) valuation).getValue();
							colors[i] = NumericIntegerDomainChart.colors[i % NumericIntegerDomainChart.colors.length];
							i++;
						} else if(valuation instanceof RealValuation) {
							measuresReal[i] = ((RealValuation) valuation).getValue();
							colors[i] = NumericRealDomainChart.colors[i % NumericRealDomainChart.colors.length];
							i++;
						} else {
							alternatives[i] = null;
						}
					}
					if (_chart instanceof LinguisticDomainChart) {
						((LinguisticDomainChart) _chart).displayAlternatives(alternatives, pos, alpha);
					} else if(_chart instanceof NumericRealDomainChart) {
						((NumericRealDomainChart) _chart).displayAlternatives(alternatives, measuresReal, colors);
					} else if(_chart instanceof NumericIntegerDomainChart) {
						((NumericIntegerDomainChart) _chart).displayAlternatives(alternatives, measuresInteger, colors);
					}
				}
			}
		}
	}
	
	private Domain getDomain() {
		return _aggregationPhase.getUnifiedDomain();
	}
	
	private void removeOldChart() {
		if (_chart != null) {
			_chart.getChartComposite().dispose();
			_chartView.layout();
		}
	}
	
	private void hookFilters() {
		_expertsCheckStateListener = new CheckStateListener(_expertsCheckboxTreeViewer, _elementsSet, this);
		_expertsCheckboxTreeViewer.addCheckStateListener(_expertsCheckStateListener);

		_alternativesCheckStateListener = new CheckStateListener(_alternativesCheckboxTreeViewer, _elementsSet, this);
		_alternativesCheckboxTreeViewer.addCheckStateListener(_alternativesCheckStateListener);

		_criteriaCheckStateListener = new CheckStateListener(_criteriaCheckboxTreeViewer, _elementsSet, this);
		_criteriaCheckboxTreeViewer.addCheckStateListener(_criteriaCheckStateListener);
	}
	
	public void refreshRanking() {
		Set<ProblemElement> experts = getCheckedElements(_expertsCheckboxTreeViewer);
		Set<ProblemElement> alternatives = getCheckedElements(_alternativesCheckboxTreeViewer);
		Set<ProblemElement> criteria = getCheckedElements(_criteriaCheckboxTreeViewer);

		_aggregationResult = _aggregationPhase.aggregateAlternatives(experts, alternatives, criteria);
		
		if(getDomain() instanceof Unbalanced) {
			_aggregationResult = _aggregationPhase.transform(_aggregationResult, (Unbalanced) getDomain());
			_rankingViewer.setInput(_aggregationResult);
			setChart(getDomain());
		} else {
			MethodsManager methodsManager = MethodsManager.getInstance();
			Method method = methodsManager.getActiveMethod();
			for(PhaseMethod pm: method.getPhases()) {
				if(pm.getImplementation().equals(_retranslationPhase)) {
					 System.out.println("lo misma mierda son");
				}
			}
			if(method.getPhases().contains(_retranslationPhase)) {
				Domain domain = _analysisPhase.getDomain();
				_aggregationResult = _retranslationPhase.transform(_aggregationResult, (FuzzySet) domain);
				_rankingViewer.setInput(_aggregationResult);
				setChart(domain);
			} else {
				_rankingViewer.setInput(_aggregationResult);
				setChart(getDomain());
			}
		}
	}
	
	private Set<ProblemElement> getCheckedElements(CheckboxTreeViewer tree) {
		Object[] checkedElements = tree.getCheckedElements();
		Set<ProblemElement> result = new HashSet<ProblemElement>();
		for (int i = 0; i < checkedElements.length; i++) {
			result.add((ProblemElement) checkedElements[i]);
		}

		return result;
	}
	
	@Override
	public void setFocus() {
		_rankingViewer.getControl().setFocus();
	}

	@Override
	public String getPartName() {
		return "Analysis";
	}
	
	public void activate() {
		_expertsCheckboxTreeViewer.setInput(_elementsSet.getExperts());
		_alternativesCheckboxTreeViewer.setInput(_elementsSet.getAlternatives());
		_criteriaCheckboxTreeViewer.setInput(_elementsSet.getCriteria());

		_expertsCheckStateListener.checkAll("EXPERTS");
		_alternativesCheckStateListener.checkAll("ALTERNATIVES");
		_criteriaCheckStateListener.checkAll("CRITERIA");
	}

	@Override
	public void notifyStepStateChange() {
		refreshRanking();	
	}

	@Override
	public void notifyRatingView(RatingView rating) {}
}

