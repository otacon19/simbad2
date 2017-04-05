package sinbad2.phasemethod.emergency.computinggainslosses.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public class CriterionColumnLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		return ((String[]) element)[1];
	}

}
