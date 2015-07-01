package sinbad2.domain.ui.view.domainindex.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public class DomainIndexIdLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		return ((String[]) element)[1];
	}
}
