package flintstones.elements.ui.view.experts.provider;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import flintstones.elements.ui.view.experts.Expert;

/**
 * ExpertsContentProvider.java
 * 
 * Clase que se encarga de extraer los objetos de entrada y pasarlo a un Tree para mostrarlos
 * 
 * @author Labella Romero, Álvaro
 * @version 1.0
 *
 */
public class ExpertsContentProvider implements ITreeContentProvider {
	
	private List<Expert> _experts;
	private TreeViewer _viewer;
	
	public ExpertsContentProvider(){
		this._experts = null;
	}
	
	public ExpertsContentProvider(TreeViewer viewer){
		this();
		this._viewer = viewer;
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		return false;
	}
	

}
