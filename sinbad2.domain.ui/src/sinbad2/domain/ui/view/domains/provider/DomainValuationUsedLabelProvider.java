package sinbad2.domain.ui.view.domains.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.domain.Domain;
import sinbad2.domain.valuations.DomainsValuationsManager;

public class DomainValuationUsedLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		DomainsValuationsManager dvm = DomainsValuationsManager.getInstance();
		
		String nameValuation = dvm.getNameValuation(dvm.getValuationSupportedForSpecificDomain(((Domain) element).getId()));

		return nameValuation;
	}

}
