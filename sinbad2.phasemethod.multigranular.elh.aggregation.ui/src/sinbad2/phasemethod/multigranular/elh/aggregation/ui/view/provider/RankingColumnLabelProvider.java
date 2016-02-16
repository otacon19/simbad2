package sinbad2.phasemethod.multigranular.elh.aggregation.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public class RankingColumnLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
		return ((Integer) ((Object[]) element)[0]).toString();
	}
}