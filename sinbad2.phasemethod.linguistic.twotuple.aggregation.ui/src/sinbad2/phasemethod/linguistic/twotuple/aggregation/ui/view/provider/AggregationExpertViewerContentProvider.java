package sinbad2.phasemethod.linguistic.twotuple.aggregation.ui.view.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.element.ProblemElement;
import sinbad2.element.expert.Expert;

public class AggregationExpertViewerContentProvider implements ITreeContentProvider {

	private List<Expert> _experts;
	
	private AggregationExpertViewerContentProvider() {}
	
	public AggregationExpertViewerContentProvider(List<Expert> experts) {
		this();

		_experts = experts;
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
		if(element instanceof Expert) {
			for(ProblemElement e: _experts) {
				List<Expert> sons = ((Expert) e).getChildren();
				for(Expert son: sons) {
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
		if (element instanceof Expert) {
			return ((Expert) element).getParent();
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof Expert) {
			return ((Expert) element).hasChildren();
		}
		return false;
	}
}