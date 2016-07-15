package sinbad2.phasemethod.todim.resolution.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public class DistanceColumnLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof Object[]) {
			return (String) ((Object[]) element)[5];
		} else {
			return null;
		}
	}
}
