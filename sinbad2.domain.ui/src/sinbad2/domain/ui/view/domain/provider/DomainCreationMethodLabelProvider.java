package sinbad2.domain.ui.view.domain.provider;

import org.eclipse.jface.viewers.LabelProvider;

import sinbad2.domain.Domain;

public class DomainCreationMethodLabelProvider extends LabelProvider {
	
	@Override
	public String getText(Object element) {
		return ((Domain) ((Object[]) element)[0]).getName();
	}
	
}
