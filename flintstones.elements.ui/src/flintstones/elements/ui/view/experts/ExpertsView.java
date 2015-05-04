package flintstones.elements.ui.view.experts;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import flintstones.elements.ui.view.experts.provider.ExpertsContentProvider;

public class ExpertsView extends ViewPart {
	
	public static final String ID = "flintstones.element.ui.view.experts";
	
	private TreeViewer _viewer;
	
	private ExpertsContentProvider _provider;
	
	public ExpertsView() {
		
	}
	
	public TreeViewer get_viewer() {
		return _viewer;
	}

	public void set_viewer(TreeViewer _viewer) {
		this._viewer = _viewer;
	}

	public ExpertsContentProvider get_provider() {
		return _provider;
	}

	public void set_provider(ExpertsContentProvider _provider) {
		this._provider = _provider;
	}

	public static String getId() {
		return ID;
	}


	@Override
	public void createPartControl(Composite parent) {
		
		_viewer = new TreeViewer(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		
		_provider = new ExpertsContentProvider(_viewer);
		
		_viewer.setContentProvider(_provider);
		
		//_viewer.setInput(Expert.createExpertsExample());
		getSite().setSelectionProvider(_viewer);
			
		
	}

	@Override
	public void setFocus() {
		_viewer.getControl().setFocus();
		
	}

}
