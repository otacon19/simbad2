package sinbad2.phasemethod.topsis.selection.ui.view.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ColectiveValuationsTableViewerContentProvider implements IStructuredContentProvider {

	private List<Object[]> _decisionMatrix;
	
	public ColectiveValuationsTableViewerContentProvider() {
		_decisionMatrix = new LinkedList<Object[]>();
	}
	
	public List<Object[]> getInput() {
		return _decisionMatrix;
	}
	
	public void setInput(List<Object[]> decisionMatrix) {
		_decisionMatrix = decisionMatrix;
	}
	
	@Override
	public void dispose() {
		_decisionMatrix.clear();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Object[]>) inputElement).toArray();	
	}

}
