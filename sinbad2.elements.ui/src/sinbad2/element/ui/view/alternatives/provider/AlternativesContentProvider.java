package sinbad2.element.ui.view.alternatives.provider;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.TableColumn;

import sinbad2.element.IProblemElementsSetChangeListener;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.alternative.listener.AlternativesChangeEvent;
import sinbad2.element.alternative.listener.IAlternativesChangeListener;

public class AlternativesContentProvider implements IStructuredContentProvider,
		IAlternativesChangeListener, IProblemElementsSetChangeListener {
	
	private ProblemElementsManager _elementManager;
	private ProblemElementsSet _elementSet;
	private List<Alternative> _alternatives;
	private TableViewer _tableViewer;
	
	
	public AlternativesContentProvider() {
		_alternatives = null;
	}
	
	public AlternativesContentProvider(TableViewer tableViewer) {
		_tableViewer = tableViewer;
		
		_elementManager = ProblemElementsManager.getInstance();
		_elementSet = _elementManager.getActiveElementSet();
		_alternatives = _elementSet.getAlternatives();
		
		_elementSet.registerAlternativesChangesListener(this);
		_elementManager.registerElementsSetChangeListener(this);
	}
	
	@Override
	public void dispose() {
		_elementSet.unregisterAlternativesChangeListener(this);
		_elementManager.unregisterElementsSetChangeListener(this);
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Alternative>) inputElement).toArray();
	}
	
	public Object getInput() {
		return _alternatives;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void notifyAlternativesChange(AlternativesChangeEvent event) {
		
		switch(event.getChange()) {
			case ALTERNATIVES_CHANGES:
				_alternatives = (List<Alternative>) event.getNewValue();
				_tableViewer.setInput(_alternatives);
				break;
			case ADD_ALTERNATIVE:
				addAlternative((Alternative) event.getNewValue());
				break;
			case ADD_MULTIPLE_ALTERNATIVES:
				for(Alternative alternative: (List<Alternative>) event.getNewValue()) {
					addAlternative(alternative);
				}
				break;
			case REMOVE_ALTERNATIVE:
				_tableViewer.refresh();
				break;
				
			case REMOVE_MULTIPLE_ALTERNATIVES:
				_tableViewer.refresh();
				break;
				
			case MODIFY_ALTERNATIVE:
				_tableViewer.refresh();
				break;	
		}
		
		packViewer();
		
	}
	
	private void packViewer() {
		
		for(TableColumn column: _tableViewer.getTable().getColumns()) {
			column.pack();
		}
		
	}

	private void addAlternative(Alternative newAlternative) {
		int pos = 0;
		boolean find = false;
		
		do {
			if(_alternatives.get(pos) == newAlternative) {
				find = true;
			} else {
				pos++;
			}
		} while (!find);
		_tableViewer.insert(newAlternative, pos);
	}

	@Override
	public void notifyNewProblemElementsSet(ProblemElementsSet elementSet) {
		
		if(_elementSet != elementSet) {
			_elementSet.unregisterAlternativesChangeListener(this);
			_elementSet = elementSet;
			_alternatives = _elementSet.getAlternatives();
			_elementSet.registerAlternativesChangesListener(this);
			_tableViewer.setInput(_alternatives);
		}
		
	}

}
