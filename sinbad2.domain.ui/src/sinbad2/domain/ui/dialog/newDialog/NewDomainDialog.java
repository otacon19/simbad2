package sinbad2.domain.ui.dialog.newDialog;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import sinbad2.domain.Domain;
import sinbad2.domain.ui.dialog.DomainDialog;


public class NewDomainDialog extends DomainDialog {

	private Domain _domain;

	public NewDomainDialog(Shell parentShell) {
		super(Display.getCurrent().getActiveShell());
	}
	
	public void setDomain(Domain domain) {
		_domain = domain;
		_id = "";
	}
	
	public Domain getDomain() {
		return _domain;
	}
}
