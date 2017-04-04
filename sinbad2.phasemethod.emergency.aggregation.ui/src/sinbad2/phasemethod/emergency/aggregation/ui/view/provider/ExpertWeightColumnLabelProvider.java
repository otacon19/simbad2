package sinbad2.phasemethod.emergency.aggregation.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public class ExpertWeightColumnLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
		double value = Math.round(Double.parseDouble((String) ((Object[]) element)[1]) * 100d) / 100d;
		return Double.toString(value);
	}
	
}
