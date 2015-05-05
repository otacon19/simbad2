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

import flintstones.element.expert.Expert;
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
	
	/**
	 * Método get del parámetro _viewer
	 * @return árbol
	 */
	public TreeViewer getViewer() {
		return _viewer;
	}

	/**
	 * Método set del parámetro _viewer
	 * @param _viewer árbol a asignar
	 */
	public void setViewer(TreeViewer _viewer) {
		this._viewer = _viewer;
	}
	
	/**
	 * Método get del atributo _provider
	 * @return proveedor de contenido de la vista
	 */
	public ExpertsContentProvider getProvider() {
		return _provider;
	}
	
	/**
	 * Método set del atributo _provider
	 * @param _provider proveedor de contenido a asignar
	 */
	public void setProvider(ExpertsContentProvider _provider) {
		this._provider = _provider;
	}
	
	/**
	 * Método get del atributo ID
	 * @return ID de la vista
	 */
	public static String getId() {
		return ID;
	}

	/**
	 * Método que crea los controles que forman la vista
	 */
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
		
		Expert e = new Expert();
		
		_viewer.setInput(/*_provider.getInput()*/e.exampleExperts());
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
	
	/**
	 * Método que asigna el foco en el control de la vista adecuado
	 */
	@Override
	public void setFocus() {
		_viewer.getControl().setFocus();
		
	}

}
