package sinbad2.element.ui.view.criteria.provider;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import sinbad2.element.IProblemElementsSetChangeListener;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.criterion.listener.CriteriaChangeEvent;
import sinbad2.element.criterion.listener.ICriteriaChangeListener;

public class CriteriaContentProvider implements ITreeContentProvider, ICriteriaChangeListener, IProblemElementsSetChangeListener {

	private ProblemElementsManager _elementManager;
	private ProblemElementsSet _elementSet;
	private List<Criterion> _criteria;
	private TreeViewer _treeViewer;
	
	public CriteriaContentProvider() {
		_criteria = null;
	}
	
	public CriteriaContentProvider(TreeViewer treeViewer) {
		this();
		_treeViewer = treeViewer;
		hookTreeListener();
		
		_elementManager = ProblemElementsManager.getInstance();
		_elementSet = _elementManager.getActiveElementSet();
		_criteria = _elementSet.getCriteria();
		
		_elementSet.registerCriteriaChangeListener(this);
		_elementManager.registerElementsSetChangeListener(this);
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
		_elementSet.unregisterCriteriaChangeListener(this);
		_elementManager.unregisterElementsSetChangeListener(this);
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Criterion>) inputElement).toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return ((Criterion) parentElement).getSubcriteria().toArray();
	}

	@Override
	public Object getParent(Object element) {
		return ((Criterion) element).getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		return ((Criterion) element).hasSubcriteria();
	}
	
	public Object getInput() {
		return _criteria;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void notifyCriteriaChange(CriteriaChangeEvent event) {
		
		switch(event.getChange()) {
			case CRITERIA_CHANGES:
				_criteria = (List<Criterion>) event.getNewValue();
				_treeViewer.setInput(_criteria);
				break;
			case ADD_CRITERION:
				addCriterion((Criterion) event.getNewValue());
				break;
			case ADD_CRITERIA:
				for(Criterion criterion: (List<Criterion>) event.getNewValue()) {
					addCriterion(criterion);
				}
				break;
			case REMOVE_CRITERION:
				removeCriterion((Criterion) event.getOldValue());
				break;
			case REMOVE_CRITERIA:
				for(Criterion criterion: (List<Criterion>) event.getOldValue()) {
					removeCriterion(criterion);
				}
				break;
			case MODIFY_CRITERION:
				modifyCriterion((Criterion) event.getNewValue());
				break;
			case MOVE_CRITERION:
				_treeViewer.refresh();
				break;
		}
		
		packViewer();
	}
	
	private void addCriterion(Criterion criterion) {
		Criterion parent = criterion.getParent();
		
		if(parent != null) {
			_treeViewer.add(parent, criterion);
			_treeViewer.refresh();
			_treeViewer.reveal(criterion);
		} else {
			int pos = 0;
			boolean find = false;
			do {
				if(_criteria.get(pos) == criterion) {
					find = true;
				} else {
					pos++;
				}
			} while (!find);
			_treeViewer.insert(_treeViewer.getInput(), criterion, pos);
		}
		
	}
	
	private void removeCriterion(Criterion criterion) {
		_treeViewer.refresh(criterion.getParent());
		
	}
	
	private void modifyCriterion(Criterion criterion) {
		Object[] expandedElements = _treeViewer.getExpandedElements();
		_treeViewer.refresh(criterion.getParent());
		_treeViewer.setExpandedElements(expandedElements);
		
	}
	
	private void packViewer() {
		for(int i = 0; i < _treeViewer.getTree().getColumns().length - 1; ++i ) {
			 _treeViewer.getTree().getColumn(i).pack();
		}
	}

	@Override
	public void notifyNewProblemElementsSet(ProblemElementsSet elementSet) {
		
		if(_elementSet != elementSet) {
			_elementManager.unregisterElementsSetChangeListener(this);
			_elementSet = elementSet;
			_criteria = _elementSet.getCriteria();
			_elementSet.registerCriteriaChangeListener(this);
			_treeViewer.setInput(_criteria);
		}
		
	}

}
