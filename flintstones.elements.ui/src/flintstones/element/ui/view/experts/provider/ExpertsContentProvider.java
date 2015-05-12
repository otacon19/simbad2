package flintstones.element.ui.view.experts.provider;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;

import flintstones.element.ElementSet;
import flintstones.element.ElementsManager;
import flintstones.element.IElementSetChangeListener;
import flintstones.element.expert.Expert;
import flintstones.element.expert.listener.ExpertsChangeEvent;
import flintstones.element.expert.listener.IExpertsChangeListener;

/**
 * ExpertsContentProvider.java
 * 
 * Clase que se encarga de extraer los objetos de entrada y pasarlo a un Tree para mostrarlos (Content Provider)
 * 
 * @author Labella Romero, Álvaro
 * @version 1.0
 *
 */
public class ExpertsContentProvider implements ITreeContentProvider, IExpertsChangeListener, IElementSetChangeListener {
	
	private ElementsManager _elementsManager;
	private ElementSet _elementSet;
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
		
		_elementsManager = ElementsManager.getInstance();
		_elementSet = _elementsManager.getActiveElementSet();

		_experts = _elementSet.getExperts();
				
		_elementSet.registerExpertsChangesListener(this);
		_elementsManager.registerElementsSetChangeListener(this);
		
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
		_elementSet.unregisterExpertsChangeListener(this);
		_elementsManager.unregisterElementsSetChangeListener(this);

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
	
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
	
	@SuppressWarnings("unchecked")
	@Override
	public void notifyExpertsChange(ExpertsChangeEvent event) {
		switch(event.getChange()) {
			case EXPERTS_CHANGES:
				_experts = (List<Expert>) event.getNewValue();
				_viewer.setInput(_experts);
				break;
			case ADD_EXPERT:
				addExpert((Expert) event.getNewValue());
				break;
			case REMOVE_EXPERT:
				removeExpert((Expert) event.getOldValue());
				break;
			case MODIFY_EXPERT:
				modifyExpert((Expert) event.getNewValue());
				break;
		default:
			break;
		}
		
		packViewer();
		
	}
	
	private void addExpert(Expert expert) {
		Expert parent = expert.getParent();

		if(parent != null) {
			_viewer.add(parent, expert);
			_viewer.refresh(parent);
			_viewer.reveal(expert);
		} else {
			int pos = 0;
			boolean find = false;
			do {
				if(_experts.get(pos) == expert) {
					find = true;
				} else {
					pos++;
				}
			} while (!find);
			_viewer.insert(_viewer.getInput(), expert, pos);
		}
	}
	
	private void removeExpert(Expert expert) {
		_viewer.refresh(expert.getParent());
	}
	
	private void modifyExpert(Expert expert) {
		Object[] expandedElements = _viewer.getExpandedElements();
		_viewer.refresh(expert.getParent());
		_viewer.setExpandedElements(expandedElements);
	}
	
	/**
	 * Método que autoajusta el árbol cuando se hace una modificación sobre él
	 */
	private void packViewer() {
		for(TreeColumn column: _viewer.getTree().getColumns()) {
			column.pack();
		}
	}

	@Override
	public void newActiveSetElementSet(ElementSet elementSet) {
		if(_elementSet != elementSet) {
			_elementSet.unregisterExpertsChangeListener(this);
			_elementSet = elementSet;
			_experts = _elementSet.getExperts();
			_elementSet.registerExpertsChangesListener(this);
			_viewer.setInput(_experts);
		}
		
	}

}
