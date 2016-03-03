package sinbad2.phasemethod.heterogeneous.fusion.aggregation.ui.view;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.ui.jfreechart.LinguisticDomainChart;
import sinbad2.element.ProblemElement;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.phasemethod.heterogeneous.fusion.aggregation.AggregationPhase;
import sinbad2.phasemethod.heterogeneous.fusion.aggregation.listener.AggregationProcessListener;
import sinbad2.phasemethod.heterogeneous.fusion.aggregation.listener.AggregationProcessStateChangeEvent;
import sinbad2.phasemethod.heterogeneous.fusion.aggregation.ui.Images;
import sinbad2.phasemethod.heterogeneous.fusion.aggregation.ui.view.editingsupport.AggregationOperatorEditingSupport;
import sinbad2.phasemethod.heterogeneous.fusion.aggregation.ui.view.editingsupport.OperatorWeightsEditingSupport;
import sinbad2.phasemethod.heterogeneous.fusion.aggregation.ui.view.provider.AggregationCriterionViewerContentProvider;
import sinbad2.phasemethod.heterogeneous.fusion.aggregation.ui.view.provider.AggregationExpertViewerContentProvider;
import sinbad2.phasemethod.heterogeneous.fusion.aggregation.ui.view.provider.AlternativeColumnLabelProvider;
import sinbad2.phasemethod.heterogeneous.fusion.aggregation.ui.view.provider.ElementColumnLabelProvider;
import sinbad2.phasemethod.heterogeneous.fusion.aggregation.ui.view.provider.EvaluationColumnLabelProvider;
import sinbad2.phasemethod.heterogeneous.fusion.aggregation.ui.view.provider.OperatorColumnLabelProvider;
import sinbad2.phasemethod.heterogeneous.fusion.aggregation.ui.view.provider.RankingColumnLabelProvider;
import sinbad2.phasemethod.heterogeneous.fusion.aggregation.ui.view.provider.RankingViewerProvider;
import sinbad2.phasemethod.heterogeneous.fusion.unification.UnificationPhase;
import sinbad2.phasemethod.heterogeneous.fusion.unification.ui.view.SelectBLTS;
import sinbad2.resolutionphase.rating.ui.listener.IStepStateListener;
import sinbad2.resolutionphase.rating.ui.view.RatingView;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;

public class AggregationProcess extends ViewPart implements AggregationProcessListener, IStepStateListener {
	
	public static final String ID = "flintstones.phasemethod.heterogeneous.fusion.aggregation.ui.view.aggregationprocess";

	private Composite _parent;
	private Composite _operatorsPanel;
	private Composite _resultsPanel;
	private Composite _chartView;
	private Composite _expertsComposite;
	private Composite _criteriaComposite;
	private Composite _orderCompositeContainer;
	
	private TableViewer _rankingViewer;
	private TableViewerColumn _rankingViewerRankingColumn;
	private TableColumn _rankingRankingColumn;
	private TableViewerColumn _rankingViewerAlternativeColumn;
	private TableColumn _rankingAlternativeColumn;
	private TableViewerColumn _rankingViewerEvaluationColumn;
	private TableColumn _rankingEvaluationColumn;
	private TreeViewer _expertsViewer;
	private Tree _expertsTree;
	private TreeViewerColumn _treeViewerExpertColumn;
	private TreeColumn _treeExpertColumn;
	private TreeViewerColumn _treeViewerExpertOperatorWeightsColumn;
	private TreeColumn _treeExpertOperatorWeightsColumn;
	private TreeViewerColumn _treeViewerExpertOperatorColumn;
	private TreeColumn _treeExpertOperatorColumn;
	private TreeViewer _criteriaViewer;
	private Tree _criteriaTree;
	private TreeViewerColumn _treeViewerCriterionColumn;
	private TreeColumn _treeCriterionColumn;
	private TreeViewerColumn _treeViewerCriterionOperatorWeightsColumn;
	private TreeColumn _treeCriterionOperatorWeightsColumn;
	private TreeViewerColumn _treeViewerCriterionOperatorColumn;
	private TreeColumn _treeCriterionOperatorColumn;
	
	private ControlAdapter _controlListener;
	
	private LinguisticDomainChart _chart;
	
	private boolean _completed;
	private boolean _loaded;
	
