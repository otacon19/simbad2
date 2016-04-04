package sinbad2.resolutionphase.sensitivityanalysis.ui.ranking;


import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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

	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);

	@Override
	public void createPartControl(Composite parent) {
		_rankingViewer = new TableViewer(parent, SWT.BORDER | SWT.MULTI| SWT.FULL_SELECTION | SWT.NO_FOCUS | SWT.HIDE_SELECTION);

		Table rankingTable = _rankingViewer.getTable();
		rankingTable.setHeaderVisible(true);
		rankingTable.setLinesVisible(true);

		_sensitivityAnalysis = (SensitivityAnalysis) Workspace.getWorkspace().getElement(SensitivityAnalysis.ID);
		
		_rankingViewer.setContentProvider(new RankingContentProvider(_sensitivityAnalysis));

		addColumn("Ranking", 0); //$NON-NLS-1$
		addColumn("Alternative", 1); //$NON-NLS-1$
		addColumn("Value", 2); //$NON-NLS-1$

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
		if(pos == 2) {
			tc.setWidth(45);
		} else {
			tc.pack();
		}
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
	}
}
