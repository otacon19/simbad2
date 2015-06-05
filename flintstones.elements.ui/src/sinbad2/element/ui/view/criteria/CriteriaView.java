package sinbad2.element.ui.view.criteria;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

import sinbad2.element.criterion.Criterion;
import sinbad2.element.ui.Images;
import sinbad2.element.ui.draganddrop.CriteriaDropListener;
import sinbad2.element.ui.draganddrop.DragListener;
import sinbad2.element.ui.handler.criterion.modify.ModifyCriterionHandler;
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
		hookDoubleClickListener();
		
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
		
		_treeViewer.getTree().addControlListener(new ControlAdapter() {
		
			public void controlResized(ControlEvent e) {
	            packAndFillLastColumn();
	        }
	
			private void packAndFillLastColumn() {
				Tree tree = _treeViewer.getTree();
			    int columnsWidth = 0;
			    
			    for (int i = 0; i < tree.getColumnCount() - 1; i++) {
			        columnsWidth += tree.getColumn(i).getWidth();
			    }
			    TreeColumn lastColumn = tree.getColumn(tree.getColumnCount() - 1);
			    lastColumn.pack();
	
			    Rectangle area = tree.getClientArea();
	
			    Point preferredSize = tree.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			    int width = area.width - 2*tree.getBorderWidth();
	
			    if (preferredSize.y > area.height + tree.getHeaderHeight()) {
			        Point vBarSize = tree.getVerticalBar().getSize();
			        width -= vBarSize.x;
			    }
	
			    if(lastColumn.getWidth() < width - columnsWidth) {
			        lastColumn.setWidth(width - columnsWidth);
			    }
				
			}
	    });
	}
	
	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(_treeViewer.getTree());
		_treeViewer.getTree().setMenu(menu);
		getSite().registerContextMenu(menuManager, _treeViewer);
	}
	
	private void hookDoubleClickListener() {
		_treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Criterion criterion = (Criterion) selection.getFirstElement();
				
				ModifyCriterionHandler modifyCriterionHandler = new ModifyCriterionHandler(criterion);
				
				try {
					modifyCriterionHandler.execute(null);
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
	}
	
	
	@Override
	public void setFocus() {
		_treeViewer.getControl().setFocus();
		
	}

}
