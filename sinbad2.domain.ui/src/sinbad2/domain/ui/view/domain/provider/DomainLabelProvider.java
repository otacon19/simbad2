package sinbad2.domain.ui.view.domain.provider;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import sinbad2.domain.Domain;
import sinbad2.domain.ui.DomainUI;
import sinbad2.domain.ui.DomainUIsManager;

public class DomainLabelProvider extends LabelProvider {
	
	@Override
	public String getText(Object element) {
		return ((Domain) ((Object[]) element)[0]).getName();
	}
	
	@Override
	public Image getImage(Object element) {
		DomainUIsManager domainUisManager = DomainUIsManager.getInstance();
		DomainUI domainUI = domainUisManager.getDomainUI(((Domain) ((Object[]) element)[0]).getType());
		
		return domainUI.getIcon();
}
}
