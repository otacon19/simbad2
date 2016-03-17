package sinbad2.phasemethod.topsis.selection.ui.view.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class DistanceIdealSolutionTableViewerContentProvider implements IStructuredContentProvider {

private List<Object[]> _distanceIdealSolution;
	
	public DistanceIdealSolutionTableViewerContentProvider() {
		_distanceIdealSolution = new LinkedList<Object[]>();
	}

	public List<Object[]> getInput() {
		return _distanceIdealSolution;
	}
	
	public void setInput(List<Object[]> idealSolution) {
		_distanceIdealSolution = idealSolution;
	}
	
	@Override
	public void dispose() {
		_distanceIdealSolution.clear();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Object[]>) inputElement).toArray();	
	}
	
}
