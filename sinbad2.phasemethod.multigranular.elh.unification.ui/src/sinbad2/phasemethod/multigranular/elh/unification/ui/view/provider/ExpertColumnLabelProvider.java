package sinbad2.phasemethod.multigranular.elh.unification.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public class ExpertColumnLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof Object[]) {
			return (String) ((Object[]) element)[0];
		} else {
			return null;
		}
	}
}
