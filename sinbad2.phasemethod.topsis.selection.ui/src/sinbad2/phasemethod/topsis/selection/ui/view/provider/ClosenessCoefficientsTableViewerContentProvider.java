package sinbad2.phasemethod.topsis.selection.ui.view.provider;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.alternative.Alternative;
import sinbad2.valuation.twoTuple.TwoTuple;

public class ClosenessCoefficientsTableViewerContentProvider implements IStructuredContentProvider {

	private List<Object[]> _closenessCoefficients;
	
	public ClosenessCoefficientsTableViewerContentProvider() {
		_closenessCoefficients = new LinkedList<Object[]>();
		ProblemElementsManager.getInstance().getActiveElementSet();
	}
	
	public void setInput(Map<Alternative, TwoTuple> closenessCoefficients) {
		_closenessCoefficients.clear();
		Object[] data;
		
		int ranking = 1;
		for(Alternative a: closenessCoefficients.keySet()) {
			data = new Object[3];
			data[0] = a;
			data[1] = closenessCoefficients.get(a);
			data[2] = ranking;
			
			ranking++;
			
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
