package sinbad2.phasemethod.heterogeneous.fusion.unification.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public class CriterionColumnLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof Object[]) {
			return (String) ((Object[]) element)[2];
		} else {
			return null;
		}
	}
}
