package sinbad2.phasemethod.topsis.selection.ui.view.provider;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.valuation.twoTuple.TwoTuple;

public class NoIdealSolutionTableViewerContentProvider implements IStructuredContentProvider {
	
	private List<Object[]> _noIdealSolutionData;
	private ProblemElementsSet _elementsSet;
	
	@SuppressWarnings("rawtypes")
	public static class DataComparator implements Comparator {
		@Override
		public int compare(Object d1, Object d2) {
			Criterion c1 = (Criterion) ((Object[]) d1)[0];
			Criterion c2 = (Criterion) ((Object[]) d2)[0];
			
			return c1.compareTo(c2);
		}
	 }
	
	@Override
	public void dispose() {}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	public NoIdealSolutionTableViewerContentProvider() {
		_noIdealSolutionData = new LinkedList<Object[]>();
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();
	}
	
	@SuppressWarnings("unchecked")
	public void setInput(List<TwoTuple> noIdealSolution) {
		Object[] data;
		for(int i = 0; i < noIdealSolution.size(); ++i) {
			data = new Object[2];
			data[0] = _elementsSet.getAllSubcriteria().get(i);
			data[1] = noIdealSolution.get(i);
			
			_noIdealSolutionData.add(data);
		}

		Collections.sort(_noIdealSolutionData, new DataComparator());
	}
	
	public List<Object[]> getInput() {
		return _noIdealSolutionData;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Object[]>) inputElement).toArray(new Object[0]);	
	}

	
}
