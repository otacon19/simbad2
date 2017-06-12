package sinbad2.resolutionphase.sensitivityanalysis.ui.ranking;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import sinbad2.core.workspace.Workspace;
import sinbad2.method.MethodsManager;
import sinbad2.resolutionphase.sensitivityanalysis.EModel;
import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;
import sinbad2.resolutionphase.sensitivityanalysis.ui.nls.Messages;
import sinbad2.resolutionphase.sensitivityanalysis.ui.ranking.provider.RankingContentProvider;

public class RankingView extends ViewPart implements IDisplayRankingChangeListener {
	
	public static final String ID = "flintstones.resolutionphase.sensitivityanalysis.ui.views.ranking"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.resolutionphase.sensitivityanalysis.ui.views.ranking.ranking_view"; //$NON-NLS-1$
	
	private TableViewer _rankingViewer;
	private Combo _sensitivityModels;
	private Composite _parent;
	
	private SensitivityAnalysis _sensitivityAnalysis;
	
	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);

	@Override
	public void createPartControl(Composite parent) {
		_parent = parent;
		_parent.setLayout(new GridLayout());
		_parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite rankingComposite = new Composite(_parent, SWT.NONE);
		rankingComposite.setLayout(new GridLayout());
		rankingComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_rankingViewer = new TableViewer(rankingComposite, SWT.BORDER | SWT.MULTI| SWT.FULL_SELECTION | SWT.NO_FOCUS | SWT.HIDE_SELECTION);

		Table rankingTable = _rankingViewer.getTable();
		rankingTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		rankingTable.setHeaderVisible(true);
		rankingTable.setLinesVisible(true);

		_sensitivityAnalysis = (SensitivityAnalysis) Workspace.getWorkspace().getElement(SensitivityAnalysis.ID);
		
		_rankingViewer.setContentProvider(new RankingContentProvider(_sensitivityAnalysis));

		addColumn("Ranking", 0); //$NON-NLS-1$
		addColumn(Messages.RankingView_Alternative, 1);
		
		TableViewerColumn tvc = new TableViewerColumn(_rankingViewer, SWT.NONE);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Object[]) element)[2].toString();
			}
		});
		TableColumn tc = tvc.getColumn();
		tc.setText(Messages.RankingView_Value);
		tc.setResizable(true);
		tc.setMoveable(true);
		tc.setWidth(55);

		Composite comboComposite = new Composite(_parent, SWT.NONE);
		comboComposite.setLayout(new GridLayout());
		comboComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		_sensitivityModels = new Combo(comboComposite, SWT.READ_ONLY);
		_sensitivityModels.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, 1, 1));
		String[] methods = new String[3];
		methods[0] = Messages.RankingView_Weighted_sum;
		methods[1] = Messages.RankingView_Weighted_product;
		methods[2] = Messages.RankingView_Analytic_Hierarchy_process;
		_sensitivityModels.setItems(methods);
		_sensitivityModels.select(0);
		_sensitivityModels.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				_sensitivityAnalysis.setModel(EModel.values()[_sensitivityModels.getSelectionIndex()]);		
				_sensitivityAnalysis.compute();
				
				int selectedIndex = _sensitivityModels.getSelectionIndex();
				if(selectedIndex == 0 || selectedIndex == 2) {
					_rankingViewer.getTable().getColumn(2).setText(Messages.RankingView_Value);
				} else {
					_rankingViewer.getTable().getColumn(2).setText("Ratios"); //$NON-NLS-1$
				}
				
				_rankingViewer.getTable().getColumn(2).pack();
			}
		});
		
		updateCombo();
		
		RankingViewManager.getInstance().registerDisplayRankingChangeListener(this);
		hookFocusListener();
	}
	
	private void updateCombo() {
		String categoryMethod = MethodsManager.getInstance().getActiveMethod().getCategory();
		if(categoryMethod.contains(Messages.RankingView_Multi_criteria_decision_analysis)) {
			_sensitivityModels.setEnabled(false);
		} else {
			_sensitivityModels.setEnabled(true);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		
		RankingViewManager.getInstance().unregisterDisplayRankingChangeListener(this);
	}

	private void addColumn(String text, final int pos) {
		TableViewerColumn tvc = new TableViewerColumn(_rankingViewer, SWT.NONE);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Object[]) element)[pos].toString();
			}
		});
		TableColumn tc = tvc.getColumn();
		tc.setText(text);
		tc.setResizable(true);
		tc.setMoveable(true);
		tc.pack();
	}

	private void hookFocusListener() {
		_rankingViewer.getControl().addFocusListener(new FocusListener() {

			private IContextActivation activation = null;

			@Override
			public void focusLost(FocusEvent e) {
				_contextService.deactivateContext(activation);
			}

			@Override
			public void focusGained(FocusEvent e) {
				activation = _contextService.activateContext(CONTEXT_ID);
			}
		});	
	}

	@Override
	public void setFocus() {
		_rankingViewer.getControl().setFocus();
	}
	
	@Override
	public void displayRankingChange(Object ranking) {
		_rankingViewer.setInput(ranking);
		updateCombo();
	}
}
