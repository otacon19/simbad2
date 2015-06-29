package sinbad2.domain.linguistic.fuzzy.ui.dialog.provider;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.domain.linguistic.fuzzy.FuzzySet;

public class FuzzyTableContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@Override
	public Object[] getElements(Object inputElement) {
		return ((FuzzySet) inputElement).getLabelSet().getLabels().toArray();
	}

}