	private AggregationPhase _aggregationPhase;
	private Map<ProblemElement, Valuation> _aggregationResult;
	
	private UnificationPhase _unificationPhase;
	
	private RatingView _ratingView;
	
	private ProblemElementsSet _elementsSet;
	
	public AggregationProcess() {}
	
	public abstract class CenterImageLabelProvider extends OwnerDrawLabelProvider {

		protected void measure(Event event, Object element) {}

		protected void paint(Event event, Object element) {

			Image img = getImage(element);

			if (img != null) {
				Rectangle bounds = ((TreeItem) event.item).getBounds(event.index);
				Rectangle imgBounds = img.getBounds();
				bounds.width /= 2;
				bounds.width -= imgBounds.width / 2;
				bounds.height /= 2;
				bounds.height -= imgBounds.height / 2;

				int x = bounds.width > 0 ? bounds.x + bounds.width : bounds.x;
				int y = bounds.height > 0 ? bounds.y + bounds.height : bounds.y;

				event.gc.drawImage(img, x, y);
			}
		}

		protected abstract Image getImage(Object element);
	}
	
	@Override
	public void createPartControl(Composite parent) {		
		_unificationPhase = UnificationPhase.getInstance();
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		_completed = false;
		_loaded = false;
		
		_aggregationPhase = AggregationPhase.getInstance();
		_aggregationPhase.clear();
		
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

		createOperatorsSelectors();
		createViews();
		
		_aggregationPhase.addAggregationProcessListener(this);
	}
	
