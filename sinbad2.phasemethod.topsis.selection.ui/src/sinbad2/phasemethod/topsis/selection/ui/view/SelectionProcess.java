package sinbad2.phasemethod.topsis.selection.ui.view;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;

public class SelectionProcess extends ViewPart {
	
	public static final String ID = "flintstones.phasemethod.topsis.selection.ui.view.selectionprocess";
	
	private Composite _parent;
	private Composite _ratingEditorPanel;
	
	private CTabFolder _tabFolder;
	
	private TableViewer _tableViewer;
	
	private ProblemElementsSet _elementsSet;

	@Override
	public void createPartControl(Composite parent) {
		_parent = parent;
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_parent.setLayoutData(gridData);
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 15;
		layout.verticalSpacing = 15;
		_parent.setLayout(layout);	
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		createContent();
	}

	private void createContent() {
		_ratingEditorPanel = new Composite(_parent, SWT.BORDER);
		
		GridLayout ratingEditorPanelLayout = new GridLayout(1, false);
		ratingEditorPanelLayout.marginRight = 0;
		ratingEditorPanelLayout.verticalSpacing = 0;
		ratingEditorPanelLayout.marginWidth = 0;
		ratingEditorPanelLayout.marginHeight = 0;
		_ratingEditorPanel.setLayout(ratingEditorPanelLayout);
		
		_tabFolder = new CTabFolder(_ratingEditorPanel, SWT.BORDER | SWT.VERTICAL);
		_tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		Display display = Display.getCurrent();
		_tabFolder.setSelectionBackground(display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		_tabFolder.layout();
		 
		createItems();
		
	}

	private void createItems() {
		CTabItem item1 = new CTabItem(_tabFolder, SWT.CLOSE, 0);
	    item1.setText("Decision matrix");
	    item1.setShowClose(false);
	    CTabItem item2 = new CTabItem(_tabFolder, SWT.CLOSE, 1);
	    item2.setText("Weigthed decision matrix");
	    item2.setShowClose(false);
	    CTabItem item3 = new CTabItem(_tabFolder, SWT.CLOSE, 2);
	    item3.setText("Positive ideal distance");
	    item3.setShowClose(false);
	    CTabItem item4 = new CTabItem(_tabFolder, SWT.CLOSE, 3);
	    item4.setText("Negative ideal distance");
	    item4.setShowClose(false);
		
	    GridLayout topsisPanelLayout = new GridLayout(1, false);
	    topsisPanelLayout.marginRight = 0;
	    topsisPanelLayout.marginWidth = 0;
		topsisPanelLayout.marginHeight = 0;
	    topsisPanelLayout.verticalSpacing = 0;
	    Composite topsisPanel = new Composite(_ratingEditorPanel, SWT.BORDER);
	    topsisPanel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	    topsisPanel.setLayout(topsisPanelLayout);
	   
	    _tableViewer = new TableViewer(topsisPanel, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gridData.verticalIndent = 0;
		_tableViewer.getTable().setLayoutData(gridData);
		_tableViewer.getTable().setHeaderVisible(true);
		
		TableViewerColumn alternativeColumn = new TableViewerColumn(_tableViewer, SWT.NONE);
		alternativeColumn.getColumn().setWidth(25);
		alternativeColumn.getColumn().setText("Alternative");
		alternativeColumn.getColumn().setResizable(false);
		alternativeColumn.getColumn().setMoveable(false);
		alternativeColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return "";
			}
		});
		
		for(int i = 0; i < _elementsSet.getAllExperts().size(); ++i) {
			TableViewerColumn columnCoeficient = new TableViewerColumn(_tableViewer, SWT.NONE);
			columnCoeficient.getColumn().setWidth(25);
			columnCoeficient.getColumn().setText("CO" + (i + 1));
			columnCoeficient.getColumn().setResizable(false);
			columnCoeficient.getColumn().setMoveable(false);
		}
		
		item1.setControl(topsisPanel);
		item2.setControl(topsisPanel);
		item3.setControl(topsisPanel);
		item4.setControl(topsisPanel);
	    
	}

	@Override
	public void setFocus() {
		_tableViewer.getTable().setFocus();	
	}

}
