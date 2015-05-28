package sinbad2.domain.ui.view.domains.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.domain.Domain;

public class DomainDescriptionLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		return ((Domain) element).formatDescriptionDomain();
	}
}
