package sinbad2.element.ui.view.experts;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import sinbad2.element.expert.Expert;
import sinbad2.element.ui.handler.expert.modify.ModifyExpertHandler;
import sinbad2.element.ui.view.experts.draganddrop.ExpertsDragListener;
import sinbad2.element.ui.view.experts.draganddrop.ExpertsDropListener;
import sinbad2.element.ui.view.experts.provider.ExpertIdLabelProvider;
import sinbad2.element.ui.view.experts.provider.ExpertsContentProvider;

public class ExpertsView extends ViewPart {
	
	public static final String ID = "flintstones.element.ui.view.experts"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.element.ui.view.experts.experts_view"; //$NON-NLS-1$
	
	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);
	
	private TreeViewer _treeViewer;
	
	private ExpertsContentProvider _provider;
	
	public ExpertsView() {
		
	}

	public TreeViewer getViewer() {
		return _treeViewer;
	}

	public void setViewer(TreeViewer _viewer) {
		this._treeViewer = _viewer;
	}
	
	public ExpertsContentProvider getProvider() {
		return _provider;
	}
	
	public void setProvider(ExpertsContentProvider _provider) {
		this._provider = _provider;
	}
	
	public static String getId() {
		return ID;
	}

	@Override
	public void createPartControl(Composite parent) {
		
		_treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		
		_treeViewer.getTree().addListener(SWT.MeasureItem, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				event.height = 25;
				
			}
		});
		
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance() };
		_treeViewer.addDragSupport(operations, transferTypes, new ExpertsDragListener(_treeViewer));
		_treeViewer.addDropSupport(operations, transferTypes, new ExpertsDropListener(_treeViewer));
		
		_provider = new ExpertsContentProvider(_treeViewer);
		_treeViewer.setContentProvider(_provider);
		
		addColumns();
		hookContextMenu();
		hookFocusListener();
		//TODO hookSelectionChangeListener();
		hookDoubleClickListener();

		_treeViewer.setInput(_provider.getInput());
		getSite().setSelectionProvider(_treeViewer);
			
		
	}
	
	private void addColumns() {
		TreeViewerColumn tvc = new TreeViewerColumn(_treeViewer, SWT.NONE);
		tvc.setLabelProvider(new ExpertIdLabelProvider());
		TreeColumn tc = tvc.getColumn();
		tc.setMoveable(false);
		tc.setResizable(false);
		tc.pack();	
	}
	
	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(_treeViewer.getTree());
		_treeViewer.getTree().setMenu(menu);
		getSite().registerContextMenu(menuManager, _treeViewer);
		
	}
	
	private void hookFocusListener() {
		_treeViewer.getControl().addFocusListener(new FocusListener() {
			
			private IContextActivation activation = null;
			
			@Override
			public void focusLost(FocusEvent e) {
				//_contextService.deactivateContext(activation);
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				//activation = _contextService.activateContext(CONTEXT_ID);
				
			}
		});
	}
	
	private void hookDoubleClickListener() {
		_treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Expert expert = (Expert) selection.getFirstElement();
				
				ModifyExpertHandler modifyExpertHandler = new ModifyExpertHandler(expert);
				try {
					modifyExpertHandler.execute(null);
				} catch (ExecutionException e) {
					
				}
				
			}
		});
	}
	
	@Override
	public void setFocus() {
		_treeViewer.getControl().setFocus();
		
	}

}
