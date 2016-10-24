package sinbad2.phasemethod.multigranular.lh.unification.ui.view;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.excel.ExcelUtil;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.multigranular.lh.unification.UnificationPhase;
import sinbad2.phasemethod.multigranular.lh.unification.ui.Images;
import sinbad2.phasemethod.multigranular.lh.unification.ui.comparator.UnificationTreeViewerComparator;
import sinbad2.phasemethod.multigranular.lh.unification.ui.nls.Messages;
import sinbad2.phasemethod.multigranular.lh.unification.ui.view.provider.AlternativeColumnLabelProvider;
import sinbad2.phasemethod.multigranular.lh.unification.ui.view.provider.CriterionColumnLabelProvider;
import sinbad2.phasemethod.multigranular.lh.unification.ui.view.provider.DomainColumnLabelProvider;
import sinbad2.phasemethod.multigranular.lh.unification.ui.view.provider.EvaluationColumnLabelProvider;
import sinbad2.phasemethod.multigranular.lh.unification.ui.view.provider.ExpertColumnLabelProvider;
import sinbad2.phasemethod.multigranular.lh.unification.ui.view.provider.TreeViewerContentProvider;
import sinbad2.phasemethod.multigranular.lh.unification.ui.view.provider.UnifiedEvaluationColumnLabelProvider;
import sinbad2.phasemethod.retranslation.RetranslationPhase;
import sinbad2.resolutionphase.rating.ui.listener.IStepStateListener;
import sinbad2.resolutionphase.rating.ui.view.RatingView;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.valuationset.ValuationKey;

public class Unification extends ViewPart implements IStepStateListener {

public static final String ID = "flintstones.phasemethod.multigranular.unification.ui.view.unification"; //$NON-NLS-1$

	private FuzzySet _unifiedDomain;
	private Map<ValuationKey, Valuation> _unifiedValues;

	private boolean _completed;
	
	private Composite _parent;
	
	private TreeViewer _treeViewer;
	private Tree _tree;
	
	private TreeViewerColumn _treeViewerExpertColumn;
	private TreeColumn _treeExpertColumn;
	private TreeViewerColumn _treeViewerAlternativeColumn;
	private TreeColumn _treeAlternativeColumn;
	private TreeViewerColumn _treeViewerCriterionColumn;
	private TreeColumn _treeCriterionColumn;
	private TreeViewerColumn _treeViewerDomainColumn;
	private TreeColumn _treeDomainColumn;
	private TreeViewerColumn _treeViewerEvaluationColumn;
	private TreeColumn _treeEvaluationColumn;
	private TreeViewerColumn _treeViewerUnifiedEvaluationColumn;
	private TreeColumn _treeUnifiedEvaluationColumn;
	
	private Button _saveButton;
	
	private RatingView _ratingView;
	
	private UnificationPhase _unificationPhase;
	
	@Override
	public void createPartControl(Composite parent) {	
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		_unificationPhase = (UnificationPhase) pmm.getPhaseMethod(UnificationPhase.ID).getImplementation();
		
		_unifiedDomain = null;
		
		_completed = true;
		
		_parent = parent;
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_parent.setLayoutData(gridData);
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 15;
		layout.marginLeft = 20;
		layout.marginRight = 20;
		layout.marginBottom = 15;
		layout.marginTop = 20;
		_parent.setLayout(layout);

		createEvaluationsTable();
		createButtons();
		compactTable();
	}
	
