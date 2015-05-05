package flintstones.element.ui.view.experts;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import flintstones.element.ui.view.experts.provider.ExpertIdLabelProvider;
import flintstones.element.ui.view.experts.provider.ExpertsContentProvider;

/**
 * ExpertsView.java
 * 
 * Clase que define la vista de expertos
 * 
 * @author Labella Romero, Álvaro
 * @version 1.0
 *
 */
public class ExpertsView extends ViewPart {
	
	public static final String ID = "flintstones.element.ui.view.experts";
	public static final String CONTEXT_ID = "flintstones.element.ui.view.experts.experts_view";
	
	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);
	
	private TreeViewer _viewer;
	
	private ExpertsContentProvider _provider;
	
	/**
	 * Constructor de la clase ExpertsView
	 */
	public ExpertsView() {
		
	}
	
	public TreeViewer getViewer() {
		return _viewer;
	}

	public void setsViewer(TreeViewer _viewer) {
		this._viewer = _viewer;
	}

	public ExpertsContentProvider get_provider() {
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
		
		_viewer = new TreeViewer(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		
		_viewer.getTree().addListener(SWT.MeasureItem, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				event.height = 25;
				
			}
		});
		
		//TODO
		
		_provider = new ExpertsContentProvider(_viewer);
		
		_viewer.setContentProvider(_provider);
		
		addColumns();
		hookContextMenu();
		hookFocusListener();
		
		_viewer.setInput(_provider.getInput());
		getSite().setSelectionProvider(_viewer);
			
		
	}
	
	private void addColumns() {
		TreeViewerColumn tvc = new TreeViewerColumn(_viewer, SWT.NONE);
		tvc.setLabelProvider(new ExpertIdLabelProvider());
		TreeColumn tc = tvc.getColumn();
		tc.setMoveable(false);
		tc.setResizable(false);
		tc.pack();	
	}
	
	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(_viewer.getTree());
		_viewer.getTree().setMenu(menu);
		getSite().registerContextMenu(menuManager, _viewer);
		
	}
	
	private void hookFocusListener() {
		_viewer.getControl().addFocusListener(new FocusListener() {
			
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
		_viewer.getControl().setFocus();
		
	}

}
