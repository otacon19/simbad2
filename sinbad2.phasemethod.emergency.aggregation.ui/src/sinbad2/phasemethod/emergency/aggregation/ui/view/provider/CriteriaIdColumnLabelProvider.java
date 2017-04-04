package sinbad2.phasemethod.emergency.aggregation.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public class CriteriaIdColumnLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		return (String) ((Object[]) element)[0];
	}

}
