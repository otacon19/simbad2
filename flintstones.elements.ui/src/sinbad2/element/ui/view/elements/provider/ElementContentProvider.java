package sinbad2.element.ui.view.elements.provider;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;

import sinbad2.element.IProblemElementsSetChangeListener;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.alternative.listener.AlternativesChangeEvent;
import sinbad2.element.alternative.listener.IAlternativesChangeListener;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.criterion.listener.CriteriaChangeEvent;
import sinbad2.element.criterion.listener.ICriteriaChangeListener;
import sinbad2.element.expert.Expert;
import sinbad2.element.expert.listener.ExpertsChangeEvent;
import sinbad2.element.expert.listener.IExpertsChangeListener;
import sinbad2.element.ui.nls.Messages;

public class ElementContentProvider implements ITreeContentProvider, IExpertsChangeListener, ICriteriaChangeListener,
	IAlternativesChangeListener, IProblemElementsSetChangeListener {
	
	private static final Class<?>[] _types = new Class<?>[] { Expert.class, Alternative.class, Criterion.class };
	
	private static final String[] _typesToString = new String[] {Messages.ElementContentProvider_0, Messages.ElementContentProvider_1, Messages.ElementContentProvider_2};
	private ProblemElementsManager _elementsManager;
	private ProblemElementsSet _elementSet;
	private Class<?> _type;
	private List<Expert> _experts;
	private List<Alternative> _alternatives;
	private List<Criterion> _criteria;
	private TreeViewer _viewer;
	
	private ElementContentProvider() {
		_experts = null;
		_alternatives = null;
		_criteria = null;
		_type = null;
	}
	
	public ElementContentProvider(TreeViewer viewer) {
		this();
		
		_viewer = viewer;
		hookTreeListener();
		_elementsManager = ProblemElementsManager.getInstance();
		_elementSet = _elementsManager.getActiveElementSet();
		_experts = _elementSet.getExperts();
		_alternatives = _elementSet.getAlternatives();
		_criteria = _elementSet.getCriteria();
		_type = Expert.class;
		
		_elementSet.registerExpertsChangesListener(this);
		_elementSet.registerAlternativesChangesListener(this);
		_elementSet.registerCriteriaChangesListener(this);
		_elementsManager.registerElementsSetChangeListener(this);
	}
	
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
		_elementSet.unregisterAlternativesChangeListener(this);
		_elementSet.unregisterCriteriaChangeListener(this);
		_elementsManager.unregisterElementsSetChangeListener(this);
	}
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		
		if (Expert.class.equals(_type)) {
			return ((List<Expert>) inputElement).toArray();
		} else if (Alternative.class.equals(_type)) {
			return ((List<Alternative>) inputElement).toArray();
		} else if (Criterion.class.equals(_type)) {
			return ((List<Criterion>) inputElement).toArray();
		} else {
			return null;
		}
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		
		if (Expert.class.equals(_type)) {
			return ((Expert) parentElement).getChildren().toArray();
		} else if (Alternative.class.equals(_type)) {
			return null;
		} else if (Criterion.class.equals(_type)) {
			return ((Criterion) parentElement).getSubcriteria().toArray();
		} else {
			return null;
		}
	}
	
	@Override
	public Object getParent(Object element) {
		
		try {
			if (Expert.class.equals(_type)) {
				return ((Expert) element).getParent();
			} else if (Alternative.class.equals(_type)) {
				return null;
			} else if (Criterion.class.equals(_type)) {
				return ((Criterion) element).getParent();
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public boolean hasChildren(Object element) {
		
		if (Expert.class.equals(_type)) {
			return ((Expert) element).hasChildren();
		} else if (Alternative.class.equals(_type)) {
			return false;
		} else if (Criterion.class.equals(_type)) {
			return ((Criterion) element).hasSubcriteria();
		} else {
			return false;
		}
	}
	
	public Object getInput() {
		
		if (Expert.class.equals(_type)) {
			return _experts;
		} else if (Alternative.class.equals(_type)) {
			return _alternatives;
		} else if (Criterion.class.equals(_type)) {
			return _criteria;
		} else {
			return null;
		}
	}
	
	public void setType(Class<?> c) {
		_type = c;
		_viewer.setInput(getInput());
		packViewer();
	}
	
	public Class<?> getType() {
		return _type;
	}
	
	public Class<?>[] getTypes() {
		return _types;
	}
	
	public String[] getTypesToString() {
		return _typesToString;
	}
	
	@Override
	public void notifyExpertsChange(ExpertsChangeEvent event) {
		_experts = _elementSet.getExperts();
		if (Expert.class.equals(_type)) {
			_viewer.setInput(getInput());
			packViewer();
		}
	}
	
	@Override
	public void notifyAlternativesChange(AlternativesChangeEvent event) {
		_alternatives = _elementSet.getAlternatives();
		
		if (Alternative.class.equals(_type)) {
			_viewer.setInput(getInput());
			packViewer();
		}
	}
	
	@Override
	public void notifyCriteriaChange(CriteriaChangeEvent event) {
		_criteria = _elementSet.getCriteria();
		
		if (Criterion.class.equals(_type)) {
			_viewer.setInput(getInput());
			packViewer();
		}
	}
	
	private void packViewer() {
		
		for (TreeColumn column : _viewer.getTree().getColumns()) {
			column.pack();
		}
	}
	
	@Override
	public void notifyNewProblemElementsSet(ProblemElementsSet elementSet) {
		
		if (_elementSet != elementSet) {
			_elementSet.unregisterExpertsChangeListener(this);
			_elementSet.unregisterAlternativesChangeListener(this);
			_elementSet.unregisterCriteriaChangeListener(this);
	
			_elementSet = elementSet;
			_experts = _elementSet.getExperts();
			_alternatives = _elementSet.getAlternatives();
			_criteria = _elementSet.getCriteria();
	
			_elementSet.registerExpertsChangesListener(this);
			_elementSet.registerAlternativesChangesListener(this);
			_elementSet.registerCriteriaChangesListener(this);
	
			_viewer.setInput(getInput());
			packViewer();
		}
	}
	
	public boolean canSelect() {
		Object input = getInput();
		
		if (input == null) {
			return false;
		}
		
		Object[] elements = getElements(input);
		if (elements == null) {
			return false;
		}
		
		return (elements.length > 0);
	}

}
