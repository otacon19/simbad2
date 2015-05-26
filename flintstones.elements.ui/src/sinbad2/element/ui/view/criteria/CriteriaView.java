package sinbad2.element.ui.view.criteria;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

import sinbad2.element.ui.Images;
import sinbad2.element.ui.draganddrop.CriteriaDropListener;
import sinbad2.element.ui.draganddrop.DragListener;
import sinbad2.element.ui.view.criteria.editing.CriterionCostEditingSupport;
import sinbad2.element.ui.view.criteria.provider.CriteriaContentProvider;
import sinbad2.element.ui.view.criteria.provider.CriterionCostLabelProvider;
import sinbad2.element.ui.view.criteria.provider.CriterionIdLabelProvider;

public class CriteriaView extends ViewPart {
	
	public static final String ID = "flintstones.element.ui.view.criteria"; //$NON-NLS-1$
	
	private TreeViewer _treeViewer;
	
	private CriteriaContentProvider _provider;
	
	public CriteriaView() {}
	
	@Override
	public void createPartControl(Composite parent) {
		_treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		_treeViewer.getTree().setHeaderVisible(true);
		
		_treeViewer.getTree().addListener(SWT.MeasureItem, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				event.height = 25;
				
			}
		});
		
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance() };
		_treeViewer.addDragSupport(operations, transferTypes, new DragListener(_treeViewer));
		_treeViewer.addDropSupport(operations, transferTypes, new CriteriaDropListener(_treeViewer));
		
		_provider = new CriteriaContentProvider(_treeViewer);
		_treeViewer.setContentProvider(_provider);
		
		addColumns();
		hookContextMenu();
		//hookFocusListener();
		//TODO hookSelectionChangedListener();
		//TODO hookDoubleClickListener();
		
		_treeViewer.setInput(_provider.getInput());
		getSite().setSelectionProvider(_treeViewer);
	}
	
	private void addColumns() {
		TreeViewerColumn tvc = new TreeViewerColumn(_treeViewer, SWT.NONE);
		tvc.setLabelProvider(new CriterionIdLabelProvider());
		TreeColumn tc = tvc.getColumn();
		tc.setMoveable(true);
		tc.setResizable(false);
		tc.setText("Criteria");
		tc.setImage(Images.Criterion);
		tc.pack();	
		
		tvc = new TreeViewerColumn(_treeViewer, SWT.NONE);
		tvc.setLabelProvider(new CriterionCostLabelProvider());
		tvc.setEditingSupport(new CriterionCostEditingSupport(_treeViewer));
		tc = tvc.getColumn();
		tc.setToolTipText("Cost/Benefit");
		tc.setMoveable(true);
		tc.setResizable(false);
		tc.setImage(Images.TypeOfCriterion);
		tc.pack();
	}
	
	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(_treeViewer.getTree());
		_treeViewer.getTree().setMenu(menu);
		getSite().registerContextMenu(menuManager, _treeViewer);
	}
	
	
	@Override
	public void setFocus() {
		_treeViewer.getControl().setFocus();
		
	}

}
