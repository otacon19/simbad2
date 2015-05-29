package sinbad2.domain.ui.view.domain.provider;

import org.eclipse.jface.viewers.LabelProvider;

public class DomainValuationLabelProvider extends LabelProvider {
	
	@Override
	public String getText(Object element) {
		return (String) ((Object[]) ((Object[]) element)[1])[1];
	}
	
}
