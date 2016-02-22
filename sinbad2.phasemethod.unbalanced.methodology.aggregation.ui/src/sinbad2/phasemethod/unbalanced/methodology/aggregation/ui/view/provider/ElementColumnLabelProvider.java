package sinbad2.phasemethod.unbalanced.methodology.aggregation.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.element.ProblemElement;

public class ElementColumnLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {

		if (element instanceof ProblemElement) {
			return ((ProblemElement) element).getId();
		} else {
			return "All";
		}
	}
}