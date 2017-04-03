package sinbad2.phasemethod.emergency.aggregation.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public class ExpertWeightColumnLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
		return ((String[]) element)[1];
	}
	
}
