package sinbad2.domain.ui.view.domainindex.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public class DomainIndexIndexLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		return ((String[]) element)[0];
	}

}
