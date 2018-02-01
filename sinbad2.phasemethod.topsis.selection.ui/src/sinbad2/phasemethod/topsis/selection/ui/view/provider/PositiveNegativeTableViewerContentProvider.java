package sinbad2.phasemethod.topsis.selection.ui.view.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class PositiveNegativeTableViewerContentProvider implements IStructuredContentProvider {

	private List<Object[]> _positiveNegativeDistances;
	
	public PositiveNegativeTableViewerContentProvider() {
		_positiveNegativeDistances = new LinkedList<Object[]>();
	}

	public List<Object[]> getInput() {
		return _positiveNegativeDistances;
	}
	
	public void setInput(List<Object[]> positiveNegativeDistances) {
		_positiveNegativeDistances = positiveNegativeDistances;
	}
	
	@Override
	public void dispose() {
		_positiveNegativeDistances.clear();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Object[]>) inputElement).toArray();	
	}
	
}
