package sinbad2.phasemethod.emergency.computeweights.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public class CriterionIdColumnLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		return (String) ((Object[]) element)[0];
	}

}