	private void createEvaluationsTable() {
		Composite container = new Composite(_parent, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gridData.verticalIndent = 0;
		container.setLayoutData(gridData);
		GridLayout gl_container = new GridLayout(1, false);
		gl_container.verticalSpacing = 0;
		gl_container.marginWidth = 0;
		gl_container.marginHeight = 0;
		gl_container.horizontalSpacing = 0;
		container.setLayout(gl_container);

		_treeViewer = new TreeViewer(container, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		_treeViewer.setComparator(new UnificationTreeViewerComparator());
		_tree = _treeViewer.getTree();
		_tree.setHeaderVisible(true);
		_tree.setLinesVisible(true);
		_tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_treeViewer.addTreeListener(new ITreeViewerListener() {

			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (_tree != null) {
							if (!_tree.isDisposed()) {
								for (TreeColumn column : _tree.getColumns()) {
									column.pack();
								}
							}
						}
					}
				});
			}

			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (_tree != null) {
							if (!_tree.isDisposed()) {
								for (TreeColumn column : _tree.getColumns()) {
									column.pack();
								}
							}
						}
					}
				});
			}
		});
		
		_treeViewerUnifiedEvaluationColumn = new TreeViewerColumn(_treeViewer, SWT.NONE);
		_treeUnifiedEvaluationColumn = _treeViewerUnifiedEvaluationColumn.getColumn();
		_treeUnifiedEvaluationColumn.setWidth(125);
		_treeUnifiedEvaluationColumn.setText(Messages.Unification_Unified);
		_treeUnifiedEvaluationColumn.setImage(Images.Valuation);
		_treeUnifiedEvaluationColumn.addSelectionListener(getSelectionAdapter(_treeEvaluationColumn, 5));
		_treeViewerUnifiedEvaluationColumn.setLabelProvider(new UnifiedEvaluationColumnLabelProvider());

		_treeViewerExpertColumn = new TreeViewerColumn(_treeViewer, SWT.NONE);
		_treeExpertColumn = _treeViewerExpertColumn.getColumn();
		_treeExpertColumn.setWidth(125);
		_treeExpertColumn.setText(Messages.Unification_Expert);
		_treeExpertColumn.setImage(Images.GroupOfExperts);
		_treeExpertColumn.addSelectionListener(getSelectionAdapter(_treeExpertColumn, 0));
		_treeViewerExpertColumn.setLabelProvider(new ExpertColumnLabelProvider());

		_treeViewerAlternativeColumn = new TreeViewerColumn(_treeViewer, SWT.NONE);
		_treeAlternativeColumn = _treeViewerAlternativeColumn.getColumn();
		_treeAlternativeColumn.setWidth(125);
		_treeAlternativeColumn.setText(Messages.Unification_Alternative);
		_treeAlternativeColumn.setImage(Images.Alternative);
		_treeAlternativeColumn.addSelectionListener(getSelectionAdapter(_treeAlternativeColumn, 1));
		_treeViewerAlternativeColumn.setLabelProvider(new AlternativeColumnLabelProvider());

		_treeViewerCriterionColumn = new TreeViewerColumn(_treeViewer, SWT.NONE);
		_treeCriterionColumn = _treeViewerCriterionColumn.getColumn();
		_treeCriterionColumn.setWidth(125);
		_treeCriterionColumn.setText(Messages.Unification_Criterion);
		_treeCriterionColumn.setImage(Images.Criteria);
		_treeCriterionColumn.addSelectionListener(getSelectionAdapter(_treeCriterionColumn, 2));
		_treeViewerCriterionColumn.setLabelProvider(new CriterionColumnLabelProvider());

		_treeViewerDomainColumn = new TreeViewerColumn(_treeViewer, SWT.NONE);
		_treeDomainColumn = _treeViewerDomainColumn.getColumn();
		_treeDomainColumn.setWidth(125);
		_treeDomainColumn.setText(Messages.Unification_Source_domain);
		_treeDomainColumn.setImage(Images.Domain);
		_treeDomainColumn.addSelectionListener(getSelectionAdapter(_treeDomainColumn, 3));
		_treeViewerDomainColumn.setLabelProvider(new DomainColumnLabelProvider());

		_treeViewerEvaluationColumn = new TreeViewerColumn(_treeViewer, SWT.NONE);
		_treeEvaluationColumn = _treeViewerEvaluationColumn.getColumn();
		_treeEvaluationColumn.setWidth(125);
		_treeEvaluationColumn.setText(Messages.Unification_Evaluation);
		_treeEvaluationColumn.setImage(Images.Valuation);
		_treeEvaluationColumn.addSelectionListener(getSelectionAdapter(_treeEvaluationColumn, 4));
		_treeViewerEvaluationColumn.setLabelProvider(new EvaluationColumnLabelProvider());
		
		List<Object[]> lhDomains = _unificationPhase.generateLH();
		if (lhDomains != null) {
			if(lhDomains.size() > 0) {
				_unifiedDomain = ((FuzzySet) lhDomains.get(lhDomains.size() - 1)[2]);
				_unificationPhase.setUnifiedDomain(_unifiedDomain);
			}
		}
		
		_unifiedValues = _unificationPhase.unification();
		TreeViewerContentProvider provider = new TreeViewerContentProvider(_unifiedValues);
		_treeViewer.setContentProvider(provider);
		_treeViewer.setInput(provider.getInput());
		compactTable();
	}
	
	private SelectionAdapter getSelectionAdapter(final TreeColumn column, final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				UnificationTreeViewerComparator comparator = (UnificationTreeViewerComparator) _treeViewer.getComparator();
				comparator.setColumn(index);
				int dir = comparator.getDirection();
				_treeViewer.getTree().setSortDirection(dir);
				_treeViewer.getTree().setSortColumn(column);
				_treeViewer.refresh();
			}
		};
		return selectionAdapter;
	}
	
	private void createButtons() {
		Composite container = new Composite(_parent, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gridData.verticalIndent = 0;
		container.setLayoutData(gridData);
		GridLayout gl_container = new GridLayout(1, false);
		gl_container.verticalSpacing = 0;
		gl_container.marginWidth = 0;
		gl_container.marginHeight = 0;
		gl_container.horizontalSpacing = 0;
		container.setLayout(gl_container);
		
		_saveButton = new Button(container, SWT.PUSH);
		_saveButton.setText(Messages.Unification_Save);
		_saveButton.setImage(Images.Excel);
		_saveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ExcelUtil excelUtil = new ExcelUtil();
				excelUtil.createExcelFile(_unificationPhase.getTwoTupleValuations());
			}
		});
	}

	private void compactTable() {
		_treeExpertColumn.pack();
		_treeAlternativeColumn.pack();
		_treeCriterionColumn.pack();
		_treeDomainColumn.pack();
		_treeEvaluationColumn.pack();
		_treeUnifiedEvaluationColumn.pack();
	}
	
	@Override
	public String getPartName() {
		return Messages.Unification_Unification;
	}
	
	@Override
	public void setFocus() {
		_treeViewer.getControl().setFocus();
	}
	
	@Override
		public void dispose() {
			super.dispose();
			
			_completed = false;
			_unifiedDomain = null;
			_unifiedValues = null;
		}

	@Override
	public void notifyStepStateChange() {
		List<Object[]> lhDomains = new LinkedList<Object[]>();
		lhDomains.addAll(_unificationPhase.getLHDomains());
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		RetranslationPhase retranslationPhase = (RetranslationPhase) pmm.getPhaseMethod(RetranslationPhase.ID).getImplementation();
		retranslationPhase.clear();
		retranslationPhase.setLHDomains(lhDomains);
		
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
