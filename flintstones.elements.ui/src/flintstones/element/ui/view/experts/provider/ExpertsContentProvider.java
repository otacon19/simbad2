package flintstones.element.ui.view.experts.provider;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;

import flintstones.element.expert.Expert;

/**
 * ExpertsContentProvider.java
 * 
 * Clase que se encarga de extraer los objetos de entrada y pasarlo a un Tree para mostrarlos (Content Provider)
 * 
 * @author Labella Romero, Álvaro
 * @version 1.0
 *
 */
public class ExpertsContentProvider implements ITreeContentProvider {
	
	private List<Expert> _experts;
	private TreeViewer _viewer;
	
	/**
	 * Constructor de la clase ExpertsContentProvider
	 */
	public ExpertsContentProvider(){
		_experts = null;
	}
	
	/**
	 * Constructor de la clase ExpertsContentProvider
	 * @param viewer árbol a visualizar
	 */
	public ExpertsContentProvider(TreeViewer viewer){
		this();
		_viewer = viewer;
		hookTreeListener();
		
	}
	
	/**
	 * Método que controla los eventos del árbol expandir nodo y contraer nodo
	 */
	private void hookTreeListener() {
		_viewer.addTreeListener(new ITreeViewerListener() {
			
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				Display.getCurrent().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						packViewer();
						
					}
				});
				
			}
			
			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				Display.getCurrent().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						packViewer();
						
					}
				});
				
			}
		});
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Método get de los elementos de entrada (lista de expertos)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Expert>) inputElement).toArray();
	}
	
	/**
	 * Método get de los hijos de un padre en el árbol
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		return ((Expert) parentElement).getMembers().toArray();
	}
	
	/**
	 * Método get de un padre del árbol
	 */
	@Override
	public Object getParent(Object element) {
		return ((Expert) element).getParent();
	}
	
	/**
	 * Método que comprueba si un padre tiene hijos en el árbol
	 */
	@Override
	public boolean hasChildren(Object element) {
		return ((Expert) element).hasMembers();
	}
	
	/**
	 * Método get que obtiene los datos de entrada (lista de expertos)
	 * @return
	 */
	public Object getInput() {
		return _experts;
	}
	
	//TODO notifyExpertsChange
	
	//TODO addExpert
	
	//TODO removeExpert
	
	//TODO modifyExpert
	
	/**
	 * Método que autoajusta el árbol cuando se hace una modificación sobre él
	 */
	private void packViewer() {
		for(TreeColumn column: _viewer.getTree().getColumns()) {
			column.pack();
		}
	}

}
