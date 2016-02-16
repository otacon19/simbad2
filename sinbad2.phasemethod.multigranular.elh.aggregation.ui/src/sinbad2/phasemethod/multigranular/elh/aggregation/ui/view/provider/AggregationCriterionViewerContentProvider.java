package sinbad2.phasemethod.multigranular.elh.aggregation.ui.view.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.element.ProblemElement;
import sinbad2.element.criterion.Criterion;

public class AggregationCriterionViewerContentProvider implements ITreeContentProvider {

	private List<Criterion> _criteria;
	
	private AggregationCriterionViewerContentProvider() {}
	
	public AggregationCriterionViewerContentProvider(List<Criterion> criteria) {
		this();

		_criteria = criteria;
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
		if(element instanceof Criterion) {
			for(ProblemElement c: _criteria) {
				List<Criterion> sons = ((Criterion) c).getSubcriteria();
				for(Criterion son: sons) {
					result.add(son);
				}
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
		if(element instanceof Criterion) {
			return ((Criterion) element).hasSubcriteria();
		}
		return false;
	}
}
