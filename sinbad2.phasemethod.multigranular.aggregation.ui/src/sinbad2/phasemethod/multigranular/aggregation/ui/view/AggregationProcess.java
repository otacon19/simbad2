package sinbad2.phasemethod.multigranular.aggregation.ui.view;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import sinbad2.phasemethod.multigranular.aggregation.ui.view.provider.AlternativeColumnLabelProvider;
import sinbad2.phasemethod.multigranular.aggregation.ui.view.provider.EvaluationColumnLabelProvider;
import sinbad2.phasemethod.multigranular.aggregation.ui.view.provider.RankingColumnLabelProvider;
import sinbad2.phasemethod.multigranular.aggregation.ui.view.provider.RankingViewerProvider;

public class AggregationProcess extends ViewPart {
	
	public static final String ID = "flintstones.phasemethod.multigranular.aggregation.ui.view.aggregationprocess";

	private Composite _parent;
	private Composite _resultsPanel;
	private Composite _chartView;
	
	private TableViewer _rankingViewer;
	private TableViewerColumn _rankingViewerRankingColumn;
	private TableColumn _rankingRankingColumn;
	private TableViewerColumn _rankingViewerAlternativeColumn;
	private TableColumn _rankingAlternativeColumn;
	private TableViewerColumn _rankingViewerEvaluationColumn;
	private TableColumn _rankingEvaluationColumn;
	
	private AggregationProcess() {}
	
	@Override
	public void createPartControl(Composite parent) {
		_parent = parent;
		
		GridLayout layout = (GridLayout) _parent.getLayout();
		layout.marginLeft = 20;
		layout.marginRight = 15;
		layout.marginBottom = 15;
		layout.marginTop = 20;
		
		_resultsPanel = new Composite(_parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true, 8, 1);
		_resultsPanel.setLayoutData(gridData);
		layout = new GridLayout(1, false);
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
		_rankingAlternativeColumn.setWidth(100);
		_rankingAlternativeColumn.setText("Alternative");
		_rankingViewerAlternativeColumn.setLabelProvider(new AlternativeColumnLabelProvider());

		_rankingViewerEvaluationColumn = new TableViewerColumn(_rankingViewer, SWT.NONE);
		_rankingEvaluationColumn = _rankingViewerEvaluationColumn.getColumn();
		_rankingEvaluationColumn.setWidth(100);
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
	}
	
	@Override
	public void setFocus() {
		_rankingViewer.getControl().setFocus();
	}
	
	@Override
	public String getPartName() {
		return "Aggregation process";
	}

}
