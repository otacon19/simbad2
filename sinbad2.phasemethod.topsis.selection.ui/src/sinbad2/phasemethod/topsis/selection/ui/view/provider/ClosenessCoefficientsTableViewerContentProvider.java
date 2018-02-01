package sinbad2.phasemethod.topsis.selection.ui.view.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.valuation.twoTuple.TwoTuple;

public class ClosenessCoefficientsTableViewerContentProvider implements IStructuredContentProvider {

	private List<Object[]> _closenessCoefficients;
	private ProblemElementsSet _elementsSet;
	
	public ClosenessCoefficientsTableViewerContentProvider() {
		_closenessCoefficients = new LinkedList<Object[]>();
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();
	}
	
	public void setInput(List<TwoTuple> closenessCoefficients) {
		Object[] data;
		for(int i = 0; i < closenessCoefficients.size(); ++i) {
			data = new Object[3];
			data[0] = _elementsSet.getAlternatives().get(i);
			data[1] = closenessCoefficients.get(i);
			data[2] = i + 1;
			
			_closenessCoefficients.add(data);
		}
	}
	
	public List<Object[]> getInput() {
		return _closenessCoefficients;
	}
	
	@Override
	public void dispose() {
		_closenessCoefficients.clear();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Object[]>) inputElement).toArray();	
	}
	
}
