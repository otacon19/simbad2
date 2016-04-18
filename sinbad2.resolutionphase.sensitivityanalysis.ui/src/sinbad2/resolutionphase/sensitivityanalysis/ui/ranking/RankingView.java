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
import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;
import sinbad2.resolutionphase.sensitivityanalysis.ui.ranking.provider.RankingContentProvider;

public class RankingView extends ViewPart implements IDisplayRankingChangeListener {
	
	public static final String ID = "flintstones.resolutionphase.sensitivityanalysis.ui.views.ranking"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.resolutionphase.sensitivityanalysis.ui.views.ranking.ranking_view"; //$NON-NLS-1$
	
	private SensitivityAnalysis _sensitivityAnalysis;
	
	private TableViewer _rankingViewer;
	private Combo _sensitivityModels;

	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite rankingComposite = new Composite(parent, SWT.NONE);
		rankingComposite.setLayout(new GridLayout());
		rankingComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_rankingViewer = new TableViewer(rankingComposite, SWT.BORDER | SWT.MULTI| SWT.FULL_SELECTION | SWT.NO_FOCUS | SWT.HIDE_SELECTION);

		Table rankingTable = _rankingViewer.getTable();
		rankingTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		rankingTable.setHeaderVisible(true);
		rankingTable.setLinesVisible(true);

		_sensitivityAnalysis = (SensitivityAnalysis) Workspace.getWorkspace().getElement(SensitivityAnalysis.ID);
		
		_rankingViewer.setContentProvider(new RankingContentProvider(_sensitivityAnalysis, this));

		addColumn("Ranking", 0); //$NON-NLS-1$
		addColumn("Alternative", 1); //$NON-NLS-1$
		
		TableViewerColumn tvc = new TableViewerColumn(_rankingViewer, SWT.NONE);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Object[]) element)[2].toString();
			}
		});
		TableColumn tc = tvc.getColumn();
		tc.setText("Value");
		tc.setResizable(true);
		tc.setMoveable(true);
		tc.setWidth(55);

		Composite comboComposite = new Composite(parent, SWT.NONE);
		comboComposite.setLayout(new GridLayout());
		comboComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		_sensitivityModels = new Combo(comboComposite, SWT.READ_ONLY);
		_sensitivityModels.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, 1, 1));
		String[] methods = new String[3];
		methods[0] = "Weighted sum";
		methods[1] = "Weighted product";
		methods[2] = "Analytic Hierarchy process";
		_sensitivityModels.setItems(methods);
		_sensitivityModels.select(0);
		_sensitivityModels.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(_sensitivityModels.getSelectionIndex() == 0) {
					_sensitivityAnalysis.computeWeightedSumModel();
					_rankingViewer.getTable().getColumn(2).setText("Value");
				} else if(_sensitivityModels.getSelectionIndex() == 1){
					_sensitivityAnalysis.computeWeightedProductModel();
					_rankingViewer.getTable().getColumn(2).setText("Ratios");
				} else if(_sensitivityModels.getSelectionIndex() == 2) {
					_sensitivityAnalysis.computeAnalyticHierarchyProcess();
					_rankingViewer.getTable().getColumn(2).setText("Value");
				}
				_rankingViewer.getTable().getColumn(2).pack();
			}
		});
		
		RankingViewManager.getInstance().registerDisplayRankingChangeListener(this);
		hookFocusListener();
	}
	
	@Override
	public void dispose() {
		RankingViewManager.getInstance().unregisterDisplayRankingChangeListener(this);
		super.dispose();
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
	
	public int getModel() {
		return _sensitivityModels.getSelectionIndex();
	}

	@Override
	public void setFocus() {
		_rankingViewer.getControl().setFocus();
	}
	
	@Override
	public void displayRankingChange(Object ranking) {
		_rankingViewer.setInput(ranking);
	}
}
