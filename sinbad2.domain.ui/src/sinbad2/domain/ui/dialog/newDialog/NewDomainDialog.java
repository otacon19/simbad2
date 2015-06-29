package sinbad2.domain.ui.dialog.newDialog;

import sinbad2.domain.Domain;
import sinbad2.domain.ui.dialog.DomainDialog;


public class NewDomainDialog extends DomainDialog {

	protected Domain _domain;
	
	public NewDomainDialog() {
		super();
	}
	
	public void setDomain(Domain domain) {
		_domain = domain;
		_id = ""; //$NON-NLS-1$
	}
	
	public Domain getDomain() {
		return _domain;
	}
}
