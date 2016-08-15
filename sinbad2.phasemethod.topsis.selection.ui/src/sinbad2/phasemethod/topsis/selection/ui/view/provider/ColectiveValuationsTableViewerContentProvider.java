package sinbad2.phasemethod.topsis.selection.ui.view.provider;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.core.utils.Pair;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.valuation.Valuation;

public class ColectiveValuationsTableViewerContentProvider implements IStructuredContentProvider {

	private List<Object[]> _elementsDecisionMatrix;
	
	public ColectiveValuationsTableViewerContentProvider() {
		_elementsDecisionMatrix = new LinkedList<Object[]>();
	}
	
	public void setInput(Map<Pair<Alternative, Criterion>, Valuation> decisionMatrix) {
		Object[] data;
		for(Pair<Alternative, Criterion> pair: decisionMatrix.keySet()) {
			data = new Object[3];
			data[0] = pair.getLeft();
			data[1] = pair.getRight();
			data[2] = decisionMatrix.get(pair);
			
			_elementsDecisionMatrix.add(data);
		}
	}
	
	public List<Object[]> getInput() {
		return _elementsDecisionMatrix;
	}
	
	@Override
	public void dispose() {
		_elementsDecisionMatrix = null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Object[]>) inputElement).toArray(new Object[0]);	
	}
}
