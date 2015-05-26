package sinbad2.domain.ui.dialog.modifyDialog;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import sinbad2.domain.Domain;
import sinbad2.domain.ui.dialog.DomainDialog;

public class ModifyDomainDialog extends DomainDialog {
	
	private Domain _newDomain;
	private Domain _oldDomain;

	public ModifyDomainDialog(Shell parentShell) {
		super(Display.getCurrent().getActiveShell());
	}
	
	public void setDomain(Domain domain) {
		//TODO no le veo sentido a esta funcion (cambiada)
		//_oldDomain = domain;
		_newDomain = (Domain) domain.clone();
		_id = domain.getId();
		//_ids.remove(_id);
		_ids.remove(_oldDomain.getId());
	}
	
	public void setNewDomain(Domain newDomain) {
		_newDomain = newDomain;
	}
	
	public void setOldDomain(Domain oldDomain) {
		_oldDomain = oldDomain;
	}
	
	public Domain getNewDomain() {
		return _newDomain;
	}
	
	public Domain getOldDomain() {
		return _oldDomain;
	}

}
