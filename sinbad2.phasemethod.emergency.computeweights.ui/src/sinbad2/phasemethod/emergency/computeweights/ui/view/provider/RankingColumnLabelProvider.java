package sinbad2.phasemethod.emergency.computeweights.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public class RankingColumnLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		return Integer.toString((int) ((Object[]) element)[0]);
	}

}
