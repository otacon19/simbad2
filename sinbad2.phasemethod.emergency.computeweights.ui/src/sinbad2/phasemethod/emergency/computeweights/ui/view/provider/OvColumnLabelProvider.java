package sinbad2.phasemethod.emergency.computeweights.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public class OvColumnLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		return Double.toString((Double) ((Object[]) element)[1]);
	}

}
