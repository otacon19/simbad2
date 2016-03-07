package sinbad2.phasemethod.aggregation.ui.view.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.element.ProblemElement;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;

public class AggregationCriterionViewerContentProvider implements ITreeContentProvider {

	private ProblemElementsSet _elementsSet;
	
	private AggregationCriterionViewerContentProvider() {}
	
	public AggregationCriterionViewerContentProvider(ProblemElementsSet elementsSet) {
		this();

		_elementsSet = elementsSet;
	}
	
	@Override
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {}

	@Override
	public void dispose() {}

	@Override
	public Object[] getElements(Object parent) {
		return new Object[] {""}; //$NON-NLS-1$
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		ProblemElement element = null;
		if (parentElement instanceof ProblemElement) {
			element = (ProblemElement) parentElement;
		}

		List<ProblemElement> result = new LinkedList<ProblemElement>();
		for (ProblemElement son : _elementsSet.getElementCriterionSubcriteria(element)) {
			if (((Criterion) son).hasSubcriteria()) {
				result.add(son);
			}
		}
		
		if (result.size() > 0) {
			return result.toArray(new ProblemElement[0]);
		} else {
			return null;
		}
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof Criterion) {
			return ((Criterion) element).getParent();
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	@Override
	public boolean hasChildren(Object element) {
		ProblemElement problemElement = null;
		if (element instanceof ProblemElement) {
			problemElement = (ProblemElement) element;
		}

		for (ProblemElement son : _elementsSet.getElementCriterionSubcriteria(problemElement)) {
			if (((Criterion) son).hasSubcriteria()) {
				return true;
			}
		}
		
		return false;
	}
}
