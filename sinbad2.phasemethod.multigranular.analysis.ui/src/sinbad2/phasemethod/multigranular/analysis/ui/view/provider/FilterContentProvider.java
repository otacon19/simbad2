package sinbad2.phasemethod.multigranular.analysis.ui.view.provider;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;

public class FilterContentProvider implements ITreeContentProvider {
	
	private ProblemElementsSet _elementsSet;
	
	public FilterContentProvider(ProblemElementsSet elementsSet) {
		_elementsSet = elementsSet;
	}
	
	@Override
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {}

	@Override
	public void dispose() {}

	@Override
	public Object[] getElements(Object parent) {
		
		if(parent instanceof Expert) {
			return _elementsSet.getExpertChildren(null).toArray();
		} else if(parent instanceof Criterion) {
			return _elementsSet.getCriterionSubcriteria(null).toArray();
		} else {
			return _elementsSet.getAlternatives().toArray();
		}
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		
		if(parentElement instanceof Expert) {
			return _elementsSet.getExpertChildren(null).toArray();
		} else if(parentElement instanceof Criterion) {
			return _elementsSet.getCriterionSubcriteria(null).toArray();
		} else if(parentElement instanceof Alternative){
			return _elementsSet.getAlternatives().toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		
		if(element instanceof Expert) {
			return ((Expert) element).getParent();
		} else if(element instanceof Criterion) {
			return ((Criterion) element).getParent();
		} else if(element instanceof Alternative){
			return element;
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		
		if(element instanceof Expert) {
			return ((Expert) element).hasChildrens();
		} else if(element instanceof Criterion) {
			return ((Criterion) element).hasSubcriteria();
		} else {
			return false;
		}
	}
}
