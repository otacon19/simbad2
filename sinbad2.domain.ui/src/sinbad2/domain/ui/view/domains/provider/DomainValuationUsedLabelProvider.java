package sinbad2.domain.ui.view.domains.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.domain.Domain;
import sinbad2.domain.valuations.DomainValuationsManager;

public class DomainValuationUsedLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		DomainValuationsManager dvm = DomainValuationsManager.getInstance();
		return dvm.getTypeOfValuation(((Domain) element).getId());
	}

}