	private void createOperatorsSelectors() {
		_operatorsPanel = new Composite(_parent, SWT.NONE);
		_operatorsPanel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 6, 1));
		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		_operatorsPanel.setLayout(layout);

		GridData gridData;
		boolean multiExperts = _elementsSet.getExperts().size() > 1;
		boolean multiCriteria = _elementsSet.getCriteria().size() > 1;
		
		if (multiExperts) {
			_expertsComposite = new Composite(_operatorsPanel, SWT.NONE);
			if (multiCriteria) {
				gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
				gridData.heightHint = 170;
			} else {
				gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
			}
			_expertsComposite.setLayoutData(gridData);
			layout = new GridLayout(1, false);
			layout.marginTop = 0;
			layout.marginBottom = 0;
			layout.marginLeft = 0;
			layout.marginRight = 0;
			layout.verticalSpacing = 0;
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			layout.horizontalSpacing = 0;
			_expertsComposite.setLayout(layout);

			_expertsViewer = new TreeViewer(_expertsComposite, SWT.BORDER | SWT.FULL_SELECTION);
			_expertsViewer.setContentProvider(new AggregationExpertViewerContentProvider(_elementsSet.getExperts()));
			_expertsTree = _expertsViewer.getTree();
			_expertsTree.setHeaderVisible(true);
			_expertsTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,true, 1, 1));
			_expertsViewer.addTreeListener(new ITreeViewerListener() {

				@Override
				public void treeExpanded(TreeExpansionEvent event) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (_expertsTree != null) {
								if (!_expertsTree.isDisposed()) {
									_expertsTree.getColumns()[0].pack();
								}
							}
						}
					});
				}

				@Override
				public void treeCollapsed(TreeExpansionEvent event) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (_expertsTree != null) {
								if (!_expertsTree.isDisposed()) {
									_expertsTree.getColumns()[0].pack();
								}
							}
						}
					});
				}
			});

			_treeViewerExpertColumn = new TreeViewerColumn(_expertsViewer, SWT.NONE);

			_treeExpertColumn = _treeViewerExpertColumn.getColumn();
			_treeExpertColumn.setWidth(120);
			_treeExpertColumn.setText("Expert");
			_treeExpertColumn.setImage(Images.GroupOfExperts);
			_treeViewerExpertColumn.setLabelProvider(new ElementColumnLabelProvider());

			_treeViewerExpertOperatorWeightsColumn = new TreeViewerColumn(_expertsViewer, SWT.NONE);
			_treeExpertOperatorWeightsColumn = _treeViewerExpertOperatorWeightsColumn.getColumn();
			_treeExpertOperatorWeightsColumn.setAlignment(SWT.CENTER);
			_treeExpertOperatorWeightsColumn.setWidth(45);
			_treeExpertOperatorWeightsColumn.setText(""); //$NON-NLS-1$
			_treeExpertOperatorWeightsColumn.setImage(Images.Edit);
			_treeViewerExpertOperatorWeightsColumn.setEditingSupport(new OperatorWeightsEditingSupport(_expertsViewer, 	AggregationPhase.EXPERTS, _aggregationPhase));
			_treeViewerExpertOperatorWeightsColumn.setLabelProvider(new CenterImageLabelProvider() {
						@Override
						public Image getImage(Object element) {

							ProblemElement problemElement = null;
							if (element instanceof ProblemElement) {
								problemElement = (ProblemElement) element;
							}

							AggregationOperator operator = _aggregationPhase.getExpertOperator(problemElement);
							if (operator != null) {
								if (operator instanceof WeightedAggregationOperator) {
									return Images.Edit;
								}
								return null;
							} else {
								return null;
							}
						}
					});
		
			_treeViewerExpertOperatorColumn = new TreeViewerColumn(_expertsViewer, SWT.NONE);
			_treeExpertOperatorColumn = _treeViewerExpertOperatorColumn.getColumn();
			_treeExpertOperatorColumn.setWidth(120);
			_treeExpertOperatorColumn.setText("Operator");
			_treeExpertOperatorColumn.setImage(Images.AggregationOperator);

			_treeViewerExpertOperatorColumn.setEditingSupport(new AggregationOperatorEditingSupport(_aggregationPhase, this, _expertsViewer, AggregationPhase.EXPERTS));
			_treeViewerExpertOperatorColumn.setLabelProvider(new OperatorColumnLabelProvider(_aggregationPhase, AggregationPhase.EXPERTS));

			_expertsViewer.setInput(_aggregationPhase);
		}

		if (multiCriteria) {
			_criteriaComposite = new Composite(_operatorsPanel, SWT.NONE);
			if (multiExperts) {
				gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
				gridData.heightHint = 170;
			} else {
				gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
			}
			_criteriaComposite.setLayoutData(gridData);
			layout = new GridLayout(1, false);

			if (multiExperts) {
				layout.marginTop = 10;
			} else {
				layout.marginTop = 0;
			}
			layout.marginBottom = 0;
			layout.marginLeft = 0;
			layout.marginRight = 0;
			layout.verticalSpacing = 0;
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			layout.horizontalSpacing = 0;
			_criteriaComposite.setLayout(layout);

			_criteriaViewer = new TreeViewer(_criteriaComposite, SWT.BORDER | SWT.FULL_SELECTION);
			_criteriaViewer.setContentProvider(new AggregationCriterionViewerContentProvider(_elementsSet.getCriteria()));
			_criteriaTree = _criteriaViewer.getTree();
			_criteriaTree.setHeaderVisible(true);
			_criteriaTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			_treeViewerCriterionColumn = new TreeViewerColumn(_criteriaViewer, SWT.NONE);
			_treeCriterionColumn = _treeViewerCriterionColumn.getColumn();
			_treeCriterionColumn.setWidth(120);
			_treeCriterionColumn.setText("Criterion");
			_treeCriterionColumn.setImage(Images.Criterion);
			_treeViewerCriterionColumn.setLabelProvider(new ElementColumnLabelProvider());
			_criteriaViewer.addTreeListener(new ITreeViewerListener() {

				@Override
				public void treeExpanded(TreeExpansionEvent event) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (_criteriaTree != null) {
								if (!_criteriaTree.isDisposed()) {
									_criteriaTree.getColumns()[0].pack();
								}
							}
						}
					});
				}

				@Override
				public void treeCollapsed(TreeExpansionEvent event) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (_criteriaTree != null) {
								if (!_criteriaTree.isDisposed()) {
									_criteriaTree.getColumns()[0].pack();
								}
							}
						}
					});
				}
			});

			_treeViewerCriterionOperatorWeightsColumn = new TreeViewerColumn(_criteriaViewer, SWT.NONE);
			_treeCriterionOperatorWeightsColumn = _treeViewerCriterionOperatorWeightsColumn.getColumn();
			_treeCriterionOperatorWeightsColumn.setAlignment(SWT.CENTER);
			_treeCriterionOperatorWeightsColumn.setWidth(45);
			_treeCriterionOperatorWeightsColumn.setText(""); //$NON-NLS-1$
			_treeCriterionOperatorWeightsColumn.setImage(Images.Edit);
			_treeViewerCriterionOperatorWeightsColumn.setEditingSupport(new OperatorWeightsEditingSupport(_criteriaViewer, AggregationPhase.CRITERIA, _aggregationPhase));
			_treeViewerCriterionOperatorWeightsColumn.setLabelProvider(new CenterImageLabelProvider() {
						@Override
						public Image getImage(Object element) {

							ProblemElement problemElement = null;
							if (element instanceof ProblemElement) {
								problemElement = (ProblemElement) element;
							}

							AggregationOperator operator = _aggregationPhase.getCriterionOperator(problemElement);
							if (operator != null) {
								if (operator instanceof WeightedAggregationOperator) {
									return Images.Edit;
								}
								return null;
							} else {
								return null;
							}
						}
					});
			
			_treeViewerCriterionOperatorColumn = new TreeViewerColumn(_criteriaViewer, SWT.NONE);
			_treeCriterionOperatorColumn = _treeViewerCriterionOperatorColumn.getColumn();
			_treeCriterionOperatorColumn.setWidth(120);
			_treeCriterionOperatorColumn.setText("Operator");
			_treeCriterionOperatorColumn.setImage(Images.AggregationOperator);

			_treeViewerCriterionOperatorColumn.setEditingSupport(new AggregationOperatorEditingSupport(_aggregationPhase, this, _criteriaViewer, AggregationPhase.CRITERIA));
			_treeViewerCriterionOperatorColumn.setLabelProvider(new OperatorColumnLabelProvider(_aggregationPhase, AggregationPhase.CRITERIA));
			
			_criteriaViewer.setInput(_aggregationPhase);
		}

		if ((multiExperts) && (multiCriteria)) {
			_orderCompositeContainer = new Composite(_operatorsPanel, SWT.NONE);
			_orderCompositeContainer.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
			layout = new GridLayout(1, false);
			layout.marginTop = 5;
			layout.marginHeight = 0;
			layout.horizontalSpacing = 0;
			_orderCompositeContainer.setLayout(layout);

			Composite orderComposite = new Composite(_orderCompositeContainer, SWT.NONE);
			orderComposite.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false, 1, 1));
			layout = new GridLayout(1, false);
			layout.marginHeight = 0;
			layout.horizontalSpacing = 0;
			orderComposite.setLayout(layout);

			final Label aggregateProcess = new Label(orderComposite, SWT.LEFT);
			aggregateProcess.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD));
			gridData = new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1);
			aggregateProcess.setLayoutData(gridData);
			aggregateProcess.setText("Aggregate Process");

			Composite orderValuesComposite = new Composite(orderComposite, SWT.NONE);
			orderValuesComposite.setLayoutData(new GridData(SWT.CENTER, SWT.END, true, false, 1, 1));
			layout = new GridLayout(2, false);
			layout.marginHeight = 0;
			layout.horizontalSpacing = 0;
			orderValuesComposite.setLayout(layout);

			Composite changeOrderComposite = new Composite(orderValuesComposite, SWT.NONE);
			changeOrderComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			layout = new GridLayout(1, false);
			layout.marginTop = 0;
			layout.marginRight = 0;
			changeOrderComposite.setLayout(layout);

			Label changeOrderLabel = null;
			changeOrderLabel = new Label(changeOrderComposite, SWT.PUSH);
			changeOrderLabel.setImage(Images.ChangeOrder);
			changeOrderLabel.setToolTipText(null);
			gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
			changeOrderLabel.setLayoutData(gridData);

			Composite elementsOrderComposite = new Composite(orderValuesComposite, SWT.NONE);
			gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
			elementsOrderComposite.setLayoutData(gridData);
			layout = new GridLayout(2, false);
			layout.marginTop = 0;
			layout.marginLeft = 0;
			layout.horizontalSpacing = 0;
			elementsOrderComposite.setLayout(layout);

			Label firstLabel = new Label(elementsOrderComposite, SWT.CENTER);
			firstLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NONE));
			gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
			firstLabel.setLayoutData(gridData);
			firstLabel.setText("1º "); //$NON-NLS-1$

			final Label firstValueLabel = new Label(elementsOrderComposite, SWT.CENTER);
			firstValueLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NONE));
			gridData = new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1);
			firstValueLabel.setLayoutData(gridData);
			firstValueLabel.setText("Experts");

			Label secondLabel = new Label(elementsOrderComposite, SWT.CENTER);
			secondLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NONE));
			gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
			secondLabel.setLayoutData(gridData);
			secondLabel.setText("2º "); //$NON-NLS-1$

			final Label secondValueLabel = new Label(elementsOrderComposite, SWT.CENTER);
			secondValueLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NONE));
			gridData = new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1);
			secondValueLabel.setLayoutData(gridData);
			secondValueLabel.setText("Criteria");

			changeOrderLabel.setCursor(new Cursor(Display.getDefault(), SWT.CURSOR_HAND));

			changeOrderLabel.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseUp(MouseEvent e) {
					String first = firstValueLabel.getText();
					String second = secondValueLabel.getText();
					firstValueLabel.setText(second);
					secondValueLabel.setText(first);
					firstValueLabel.pack();
					secondValueLabel.pack();
					if (AggregationPhase.EXPERTS.equals(_aggregationPhase.getAggregateBy())) {
						_aggregationPhase.aggregateBy(AggregationPhase.CRITERIA);
					} else {
						_aggregationPhase.aggregateBy(AggregationPhase.EXPERTS);
					}
				}
			});
		}
	}
	
	private void createViews() {
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
		_rankingRankingColumn.setText("Ranking");
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
		
		_rankingViewer.setInput(_aggregationResult);
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
		if (domain instanceof FuzzySet) {
			_chart = new LinguisticDomainChart();
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
					double[] alpha = new double[size];
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
						} else {
							alternatives[i] = null;
						}
					}
					if (_chart instanceof LinguisticDomainChart) {
						((LinguisticDomainChart) _chart).displayAlternatives(alternatives, pos, alpha);
					}
				}
			}
		}
	}
	
	private Domain getDomain() {
		return SelectBLTS.getBLTSDomain() ;
	}
	
	private void removeOldChart() {
		if (_chart != null) {
			_chart.getChartComposite().dispose();
			_chartView.layout();
		}
	}
	
	@Override
	public void notifyAggregationProcessChange(AggregationProcessStateChangeEvent event) {
		if (_expertsViewer != null) {
			_expertsViewer.refresh();
			_treeExpertColumn.pack();
			_treeExpertOperatorColumn.pack();
		}

		if (_criteriaViewer != null) {
			_criteriaViewer.refresh();
			_treeCriterionColumn.pack();
			_treeCriterionOperatorColumn.pack();
		}

		testAggregationProcess();
	}
	
	private void testAggregationProcess() {
		_unificationPhase.unifiedEvaluationToTwoTuple((FuzzySet) getDomain());
		
		Set<ProblemElement> experts = new HashSet<ProblemElement>();
		experts.addAll(_elementsSet.getAllExperts());
		Set<ProblemElement> alternatives = new HashSet<ProblemElement>();
		alternatives.addAll(_elementsSet.getAlternatives());
		Set<ProblemElement> criteria = new HashSet<ProblemElement>();
		criteria.addAll(_elementsSet.getAllCriteria());
		
		_aggregationResult = _aggregationPhase.aggregateAlternatives(experts, alternatives, criteria);
		
		refreshView();	
	}
	
	private void refreshView() {
		if (_rankingViewer != null) {
			_rankingViewer.setInput(_aggregationResult);
			setChart(getDomain());
		}
	}
	
	public void completed(boolean state) {
		_completed = state;
	}
	
	@Override
	public void setFocus() {
		_rankingViewer.getControl().setFocus();
	}
	
	@Override
	public String getPartName() {
		return "Aggregation process";
	}
	
	@Override
	public void notifyStepStateChange() {
		boolean notYet = false;
		if(_completed && !_loaded) {
			for(ProblemElement alternative: _aggregationResult.keySet()) {
				if(_aggregationResult.get(alternative) == null) {
					notYet = true;
					break;
				}
			}
			if(!notYet) {
				_ratingView.loadNextStep();
				_completed = false;
				_loaded = true;
			}
		}
	}

	@Override
	public void notifyRatingView(RatingView rating) {
		_ratingView = rating;	
	}
}

