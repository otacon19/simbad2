package sinbad2.element.ui.view.experts.provider;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.IProblemElementsSetChangeListener;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.expert.Expert;
import sinbad2.element.expert.listener.ExpertsChangeEvent;
import sinbad2.element.expert.listener.IExpertsChangeListener;

public class ExpertsContentProvider implements ITreeContentProvider, IExpertsChangeListener, IProblemElementsSetChangeListener {
	
	private ProblemElementsManager _elementsManager;
	private ProblemElementsSet _elementSet;
	private List<Expert> _experts;
	private TreeViewer _treeViewer;

	public ExpertsContentProvider(){
		
		 _experts = null;
	}
	
	public ExpertsContentProvider(TreeViewer viewer){
		this();
		_treeViewer = viewer;
		hookTreeListener();
		
		_elementsManager = ProblemElementsManager.getInstance();
		_elementSet = _elementsManager.getActiveElementSet();

		_experts = _elementSet.getExperts();
				
		_elementSet.registerExpertsChangesListener(this);
		_elementsManager.registerElementsSetChangeListener(this);
		
	}
	
	private void hookTreeListener() {
		_treeViewer.addTreeListener(new ITreeViewerListener() {
			
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
	

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Expert>) inputElement).toArray();
	}

	@Override
	public Object[] getChildren(Object parentExpertElement) {
		return ((Expert) parentExpertElement).getChildrens().toArray();
	}

	@Override
	public Object getParent(Object expertElement) {
		return ((Expert) expertElement).getParent();
	}
	
	@Override
	public boolean hasChildren(Object expertElement) {
		return ((Expert) expertElement).hasChildrens();
	}
	
	public Object getInput() {
		return _experts;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void notifyExpertsChange(ExpertsChangeEvent event) {
		switch(event.getChange()) {
			case EXPERTS_CHANGES:
				_experts = (List<Expert>) event.getNewValue();
				_treeViewer.setInput(_experts);
				break;
			case ADD_EXPERT:
				addExpert((Expert) event.getNewValue());
				break;
			case ADD_MULTIPLE_EXPERTS:
				for(Expert expert: (List<Expert>) event.getNewValue()) {
					addExpert(expert);
				}
				break;
			case REMOVE_EXPERT:
				removeExpert((Expert) event.getOldValue());
				break;
			case REMOVE_MULTIPLE_EXPERTS:
				for(Expert expert: (List<Expert>) event.getOldValue()) {
					removeExpert(expert);
				}
				break;
			case MODIFY_EXPERT:
				modifyExpert((Expert) event.getNewValue());
				break;
			case MOVE_EXPERT:
				_treeViewer.refresh();
				break;
		}
		
		packViewer();
		
	}
	
	private void addExpert(Expert expert) {
		Expert parent = expert.getParent();

		if(parent != null) {
			_treeViewer.add(parent, expert);
			_treeViewer.refresh(parent);
			_treeViewer.reveal(expert);
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
			_treeViewer.insert(_treeViewer.getInput(), expert, pos);
		}
	}
	
	private void removeExpert(Expert expert) {
		_treeViewer.refresh(expert.getParent());
	}
	
	private void modifyExpert(Expert expert) {
		Object[] expandedElements = _treeViewer.getExpandedElements();
		_treeViewer.refresh(expert.getParent());
		_treeViewer.setExpandedElements(expandedElements);
	}
	
	private void packViewer() {
		for(TreeColumn column: _treeViewer.getTree().getColumns()) {
			column.pack();
		}
	}

	@Override
	public void notifyNewProblemElementsSet(ProblemElementsSet elementSet) {
		if(_elementSet != elementSet) {
			_elementSet.unregisterExpertsChangeListener(this);
			_elementSet = elementSet;
			_experts = _elementSet.getExperts();
			_elementSet.registerExpertsChangesListener(this);
			_treeViewer.setInput(_experts);
		}
		
	}

}
