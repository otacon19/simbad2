package sinbad2.phasemethod.topsis.selection.ui.view.provider;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.twoTuple.TwoTuple;

public class CollectiveValuationsTableViewerContentProvider implements IStructuredContentProvider {

	private List<Object[]> _elementsDecisionMatrix;
	private ProblemElementsSet _elementsSet;
	
	@SuppressWarnings("rawtypes")
	public static class DataComparator implements Comparator {
		@Override
		public int compare(Object d1, Object d2) {
			Alternative a1 = (Alternative) ((Object[]) d1)[0];
			Alternative a2 = (Alternative) ((Object[]) d2)[0];
			Criterion c1 = (Criterion) ((Object[]) d1)[1];
			Criterion c2 = (Criterion) ((Object[]) d2)[1];
			
			int alternativeComparation = a1.compareTo(a2);
			if(alternativeComparation != 0) {
				return alternativeComparation;
			} else {
				return c1.compareTo(c2);
			}
		}
	 }
	
	public CollectiveValuationsTableViewerContentProvider() {
		_elementsDecisionMatrix = new LinkedList<Object[]>();
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();
	}
	
	@SuppressWarnings("unchecked")
	public void setInput(Valuation[][] decisionMatrix) {
		Object[] data;
		for(int i = 0; i < decisionMatrix.length; ++i) {
			for(int j = 0; j < decisionMatrix[i].length; ++j) {
				data = new Object[3];
				data[0] = _elementsSet.getAlternatives().get(j) ;
				data[1] = _elementsSet.getAllSubcriteria().get(i);
				data[2] = ((TwoTuple) decisionMatrix[i][j]);
				
				_elementsDecisionMatrix.add(data);
			}
		}

		Collections.sort(_elementsDecisionMatrix, new DataComparator());
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
