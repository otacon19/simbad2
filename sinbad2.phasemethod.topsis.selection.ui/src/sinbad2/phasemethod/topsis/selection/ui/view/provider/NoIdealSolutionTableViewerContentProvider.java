package sinbad2.phasemethod.topsis.selection.ui.view.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class NoIdealSolutionTableViewerContentProvider implements IStructuredContentProvider {
	
private List<Object[]> _noIdealSolution;
	
	public NoIdealSolutionTableViewerContentProvider() {
		_noIdealSolution = new LinkedList<Object[]>();
	}

	public List<Object[]> getInput() {
		return _noIdealSolution;
	}
	
	public void setInput(List<Object[]> idealSolution) {
		_noIdealSolution = idealSolution;
	}
	
	@Override
	public void dispose() {
		_noIdealSolution.clear();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Object[]>) inputElement).toArray();	
	}

	
}
